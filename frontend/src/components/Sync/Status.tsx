import {useEffect, useState} from "react";
import NotFoundTable from "./NotFoundTable";
import {SyncStatus} from "../../models/SyncStatus";

const Status = () => {
  const [status, setStatus] = useState<SyncStatus | null>(null);

  const fetchStatus = async () => {
    const response = await fetch("sync-status");

    if (response.status === 404) {
      setStatus(null);
    } else {
      const json: SyncStatus = await response.json();
      setStatus(json);
    }
  };

  let lastSync = 'n/a';
  if (status != null) {
    lastSync = status.running ? `running now (${status.numberOfAlbums} albums)` : `${new Date(status.startedAt).toLocaleString()} (${status.numberOfAlbums} albums)`
  }

  useEffect(() => {
    const interval = setInterval(() => {
      fetchStatus().catch(console.error)
    }, 5000);

    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    fetchStatus().catch(console.error);
  }, []);

  const errorDisplay = status?.error && <p className="bg-red-500 text-white px-2 py-2 rounded-lg mt-2">{status.error}</p>;
  const notFoundList = status ? status.notFound : [];
  const notFoundDisplay = notFoundList.length > 0 && <NotFoundTable albums={notFoundList}/>;

  return (<div className="space-py-2">
    <p>Last sync: <span className="font-bold">{lastSync}</span></p>
    {errorDisplay}
    {notFoundDisplay}
  </div>)
};

export default Status;