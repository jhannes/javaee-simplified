import React from "react";
import ReactDOM from "react-dom/client";
import { Application } from "./modules/application/";

const reactDom = ReactDOM.createRoot(document.getElementById("root")!);
reactDom.render(<Application />);
