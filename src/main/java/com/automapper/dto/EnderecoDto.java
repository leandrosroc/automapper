package com.automapper.dto;

public class EnderecoDto {
    private String logradouro;
    private int numero;

    public EnderecoDto() {}

    public EnderecoDto(String logradouro, int numero) {
        this.logradouro = logradouro;
        this.numero = numero;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }
}
