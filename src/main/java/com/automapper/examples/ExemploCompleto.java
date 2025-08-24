package com.automapper.examples;

import com.automapper.core.*;
import com.automapper.dto.*;
import com.automapper.viewmodel.*;
import com.automapper.validation.ValidationResult;
import java.time.LocalDate;
import java.util.Arrays;

/**
 * Exemplo demonstrando todas as funcionalidades do AutoMapper avançado
 */
public class ExemploCompleto {
    
    public static void main(String[] args) {
        demonstrarValidacaoTipos();
        demonstrarAnotacoes();
        demonstrarMapeamentoBidirecional();
        demonstrarExpressoesLambda();
        demonstrarProfiles();
    }
    
    /**
     * Demonstra a validação de tipos em tempo de compilação
     */
    public static void demonstrarValidacaoTipos() {
        System.out.println("=== VALIDAÇÃO DE TIPOS ===");
        
        // Valida compatibilidade entre PessoaDto e PessoaViewModel
        ValidationResult resultado = AutoMapper.validate(PessoaDto.class, PessoaViewModel.class);
        
        System.out.println("Validação PessoaDto -> PessoaViewModel:");
        System.out.println(resultado);
        
        if (resultado.isValid()) {
            System.out.println("✓ Mapeamento é válido!");
        } else {
            System.out.println("✗ Mapeamento tem erros!");
        }
        
        System.out.println();
    }
    
    /**
     * Demonstra o uso de anotações para configuração
     */
    public static void demonstrarAnotacoes() {
        System.out.println("=== ANOTAÇÕES ===");
        
        // Cria dados de teste
        EnderecoDto enderecoDto = new EnderecoDto("Rua das Flores", 123);
        PessoaDto pessoaDto = new PessoaDto(
            "joão silva santos",
            LocalDate.of(1990, 5, 15),
            enderecoDto,
            Arrays.asList("11999999999", "1133333333"),
            95.5
        );
        
        // Mapeia usando as anotações configuradas
        AutoMapper<PessoaDto, PessoaViewModel> mapper = AutoMapper.create(PessoaDto.class, PessoaViewModel.class);
        PessoaViewModel viewModel = mapper.map(pessoaDto);
        
        System.out.println("Dados originais (DTO):");
        System.out.println("Nome: " + pessoaDto.getNomeCompleto());
        System.out.println("Data Nascimento: " + pessoaDto.getDtNascimento());
        System.out.println("Score: " + pessoaDto.getScore());
        
        System.out.println("\nDados mapeados (ViewModel):");
        System.out.println("Nome: " + viewModel.getNome());
        System.out.println("Data Nascimento: " + viewModel.getDataNascimento());
        System.out.println("Score: " + viewModel.getScore() + " (campo ignorado, valor padrão)");
        
        System.out.println();
    }
    
    /**
     * Demonstra o mapeamento bidirecional
     */
    public static void demonstrarMapeamentoBidirecional() {
        System.out.println("=== MAPEAMENTO BIDIRECIONAL ===");
        
        // Cria mapper bidirecional
        BidirectionalMapper<PessoaDto, PessoaViewModel> bidirectionalMapper = 
            AutoMapper.createBidirectional(PessoaDto.class, PessoaViewModel.class);
        
        // Dados de teste
        EnderecoDto enderecoDto = new EnderecoDto("Rua das Flores", 123);
        PessoaDto pessoaDto = new PessoaDto(
            "Maria Silva",
            LocalDate.of(1985, 3, 20),
            enderecoDto,
            Arrays.asList("11888888888"),
            88.0
        );
        
        // Mapeamento direto: DTO -> ViewModel
        PessoaViewModel viewModel = bidirectionalMapper.mapForward(pessoaDto);
        System.out.println("Mapeamento direto (DTO -> ViewModel):");
        System.out.println("Nome: " + viewModel.getNome());
        
        // Mapeamento reverso: ViewModel -> DTO
        PessoaDto dtoReverso = bidirectionalMapper.mapReverse(viewModel);
        System.out.println("\nMapeamento reverso (ViewModel -> DTO):");
        System.out.println("Nome: " + dtoReverso.getNomeCompleto());
        
        System.out.println();
    }
    
    /**
     * Demonstra o uso de expressões lambda
     */
    public static void demonstrarExpressoesLambda() {
        System.out.println("=== EXPRESSÕES LAMBDA ===");
        
        // Configura mapeamento com lambdas
        AutoMapper<PessoaDto, PessoaViewModel> mapper = AutoMapper
            .create(PessoaDto.class, PessoaViewModel.class)
            .configure(config -> {
                // Configurações personalizadas podem ser adicionadas aqui
                System.out.println("Configurando mapeamento com lambda...");
            })
            .addLambdaConverter("nomeCompleto", nome -> 
                nome.toString().toUpperCase() + " [PROCESSADO]")
            .ignoreField("score");
        
        // Dados de teste
        EnderecoDto enderecoDto = new EnderecoDto("Rua das Flores", 123);
        PessoaDto pessoaDto = new PessoaDto(
            "ana costa",
            LocalDate.of(1992, 8, 10),
            enderecoDto,
            Arrays.asList("11777777777"),
            92.3
        );
        
        PessoaViewModel resultado = mapper.map(pessoaDto);
        
        System.out.println("Nome original: " + pessoaDto.getNomeCompleto());
        System.out.println("Nome processado: " + resultado.getNome());
        System.out.println("Score ignorado: " + resultado.getScore());
        
        System.out.println();
    }
    
    /**
     * Demonstra o uso de profiles reutilizáveis
     */
    public static void demonstrarProfiles() {
        System.out.println("=== PROFILES ===");
        
        // Registra o profile
        ProfileManager.registerProfile("pessoa", new PessoaProfile());
        
        // Verifica se o profile foi registrado
        if (ProfileManager.hasProfile("pessoa")) {
            System.out.println("✓ Profile 'pessoa' registrado com sucesso!");
            
            MappingProfile profile = ProfileManager.getProfile("pessoa");
            System.out.println("Conversores no profile: " + profile.getConverters().size());
            
            // Lista todos os profiles
            System.out.println("Profiles disponíveis: " + ProfileManager.getAllProfiles().keySet());
        }
        
        System.out.println();
    }
}
