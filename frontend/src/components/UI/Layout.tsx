import React from "react";

type LayoutProps = {
  children?: React.ReactNode;
}

const Layout = (props: LayoutProps) => {
  return (<div className="min-h-screen bg-gray-100 py-6 flex flex-col justify-center sm:py-12">
    <div className="relative py-3 sm:max-w-xl sm:mx-auto">
      {props.children}
    </div>
  </div>);
};

export default Layout;