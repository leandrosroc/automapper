package com.automapper.validation;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Validador de tipos para mapeamento
 */
public class TypeValidator {
    
    /**
     * Valida se o mapeamento entre dois tipos é possível
     */
    public static <TSource, TTarget> ValidationResult validate(Class<TSource> sourceClass, Class<TTarget> targetClass) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        Field[] sourceFields = sourceClass.getDeclaredFields();
        Field[] targetFields = targetClass.getDeclaredFields();
        
        // Verifica se o tipo de destino tem construtor padrão
        try {
            targetClass.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            errors.add("Target class " + targetClass.getName() + " must have a default constructor");
        }
        
        // Valida compatibilidade dos campos
        for (Field sourceField : sourceFields) {
            Field targetField = findTargetField(sourceField.getName(), targetFields);
            
            if (targetField != null) {
                ValidationResult fieldValidation = validateFieldCompatibility(sourceField, targetField);
                errors.addAll(fieldValidation.getErrors());
                warnings.addAll(fieldValidation.getWarnings());
            } else {
                warnings.add("Source field '" + sourceField.getName() + "' has no corresponding target field");
            }
        }
        
        return new ValidationResult(errors, warnings);
    }
    
    private static Field findTargetField(String fieldName, Field[] targetFields) {
        for (Field field : targetFields) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        return null;
    }
    
    private static ValidationResult validateFieldCompatibility(Field sourceField, Field targetField) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        Class<?> sourceType = sourceField.getType();
        Class<?> targetType = targetField.getType();
        
        // Tipos idênticos - sempre compatíveis
        if (sourceType.equals(targetType)) {
            return new ValidationResult(errors, warnings);
        }
        
        // Verifica conversões automáticas
        if (isAutoConvertible(sourceType, targetType)) {
            return new ValidationResult(errors, warnings);
        }
        
        // Verifica se são tipos de coleção compatíveis
        if (isCollectionType(sourceType) && isCollectionType(targetType)) {
            ValidationResult collectionValidation = validateCollectionTypes(sourceField, targetField);
            errors.addAll(collectionValidation.getErrors());
            warnings.addAll(collectionValidation.getWarnings());
            return new ValidationResult(errors, warnings);
        }
        
        // Verifica se são objetos complexos
        if (!isSimpleType(sourceType) && !isSimpleType(targetType)) {
            // Objetos complexos podem ser mapeados recursivamente
            warnings.add("Complex object mapping from " + sourceType.getName() + " to " + targetType.getName() + 
                        " will be attempted recursively");
            return new ValidationResult(errors, warnings);
        }
        
        // Tipos incompatíveis
        warnings.add("Potential incompatible types: " + sourceType.getName() + " -> " + targetType.getName());
        
        return new ValidationResult(errors, warnings);
    }
    
    private static ValidationResult validateCollectionTypes(Field sourceField, Field targetField) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        Type sourceGenericType = sourceField.getGenericType();
        Type targetGenericType = targetField.getGenericType();
        
        if (sourceGenericType instanceof ParameterizedType && targetGenericType instanceof ParameterizedType) {
            ParameterizedType sourceParamType = (ParameterizedType) sourceGenericType;
            ParameterizedType targetParamType = (ParameterizedType) targetGenericType;
            
            Type[] sourceTypeArgs = sourceParamType.getActualTypeArguments();
            Type[] targetTypeArgs = targetParamType.getActualTypeArguments();
            
            if (sourceTypeArgs.length > 0 && targetTypeArgs.length > 0) {
                Class<?> sourceElementType = (Class<?>) sourceTypeArgs[0];
                Class<?> targetElementType = (Class<?>) targetTypeArgs[0];
                
                if (!sourceElementType.equals(targetElementType)) {
                    warnings.add("Collection element types differ: " + 
                                sourceElementType.getName() + " -> " + targetElementType.getName());
                }
            }
        }
        
        return new ValidationResult(errors, warnings);
    }
    
    private static boolean isAutoConvertible(Class<?> sourceType, Class<?> targetType) {
        // Conversões numéricas
        if (isNumericType(sourceType) && isNumericType(targetType)) {
            return true;
        }
        
        // String para qualquer tipo (toString)
        if (targetType.equals(String.class)) {
            return true;
        }
        
        // Wrapper para primitivo e vice-versa
        if (isPrimitiveWrapper(sourceType, targetType) || isPrimitiveWrapper(targetType, sourceType)) {
            return true;
        }
        
        return false;
    }
    
    private static boolean isNumericType(Class<?> type) {
        return Number.class.isAssignableFrom(type) || 
               type.equals(int.class) || type.equals(long.class) || 
               type.equals(double.class) || type.equals(float.class) ||
               type.equals(short.class) || type.equals(byte.class);
    }
    
    private static boolean isPrimitiveWrapper(Class<?> wrapper, Class<?> primitive) {
        return (wrapper.equals(Integer.class) && primitive.equals(int.class)) ||
               (wrapper.equals(Long.class) && primitive.equals(long.class)) ||
               (wrapper.equals(Double.class) && primitive.equals(double.class)) ||
               (wrapper.equals(Float.class) && primitive.equals(float.class)) ||
               (wrapper.equals(Boolean.class) && primitive.equals(boolean.class)) ||
               (wrapper.equals(Character.class) && primitive.equals(char.class)) ||
               (wrapper.equals(Byte.class) && primitive.equals(byte.class)) ||
               (wrapper.equals(Short.class) && primitive.equals(short.class));
    }
    
    private static boolean isCollectionType(Class<?> type) {
        return java.util.Collection.class.isAssignableFrom(type);
    }
    
    private static boolean isSimpleType(Class<?> type) {
        return type.isPrimitive() ||
               type.equals(String.class) ||
               Number.class.isAssignableFrom(type) ||
               type.equals(Boolean.class) ||
               type.equals(Character.class) ||
               java.time.temporal.Temporal.class.isAssignableFrom(type);
    }
}
