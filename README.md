# Java AutoMapper

Uma implementação Java de um utilitário AutoMapper para mapeamento automático entre objetos de diferentes tipos.

## Estrutura do Projeto

```
src/
├── main/
│   └── java/
│       └── com/
│           └── automapper/
│               ├── Program.java              # Classe principal com exemplo de uso
│               ├── core/
│               │   ├── AutoMapper.java       # Implementação principal do AutoMapper
│               │   └── TypeConverter.java    # Interface para conversores de tipo
│               ├── dto/
│               │   ├── PessoaDto.java        # Classe DTO de exemplo
│               │   └── EnderecoDto.java      # Classe DTO de endereço
│               └── viewmodel/
│                   ├── PessoaViewModel.java  # Classe ViewModel de exemplo
│                   └── EnderecoViewModel.java # Classe ViewModel de endereço
└── pom.xml                                   # Configuração do Maven
```

## Características

### Funcionalidades Implementadas

1. **Mapeamento Automático**: Mapeia propriedades com nomes idênticos automaticamente
2. **Mapeamentos Customizados**: Permite mapear propriedades com nomes diferentes
3. **Conversores de Tipo**: Suporte a conversões customizadas de tipos
4. **Mapeamento de Objetos Aninhados**: Mapeia automaticamente objetos complexos
5. **Mapeamento de Coleções**: Suporte a List, Set e Collection
6. **Conversões Automáticas**: Conversões entre tipos primitivos compatíveis
7. **Cache de Mappers**: Otimização através de cache dos mappers criados

### Conversões Automáticas Suportadas

- **Tipos Primitivos**: int ↔ double, long ↔ int, etc.
- **Data para String**: LocalDate → String (formato dd/MM/yyyy)
- **Coleções**: List ↔ Collection, Set ↔ Collection
- **Objetos Complexos**: Mapeamento recursivo automático

## Como Usar

### 1. Configuração Básica

```java
// Criar o mapper
AutoMapper<SourceClass, TargetClass> mapper = 
    AutoMapper.create(SourceClass.class, TargetClass.class);

// Executar mapeamento
TargetClass result = mapper.map(sourceObject);
```

### 2. Mapeamentos Customizados

```java
Map<String, String> customMappings = new HashMap<>();
customMappings.put("nomeCompleto", "nome");
customMappings.put("dtNascimento", "dataNascimento");

AutoMapper<PessoaDto, PessoaViewModel> mapper = AutoMapper
    .create(PessoaDto.class, PessoaViewModel.class)
    .configureMapping(customMappings);
```

### 3. Conversores de Tipo

```java
mapper.addTypeConverter("dtNascimento", 
    obj -> ((LocalDate) obj).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
```

## Executando o Projeto

### Pré-requisitos

- Java 11 ou superior
- Maven 3.6 ou superior

### Comandos

```bash
# Compilar o projeto
mvn compile

# Executar o programa principal
mvn exec:java

# Executar testes (quando implementados)
mvn test

# Gerar JAR
mvn package
```

## Exemplo de Uso

O projeto inclui um exemplo completo na classe `Program.java` que demonstra:

- Mapeamento de propriedades com nomes diferentes
- Conversão de data para string
- Mapeamento de objetos aninhados
- Mapeamento de coleções
- Conversão automática de tipos (double → int)

## Extensibilidade

O AutoMapper foi projetado para ser facilmente extensível:

1. **Novos Conversores**: Implemente a interface `TypeConverter<T, R>`
2. **Tipos Customizados**: Adicione lógica no método `mapValue()` 
3. **Estratégias de Cache**: Modifique a implementação do cache estático

## Limitações Atuais

- Mapeamento baseado em reflexão (pode ter impacto na performance)
- Não suporta mapeamento de propriedades privadas sem getters/setters
- Mapeamento de generics complexos tem limitações
- Não suporta mapeamento circular automático

## Melhorias Futuras

- [ ] Suporte a anotações para configuração
- [ ] Mapeamento bidirecional
- [ ] Validação de tipos em tempo de compilação
- [ ] Suporte a expressões lambda para mapeamento
- [ ] Profile de configurações reutilizáveis
