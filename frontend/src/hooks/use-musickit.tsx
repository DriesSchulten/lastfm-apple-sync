import {DeveloperToken} from "../models/DeveloperToken";
import {useEffect, useState} from "react";
import MusicKitInstance = MusicKit.MusicKitInstance;

const useMusickit = (): { musickit: MusicKit.MusicKitInstance | null } => {
  const [musickit, setMusickit] = useState<MusicKitInstance | null>(null);

  const fetchDeveloperToken = async () => {
    const response = await fetch('/apple-music/developer-token');
    const json: DeveloperToken = await response.json();

    const instance = MusicKit.configure({
      developerToken: json.token,
      app: {
        build: '0.0.1',
        name: 'Last.fm to Apple Music sync"'
      }
    })

    setMusickit(instance)
  };

  useEffect(() => {
    fetchDeveloperToken().catch(console.error);
  }, []);

  return {
    musickit
  }
};

export default useMusickit;