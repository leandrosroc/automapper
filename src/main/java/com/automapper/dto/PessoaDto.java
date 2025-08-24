package com.automapper.dto;

import com.automapper.annotations.*;
import com.automapper.examples.NomeConverter;
import java.time.LocalDate;
import java.util.List;

@AutoMappable(profile = "pessoa")
public class PessoaDto {
    @UseConverter(NomeConverter.class)
    private String nomeCompleto;
    
    @MapTo("dataNascimento")
    private LocalDate dtNascimento;
    
    private EnderecoDto endereco;
    private List<String> telefones;
    
    @Ignore
    private double score;

    public PessoaDto() {}

    public PessoaDto(String nomeCompleto, LocalDate dtNascimento, EnderecoDto endereco, 
                     List<String> telefones, double score) {
        this.nomeCompleto = nomeCompleto;
        this.dtNascimento = dtNascimento;
        this.endereco = endereco;
        this.telefones = telefones;
        this.score = score;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public LocalDate getDtNascimento() {
        return dtNascimento;
    }

    public void setDtNascimento(LocalDate dtNascimento) {
        this.dtNascimento = dtNascimento;
    }

    public EnderecoDto getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoDto endereco) {
        this.endereco = endereco;
    }

    public List<String> getTelefones() {
        return telefones;
    }

    public void setTelefones(List<String> telefones) {
        this.telefones = telefones;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
