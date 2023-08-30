import React, { Suspense } from "react";
import { HashRouter, Link, NavLink, Route, Routes } from "react-router-dom";
import { TodoList } from "./todoList";
import { NewTodoForm } from "./newTodoForm";

function FrontPage() {
  return (
    <>
      <h2>Welcome to the task application</h2>
      <ul>
        <li>
          <Link to={"/todos"}>List tasks</Link>
        </li>
        <li>
          <Link to={"/todos/new"}>Create new task</Link>
        </li>
      </ul>
    </>
  );
}

function TodoRoutes() {
  return (
    <Routes>
      <Route path={"/"} element={<FrontPage />} />
      <Route path={"/todos"} element={<TodoList />} />
      <Route path={"/todos/new"} element={<NewTodoForm />} />
      <Route path={"*"} element={<h2>Not found</h2>} />
    </Routes>
  );
}

export function TodoApplication() {
  return (
    <HashRouter>
      <header>
        <h1>Task Tracker</h1>
      </header>
      <nav>
        <NavLink to={"/"}>Front page</NavLink>
        <div className={"spacer"}></div>
        <NavLink to={"/login"}>User profile</NavLink>
      </nav>
      <main>
        <Suspense fallback={<h2>Loading</h2>}>
          <TodoRoutes />
        </Suspense>
      </main>
      <footer>💚 By Johannes Brodwall @ Sopra Steria</footer>
    </HashRouter>
  );
}
