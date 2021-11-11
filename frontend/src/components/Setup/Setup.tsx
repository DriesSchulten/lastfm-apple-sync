import {Fragment} from "react";
import Button from "../UI/Button";

type SetupProps = {
  musickit: MusicKit.MusicKitInstance | null,
  signedIn: () => void
}

const Setup = (props: SetupProps) => {
  const signInHandler = async () => {
    if (props.musickit !== null) {
      await props.musickit.authorize();

      const data = {
        'token': props.musickit.musicUserToken,
        'storefrontId': props.musickit.storefrontId
      };

      const options = {
        method: 'POST',
        headers: new Headers({'content-type': 'application/json'}),
        body: JSON.stringify(data)
      }

      const response = await fetch('apple-music/user-token', options);
      console.log(`Send token: ${response.ok}`);

      props.signedIn();
    }
  };

  return (
    <Fragment>
      <p id="sign-in-help" className="py-6">
        Please Sign In to Apple Music to allow synchronizing your music.
      </p>
      <div>
        <Button title="Sign in to Apple Music" onClick={signInHandler}/>
      </div>
    </Fragment>
  );
};

export default Setup;