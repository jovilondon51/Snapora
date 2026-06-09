const App = (() => {
  function initDarkMode() {
    const saved = localStorage.getItem('sn_theme') || 'light';
    document.documentElement.dataset.theme = saved;
    const btn = document.getElementById('dark-mode-toggle');
    if (btn) {
      btn.textContent = saved === 'dark' ? '☀️' : '🌙';
      btn.addEventListener('click', () => {
        const next = document.documentElement.dataset.theme === 'dark' ? 'light' : 'dark';
        document.documentElement.dataset.theme = next;
        localStorage.setItem('sn_theme', next);
        btn.textContent = next === 'dark' ? '☀️' : '🌙';
      });
    }
  }

  function initNav() {
    const page = window.location.pathname.split('/').pop();
    document.querySelectorAll('.nav-item, .bottom-nav-item').forEach(item => {
      const href = item.getAttribute('href') || '';
      if (href.includes(page)) item.classList.add('active');
    });

    // Logout buttons
    document.querySelectorAll('.logout-btn').forEach(btn => {
      btn.addEventListener('click', () => Auth.logout());
    });

    // Update avatar in nav
    const user = Auth.getUser();
    if (user) {
      document.querySelectorAll('.nav-user-avatar').forEach(el => {
        el.src = Components.avatarSrc(user.avatarUrl, user.username);
      });
      document.querySelectorAll('.nav-username').forEach(el => {
        el.textContent = user.username;
      });
    }
  }

  function initNotificationBadge() {
    const badge = document.querySelectorAll('.notif-badge');
    if (!badge.length || !Auth.isLoggedIn()) return;
    API.get('/notifications/unread-count').then(data => {
      if (data.count > 0) {
        badge.forEach(b => { b.textContent = data.count > 9 ? '9+' : data.count; b.classList.remove('hidden'); });
      }
    }).catch(() => {});
  }

  function initSSE() {
    if (!Auth.isLoggedIn()) return;
    const token = Auth.getToken();
    const es = new EventSource(`${API_BASE}/notifications/stream?token=${token}`);
    es.addEventListener('notification', (e) => {
      const notif = JSON.parse(e.data);
      showToast(`New notification from ${notif.actor?.username || 'someone'}`);
      initNotificationBadge();
    });
    es.onerror = () => es.close();
  }

  function init() {
    initDarkMode();
    if (Auth.isLoggedIn()) {
      initNav();
      initNotificationBadge();
    }
    // Route to page init
    const page = window.location.pathname.split('/').pop();
    if (page === 'feed.html' && typeof Feed !== 'undefined') Feed.init();
    if (page === 'explore.html' && typeof Explore !== 'undefined') Explore.init();
    if (page === 'profile.html' && typeof Profile !== 'undefined') Profile.init();
    if (page === 'notifications.html') initNotificationsPage();
    if (page === 'reels.html') initReelsPage();
  }

  async function initNotificationsPage() {
    if (!Auth.isLoggedIn()) { window.location.href = '/frontend/index.html'; return; }
    const list = document.getElementById('notifications-list');
    if (!list) return;
    try {
      const data = await API.get('/notifications?page=0&size=30');
      list.innerHTML = '';
      if (!data.content.length) { list.innerHTML = '<p style="color:var(--text-secondary);padding:24px;text-align:center">No notifications yet</p>'; return; }
      data.content.forEach(n => {
        const el = document.createElement('div');
        el.className = `notif-item ${n.isRead ? '' : 'unread'}`;
        const avatar = Components.avatarSrc(n.actor?.avatarUrl, n.actor?.username);
        const text = n.type === 'LIKE' ? 'liked your post' : n.type === 'COMMENT' ? 'commented on your post' : 'started following you';
        el.innerHTML = `
          <img class="avatar avatar-md" src="${avatar}" alt="">
          <div class="notif-text"><strong>${Components.escHtml(n.actor?.username || '')}</strong> ${text}</div>
          <span class="notif-time">${Components.timeAgo(n.createdAt)}</span>
        `;
        el.addEventListener('click', async () => {
          if (!n.isRead) { await API.put(`/notifications/${n.id}/read`).catch(() => {}); el.classList.remove('unread'); n.isRead = true; }
          if (n.referenceId && n.type !== 'FOLLOW') window.location.href = `/frontend/feed.html`;
        });
        list.appendChild(el);
      });
    } catch (e) { list.innerHTML = ''; }
  }

  async function initReelsPage() {
    if (!Auth.isLoggedIn()) { window.location.href = '/frontend/index.html'; return; }
    const container = document.getElementById('reels-container');
    if (!container) return;
    try {
      const data = await API.get('/posts/explore?page=0&size=10');
      data.content.forEach(post => {
        const reel = document.createElement('div');
        reel.className = 'reel-item';
        reel.innerHTML = `
          <img src="${post.imageUrl}" alt="Reel" style="width:100%;height:100%;object-fit:cover">
          <div class="reel-progress"><div class="reel-progress-fill" style="width:100%"></div></div>
          <div class="reel-overlay">
            <div class="reel-username">@${Components.escHtml(post.user.username)}</div>
            <div class="reel-caption">${Components.escHtml(post.caption || '')}</div>
          </div>
          <div class="reel-actions">
            <button class="reel-action-btn like-btn">
              <svg viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2" width="28" height="28"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/></svg>
              <span>${post.likesCount}</span>
            </button>
            <button class="reel-action-btn save-btn">
              <svg viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2" width="28" height="28"><path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/></svg>
              <span>Save</span>
            </button>
          </div>`;

        reel.querySelector('.like-btn').addEventListener('click', async () => {
          const res = await API.post(`/posts/${post.id}/like`).catch(() => null);
          if (res) { post.likesCount += res.liked ? 1 : -1; reel.querySelector('.like-btn span').textContent = post.likesCount; }
        });
        container.appendChild(reel);
      });
    } catch (e) { showToast(e.message); }
  }

  return { init };
})();

document.addEventListener('DOMContentLoaded', App.init);
