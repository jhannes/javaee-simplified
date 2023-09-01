import React, { useState } from "react";
import { servers, TodoDto, TodoDtoStateEnum } from "api";
import { usePromise } from "@mittwald/react-use-promise";
import { ModalDialog } from "../modalDialog/modalDialog";

const stateString: Record<TodoDtoStateEnum, string> = {
  WAITING: "not started",
  DOING: "in progress",
  DONE: "completed",
};

function UpdateDescription(props: { todo: TodoDto; onClose: () => void }) {
  const { todo, onClose } = props;
  const [description, setDescription] = useState(todo.description || "");
  async function handleSubmit() {
    console.log({ id: todo.id, description });
    throw new Error("Oh noes!");
  }
  return (
    <form onSubmit={handleSubmit} method={"dialog"}>
      <h2>Update description</h2>
      <div>
        <textarea
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />
      </div>
      <div>
        <button onClick={onClose}>Save</button>
      </div>
      <div>
        <button onClick={onClose}>Cancel</button>
      </div>
    </form>
  );
}

function TodoListing({ todo }: { todo: TodoDto }) {
  const [updateDescription, setUpdateDescription] = useState(false);
  return (
    <>
      <h3>
        {todo.title} ({stateString[todo.state]})
      </h3>
      <div>{todo.description}</div>
      <ModalDialog show={updateDescription}>
        <UpdateDescription
          onClose={() => setUpdateDescription(false)}
          todo={todo}
        />
      </ModalDialog>
      <div>
        <button onClick={() => setUpdateDescription(true)}>
          Update description
        </button>
      </div>
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
