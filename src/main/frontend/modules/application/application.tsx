import React, { useEffect } from "react";
import { DefaultApi } from "@api/api";
import { LoggedOutError } from "@api/base";

export function Application() {
  const defaultApi = new DefaultApi();
  useEffect(() => {
    defaultApi
      .apiLoginGet()
      .then((value) => {
        console.log("callback", value);
      })
      .catch((error) => {
        if (error instanceof LoggedOutError) {
          window.location.pathname = "/api/login/start";
        } else {
          alert("error trying to log in");
        }
      });
  }, []);

  return <h1>React TODO application</h1>;
}
