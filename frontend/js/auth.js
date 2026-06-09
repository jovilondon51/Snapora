const Auth = (() => {
  const TOKEN_KEY = 'sn_access_token';
  const REFRESH_KEY = 'sn_refresh_token';
  const USER_KEY = 'sn_user';

  function getToken() { return localStorage.getItem(TOKEN_KEY); }
  function getRefreshToken() { return localStorage.getItem(REFRESH_KEY); }
  function getUser() {
    const u = localStorage.getItem(USER_KEY);
    return u ? JSON.parse(u) : null;
  }
  function isLoggedIn() { return !!getToken(); }

  function save(data) {
    localStorage.setItem(TOKEN_KEY, data.accessToken);
    localStorage.setItem(REFRESH_KEY, data.refreshToken);
    localStorage.setItem(USER_KEY, JSON.stringify(data.user));
  }

  function logout() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(REFRESH_KEY);
    localStorage.removeItem(USER_KEY);
    window.location.href = '/frontend/index.html';
  }

  async function refreshToken() {
    const rt = getRefreshToken();
    if (!rt) { logout(); return null; }
    try {
      const res = await fetch(`${API_BASE}/auth/refresh`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken: rt })
      });
      if (!res.ok) { logout(); return null; }
      const data = await res.json();
      save(data);
      return data.accessToken;
    } catch { logout(); return null; }
  }

  async function register(username, email, password, fullName) {
    const res = await fetch(`${API_BASE}/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, email, password, fullName })
    });
    const data = await res.json();
    if (!res.ok) throw new Error(data.message || 'Registration failed');
    save(data);
    return data;
  }

  async function login(usernameOrEmail, password) {
    const res = await fetch(`${API_BASE}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ usernameOrEmail, password })
    });
    const data = await res.json();
    if (!res.ok) throw new Error(data.message || 'Login failed');
    save(data);
    return data;
  }

  return { getToken, getUser, isLoggedIn, save, logout, refreshToken, register, login };
})();
