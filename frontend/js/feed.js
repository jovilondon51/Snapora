const Feed = (() => {
  let page = 0;
  let loading = false;
  let hasMore = true;
  const feedEl = document.getElementById('feed-posts');
  const storiesEl = document.getElementById('stories-bar');

  async function loadStories() {
    if (!storiesEl) return;
    try {
      const data = await API.get('/stories/feed');
      if (!data || !data.length) { storiesEl.innerHTML = '<p style="color:var(--text-secondary);font-size:13px;padding:4px 0">No stories yet</p>'; return; }
      storiesEl.innerHTML = '';

      // Group stories by user
      const userMap = new Map();
      data.forEach(s => {
        if (!userMap.has(s.user.id)) userMap.set(s.user.id, { user: s.user, stories: [], viewedByMe: true });
        const entry = userMap.get(s.user.id);
        entry.stories.push(s);
        if (!s.viewedByMe) entry.viewedByMe = false;
      });

      userMap.forEach(entry => {
        const ring = Components.createStoryRing({ user: entry.user, viewedByMe: entry.viewedByMe }, () => {
          StoryViewer.open(entry.stories);
        });
        storiesEl.appendChild(ring);
      });
    } catch (e) { console.error('stories error', e); }
  }

  async function loadFeed() {
    if (loading || !hasMore || !feedEl) return;
    loading = true;

    if (page === 0) Components.renderSkeletons(feedEl, 3);

    try {
      const data = await API.get(`/posts/feed?page=${page}&size=10`);
      if (page === 0) feedEl.innerHTML = '';
      if (!data.content.length) { hasMore = false; if (page === 0) feedEl.innerHTML = '<p style="color:var(--text-secondary);text-align:center;padding:40px 0">No posts yet. Follow some people!</p>'; return; }
      data.content.forEach(post => feedEl.appendChild(Components.createPostCard(post)));
      hasMore = !data.last;
      page++;
    } catch (e) { showToast(e.message); if (page === 0) feedEl.innerHTML = ''; }
    finally { loading = false; }
  }

  function initInfiniteScroll() {
    const sentinel = document.getElementById('feed-sentinel');
    if (!sentinel) return;
    const observer = new IntersectionObserver(entries => {
      if (entries[0].isIntersecting) loadFeed();
    }, { rootMargin: '200px' });
    observer.observe(sentinel);
  }

  function init() {
    if (!Auth.isLoggedIn()) { window.location.href = '/frontend/index.html'; return; }
    loadStories();
    loadFeed();
    initInfiniteScroll();
    loadSuggestedUsers();
    initCreatePost();
  }

  async function loadSuggestedUsers() {
    const el = document.getElementById('suggested-users');
    if (!el) return;
    try {
      const data = await API.get('/posts/explore?page=0&size=5');
      const users = [];
      const seen = new Set();
      data.content.forEach(p => {
        if (!seen.has(p.user.id) && !p.user.isFollowing) {
          seen.add(p.user.id);
          users.push(p.user);
        }
      });
      el.innerHTML = '';
      users.slice(0, 5).forEach(u => el.appendChild(Components.createUserRow(u)));
    } catch (e) {}
  }

  function initCreatePost() {
    const btn = document.getElementById('create-post-btn');
    const modal = document.getElementById('create-post-modal');
    const closeBtn = document.getElementById('close-create-modal');
    if (!btn || !modal) return;
    btn.addEventListener('click', () => modal.classList.remove('hidden'));
    closeBtn.addEventListener('click', () => modal.classList.add('hidden'));
    modal.addEventListener('click', (e) => { if (e.target === modal) modal.classList.add('hidden'); });
    initUploadArea();
  }

  function initUploadArea() {
    const area = document.getElementById('upload-area');
    const fileInput = document.getElementById('post-file-input');
    const preview = document.getElementById('image-preview');
    const previewImg = document.getElementById('preview-img');
    const removeBtn = document.getElementById('remove-image');
    const shareBtn = document.getElementById('share-post-btn');
    const caption = document.getElementById('post-caption');
    const counter = document.getElementById('caption-counter');

    if (!area) return;

    area.addEventListener('click', () => fileInput.click());
    area.addEventListener('dragover', (e) => { e.preventDefault(); area.classList.add('drag-over'); });
    area.addEventListener('dragleave', () => area.classList.remove('drag-over'));
    area.addEventListener('drop', (e) => { e.preventDefault(); area.classList.remove('drag-over'); handleFile(e.dataTransfer.files[0]); });
    fileInput.addEventListener('change', () => handleFile(fileInput.files[0]));

    caption.addEventListener('input', () => { counter.textContent = `${caption.value.length}/2200`; });
    removeBtn.addEventListener('click', () => { preview.classList.add('hidden'); area.classList.remove('hidden'); fileInput.value = ''; previewImg.src = ''; });

    shareBtn.addEventListener('click', async () => {
      if (!fileInput.files[0]) { showToast('Please select an image'); return; }
      shareBtn.classList.add('btn-loading');
      shareBtn.textContent = 'Sharing...';
      try {
        const fd = new FormData();
        const reqBlob = new Blob([JSON.stringify({ caption: caption.value, location: document.getElementById('post-location')?.value })], { type: 'application/json' });
        fd.append('data', reqBlob);
        fd.append('image', fileInput.files[0]);
        await API.postForm('/posts', fd);
        document.getElementById('create-post-modal').classList.add('hidden');
        caption.value = ''; fileInput.value = ''; counter.textContent = '0/2200';
        preview.classList.add('hidden'); area.classList.remove('hidden');
        page = 0; hasMore = true;
        loadFeed();
        showToast('Post shared!');
      } catch (e) { showToast(e.message); }
      finally { shareBtn.classList.remove('btn-loading'); shareBtn.textContent = 'Share'; }
    });

    function handleFile(file) {
      if (!file) return;
      const reader = new FileReader();
      reader.onload = (e) => { previewImg.src = e.target.result; preview.classList.remove('hidden'); area.classList.add('hidden'); };
      reader.readAsDataURL(file);
    }
  }

  return { init };
})();
