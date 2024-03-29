import { servers, TodoStateDto, TodoStateDtoValues } from "api";
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { todoListResource } from "./todoListResource";

export function UpdateTaskStatus() {
  const [selectedTodos, setSelectedTodos] = useState<Record<string, boolean>>(
    {},
  );
  const [state, setState] = useState<TodoStateDto>("WAITING");
  const tasks = todoListResource.watch();
  const updateTodoStateRequestDto = {
    state,
    idList: Object.keys(selectedTodos).filter((k) => selectedTodos[k]),
  };

  const navigate = useNavigate();

  async function handleSubmit() {
    await servers.current.todosApi.updateTodoState({
      updateTodoStateRequestDto,
    });
    navigate("/todos");
  }

  return (
    <form onSubmit={handleSubmit} method={"dialog"}>
      <h2>Update task status</h2>
      {tasks.map((t) => (
        <div key={t.id}>
          <label>
            <input
              type="checkbox"
              checked={selectedTodos[t.id] || false}
              onChange={(e) =>
                setSelectedTodos((old) => ({
                  ...old,
                  [t.id]: e.target.checked,
                }))
              }
            />{" "}
            {t.title} (owner {t.updatedBy})
          </label>
        </div>
      ))}
      <select
        value={state}
        onChange={(e) => setState(e.target.value as TodoStateDto)}
      >
        {TodoStateDtoValues.map((s) => (
          <option>{s}</option>
        ))}
      </select>
      <div>
        <button>Submit</button>
      </div>
    </form>
  );
}
