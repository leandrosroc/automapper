# AutoMapper com Expression Trees

Mapeador objeto-objeto genérico usando **Expression Trees** (.NET).

## ✅ Vantagens
- **Performance**: Compilação estática (sem Reflection em runtime)  
- **Tipagem forte**: Erros aparecem em tempo de compilação  
- **Zero dependências**: Implementação pura .NET  
- **Simples**: Configuração automática por convenção  

## ❌ Limitações
- **Propriedades**: Só mapeia se **nome + tipo** forem idênticos  
- **Construtor**: Classe destino precisa de `new()`  
- **Complexidade**: Não suporta:  
  - Coleções  
  - Objetos aninhados  
  - Conversão de tipos  

## 📌 Exemplo Rápido
```csharp
var dto = new UserDto { Name = "Leandro", Age = 25 };
var model = AutoMapper<UserDto, UserModel>.Map(dto);