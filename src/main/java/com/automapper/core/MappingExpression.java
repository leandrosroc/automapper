package com.automapper.core;

import java.util.function.Function;

/**
 * Interface funcional para configuração de mapeamento usando expressões lambda
 */
@FunctionalInterface
public interface MappingExpression<TSource, TTarget> {
    void configure(MappingConfiguration<TSource, TTarget> config);
}
