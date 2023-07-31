import React, { useEffect } from "react";
import { DefaultApi } from "@api/api";

export function Application() {
  const defaultApi = new DefaultApi();
  useEffect(() => {
    defaultApi
      .apiLoginGet()
      .then((value) => {
        console.log("callback", value);
      })
      .catch((error) => {
        alert("error trying to log in");
        console.log("catch", error);
      });
  }, []);

  return <h1>React TODO application</h1>;
}
