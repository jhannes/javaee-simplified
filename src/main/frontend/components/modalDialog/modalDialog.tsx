import React, {
  Dispatch,
  MutableRefObject,
  ReactNode,
  SetStateAction,
  useEffect,
  useRef,
} from "react";

export function ModalDialog(props: {
  showState: [boolean, Dispatch<SetStateAction<boolean>>];
  children: ReactNode;
}) {
  const {
    showState: [show, setShow],
    children,
  } = props;
  const dialogRef = useRef() as MutableRefObject<HTMLDialogElement>;
  function handleClose() {
    setShow(false);
  }
  useEffect(() => {
    if (show) {
      dialogRef.current?.showModal();
    } else {
      dialogRef.current?.close();
    }
  }, [show]);
  useEffect(() => {
    dialogRef.current?.addEventListener("close", handleClose);
    return () => dialogRef.current?.removeEventListener("close", handleClose);
  }, []);
  return (
    <dialog ref={dialogRef}>
      <div>{children}</div>
    </dialog>
  );
}
