using System;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;
using System.Reflection;

public class Program
{
    public static void Main()
    {
        //1 - Configurar os mapeamentos customizados
        AutoMapper<PessoaDto, PessoaViewModel>.ConfigureMapping(new Dictionary<string, string>
        {
            { "NomeCompleto", "Nome" }, //Mapeia NomeCompleto para Nome
            { "DtNascimento", "DataNascimento" } //Mapeia DtNascimento para DataNascimento
        });

        //2 - Adicionar conversor de tipo para a data
        AutoMapper<PessoaDto, PessoaViewModel>.AddTypeConverter("DtNascimento", 
            obj => ((DateTime)obj).ToString("dd/MM/yyyy"));

        //3 - Criar objeto de origem com dados complexos
        var dto = new PessoaDto 
        {
            NomeCompleto = "Leandro Rocha",
            DtNascimento = new DateTime(1999, 09, 22),
            Endereco = new EnderecoDto 
            { 
                Logradouro = "Rua Hello World", 
                Numero = 123 
            },
            Telefones = new List<string> { "79-1234-1234", "79-4321-4321" },
            Score = 98.5
        };

        //4 - Executar o mapeamento
        var viewModel = AutoMapper<PessoaDto, PessoaViewModel>.Map(dto);

        //5 - Exibir resultados
        Console.WriteLine("Mapeamento concluído:");
        Console.WriteLine($"Nome: {viewModel.Nome}");
        Console.WriteLine($"Data Nascimento: {viewModel.DataNascimento}");
        Console.WriteLine($"Endereço: {viewModel.Endereco.Logradouro}, {viewModel.Endereco.Numero}");
        Console.WriteLine($"Telefones: {string.Join(", ", viewModel.Telefones)}");
        Console.WriteLine($"Score: {viewModel.Score}");
    }
}

//Classes de exemplo
public class PessoaDto
{
    public string NomeCompleto { get; set; }
    public DateTime DtNascimento { get; set; }
    public EnderecoDto Endereco { get; set; }
    public List<string> Telefones { get; set; }
    public double Score { get; set; }
}

public class EnderecoDto
{
    public string Logradouro { get; set; }
    public int Numero { get; set; }
}

public class PessoaViewModel
{
    public string Nome { get; set; }
    public string DataNascimento { get; set; } //Note que o tipo é diferente (string)
    public EnderecoViewModel Endereco { get; set; }
    public IEnumerable<string> Telefones { get; set; } //Tipo diferente (IEnumerable)
    public int Score { get; set; } //Tipo diferente (int)
}

public class EnderecoViewModel
{
    public string Logradouro { get; set; }
    public int Numero { get; set; }
}

//Implementação completa
public static class AutoMapper<TSource, TTarget> where TTarget : new()
{
    private static Func<TSource, TTarget> MapFunction;
    private static readonly Dictionary<string, string> CustomMappings = new();
    private static readonly Dictionary<string, Func<object, object>> TypeConverters = new();

    public static void ConfigureMapping(Dictionary<string, string> customMappings)
    {
        foreach (var mapping in customMappings)
        {
            CustomMappings[mapping.Key] = mapping.Value;
        }
        InitializeMapper();
    }

    public static void AddTypeConverter(string propertyName, Func<object, object> converter)
    {
        TypeConverters[propertyName] = converter;
        InitializeMapper();
    }

    static AutoMapper()
    {
        InitializeMapper();
    }

    private static void InitializeMapper()
    {
        var source = Expression.Parameter(typeof(TSource), "source");
        var target = Expression.New(typeof(TTarget));
        
        var sourceProperties = typeof(TSource).GetProperties();
        var targetProperties = typeof(TTarget).GetProperties()
            .ToDictionary(p => p.Name, p => p);

        var bindings = new List<MemberBinding>();
        
        foreach (var sourceProp in sourceProperties)
        {
            //Verifica mapeamento customizado
            if (CustomMappings.TryGetValue(sourceProp.Name, out var targetPropName))
            {
                if (targetProperties.TryGetValue(targetPropName, out var targetProp))
                {
                    AddPropertyBinding(source, sourceProp, targetProp, bindings);
                }
                continue;
            }

            //Mapeamento padrão por nome igual
            if (targetProperties.TryGetValue(sourceProp.Name, out var defaultTargetProp))
            {
                AddPropertyBinding(source, sourceProp, defaultTargetProp, bindings);
            }
        }

        var body = Expression.MemberInit(target, bindings);
        MapFunction = Expression.Lambda<Func<TSource, TTarget>>(body, source).Compile();
    }

    private static void AddPropertyBinding(ParameterExpression source, 
        PropertyInfo sourceProp, PropertyInfo targetProp, List<MemberBinding> bindings)
    {
        var sourcePropExpr = Expression.Property(source, sourceProp);
        
        //1. Verifica conversor de tipo
        if (TypeConverters.TryGetValue(sourceProp.Name, out var converter))
        {
            var convertedValue = Expression.Convert(
                Expression.Invoke(Expression.Constant(converter), 
                Expression.Convert(sourcePropExpr, typeof(object))),
                targetProp.PropertyType);
            
            bindings.Add(Expression.Bind(targetProp, convertedValue));
            return;
        }

        //2. Verifica objetos aninhados
        if (!IsSimpleType(sourceProp.PropertyType) && 
            !IsCollectionType(sourceProp.PropertyType) &&
            sourceProp.PropertyType == targetProp.PropertyType)
        {
            var mapMethod = typeof(AutoMapper<,>)
                .MakeGenericType(sourceProp.PropertyType, targetProp.PropertyType)
                .GetMethod("Map");
            
            var mappedValue = Expression.Call(mapMethod, sourcePropExpr);
            bindings.Add(Expression.Bind(targetProp, mappedValue));
            return;
        }

        //3. Verifica coleções
        if (IsCollectionType(sourceProp.PropertyType) && 
            IsCollectionType(targetProp.PropertyType))
        {
            var sourceElementType = GetElementType(sourceProp.PropertyType);
            var targetElementType = GetElementType(targetProp.PropertyType);
            
            var mapMethod = typeof(AutoMapper<,>)
                .MakeGenericType(sourceElementType, targetElementType)
                .GetMethod("Map");
            
            var selectMethod = typeof(Enumerable).GetMethod("Select")
                .MakeGenericMethod(sourceElementType, targetElementType);
            
            var mappedCollection = Expression.Call(
                selectMethod,
                sourcePropExpr,
                Expression.Constant(mapMethod));
            
            bindings.Add(Expression.Bind(targetProp, mappedCollection));
            return;
        }

        //4. Conversão automática para tipos compatíveis
        if (IsConvertible(sourceProp.PropertyType, targetProp.PropertyType))
        {
            var convertedValue = Expression.Convert(sourcePropExpr, targetProp.PropertyType);
            bindings.Add(Expression.Bind(targetProp, convertedValue));
            return;
        }

        //5. Mapeamento padrão para tipos idênticos
        if (targetProp.PropertyType == sourceProp.PropertyType)
        {
            bindings.Add(Expression.Bind(targetProp, sourcePropExpr));
        }
    }

    private static bool IsSimpleType(Type type)
    {
        return type.IsPrimitive || 
               type == typeof(string) || 
               type == typeof(decimal) || 
               type == typeof(DateTime) || 
               type == typeof(DateTimeOffset) || 
               type == typeof(TimeSpan) || 
               type == typeof(Guid);
    }

    private static bool IsCollectionType(Type type)
    {
        return type.IsGenericType && 
               (type.GetGenericTypeDefinition() == typeof(IEnumerable<>) ||
                type.GetGenericTypeDefinition() == typeof(ICollection<>) ||
                type.GetGenericTypeDefinition() == typeof(IList<>) ||
                type.GetGenericTypeDefinition() == typeof(List<>));
    }

    private static Type GetElementType(Type collectionType)
    {
        return collectionType.GetGenericArguments()[0];
    }

    private static bool IsConvertible(Type sourceType, Type targetType)
    {
        try
        {
            var testValue = sourceType.IsValueType ? Activator.CreateInstance(sourceType) : null;
            return testValue != null && 
                   typeof(IConvertible).IsAssignableFrom(sourceType) && 
                   Convert.ChangeType(testValue, targetType) != null;
        }
        catch
        {
            return false;
        }
    }

    public static TTarget Map(TSource source)
    {
        return MapFunction(source);
    }
}