const Profile = (() => {
  let profileUser = null;
  let activeTab = 'posts';

  async function init() {
    if (!Auth.isLoggedIn()) { window.location.href = '/frontend/index.html'; return; }
    const params = new URLSearchParams(location.search);
    const username = params.get('u') || Auth.getUser()?.username;
    if (!username) { window.location.href = '/frontend/index.html'; return; }
    await loadProfile(username);
    initTabs();
    loadPosts();
  }

  async function loadProfile(username) {
    try {
      profileUser = await API.get(`/users/${username}`);
      renderHeader();
    } catch (e) { showToast('Profile not found'); }
  }

  function renderHeader() {
    const u = profileUser;
    document.getElementById('profile-avatar').src = Components.avatarSrc(u.avatarUrl, u.username);
    document.getElementById('profile-username').textContent = u.username;
    document.getElementById('profile-fullname').textContent = u.fullName || '';
    document.getElementById('profile-bio').textContent = u.bio || '';
    document.getElementById('posts-count').textContent = u.postsCount;
    document.getElementById('followers-count').textContent = u.followersCount;
    document.getElementById('following-count').textContent = u.followingCount;

    const followBtn = document.getElementById('follow-btn');
    const editBtn = document.getElementById('edit-profile-btn');

    if (u.isOwnProfile) {
      followBtn.classList.add('hidden');
      editBtn.classList.remove('hidden');
      // Show saved tab
      document.getElementById('tab-saved').classList.remove('hidden');
    } else {
      editBtn.classList.add('hidden');
      followBtn.classList.remove('hidden');
      followBtn.textContent = u.isFollowing ? 'Following' : 'Follow';
      followBtn.className = `btn ${u.isFollowing ? 'btn-outline' : 'btn-primary'} btn-sm`;
      followBtn.addEventListener('click', async () => {
        try {
          const res = await API.post(`/users/${u.id}/follow`);
          u.isFollowing = res.following;
          followBtn.textContent = res.following ? 'Following' : 'Follow';
          followBtn.className = `btn ${res.following ? 'btn-outline' : 'btn-primary'} btn-sm`;
          const countEl = document.getElementById('followers-count');
          countEl.textContent = parseInt(countEl.textContent) + (res.following ? 1 : -1);
        } catch (e) { showToast(e.message); }
      });
    }

    document.getElementById('edit-profile-btn')?.addEventListener('click', openEditModal);
  }

  function initTabs() {
    document.querySelectorAll('.profile-tab').forEach(tab => {
      tab.addEventListener('click', () => {
        document.querySelectorAll('.profile-tab').forEach(t => t.classList.remove('active'));
        tab.classList.add('active');
        activeTab = tab.dataset.tab;
        loadPosts();
      });
    });
  }

  async function loadPosts() {
    const grid = document.getElementById('profile-grid');
    grid.innerHTML = '<p style="color:var(--text-secondary);text-align:center;grid-column:1/-1;padding:20px">Loading...</p>';
    try {
      let data;
      if (activeTab === 'saved' && profileUser.isOwnProfile) {
        data = await API.get(`/users/${profileUser.id}/saved?page=0&size=30`);
      } else {
        data = await API.get(`/users/${profileUser.id}/posts?page=0&size=30`);
      }
      const posts = data.content || [];
      grid.innerHTML = '';
      if (!posts.length) { grid.innerHTML = '<p style="color:var(--text-secondary);text-align:center;grid-column:1/-1;padding:40px">No posts yet</p>'; return; }
      posts.forEach(post => {
        const item = document.createElement('div');
        item.className = 'explore-item';
        item.innerHTML = `
          <img src="${post.imageUrl}" alt="Post" loading="lazy">
          <div class="explore-item-overlay">
            <span>❤️ ${post.likesCount}</span>
          </div>`;
        item.addEventListener('click', () => openPostDetail(post));
        grid.appendChild(item);
      });
    } catch (e) { grid.innerHTML = ''; }
  }

  function openPostDetail(post) {
    const overlay = document.createElement('div');
    overlay.className = 'modal-overlay';
    overlay.innerHTML = `
      <div class="post-detail-modal modal">
        <div class="post-detail-image">
          <img src="${post.imageUrl}" alt="Post">
        </div>
        <div class="post-detail-side">
          <div class="post-header" style="border-bottom:1px solid var(--border)">
            <div class="post-header-left">
              <img class="avatar avatar-sm" src="${Components.avatarSrc(post.user?.avatarUrl, post.user?.username)}" alt="">
              <span class="post-username">${Components.escHtml(post.user?.username || '')}</span>
            </div>
            <button id="close-post-detail" class="action-btn">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </div>
          <div class="post-detail-comments" id="post-detail-comments">Loading...</div>
        </div>
      </div>`;
    document.body.appendChild(overlay);
    overlay.querySelector('#close-post-detail').addEventListener('click', () => overlay.remove());
    overlay.addEventListener('click', (e) => { if (e.target === overlay) overlay.remove(); });
    loadComments(post.id, overlay.querySelector('#post-detail-comments'));
  }

  async function loadComments(postId, el) {
    try {
      const data = await API.get(`/posts/${postId}/comments?page=0&size=20`);
      el.innerHTML = '';
      if (!data.content.length) { el.innerHTML = '<p style="color:var(--text-secondary);padding:16px">No comments yet</p>'; return; }
      data.content.forEach(c => el.appendChild(Components.createCommentItem(c)));
    } catch (e) { el.innerHTML = ''; }
  }

  function openEditModal() {
    const u = profileUser;
    const overlay = document.createElement('div');
    overlay.className = 'modal-overlay';
    overlay.innerHTML = `
      <div class="modal">
        <div class="modal-header">
          <span>Edit Profile</span>
          <button id="close-edit">✕</button>
        </div>
        <div class="modal-body">
          <div class="input-group"><label>Full Name</label><input class="input-field" id="edit-fullname" value="${Components.escHtml(u.fullName || '')}"></div>
          <div class="input-group"><label>Bio</label><textarea class="input-field" id="edit-bio" maxlength="150">${Components.escHtml(u.bio || '')}</textarea></div>
          <button class="btn btn-primary" style="width:100%" id="save-profile-btn">Save</button>
        </div>
      </div>`;
    document.body.appendChild(overlay);
    overlay.querySelector('#close-edit').addEventListener('click', () => overlay.remove());
    overlay.querySelector('#save-profile-btn').addEventListener('click', async () => {
      try {
        await API.put('/users/me', { fullName: overlay.querySelector('#edit-fullname').value, bio: overlay.querySelector('#edit-bio').value });
        overlay.remove();
        await loadProfile(u.username);
        showToast('Profile updated');
      } catch (e) { showToast(e.message); }
    });
  }

  return { init };
})();
