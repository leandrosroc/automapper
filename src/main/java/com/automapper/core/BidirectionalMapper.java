package com.automapper.core;

/**
 * Interface para mapeamento bidirecional
 */
public class BidirectionalMapper<TFirst, TSecond> {
    private final AutoMapper<TFirst, TSecond> forwardMapper;
    private final AutoMapper<TSecond, TFirst> reverseMapper;
    
    public BidirectionalMapper(Class<TFirst> firstClass, Class<TSecond> secondClass) {
        this.forwardMapper = AutoMapper.create(firstClass, secondClass);
        this.reverseMapper = AutoMapper.create(secondClass, firstClass);
    }
    
    /**
     * Mapeia do primeiro tipo para o segundo
     */
    public TSecond mapForward(TFirst source) {
        return forwardMapper.map(source);
    }
    
    /**
     * Mapeia do segundo tipo para o primeiro
     */
    public TFirst mapReverse(TSecond source) {
        return reverseMapper.map(source);
    }
    
    /**
     * Configura o mapeamento direto
     */
    public BidirectionalMapper<TFirst, TSecond> configureForward(MappingExpression<TFirst, TSecond> expression) {
        MappingConfiguration<TFirst, TSecond> config = new MappingConfiguration<>(forwardMapper);
        expression.configure(config);
        return this;
    }
    
    /**
     * Configura o mapeamento reverso
     */
    public BidirectionalMapper<TFirst, TSecond> configureReverse(MappingExpression<TSecond, TFirst> expression) {
        MappingConfiguration<TSecond, TFirst> config = new MappingConfiguration<>(reverseMapper);
        expression.configure(config);
        return this;
    }
    
    /**
     * Obtém o mapper direto
     */
    public AutoMapper<TFirst, TSecond> getForwardMapper() {
        return forwardMapper;
    }
    
    /**
     * Obtém o mapper reverso
     */
    public AutoMapper<TSecond, TFirst> getReverseMapper() {
        return reverseMapper;
    }
}
