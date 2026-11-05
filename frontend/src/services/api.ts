const BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

function getHeaders(): HeadersInit {
  const token = localStorage.getItem('token');
  return {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };
}

export async function fetchAudiobooks(genre?: string, page = 0) {
  const params = new URLSearchParams({ page: String(page), size: '20' });
  if (genre) params.set('genre', genre);
  const res = await fetch(`${BASE_URL}/api/v1/content/audiobooks?${params}`, {
    headers: getHeaders(),
  });
  if (!res.ok) throw new Error('Failed to fetch audiobooks');
  return res.json();
}

export async function fetchAudiobook(id: string) {
  const res = await fetch(`${BASE_URL}/api/v1/content/audiobooks/${id}`, {
    headers: getHeaders(),
  });
  if (!res.ok) throw new Error('Audiobook not found');
  return res.json();
}

export async function getStreamUrl(audiobookId: string): Promise<string> {
  const res = await fetch(`${BASE_URL}/api/v1/content/audiobooks/${audiobookId}/stream`, {
    headers: getHeaders(),
  });
  if (!res.ok) throw new Error('Failed to get stream URL');
  const data = await res.json();
  return data.streamUrl;
}

export async function searchAudiobooks(query: string) {
  const res = await fetch(`${BASE_URL}/api/v1/content/search?q=${encodeURIComponent(query)}`, {
    headers: getHeaders(),
  });
  return res.json();
}

export async function getUserLibrary() {
  const res = await fetch(`${BASE_URL}/api/v1/library`, { headers: getHeaders() });
  return res.json();
}

export async function addToLibrary(audiobookId: string) {
  await fetch(`${BASE_URL}/api/v1/library/${audiobookId}`, {
    method: 'POST',
    headers: getHeaders(),
  });
}

export async function updateProgress(audiobookId: string, positionSeconds: number, chapterNumber: number) {
  await fetch(`${BASE_URL}/api/v1/library/progress`, {
    method: 'PUT',
    headers: getHeaders(),
    body: JSON.stringify({ audiobookId, positionSeconds, chapterNumber }),
  });
}

export async function getProgress(audiobookId: string) {
  const res = await fetch(`${BASE_URL}/api/v1/library/progress/${audiobookId}`, {
    headers: getHeaders(),
  });
  if (res.status === 404) return null;
  return res.json();
}

export async function login(email: string, password: string) {
  const res = await fetch(`${BASE_URL}/api/v1/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password }),
  });
  if (!res.ok) throw new Error('Login failed');
  const data = await res.json();
  localStorage.setItem('token', data.token);
  return data;
}
