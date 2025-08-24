package com.automapper.core;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Profile reutilizável para configurações de mapeamento
 */
public abstract class MappingProfile {
    protected final Map<String, Object> configurations = new HashMap<>();
    protected final Map<String, Function<Object, Object>> converters = new HashMap<>();
    protected final Map<String, String> fieldMappings = new HashMap<>();
    
    /**
     * Método abstrato para configurar os mapeamentos
     */
    public abstract void configure();
    
    /**
     * Cria um mapeamento entre duas classes
     */
    protected <TSource, TTarget> MappingConfiguration<TSource, TTarget> createMap(
            Class<TSource> sourceType, 
            Class<TTarget> targetType) {
        
        AutoMapper<TSource, TTarget> mapper = AutoMapper.create(sourceType, targetType);
        return new MappingConfiguration<>(mapper);
    }
    
    /**
     * Adiciona um conversor de tipo global
     */
    protected <TSource, TTarget> void addConverter(
            Class<TSource> sourceType,
            Class<TTarget> targetType,
            Function<TSource, TTarget> converter) {
        
        String key = sourceType.getName() + "->" + targetType.getName();
        @SuppressWarnings("unchecked")
        Function<Object, Object> objectConverter = (Function<Object, Object>) converter;
        converters.put(key, objectConverter);
    }
    
    /**
     * Obtém as configurações do profile
     */
    public Map<String, Object> getConfigurations() {
        return configurations;
    }
    
    /**
     * Obtém os conversores do profile
     */
    public Map<String, Function<Object, Object>> getConverters() {
        return converters;
    }
    
    /**
     * Obtém os mapeamentos de campos
     */
    public Map<String, String> getFieldMappings() {
        return fieldMappings;
    }
}
