package com.automapper.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para especificar um conversor de tipo customizado
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UseConverter {
    /**
     * Classe do conversor a ser utilizado
     */
    Class<?> value();
}
