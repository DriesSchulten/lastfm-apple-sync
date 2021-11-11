export interface SyncStatus {
  startedAt: string,
  numberOfAlbums: number,
  notFound: Array<SyncAlbum>,
  error?: string,
  running: boolean,
}

export interface SyncAlbum {
  name: string,
  artist: string,
}