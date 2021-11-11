import {SyncAlbum} from "../../models/SyncStatus";

type NotFoundTableProps = {
  albums: Array<SyncAlbum>
}

const NotFoundTable = (props: NotFoundTableProps) => {
  const cellClasses = 'border border-yellow-600 px-4 py-2';

  const rows = props.albums.map((album) => {
    return (
      <tr key={`${album.artist}-${album.name}`}>
        <td className={cellClasses}>{album.artist}</td>
        <td className={cellClasses}>{album.name}</td>
      </tr>
    );
  });

  return (
    <div id="sync-not-found" className="bg-yellow-500 text-white px-2 py-2 rounded-lg mt-2">
      The following albums where not found on Apple Music:
      <table className="table-auto w-full text-white mt-2">
        <thead>
        <tr>
          <th className={`${cellClasses} text-left`}>Artist</th>
          <th className={`${cellClasses} text-left`}>Album</th>
        </tr>
        </thead>
        <tbody>
        {rows}
        </tbody>
      </table>
    </div>
  );
};

export default NotFoundTable;