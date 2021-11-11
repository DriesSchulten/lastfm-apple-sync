import Scheduled from "./Scheduled";
import Status from "./Status";
import {Fragment} from "react";

const Sync = () => {
  return (
    <Fragment>
      <Scheduled/>
      <Status/>
    </Fragment>
  );
};

export default Sync;