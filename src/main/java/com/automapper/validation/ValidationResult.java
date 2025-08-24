package com.automapper.validation;

import java.util.List;

/**
 * Resultado da validação de mapeamento
 */
public class ValidationResult {
    private final List<String> errors;
    private final List<String> warnings;
    
    public ValidationResult(List<String> errors, List<String> warnings) {
        this.errors = errors;
        this.warnings = warnings;
    }
    
    /**
     * Verifica se a validação passou sem erros
     */
    public boolean isValid() {
        return errors.isEmpty();
    }
    
    /**
     * Verifica se há avisos
     */
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }
    
    /**
     * Obtém a lista de erros
     */
    public List<String> getErrors() {
        return errors;
    }
    
    /**
     * Obtém a lista de avisos
     */
    public List<String> getWarnings() {
        return warnings;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        if (!errors.isEmpty()) {
            sb.append("ERRORS:\n");
            for (String error : errors) {
                sb.append("  - ").append(error).append("\n");
            }
        }
        
        if (!warnings.isEmpty()) {
            sb.append("WARNINGS:\n");
            for (String warning : warnings) {
                sb.append("  - ").append(warning).append("\n");
            }
        }
        
        if (errors.isEmpty() && warnings.isEmpty()) {
            sb.append("Validation passed successfully");
        }
        
        return sb.toString();
    }
}
