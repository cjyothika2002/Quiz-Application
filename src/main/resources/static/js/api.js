// Thin wrapper around fetch() that attaches the JWT (Section 32) and
// normalizes error handling (Section 35's ErrorResponse shape) so every
// page can call `api(...)` the same way instead of repeating boilerplate.

const AUTH_KEY = 'quizapp_auth'; // { token, userId, name, email, role }

function getAuth() {
  const raw = localStorage.getItem(AUTH_KEY);
  return raw ? JSON.parse(raw) : null;
}

function setAuth(authResponse) {
  localStorage.setItem(AUTH_KEY, JSON.stringify(authResponse));
}

function clearAuth() {
  localStorage.removeItem(AUTH_KEY);
}

function requireAuth(requiredRole) {
  const auth = getAuth();
  if (!auth) {
    window.location.href = '/index.html';
    return null;
  }
  if (requiredRole && auth.role !== requiredRole) {
    // A USER hitting an admin page (or vice versa) — send them home rather
    // than showing a broken screen. The backend still enforces this for
    // real via Spring Security regardless of what this redirect does.
    window.location.href = auth.role === 'ADMIN' ? '/admin/dashboard.html' : '/dashboard.html';
    return null;
  }
  return auth;
}

async function api(path, options = {}) {
  const auth = getAuth();
  const headers = Object.assign({ 'Content-Type': 'application/json' }, options.headers || {});
  if (auth) {
    headers['Authorization'] = 'Bearer ' + auth.token;
  }

  const response = await fetch(path, Object.assign({}, options, { headers }));

  if (response.status === 401) {
    clearAuth();
    window.location.href = '/index.html';
    throw new Error('Session expired');
  }

  if (response.status === 204) {
    return null;
  }

  const body = await response.json().catch(() => null);

  if (!response.ok) {
    const message = (body && body.message) ? body.message : 'Something went wrong';
    throw new Error(message);
  }

  return body;
}

function showError(bannerEl, message) {
  bannerEl.textContent = message;
  bannerEl.classList.add('show');
}

function hideError(bannerEl) {
  bannerEl.classList.remove('show');
}

function logout() {
  clearAuth();
  window.location.href = '/index.html';
}
