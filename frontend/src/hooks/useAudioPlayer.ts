import { useRef, useState, useCallback, useEffect } from 'react';
import { getStreamUrl, updateProgress } from '../services/api';

interface UseAudioPlayerProps {
  audiobookId: string;
  initialPosition?: number;
}

export function useAudioPlayer({ audiobookId, initialPosition = 0 }: UseAudioPlayerProps) {
  const audioRef = useRef<HTMLAudioElement>(new Audio());
  const [isPlaying, setIsPlaying] = useState(false);
  const [position, setPosition] = useState(initialPosition);
  const [duration, setDuration] = useState(0);
  const [loading, setLoading] = useState(false);
  const progressSyncRef = useRef<NodeJS.Timeout>();

  const load = useCallback(async () => {
    setLoading(true);
    try {
      const url = await getStreamUrl(audiobookId);
      audioRef.current.src = url;
      audioRef.current.currentTime = initialPosition;
      await audioRef.current.load();
    } finally {
      setLoading(false);
    }
  }, [audiobookId, initialPosition]);

  const play = useCallback(async () => {
    await audioRef.current.play();
    setIsPlaying(true);

    // Sync progress every 10 seconds while playing
    progressSyncRef.current = setInterval(() => {
      const pos = Math.floor(audioRef.current.currentTime);
      setPosition(pos);
      updateProgress(audiobookId, pos, 1).catch(console.error);
    }, 10000);
  }, [audiobookId]);

  const pause = useCallback(() => {
    audioRef.current.pause();
    setIsPlaying(false);
    clearInterval(progressSyncRef.current);
    // Sync final position
    updateProgress(audiobookId, Math.floor(audioRef.current.currentTime), 1).catch(console.error);
  }, [audiobookId]);

  const seek = useCallback((seconds: number) => {
    audioRef.current.currentTime = seconds;
    setPosition(seconds);
  }, []);

  useEffect(() => {
    const audio = audioRef.current;
    const onDuration = () => setDuration(audio.duration);
    const onTimeUpdate = () => setPosition(Math.floor(audio.currentTime));
    audio.addEventListener('durationchange', onDuration);
    audio.addEventListener('timeupdate', onTimeUpdate);
    return () => {
      audio.removeEventListener('durationchange', onDuration);
      audio.removeEventListener('timeupdate', onTimeUpdate);
      clearInterval(progressSyncRef.current);
    };
  }, []);

  return { isPlaying, position, duration, loading, load, play, pause, seek };
}
