package com.example.armando.marketbook;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

//Activity Principale
public class MainActivity extends AppCompatActivity {

    //TODO fragment Negozio,Download,Forum
    //TODO barra di ricerca, implementare il filtraggio
    //TODO sezione commenti utente > ok
    //TODO activity specifica autore > ok
    //TODO prelevare i dati dal database, mapping del database -> work in progress
    //TODO Sistemare ActionBar -> ok
    //TODO Analisi codice a barre libro per ottenere versione digitale a partire dalla versione fisica del libro -> da inserire nel fragmento download
    //TODO altri ed eventuali

    //Dati
    private ArrayList<ArrayList<Items>> libriCategoria;
    private ArrayList<String> nomeCategoria;

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

        // Ricerco i libri. Creo l'adapter e lo associo alla RecyclerView
        //Creo un istanza di FirebaseFirestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        libriCategoria= new ArrayList<>();
        nomeCategoria = new ArrayList<>();
        final ArrayList<Items> books = new ArrayList<>();

        db.collection("Avventura").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        Items book = documentSnapshot.toObject(Items.class);
                        books.add(book);
                    }
                    libriCategoria.add(books);
                    nomeCategoria.add("Avventura");
                    VerticalRecyclerViewAdapter adapter = new VerticalRecyclerViewAdapter(libriCategoria,nomeCategoria);
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

                        //Al momento non è implementata la pressione prolungata
                        @Override
                        public void onItemLongClick(View view, int position) { }

                    });

                }
            }

        });
    }

}