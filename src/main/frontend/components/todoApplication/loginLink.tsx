import React, { useEffect, useState } from "react";
import {
  servers,
  UserProfileDto,
} from "../../../../../target/generated-sources/openapi-typescript";
import { LoggedOutError } from "../../../../../target/generated-sources/openapi-typescript/base";

export function LoginLink() {
  const [state, setState] = useState<
    "pending" | "authorized" | "unauthorized" | "error"
  >("pending");
  const [profile, setProfile] = useState<UserProfileDto>();
  const [error, setError] = useState<Error>();
  async function fetchUserProfile() {
    setState("pending");
    try {
      setProfile(await servers.current.loginApi.getUserProfile());
      setState("authorized");
    } catch (e) {
      if (e instanceof LoggedOutError) {
        setState("unauthorized");
      } else {
        setState("error");
        setError(e as Error);
      }
    }
  }
  useEffect(() => {
    fetchUserProfile();
  }, []);

  if (state === "pending") {
    return <div>Loading...</div>;
  } else if (state === "unauthorized") {
    return <a href={"/api/login/start"}>Login</a>;
  } else if (state === "authorized") {
    return (
      <div>
        <span>{profile?.username}</span>
        <a href={"/api/login/endsession"}>Log out</a>
      </div>
    );
  } else {
    return <div>{error?.toString()}</div>;
  }
}
