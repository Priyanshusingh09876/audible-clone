import React, { useEffect, useState } from 'react';
import { fetchAudiobooks, searchAudiobooks } from '../services/api';

interface Audiobook {
  id: string;
  title: string;
  author: string;
  narrator: string;
  genre: string;
  durationSeconds: number;
  coverArtKey: string;
}

export default function Home() {
  const [audiobooks, setAudiobooks] = useState<Audiobook[]>([]);
  const [query, setQuery] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchAudiobooks()
      .then(data => setAudiobooks(data.content || []))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!query.trim()) return;
    setLoading(true);
    const results = await searchAudiobooks(query);
    setAudiobooks(results);
    setLoading(false);
  };

  const formatDuration = (seconds: number) => {
    const h = Math.floor(seconds / 3600);
    const m = Math.floor((seconds % 3600) / 60);
    return `${h}h ${m}m`;
  };

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold mb-6">Audible Clone</h1>

      <form onSubmit={handleSearch} className="mb-8 flex gap-2">
        <input
          type="text"
          value={query}
          onChange={e => setQuery(e.target.value)}
          placeholder="Search audiobooks..."
          className="flex-1 border rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
        <button
          type="submit"
          className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition"
        >
          Search
        </button>
      </form>

      {loading ? (
        <div className="text-center py-12 text-gray-500">Loading...</div>
      ) : (
        <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-4">
          {audiobooks.map(book => (
            <div key={book.id} className="bg-white rounded-lg shadow hover:shadow-md transition cursor-pointer">
              <div className="bg-gray-200 h-40 rounded-t-lg flex items-center justify-center">
                <span className="text-gray-400 text-4xl">🎧</span>
              </div>
              <div className="p-3">
                <h3 className="font-semibold text-sm truncate">{book.title}</h3>
                <p className="text-gray-500 text-xs truncate">{book.author}</p>
                <p className="text-gray-400 text-xs mt-1">{formatDuration(book.durationSeconds)}</p>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
