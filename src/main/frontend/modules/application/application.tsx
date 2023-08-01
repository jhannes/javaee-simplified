import React, { useEffect, useState } from "react";
import { DefaultApi } from "@api/api";
import { LoggedOutError } from "@api/base";

export function Application() {
  const defaultApi = new DefaultApi();
  const [username, setUsername] = useState<string>();
  useEffect(() => {
    defaultApi
      .apiLoginGet()
      .then((value) => {
        console.log("callback", value);
        setUsername(value.username);
      })
      .catch((error) => {
        if (error instanceof LoggedOutError) {
          window.location.pathname = "/api/login/start";
        } else {
          alert("error trying to log in");
        }
      });
  }, []);

  return (
    <div>
      <h1>React TODO application</h1>
      {username && <div>Welcome {username}</div>}
    </div>
  );
}
