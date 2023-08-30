import React from "react";
import {
  servers,
  TodoDto,
} from "../../../../../target/generated-sources/openapi-typescript";
import usePromise from "react-promise-suspense";

function TodoListing({ todo }: { todo: TodoDto }) {
  return (
    <>
      <h3>{todo.title}</h3>
      <div>{todo.state}</div>
      <div>{todo.description}</div>
    </>
  );
}

export function TodoList() {
  const todos = usePromise(() => servers.current.todosApi.listTodos(), []);
  return (
    <>
      <h2>Current tasks</h2>
      {todos.map((todo) => (
        <TodoListing key={todo.id} todo={todo} />
      ))}
    </>
  );
}
