using System;
using System.Collections.Generic;
using System.Linq.Expressions;
using System.Reflection;

public class Program
{
    public static void Main()
    {
        //Exemplo de uso
        var dto = new PessoaDto { Nome = "Leandro", Idade = 25 };
        var viewModel = AutoMapper<PessoaDto, PessoaViewModel>.Map(dto);
        
        Console.WriteLine($"Nome: {viewModel.Nome}, Idade: {viewModel.Idade}");
    }
}

public class PessoaDto
{
    public string Nome { get; set; }
    public int Idade { get; set; }
}

public class PessoaViewModel
{
    public string Nome { get; set; }
    public int Idade { get; set; }
}

public static class AutoMapper<TSource, TTarget>
    where TTarget : new()
{
    private static readonly Func<TSource, TTarget> MapFunction;

    static AutoMapper()
    {
        var source = Expression.Parameter(typeof(TSource), "source");
        var target = Expression.New(typeof(TTarget));
        
        var sourceProperties = typeof(TSource).GetProperties();
        var targetProperties = typeof(TTarget).GetProperties()
            .ToDictionary(p => p.Name, p => p);

        var bindings = new List<MemberBinding>();
        
        foreach (var sourceProp in sourceProperties)
        {
            if (targetProperties.TryGetValue(sourceProp.Name, out var targetProp) &&
                targetProp.PropertyType == sourceProp.PropertyType)
            {
                var sourcePropExpr = Expression.Property(source, sourceProp);
                bindings.Add(Expression.Bind(targetProp, sourcePropExpr));
            }
        }

        var body = Expression.MemberInit(target, bindings);
        MapFunction = Expression.Lambda<Func<TSource, TTarget>>(body, source).Compile();
    }

    public static TTarget Map(TSource source)
    {
        return MapFunction(source);
    }
}