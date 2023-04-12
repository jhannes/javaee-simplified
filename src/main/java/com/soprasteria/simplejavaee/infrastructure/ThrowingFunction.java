package com.soprasteria.simplejavaee.infrastructure;

@FunctionalInterface
public interface ThrowingFunction<T, R, EX extends Exception> {
    R apply(T t) throws EX;
}
