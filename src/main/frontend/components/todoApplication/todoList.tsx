import React, { useState } from "react";
import { servers, TodoDto, TodoSnapshotDto, TodoStateDto } from "api";
import { ModalDialog } from "../modalDialog/modalDialog";
import { todoListResource } from "./todoListResource";

const stateString: Record<TodoStateDto, string> = {
  WAITING: "not started",
  DOING: "in progress",
  DONE: "completed",
};

function UpdateDescription(props: { todo: TodoDto; onClose: () => void }) {
  const { todo, onClose } = props;
  const [description, setDescription] = useState(todo.description || "");
  async function handleSubmit() {
    await servers.current.todosApi.updateTodo({
      pathParams: { id: todo.id },
      todoPropertiesDto: { description },
    });
    onClose();
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

function TodoListing({
  todo,
  onRefresh,
}: {
  todo: TodoSnapshotDto;
  onRefresh(): void;
}) {
  const [updateDescription, setUpdateDescription] = useState(false);
  function handleClose() {
    setUpdateDescription(false);
    onRefresh();
  }
  return (
    <>
      <h3>
        {todo.title} ({stateString[todo.state]})
      </h3>
      <h4>Owner: {todo.updatedBy}</h4>
      <div>{todo.description}</div>
      <ModalDialog showState={[updateDescription, setUpdateDescription]}>
        <UpdateDescription onClose={handleClose} todo={todo} />
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
  const todos = todoListResource.watch();

  return (
    <>
      <h2>Current tasks</h2>
      {todos.map((todo) => (
        <TodoListing
          key={todo.id}
          todo={todo}
          onRefresh={() => todoListResource.refresh()}
        />
      ))}
    </>
  );
}
