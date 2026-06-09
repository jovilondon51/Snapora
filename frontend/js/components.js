const Components = (() => {
  function avatarSrc(url, username) {
    return url || `https://ui-avatars.com/api/?name=${encodeURIComponent(username || 'U')}&background=E1306C&color=fff&bold=true`;
  }

  function timeAgo(dateStr) {
    const diff = (Date.now() - new Date(dateStr)) / 1000;
    if (diff < 60) return 'just now';
    if (diff < 3600) return `${Math.floor(diff / 60)}m`;
    if (diff < 86400) return `${Math.floor(diff / 3600)}h`;
    if (diff < 604800) return `${Math.floor(diff / 86400)}d`;
    return new Date(dateStr).toLocaleDateString();
  }

  function createPostCard(post) {
    const el = document.createElement('article');
    el.className = 'post-card';
    el.dataset.postId = post.id;

    const avatar = avatarSrc(post.user.avatarUrl, post.user.username);
    const caption = post.caption || '';
    const shortCaption = caption.length > 100 ? caption.slice(0, 100) : caption;
    const hasMore = caption.length > 100;

    el.innerHTML = `
      <div class="post-header">
        <div class="post-header-left">
          <a href="/frontend/profile.html?u=${post.user.username}">
            <img class="avatar avatar-sm" src="${avatar}" alt="${post.user.username}">
          </a>
          <div>
            <a href="/frontend/profile.html?u=${post.user.username}" class="post-username">${escHtml(post.user.username)}</a>
            ${post.location ? `<div class="post-location">${escHtml(post.location)}</div>` : ''}
          </div>
        </div>
        <button class="action-btn post-menu-btn" aria-label="More options">
          <svg viewBox="0 0 24 24" fill="currentColor"><circle cx="12" cy="5" r="1.5"/><circle cx="12" cy="12" r="1.5"/><circle cx="12" cy="19" r="1.5"/></svg>
        </button>
      </div>
      <div class="post-image-wrap" data-post-id="${post.id}">
        <img src="${post.imageUrl}" alt="Post image" loading="lazy">
      </div>
      <div class="post-actions">
        <button class="action-btn like-btn ${post.likedByMe ? 'liked' : ''}" aria-label="Like">
          <svg viewBox="0 0 24 24" fill="${post.likedByMe ? '#E1306C' : 'none'}" stroke="currentColor" stroke-width="2">
            <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
          </svg>
        </button>
        <button class="action-btn comment-btn" aria-label="Comment">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
          </svg>
        </button>
        <button class="action-btn share-btn" aria-label="Share">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="22" y1="2" x2="11" y2="13"/><polygon points="22 2 15 22 11 13 2 9 22 2"/>
          </svg>
        </button>
        <button class="action-btn save-btn ${post.savedByMe ? 'saved' : ''}" aria-label="Save">
          <svg viewBox="0 0 24 24" fill="${post.savedByMe ? 'currentColor' : 'none'}" stroke="currentColor" stroke-width="2">
            <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>
          </svg>
        </button>
      </div>
      <div class="post-meta">
        <div class="like-count">${post.likesCount} ${post.likesCount === 1 ? 'like' : 'likes'}</div>
        ${caption ? `<div class="post-caption"><strong>${escHtml(post.user.username)}</strong><span class="caption-text">${escHtml(shortCaption)}${hasMore ? `<span class="read-more">... more</span>` : ''}</span></div>` : ''}
        <div class="post-time">${timeAgo(post.createdAt)}</div>
      </div>
      <div class="comment-box">
        <input type="text" class="comment-input" placeholder="Add a comment…" />
        <button class="post-btn">Post</button>
      </div>
    `;

    attachPostCardEvents(el, post);
    return el;
  }

  function attachPostCardEvents(el, post) {
    const likeBtn = el.querySelector('.like-btn');
    const saveBtn = el.querySelector('.save-btn');
    const likeCount = el.querySelector('.like-count');
    const imageWrap = el.querySelector('.post-image-wrap');
    const commentInput = el.querySelector('.comment-input');
    const postBtn = el.querySelector('.post-btn');
    const readMore = el.querySelector('.read-more');
    const captionText = el.querySelector('.caption-text');

    // Like toggle
    likeBtn.addEventListener('click', async () => {
      try {
        likeBtn.classList.add('like-animate');
        setTimeout(() => likeBtn.classList.remove('like-animate'), 400);
        const res = await API.post(`/posts/${post.id}/like`);
        const heart = likeBtn.querySelector('path');
        if (res.liked) {
          likeBtn.classList.add('liked');
          heart.setAttribute('fill', '#E1306C');
          heart.setAttribute('stroke', '#E1306C');
          post.likesCount++;
        } else {
          likeBtn.classList.remove('liked');
          heart.setAttribute('fill', 'none');
          heart.setAttribute('stroke', 'currentColor');
          post.likesCount = Math.max(0, post.likesCount - 1);
        }
        likeCount.textContent = `${post.likesCount} ${post.likesCount === 1 ? 'like' : 'likes'}`;
      } catch (e) { showToast(e.message); }
    });

    // Double-tap to like
    let lastTap = 0;
    imageWrap.addEventListener('click', async (e) => {
      const now = Date.now();
      if (now - lastTap < 300) {
        const heart = document.createElement('div');
        heart.className = 'float-heart';
        heart.textContent = '❤️';
        imageWrap.appendChild(heart);
        setTimeout(() => heart.remove(), 800);
        if (!likeBtn.classList.contains('liked')) likeBtn.click();
      }
      lastTap = now;
    });

    // Save toggle
    saveBtn.addEventListener('click', async () => {
      try {
        const res = await API.post(`/posts/${post.id}/save`);
        const icon = saveBtn.querySelector('path');
        if (res.saved) { saveBtn.classList.add('saved'); icon.setAttribute('fill', 'currentColor'); }
        else { saveBtn.classList.remove('saved'); icon.setAttribute('fill', 'none'); }
        showToast(res.saved ? 'Post saved' : 'Post unsaved');
      } catch (e) { showToast(e.message); }
    });

    // Read more
    if (readMore) {
      readMore.addEventListener('click', () => {
        captionText.innerHTML = escHtml(post.caption || '');
      });
    }

    // Comment submit
    postBtn.addEventListener('click', async () => {
      const content = commentInput.value.trim();
      if (!content) return;
      try {
        await API.post(`/posts/${post.id}/comments`, { content });
        commentInput.value = '';
        showToast('Comment posted');
      } catch (e) { showToast(e.message); }
    });

    commentInput.addEventListener('keydown', (e) => { if (e.key === 'Enter') postBtn.click(); });
  }

  function createStoryRing(story, onClick) {
    const el = document.createElement('div');
    el.className = 'story-item';
    const avatar = avatarSrc(story.user.avatarUrl, story.user.username);
    el.innerHTML = `
      <div class="story-ring ${story.viewedByMe ? 'viewed' : ''}">
        <img class="avatar avatar-md" src="${avatar}" alt="${story.user.username}" style="border:2px solid var(--surface)">
      </div>
      <span>${escHtml(story.user.username)}</span>
    `;
    el.addEventListener('click', () => onClick && onClick(story));
    return el;
  }

  function createCommentItem(c, depth = 0) {
    const el = document.createElement('div');
    el.className = 'comment-item';
    const avatar = avatarSrc(c.user.avatarUrl, c.user.username);
    el.innerHTML = `
      <img class="avatar avatar-sm" src="${avatar}" alt="${c.user.username}">
      <div class="comment-content">
        <span class="comment-author">${escHtml(c.user.username)}</span>
        <p class="comment-text">${escHtml(c.content)}</p>
        <div class="comment-meta">
          <span>${timeAgo(c.createdAt)}</span>
          <button class="reply-btn" style="font-size:11px;font-weight:600;color:var(--text-secondary)">Reply</button>
        </div>
        ${c.replies && c.replies.length ? `<div class="replies"></div>` : ''}
      </div>
    `;
    if (c.replies && c.replies.length) {
      const repliesEl = el.querySelector('.replies');
      c.replies.forEach(r => repliesEl.appendChild(createCommentItem(r, depth + 1)));
    }
    return el;
  }

  function createUserRow(user, showFollow = true) {
    const el = document.createElement('div');
    el.className = 'user-row';
    const avatar = avatarSrc(user.avatarUrl, user.username);
    el.innerHTML = `
      <a href="/frontend/profile.html?u=${user.username}">
        <img class="avatar avatar-sm" src="${avatar}" alt="${user.username}">
      </a>
      <div class="user-row-info">
        <a href="/frontend/profile.html?u=${user.username}" class="user-row-name">${escHtml(user.username)}</a>
        <div class="user-row-sub">${escHtml(user.fullName || '')}</div>
      </div>
      ${showFollow ? `<button class="btn btn-sm ${user.isFollowing ? 'btn-outline' : 'btn-primary'} follow-btn" data-user-id="${user.id}">${user.isFollowing ? 'Following' : 'Follow'}</button>` : ''}
    `;
    if (showFollow) {
      const btn = el.querySelector('.follow-btn');
      btn.addEventListener('click', async () => {
        try {
          const res = await API.post(`/users/${user.id}/follow`);
          user.isFollowing = res.following;
          btn.textContent = res.following ? 'Following' : 'Follow';
          btn.className = `btn btn-sm ${res.following ? 'btn-outline' : 'btn-primary'} follow-btn`;
        } catch (e) { showToast(e.message); }
      });
    }
    return el;
  }

  function renderSkeletons(container, count = 3) {
    container.innerHTML = '';
    for (let i = 0; i < count; i++) {
      container.innerHTML += `
        <div class="post-skeleton">
          <div class="skel-header">
            <div class="skeleton skel-avatar"></div>
            <div class="skeleton skel-text-sm"></div>
          </div>
          <div class="skeleton skel-image"></div>
          <div class="skel-actions">
            <div class="skeleton skel-action"></div>
            <div class="skeleton skel-action"></div>
            <div class="skeleton skel-action"></div>
          </div>
        </div>`;
    }
  }

  function escHtml(str) {
    if (!str) return '';
    return String(str).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
  }

  return { createPostCard, createStoryRing, createCommentItem, createUserRow, renderSkeletons, avatarSrc, timeAgo, escHtml };
})();

function showToast(msg) {
  let t = document.querySelector('.toast');
  if (!t) { t = document.createElement('div'); t.className = 'toast'; document.body.appendChild(t); }
  t.textContent = msg;
  t.classList.add('show');
  setTimeout(() => t.classList.remove('show'), 2800);
}
