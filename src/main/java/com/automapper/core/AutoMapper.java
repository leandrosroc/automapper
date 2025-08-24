package com.automapper.core;

import com.automapper.annotations.*;
import com.automapper.validation.TypeValidator;
import com.automapper.validation.ValidationResult;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AutoMapper<TSource, TTarget> {
    private final Class<TSource> sourceClass;
    private final Class<TTarget> targetClass;
    private final Map<String, String> customMappings = new HashMap<>();
    private final Map<String, TypeConverter<Object, Object>> typeConverters = new HashMap<>();
    private final Set<String> ignoredFields = new HashSet<>();
    private final Map<String, Function<Object, Object>> lambdaConverters = new HashMap<>();
    
    private static final Map<String, AutoMapper<?, ?>> mapperCache = new HashMap<>();
    private static final DateTimeFormatter DEFAULT_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @SuppressWarnings("unchecked")
    public static <S, T> AutoMapper<S, T> create(Class<S> sourceClass, Class<T> targetClass) {
        String key = sourceClass.getName() + "->" + targetClass.getName();
        return (AutoMapper<S, T>) mapperCache.computeIfAbsent(key, 
            k -> new AutoMapper<>(sourceClass, targetClass));
    }

    /**
     * Cria um mapper bidirecional
     */
    public static <S, T> BidirectionalMapper<S, T> createBidirectional(Class<S> firstClass, Class<T> secondClass) {
        return new BidirectionalMapper<>(firstClass, secondClass);
    }

    /**
     * Valida a compatibilidade entre os tipos
     */
    public static <S, T> ValidationResult validate(Class<S> sourceClass, Class<T> targetClass) {
        return TypeValidator.validate(sourceClass, targetClass);
    }

    private AutoMapper(Class<TSource> sourceClass, Class<TTarget> targetClass) {
        this.sourceClass = sourceClass;
        this.targetClass = targetClass;
        processAnnotations();
    }

    /**
     * Processa as anotações das classes para configuração automática
     */
    private void processAnnotations() {
        // Processa anotações da classe fonte
        Field[] sourceFields = getAllFields(sourceClass);
        
        for (Field field : sourceFields) {
            // Processa anotação @Ignore
            if (field.isAnnotationPresent(Ignore.class)) {
                ignoredFields.add(field.getName());
            }
            
            // Processa anotação @MapTo
            if (field.isAnnotationPresent(MapTo.class)) {
                MapTo mapTo = field.getAnnotation(MapTo.class);
                customMappings.put(field.getName(), mapTo.value());
            }
            
            // Processa anotação @UseConverter
            if (field.isAnnotationPresent(UseConverter.class)) {
                UseConverter useConverter = field.getAnnotation(UseConverter.class);
                try {
                    Object converterInstance = useConverter.value().getDeclaredConstructor().newInstance();
                    if (converterInstance instanceof TypeConverter) {
                        @SuppressWarnings("unchecked")
                        TypeConverter<Object, Object> converter = (TypeConverter<Object, Object>) converterInstance;
                        typeConverters.put(field.getName(), converter);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Error creating converter for field " + field.getName(), e);
                }
            }
        }
        
        // Processa anotações da classe alvo para criar mapeamentos reversos
        Field[] targetFields = getAllFields(targetClass);
        
        for (Field field : targetFields) {
            // Processa anotação @MapTo no alvo para criar mapeamento reverso
            if (field.isAnnotationPresent(MapTo.class)) {
                MapTo mapTo = field.getAnnotation(MapTo.class);
                // Cria mapeamento reverso: campo_fonte -> campo_alvo
                customMappings.put(mapTo.value(), field.getName());
            }
        }
    }

    public AutoMapper<TSource, TTarget> configureMapping(Map<String, String> customMappings) {
        this.customMappings.putAll(customMappings);
        return this;
    }

    /**
     * Configura mapeamento usando expressão lambda
     */
    public AutoMapper<TSource, TTarget> configure(MappingExpression<TSource, TTarget> expression) {
        MappingConfiguration<TSource, TTarget> config = new MappingConfiguration<>(this);
        expression.configure(config);
        return this;
    }

    /**
     * Ignora um campo específico
     */
    public AutoMapper<TSource, TTarget> ignoreField(String fieldName) {
        ignoredFields.add(fieldName);
        return this;
    }

    /**
     * Adiciona um conversor lambda
     */
    public AutoMapper<TSource, TTarget> addLambdaConverter(String fieldName, Function<Object, Object> converter) {
        lambdaConverters.put(fieldName, converter);
        return this;
    }

    public AutoMapper<TSource, TTarget> addTypeConverter(String propertyName, 
                                                         TypeConverter<Object, Object> converter) {
        this.typeConverters.put(propertyName, converter);
        return this;
    }

    public TTarget map(TSource source) {
        if (source == null) {
            return null;
        }

        try {
            TTarget target = targetClass.getDeclaredConstructor().newInstance();
            
            Field[] sourceFields = getAllFields(sourceClass);
            Field[] targetFields = getAllFields(targetClass);
            Map<String, Field> targetFieldMap = Arrays.stream(targetFields)
                .collect(Collectors.toMap(Field::getName, field -> field));

            for (Field sourceField : sourceFields) {
                sourceField.setAccessible(true);
                
                // Verifica se o campo deve ser ignorado
                if (ignoredFields.contains(sourceField.getName())) {
                    continue;
                }
                
                Object sourceValue = sourceField.get(source);
                
                if (sourceValue == null) {
                    continue;
                }

                String targetFieldName = customMappings.getOrDefault(sourceField.getName(), sourceField.getName());
                Field targetField = targetFieldMap.get(targetFieldName);
                
                if (targetField != null) {
                    targetField.setAccessible(true);
                    Object mappedValue = mapValue(sourceField, targetField, sourceValue);
                    targetField.set(target, mappedValue);
                }
            }
            
            return target;
        } catch (Exception e) {
            throw new RuntimeException("Error mapping from " + sourceClass.getName() + 
                                     " to " + targetClass.getName(), e);
        }
    }

    private Object mapValue(Field sourceField, Field targetField, Object sourceValue) {
        // 1. Verifica conversor lambda primeiro (tem prioridade mais alta)
        if (lambdaConverters.containsKey(sourceField.getName())) {
            return lambdaConverters.get(sourceField.getName()).apply(sourceValue);
        }
        
        // 2. Verifica conversor de tipo customizado (anotações)
        if (typeConverters.containsKey(sourceField.getName())) {
            return typeConverters.get(sourceField.getName()).apply(sourceValue);
        }

        Class<?> sourceType = sourceField.getType();
        Class<?> targetType = targetField.getType();

        // 3. Tipos idênticos
        if (sourceType.equals(targetType)) {
            return sourceValue;
        }

        // 4. Conversões automáticas de tipos primitivos
        if (isConvertiblePrimitive(sourceType, targetType)) {
            return convertPrimitive(sourceValue, targetType);
        }

        // 5. Conversão de data para string
        if (sourceType.equals(LocalDate.class) && targetType.equals(String.class)) {
            return ((LocalDate) sourceValue).format(DEFAULT_DATE_FORMAT);
        }
        
        // 6. Conversão de string para data (formato dd/MM/yyyy)
        if (sourceType.equals(String.class) && targetType.equals(LocalDate.class)) {
            try {
                return LocalDate.parse((String) sourceValue, DEFAULT_DATE_FORMAT);
            } catch (Exception e) {
                // Se falhar, tenta outros formatos comuns
                try {
                    return LocalDate.parse((String) sourceValue);
                } catch (Exception ex) {
                    throw new RuntimeException("Cannot convert string '" + sourceValue + "' to LocalDate", ex);
                }
            }
        }

        // 7. Coleções
        if (isCollectionType(sourceType) && isCollectionType(targetType)) {
            return mapCollection(sourceValue, targetType);
        }

        // 8. Objetos complexos (mapeamento recursivo)
        if (!isSimpleType(sourceType) && !isSimpleType(targetType)) {
            return mapComplexObject(sourceValue, sourceType, targetType);
        }

        // 9. Fallback - tenta conversão direta
        try {
            if (targetType.isAssignableFrom(sourceType)) {
                return sourceValue;
            }
        } catch (Exception ignored) {}

        return sourceValue;
    }

    private Object mapComplexObject(Object sourceValue, Class<?> sourceType, Class<?> targetType) {
        try {
            Object target = targetType.getDeclaredConstructor().newInstance();
            
            Field[] sourceFields = getAllFields(sourceType);
            Field[] targetFields = getAllFields(targetType);
            Map<String, Field> targetFieldMap = Arrays.stream(targetFields)
                .collect(Collectors.toMap(Field::getName, field -> field));

            for (Field sourceField : sourceFields) {
                sourceField.setAccessible(true);
                Object fieldValue = sourceField.get(sourceValue);
                
                if (fieldValue == null) {
                    continue;
                }

                Field targetField = targetFieldMap.get(sourceField.getName());
                if (targetField != null) {
                    targetField.setAccessible(true);
                    Object mappedValue = mapValue(sourceField, targetField, fieldValue);
                    targetField.set(target, mappedValue);
                }
            }
            
            return target;
        } catch (Exception e) {
            throw new RuntimeException("Error mapping complex object", e);
        }
    }

    private Object mapCollection(Object sourceCollection, Class<?> targetType) {
        if (!(sourceCollection instanceof Collection)) {
            return sourceCollection;
        }

        Collection<?> source = (Collection<?>) sourceCollection;
        
        if (source.isEmpty()) {
            return createEmptyCollection(targetType);
        }

        Collection<Object> result = createEmptyCollection(targetType);
        
        // Para coleções simples (String, etc), apenas copia
        for (Object item : source) {
            result.add(item);
        }
        
        return result;
    }

    private Collection<Object> createEmptyCollection(Class<?> collectionType) {
        if (List.class.isAssignableFrom(collectionType)) {
            return new ArrayList<>();
        } else if (Set.class.isAssignableFrom(collectionType)) {
            return new HashSet<>();
        } else {
            return new ArrayList<>(); // fallback
        }
    }

    private boolean isSimpleType(Class<?> type) {
        return type.isPrimitive() ||
               type.equals(String.class) ||
               type.equals(Integer.class) ||
               type.equals(Long.class) ||
               type.equals(Double.class) ||
               type.equals(Float.class) ||
               type.equals(Boolean.class) ||
               type.equals(LocalDate.class) ||
               Number.class.isAssignableFrom(type);
    }

    private boolean isCollectionType(Class<?> type) {
        return Collection.class.isAssignableFrom(type);
    }

    private boolean isConvertiblePrimitive(Class<?> sourceType, Class<?> targetType) {
        return (Number.class.isAssignableFrom(sourceType) || sourceType.isPrimitive()) &&
               (Number.class.isAssignableFrom(targetType) || targetType.isPrimitive());
    }

    private Object convertPrimitive(Object value, Class<?> targetType) {
        if (value instanceof Number) {
            Number num = (Number) value;
            if (targetType.equals(int.class) || targetType.equals(Integer.class)) {
                return num.intValue();
            } else if (targetType.equals(long.class) || targetType.equals(Long.class)) {
                return num.longValue();
            } else if (targetType.equals(double.class) || targetType.equals(Double.class)) {
                return num.doubleValue();
            } else if (targetType.equals(float.class) || targetType.equals(Float.class)) {
                return num.floatValue();
            }
        }
        return value;
    }

    private Field[] getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields.toArray(new Field[0]);
    }
}
