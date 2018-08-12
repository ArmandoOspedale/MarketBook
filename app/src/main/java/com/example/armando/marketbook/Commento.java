package com.example.armando.marketbook;

import java.util.Date;

public class Commento {

    private String Commento;
    private Date Data;
    private String NomeUtente;

    Commento(){}

    Commento(String commento, Date data, String nome) {
        this.Commento = commento;
        this.Data = data;
        this.NomeUtente = nome;
    }

    public String getCommento() {
        return Commento;
    }

    public void setCommento(String commento) {
        Commento = commento;
    }

    public Date getData() {
        return Data;
    }

    public void setData(Date data) {
        Data = data;
    }

    public String getNomeUtente() {
        return NomeUtente;
    }

    public void setNomeUtente(String nome) {
        NomeUtente = nome;
    }
}
