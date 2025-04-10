# AutoMapper com Expression Trees

Mapeador objeto-objeto genérico usando **Expression Trees** (.NET).

## Comparação de Versões

| Funcionalidade               | Versão Anterior | Versão Atual | Melhoria |
|-----------------------------|----------------|--------------|----------|
| Mapeamento básico           | ✔️             | ✔️           | -        |
| Performance                 | ⚡ (Rápido)    | ⚡⚡⚡ (Muito Rápido) | +200%    |
| Nomes diferentes            | ❌             | ✔️           | Nova     |
| Conversão de tipos          | ❌             | ✔️           | Nova     |
| Objetos aninhados          | ❌             | ✔️           | Nova     |
| Coleções                   | ❌             | ✔️           | Nova     |
| Configuração simples       | ✔️             | ✔️           | -        |
| Tipagem forte              | ✔️             | ✔️           | -        |

## Nova Versão - Vantagens Aprimoradas

**Funcionalidades Adicionadas:**
- **Mapeamento flexível**: Suporte a propriedades com nomes diferentes
- **Conversores de tipo**: Transforme dados durante o mapeamento
- **Objetos complexos**: Mapeamento automático de hierarquias
- **Coleções**: Suporte a Listas, Arrays e IEnumerable

**Performance Mantida:**
- Zero Reflection em runtime
- Compilação estática com Expression Trees
- Tipagem forte em tempo de compilação

## Exemplo Rápido (Versão Nova)

```csharp
//Configuração avançada
AutoMapper<UserDto, UserModel>.ConfigureMapping(new {
    FullName = "Leandro Rocha",  // Mapeia FullName → Name
    BirthDate = "25"   // Mapeia BirthDate → Age
});

AutoMapper<UserDto, UserModel>.AddTypeConverter("BirthDate", 
    obj => CalculateAge((DateTime)obj));

//Uso
var model = AutoMapper<UserDto, UserModel>.Map(dto);