package com.automapper.viewmodel;

public class EnderecoViewModel {
    private String logradouro;
    private int numero;

    public EnderecoViewModel() {}

    public EnderecoViewModel(String logradouro, int numero) {
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
