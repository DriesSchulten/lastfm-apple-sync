import React, {useState} from 'react';
import Layout from "./components/UI/Layout";
import Backdrop from "./components/UI/Backdrop";
import useMusickit from "./hooks/use-musickit";
import Card from "./components/UI/Card";
import Sync from "./components/Sync/Sync";
import Setup from "./components/Setup/Setup";

const App = () => {
  const [signedIn, setSignedIn] = useState(false);
  const {musickit} = useMusickit()

  const isMusickitSetup = musickit?.isAuthorized || signedIn;

  const signedInHandler = () => {
    setSignedIn(true);
  };

  return (
    <Layout>
      <Backdrop/>
      {isMusickitSetup && <Card title="Last.fm to Apple Music sync">
        <Sync/>
      </Card>}
      {!isMusickitSetup && <Card title="Apple Music connection">
        <Setup musickit={musickit} signedIn={signedInHandler}/>
      </Card>}
    </Layout>
  )
};

export default App;
