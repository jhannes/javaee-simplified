import React from "react";
import * as ReactDOM from "react-dom/client";
import { TodoApplication } from "./components/todoApplication/todoApplication";

import "./application.css";

const root = ReactDOM.createRoot(document.getElementById("root")!);
root.render(<TodoApplication />);
