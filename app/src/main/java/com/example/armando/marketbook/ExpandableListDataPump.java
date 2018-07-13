package com.example.armando.marketbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDataPump {

    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();
        List<String> descrizione1 = new ArrayList<String>();
        descrizione1.add("Questo è il contenuto della descrizione1");
        List<String> descrizione2 = new ArrayList<String>();
        descrizione2.add("Questo è il contenuto della descrizione2");
        expandableListDetail.put("Descrizione1", descrizione1);
        expandableListDetail.put("Descrizione2", descrizione2);
        return expandableListDetail;
    }

}