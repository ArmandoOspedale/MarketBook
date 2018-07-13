package com.example.armando.marketbook;

import android.content.Intent;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import java.util.ArrayList;

//Activity Principale
public class MainActivity extends AppCompatActivity {

    //TODO fragment Negozio,Download,Forum
    //TODO barra di ricerca, implementare il filtraggio
    //TODO sezione commenti utente > ok
    //TODO activity specifica autore > ok
    //TODO prelevare i dati dal database, mapping del database
    //TODO Sistemare ActionBar -> ok
    //TODO Analisi codice a barre libro per ottenere versione digitale a partire dalla versione fisica del libro -> da inserire nel fragmento download
    //TODO altri ed eventuali

    //Dati di prova
    private ArrayList<ArrayList<Items>> categoria;
    private ArrayList<String> nome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ottengo i riferimenti alle view
        final Toolbar toolbar = findViewById(R.id.toolbar2);
        final RecyclerView mRecyclerView = findViewById(R.id.vertical_courses_list);
        final BottomNavigationView bottomNavigationView = findViewById(R.id.navigationBar);

        // Imposto l'ActionBar personale
        setSupportActionBar(toolbar);

        // Per la RecyclerView necessita di un LayoutManager
        //Creo un istanza di  LinearLayoutManager da associare alla RecyclerView
        RecyclerView.LayoutManager RecyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(RecyclerViewLayoutManager);

        // Aggiungo gli oggetti alla RecyclerView.
        AddItemsToRecyclerViewArrayList();

        // Creo l'adapter e lo associo alla RecyclerView
        VerticalRecyclerViewAdapter adapter = new VerticalRecyclerViewAdapter(categoria,nome);
        mRecyclerView.setAdapter(adapter);

        //Imposto il listener per il click sulla CardView
        adapter.SetOnItemClickListener(new HorizontalRecyclerViewAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, Items book) {

                //Ottengo dall'adapter il libro > passare alla activity specifica per libro
                //Creo l'intent per passare all'altra activity
                Intent intent = new Intent(view.getContext(),BookActivity.class);
                intent.putExtra("Items",book);
                startActivity(intent);

            }

            //Al momento non abbiamo implementazionio per la pressione prolungata
            @Override
            public void onItemLongClick(View view, int position) { }

        });

    }

    // Funzione di prova per popolare la pagina
    public void AddItemsToRecyclerViewArrayList(){
        categoria= new ArrayList<>();
        nome = new ArrayList<>();
        ArrayList<Items> books1 = new ArrayList<>();
        Items a = new Items("titolo1","autore1","");
        books1.add(a);
        Items b = new Items("titolo2","autore2","");
        books1.add(b);
        Items c = new Items("titolo3","autore3","");
        books1.add(c);
        Items d = new Items("titolo4","autore4","");
        books1.add(d);
        Items e = new Items("titolo5","autore5","");
        books1.add(e);
        Items f = new Items("titolo6","autore6","");
        books1.add(f);
        categoria.add(books1);
        categoria.add(books1);
        categoria.add(books1);
        categoria.add(books1);
        nome.add("categoria1");
        nome.add("categoria2");
        nome.add("categoria3");
        nome.add("categoria4");
    }

}