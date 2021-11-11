import React from "react";

type CardProps = {
  title: string;
  children?: React.ReactNode;
}

const Card = (props: CardProps) => {
  return (<div className="relative px-4 py-10 bg-white shadow-lg sm:rounded-3xl sm:p-20 space-y-6">
    <h1>{props.title}</h1>
    {props.children}
  </div>);
};

export default Card;
