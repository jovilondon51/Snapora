const Explore = (() => {
  let searchDebounce = null;

  async function init() {
    if (!Auth.isLoggedIn()) { window.location.href = '/frontend/index.html'; return; }
    await loadExplore();
    initSearch();
  }

  async function loadExplore(query = '') {
    const grid = document.getElementById('explore-grid');
    const searchResults = document.getElementById('search-results');
    if (!grid) return;

    if (query) {
      grid.classList.add('hidden');
      searchResults.classList.remove('hidden');
      searchResults.innerHTML = '<p style="color:var(--text-secondary);padding:16px">Searching...</p>';
      try {
        const data = await API.get(`/users/search?q=${encodeURIComponent(query)}&size=20`);
        searchResults.innerHTML = '';
        if (!data.content.length) { searchResults.innerHTML = '<p style="color:var(--text-secondary);padding:16px">No users found</p>'; return; }
        data.content.forEach(u => searchResults.appendChild(Components.createUserRow(u)));
      } catch (e) { searchResults.innerHTML = ''; }
      return;
    }

    grid.classList.remove('hidden');
    searchResults.classList.add('hidden');
    grid.innerHTML = '';

    try {
      const data = await API.get('/posts/explore?page=0&size=30');
      data.content.forEach(post => {
        const item = document.createElement('div');
        item.className = 'explore-item';
        item.innerHTML = `
          <img src="${post.imageUrl}" alt="Post" loading="lazy">
          <div class="explore-item-overlay">
            <span>❤️ ${post.likesCount}</span>
            <span>💬 ${post.commentsCount}</span>
          </div>`;
        item.addEventListener('click', () => window.location.href = `/frontend/feed.html`);
        grid.appendChild(item);
      });
    } catch (e) { showToast(e.message); }
  }

  function initSearch() {
    const input = document.getElementById('search-input');
    if (!input) return;
    input.addEventListener('input', () => {
      clearTimeout(searchDebounce);
      searchDebounce = setTimeout(() => loadExplore(input.value.trim()), 300);
    });
  }

  return { init };
})();
