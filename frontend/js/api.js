const API = (() => {
  async function request(endpoint, options = {}) {
    const token = Auth.getToken();
    const headers = { ...options.headers };
    if (token) headers['Authorization'] = `Bearer ${token}`;
    if (!(options.body instanceof FormData) && options.body && typeof options.body === 'object') {
      headers['Content-Type'] = 'application/json';
      options.body = JSON.stringify(options.body);
    }

    let res = await fetch(`${API_BASE}${endpoint}`, { ...options, headers });

    if (res.status === 401) {
      const newToken = await Auth.refreshToken();
      if (newToken) {
        headers['Authorization'] = `Bearer ${newToken}`;
        res = await fetch(`${API_BASE}${endpoint}`, { ...options, headers });
      }
    }

    if (!res.ok) {
      const err = await res.json().catch(() => ({ message: 'Request failed' }));
      throw new Error(err.message || 'Request failed');
    }

    if (res.status === 204) return null;
    return res.json();
  }

  const get = (url) => request(url);
  const post = (url, body) => request(url, { method: 'POST', body });
  const put = (url, body) => request(url, { method: 'PUT', body });
  const del = (url) => request(url, { method: 'DELETE' });
  const postForm = (url, formData) => request(url, { method: 'POST', body: formData });
  const putForm = (url, formData) => request(url, { method: 'PUT', body: formData });

  return { get, post, put, del, postForm, putForm, request };
})();
