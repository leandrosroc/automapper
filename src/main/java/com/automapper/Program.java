package com.automapper;

import com.automapper.core.*;
import com.automapper.dto.*;
import com.automapper.viewmodel.*;
import com.automapper.examples.*;
import com.automapper.validation.ValidationResult;
import java.time.LocalDate;
import java.util.Arrays;

public class Program {
    public static void main(String[] args) {
        System.out.println("=== AutoMapper Java - Funcionalidades Avançadas ===\n");
        
        // Demonstra todas as funcionalidades
        ExemploCompleto.main(args);
        
        // Exemplo adicional com validação
        demonstrarValidacaoDetalhada();
    }
    
    private static void demonstrarValidacaoDetalhada() {
        System.out.println("=== VALIDAÇÃO DETALHADA ===");
        
        // Valida mapeamento entre tipos
        ValidationResult resultado = AutoMapper.validate(PessoaDto.class, PessoaViewModel.class);
        
        System.out.println("Resultado da validação:");
        System.out.println(resultado);
        
        // Demonstra mapeamento com configuração completa
        AutoMapper<PessoaDto, PessoaViewModel> mapper = AutoMapper
            .create(PessoaDto.class, PessoaViewModel.class)
            .configure(config -> {
                System.out.println("Aplicando configurações personalizadas...");
            })
            .addLambdaConverter("nomeCompleto", nome -> 
                "Sr(a). " + nome.toString().trim())
            .ignoreField("score");
        
        // Teste com dados reais
        EnderecoDto endereco = new EnderecoDto("Av. Paulista", 1000);
        PessoaDto pessoa = new PessoaDto(
            "Leandro Santos Rocha",
            LocalDate.of(1999, 9, 22),
            endereco,
            Arrays.asList("11987654321", "1134567890"),
            87.5
        );
        
        PessoaViewModel resultado2 = mapper.map(pessoa);
        
        System.out.println("\nResultado do mapeamento:");
        System.out.println("Nome original: " + pessoa.getNomeCompleto());
        System.out.println("Nome mapeado: " + resultado2.getNome());
        System.out.println("Data: " + resultado2.getDataNascimento());
        System.out.println("Telefones: " + resultado2.getTelefones());
        
        System.out.println("\n=== FIM DOS EXEMPLOS ===");
    }
}
