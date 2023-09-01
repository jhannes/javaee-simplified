import React, { Suspense } from "react";
import { BrowserRouter, Link, NavLink, Route, Routes } from "react-router-dom";
import { TodoList } from "./todoList";
import { NewTodoForm } from "./newTodoForm";
import { LoginLink } from "./loginLink";
import { ErrorBoundary } from "../errorBoundary/errorBoundary";

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
    <BrowserRouter>
      <header>
        <h1>Task Tracker</h1>
      </header>
      <nav>
        <NavLink to={"/"}>Front page</NavLink>
        <div className={"spacer"}></div>
        <LoginLink />
      </nav>
      <main>
        <ErrorBoundary
          onError={(error, reset) => (
            <ErrorFallback error={error} onClick={reset} />
          )}
        >
          <Suspense fallback={<LoadingFallback />}>
            <TodoRoutes />
          </Suspense>
        </ErrorBoundary>
      </main>
      <footer>💚 By Johannes Brodwall @ Sopra Steria</footer>
    </BrowserRouter>
  );
}

function LoadingFallback() {
  return <h2>Loading</h2>;
}

function ErrorFallback(props: { error: Error; onClick: () => void }) {
  const { error, onClick } = props;
  return (
    <>
      <h2>An error occurred</h2>
      <div>{error.toString()}</div>
      <div>
        <button onClick={onClick}>Retry</button>
      </div>
    </>
  );
}
