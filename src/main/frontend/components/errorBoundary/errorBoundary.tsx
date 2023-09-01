import React, { ReactNode } from "react";
import { refresh } from "@mittwald/react-use-promise";

type Props = {
  children: ReactNode;
  onError(error: Error, reset: () => void): ReactNode;
};

export class ErrorBoundary extends React.Component<
  Props,
  {
    error?: Error;
  }
> {
  constructor(props: Props) {
    super(props);
    this.state = { error: undefined };
  }

  static getDerivedStateFromError(error: Error) {
    // Update state so the next render will show the fallback UI.
    return { error };
  }

  componentDidCatch(error: Error, errorInfo: React.ErrorInfo) {
    this.setState({ error });
  }

  render() {
    if (this.state.error) {
      return this.props.onError(this.state.error, () => {
        this.setState({ error: undefined });
        refresh();
      });
    }
    return <div>{this.props.children}</div>;
  }
}
