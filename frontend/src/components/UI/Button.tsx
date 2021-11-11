type ButtonProps = {
  title: string;
  onClick: () => void;
};

const Button = (props: ButtonProps) => {
  return (<button onClick={props.onClick} className="px-4 py-2 mt-2 bg-red-500 text-white hover:text-red-500 hover:bg-white rounded-2xl shadow-md">
    {props.title}
  </button>);
};

export default Button