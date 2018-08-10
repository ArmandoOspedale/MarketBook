package com.example.armando.marketbook;

import java.util.List;

public class InfoAutore{

    private  String Generale;
    private List<Object> Info;

    InfoAutore() {}

    public InfoAutore(String generale, List<Object> info) {
        this.Generale = generale;
        this.Info = info;
    }

    public String getGenerale() {
        return Generale;
    }

    public void setGenerale(String generale) {
        Generale = generale;
    }

    public List<Object> getInfo() {
        return Info;
    }

    public void setInfo(List<Object> info) {
        Info = info;
    }
}
