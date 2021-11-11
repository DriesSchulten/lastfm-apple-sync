import Button from "../UI/Button";

const Scheduled = () => {

  const syncClick = async () => {
    const response = await fetch('sync');
    console.log(`Sync trigger: ${response.ok}`);
  };

  return (<div id="sync-functions" className="space-py-4">
    <p>
      Sync is scheduled, manual sync can be triggered using the button.
    </p>
    <Button title="Sync now" onClick={syncClick}/>
  </div>);
};

export default Scheduled;