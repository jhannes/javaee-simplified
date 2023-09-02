import React, { MutableRefObject, ReactNode, useEffect, useRef } from "react";

export function ModalDialog(props: { show: boolean; children: ReactNode }) {
  const dialogRef = useRef() as MutableRefObject<HTMLDialogElement>;
  useEffect(() => {
    if (props.show) {
      dialogRef.current?.showModal();
    } else {
      dialogRef.current?.close();
    }
    return () => dialogRef.current?.close();
  }, [props.show]);
  return (
    <dialog ref={dialogRef}>
      <div>{props.children}</div>
    </dialog>
  );
}
