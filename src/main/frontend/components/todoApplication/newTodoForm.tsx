import React, { FormEvent, useState } from "react";
import { servers, TodoDto } from "api";
import { v4 as uuidv4 } from "uuid";
import { useNavigate } from "react-router-dom";

export function NewTodoForm() {
  const [todoDto, setTodoDto] = useState<TodoDto>({
    title: "",
    state: "WAITING",
    id: uuidv4(),
  });
  const [error, setError] = useState<Error>();
  const navigate = useNavigate();

  async function handleSubmit(e: FormEvent<HTMLFormElement>) {
    e.preventDefault();
    try {
      await servers.current.todosApi.createTodo({
        todoDto,
      });
      navigate("/todos");
    } catch (e) {
      setError(e as Error);
    }
  }

  return (
    <form onSubmit={handleSubmit}>
      <h2>Add new task</h2>

      <div>
        <label>
          Title:
          <br />
          <input
            autoFocus={true}
            type="text"
            value={todoDto.title}
            onChange={(e) =>
              setTodoDto((old) => ({ ...old, title: e.target.value }))
            }
          />
        </label>
      </div>
      {error && (
        <div>
          <div className={"errorMessage"}>Error: {error.message}</div>
        </div>
      )}
      <div>
        <button>Submit</button>
      </div>
    </form>
  );
}
