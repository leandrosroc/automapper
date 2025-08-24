package com.automapper.core;

import java.util.function.Function;

@FunctionalInterface
public interface TypeConverter<T, R> extends Function<T, R> {
}
