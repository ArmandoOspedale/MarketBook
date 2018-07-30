package com.example.armando.marketbook;

import java.io.Serializable;

public class Items implements Serializable {

    private  String Titolo;
    private  String Autore;
    private  String Publicazione;
    private  String URLCopertina;
    private  String Trama;
    private  String Genere;
    private  String IDAutore;

    Items(String titolo, String autore, String publicazione, String urlcopertina, String trama, String genere, String idautore){
        this.Titolo = titolo;
        this.Autore = autore;
        this.Publicazione = publicazione;
        this.URLCopertina = urlcopertina;
        this.Trama= trama;
        this.Genere= genere;
        this.IDAutore= idautore;
    }

    Items() {}

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

    public String getPublicazione() {
        return Publicazione;
    }

    public void setPublicazione(String publicazione) {
        Publicazione = publicazione;
    }

    public String getURLCopertina() {
        return URLCopertina;
    }

    public void setURLCopertina(String URLCopertina) {
        this.URLCopertina = URLCopertina;
    }

    public String getTrama() {
        return Trama;
    }

    public void setTrama(String trama) {
        Trama = trama;
    }

    public String getGenere() {
        return Genere;
    }

    public void setGenere(String genere) {
        Genere = genere;
    }

    public String getIDAutore() {
        return IDAutore;
    }

    public void setIDAutore(String IDAutore) {
        this.IDAutore = IDAutore;
    }
}