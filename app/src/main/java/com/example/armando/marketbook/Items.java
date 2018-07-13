package com.example.armando.marketbook;

import java.io.Serializable;

public class Items implements Serializable {

    private  String Titolo;
    private  String Autore;
    private  String Copertina;

    Items(String titolo, String autore, String copertina){
        this.Titolo = titolo;
        this.Autore = autore;
        this.Copertina = copertina;
    }

    public String getTitolo() {
        return Titolo;
    }

    public void setTitolo(String titolo) {
        Titolo = titolo;
    }

    public String getAutore() {
        return Autore;
    }

    public void setAutore(String autore) {
        Autore = autore;
    }

    public String getCopertina() {
        return Copertina;
    }

    public void setCopertina(String copertina) {
        Copertina = copertina;
    }

}