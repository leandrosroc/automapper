package com.automapper.examples;

import com.automapper.core.TypeConverter;

/**
 * Conversor customizado para formatar nomes
 */
public class NomeConverter implements TypeConverter<String, String> {
    
    @Override
    public String apply(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return nome;
        }
        
        // Converte para formato título (primeira letra maiúscula)
        String[] palavras = nome.toLowerCase().split("\\s+");
        StringBuilder resultado = new StringBuilder();
        
        for (int i = 0; i < palavras.length; i++) {
            if (i > 0) {
                resultado.append(" ");
            }
            
            String palavra = palavras[i];
            if (!palavra.isEmpty()) {
                resultado.append(Character.toUpperCase(palavra.charAt(0)));
                if (palavra.length() > 1) {
                    resultado.append(palavra.substring(1));
                }
            }
        }
        
        return resultado.toString();
    }
}
