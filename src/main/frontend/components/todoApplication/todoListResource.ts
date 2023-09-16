import { servers } from "api";
import { getAsyncResource } from "@mittwald/react-use-promise";

export const todoListResource = getAsyncResource(() => servers.current.todosApi.listTodos(), []);
