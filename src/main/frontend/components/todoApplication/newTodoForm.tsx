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
  const navigate = useNavigate();
  async function handleSubmit(e: FormEvent<HTMLFormElement>) {
    e.preventDefault();
    await servers.current.todosApi.createTodo({
      todoDto,
    });
    navigate("/todos");
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
      <div>
        <button>Submit</button>
      </div>
    </form>
  );
}
