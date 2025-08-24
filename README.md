# AutoMapper Java - Versão Avançada

Um framework de mapeamento de objetos poderoso e flexível para Java, inspirado no AutoMapper do .NET, com funcionalidades avançadas para simplificar a conversão entre diferentes tipos de objetos.

## 🚀 Funcionalidades Principais

### ✅ Funcionalidades Básicas
- ✅ Mapeamento automático por nome de propriedade
- ✅ Mapeamento customizado entre propriedades
- ✅ Conversores de tipo customizados
- ✅ Suporte a objetos aninhados (composição)
- ✅ Conversões automáticas entre tipos primitivos
- ✅ Suporte a coleções (List, Set)
- ✅ Cache de mappers para performance

### 🆕 Funcionalidades Avançadas
- ✅ **Suporte a anotações** para configuração declarativa
- ✅ **Mapeamento bidirecional** para conversões nos dois sentidos
- ✅ **Validação de tipos** em tempo de compilação
- ✅ **Expressões lambda** para configuração fluente
- ✅ **Profiles reutilizáveis** para configurações complexas

## 📦 Estrutura do Projeto

```
src/main/java/com/automapper/
├── annotations/           # Anotações para configuração
│   ├── AutoMappable.java
│   ├── Ignore.java
│   ├── MapTo.java
│   └── UseConverter.java
├── core/                 # Classes principais
│   ├── AutoMapper.java
│   ├── BidirectionalMapper.java
│   ├── MappingConfiguration.java
│   ├── MappingExpression.java
│   ├── MappingProfile.java
│   ├── ProfileManager.java
│   └── TypeConverter.java
├── validation/           # Sistema de validação
│   ├── TypeValidator.java
│   └── ValidationResult.java
├── examples/            # Exemplos e demos
│   ├── ExemploCompleto.java
│   ├── NomeConverter.java
│   └── PessoaProfile.java
├── dto/                # Classes de transferência
└── viewmodel/          # Classes de visualização
```

## 🛠️ Como Usar

### 1. Anotações para Configuração

Use anotações para configurar o mapeamento diretamente nas classes:

```java
@AutoMappable(profile = "pessoa")
public class PessoaDto {
    @UseConverter(NomeConverter.class)
    private String nomeCompleto;
    
    @MapTo("dataNascimento")
    private LocalDate dtNascimento;
    
    @Ignore
    private double score;
    
    // getters e setters...
}
```

**Anotações disponíveis:**
- `@AutoMappable(profile)`: Marca uma classe como mapeável
- `@MapTo("campo")`: Mapeia para um campo com nome diferente
- `@UseConverter(Classe.class)`: Usa um conversor customizado
- `@Ignore`: Ignora o campo durante o mapeamento

### 2. Mapeamento Bidirecional

Crie mappers que funcionam nos dois sentidos:

```java
// Cria mapper bidirecional
BidirectionalMapper<PessoaDto, PessoaViewModel> mapper = 
    AutoMapper.createBidirectional(PessoaDto.class, PessoaViewModel.class);

// Mapeamento direto: DTO -> ViewModel
PessoaViewModel viewModel = mapper.mapForward(pessoaDto);

// Mapeamento reverso: ViewModel -> DTO
PessoaDto dto = mapper.mapReverse(viewModel);

// Configuração personalizada para cada direção
mapper.configureForward(config -> {
    // configurações para DTO -> ViewModel
}).configureReverse(config -> {
    // configurações para ViewModel -> DTO
});
```

### 3. Validação de Tipos

Valide a compatibilidade entre tipos antes do mapeamento:

```java
// Valida se o mapeamento é possível
ValidationResult resultado = AutoMapper.validate(PessoaDto.class, PessoaViewModel.class);

if (resultado.isValid()) {
    System.out.println("✓ Mapeamento é válido!");
} else {
    System.out.println("✗ Erros encontrados:");
    resultado.getErrors().forEach(System.out::println);
}

if (resultado.hasWarnings()) {
    System.out.println("⚠ Avisos:");
    resultado.getWarnings().forEach(System.out::println);
}
```

### 4. Expressões Lambda

Configure mapeamentos usando expressões lambda:

```java
AutoMapper<PessoaDto, PessoaViewModel> mapper = AutoMapper
    .create(PessoaDto.class, PessoaViewModel.class)
    .configure(config -> {
        // Configurações usando lambda
        System.out.println("Configurando mapeamento...");
    })
    .addLambdaConverter("nomeCompleto", nome -> 
        nome.toString().toUpperCase())
    .ignoreField("score");
```

### 5. Profiles Reutilizáveis

Crie profiles para reutilizar configurações:

```java
public class PessoaProfile extends MappingProfile {
    @Override
    public void configure() {
        // Configuração entre tipos
        createMap(PessoaDto.class, PessoaViewModel.class);
        
        // Conversores globais
        addConverter(LocalDate.class, String.class, 
            date -> date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }
}

// Registra o profile
ProfileManager.registerProfile("pessoa", new PessoaProfile());

// Usa o profile
if (ProfileManager.hasProfile("pessoa")) {
    MappingProfile profile = ProfileManager.getProfile("pessoa");
    // Use as configurações do profile
}
```

### 6. Conversores Customizados

Crie conversores reutilizáveis:

```java
public class NomeConverter implements TypeConverter<String, String> {
    @Override
    public String apply(String nome) {
        if (nome == null) return null;
        
        // Converte para formato título
        return Arrays.stream(nome.toLowerCase().split("\\s+"))
            .map(palavra -> palavra.substring(0, 1).toUpperCase() + 
                           palavra.substring(1))
            .collect(Collectors.joining(" "));
    }
}
```

## 🔧 Exemplo Completo

```java
public class ExemploUso {
    public static void main(String[] args) {
        // 1. Validação de tipos
        ValidationResult validacao = AutoMapper.validate(PessoaDto.class, PessoaViewModel.class);
        System.out.println("Validação: " + validacao);
        
        // 2. Configuração com anotações e lambda
        AutoMapper<PessoaDto, PessoaViewModel> mapper = AutoMapper
            .create(PessoaDto.class, PessoaViewModel.class)
            .configure(config -> {
                System.out.println("Aplicando configurações...");
            })
            .addLambdaConverter("nomeCompleto", nome -> 
                "Sr(a). " + nome.toString().trim());
        
        // 3. Dados de teste
        PessoaDto pessoa = new PessoaDto(
            "joão silva santos",
            LocalDate.of(1990, 5, 15),
            new EnderecoDto("Rua das Flores", 123),
            Arrays.asList("11999999999"),
            95.5
        );
        
        // 4. Mapeamento
        PessoaViewModel resultado = mapper.map(pessoa);
        
        System.out.println("Nome original: " + pessoa.getNomeCompleto());
        System.out.println("Nome processado: " + resultado.getNome());
    }
}
```

## ⚡ Performance

- **Cache de Mappers**: Mappers são reutilizados automaticamente
- **Validação Antecipada**: Problemas detectados antes da execução
- **Reflexão Otimizada**: Campos são analisados apenas uma vez
- **Conversões Eficientes**: Tipos compatíveis são detectados automaticamente

## 🧪 Executando os Exemplos

```bash
# Compila o projeto
mvn compile

# Executa os exemplos
mvn exec:java -Dexec.mainClass="com.automapper.Program"

# Executa exemplo específico
mvn exec:java -Dexec.mainClass="com.automapper.examples.ExemploCompleto"
```

## 📋 Requisitos

- Java 11 ou superior
- Maven 3.6 ou superior

## 🎯 Casos de Uso

- **APIs REST**: Conversão entre DTOs e entidades
- **Microserviços**: Mapeamento entre contratos de serviço
- **Camadas de Aplicação**: DTO ↔ ViewModel ↔ Entity
- **Integração de Sistemas**: Transformação de dados entre formatos
- **Testes**: Criação de objetos de teste a partir de templates

## 🔮 Próximas Funcionalidades

- [ ] Mapeamento condicional
- [ ] Suporte a herança e polimorfismo
- [ ] Configuração via XML/JSON
- [ ] Métricas e profiling