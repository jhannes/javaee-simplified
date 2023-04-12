package com.soprasteria.simplejavaee.infrastructure;

public interface ThrowingClosable<EX extends Exception> extends AutoCloseable {
    @Override
    void close() throws EX;
}
