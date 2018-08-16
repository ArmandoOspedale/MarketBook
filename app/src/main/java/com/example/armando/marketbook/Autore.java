package com.example.armando.marketbook;

public class Autore {

    private  String Nome;
    private  String UrlImmagine;
    private  String Date;
    private  String ID;

    Autore(String nome, String UrlImmagine, String date) {
        this.Nome = nome;
        this.UrlImmagine = UrlImmagine;
        this.Date= date;
    }

    Autore() {}

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public String getUrlImmagine() {
        return UrlImmagine;
    }

    public void setUrlImmagine(String urlImmagine) {
        this.UrlImmagine = urlImmagine;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
