package com.automapper.viewmodel;

import com.automapper.annotations.*;
import java.util.Collection;

@AutoMappable(profile = "pessoa")
public class PessoaViewModel {
    @MapTo("nomeCompleto")
    private String nome;
    
    private String dataNascimento; // Tipo diferente (String)
    private EnderecoViewModel endereco;
    private Collection<String> telefones; // Tipo diferente (Collection)
    private int score; // Tipo diferente (int)

    public PessoaViewModel() {}

    public PessoaViewModel(String nome, String dataNascimento, EnderecoViewModel endereco, 
                          Collection<String> telefones, int score) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.endereco = endereco;
        this.telefones = telefones;
        this.score = score;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public EnderecoViewModel getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoViewModel endereco) {
        this.endereco = endereco;
    }

    public Collection<String> getTelefones() {
        return telefones;
    }

    public void setTelefones(Collection<String> telefones) {
        this.telefones = telefones;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
