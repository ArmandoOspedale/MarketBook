package com.example.armando.marketbook;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.support.v7.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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
    private ArrayList<ArrayList<Book>> libriCategoria;
    private ArrayList<String> nomeCategoria;
    private boolean stato=false;

    //View
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private  BottomNavigationView bottomNavigationView;

    //Database
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupWindowAnimations();
        setContentView(R.layout.activity_main);
        setupToolbar();
        setupBottonNavigationView();
        setupRecyclerView();
        setupFirebase();
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem mSearch = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            ricercaLibri();
        } else {
            signInAnonymously();
        }
        
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(this, new  OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                ricercaLibri();
            }
        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("Errore", "signInAnonymously:FAILURE", exception);
                    }
                });

    }

    private void setupBottonNavigationView() {
        bottomNavigationView = findViewById(R.id.navigationBar);
    }

    private void ricercaLibri() {
        db = FirebaseFirestore.getInstance();
        libriCategoria= new ArrayList<>();
        nomeCategoria = new ArrayList<>();
        final ArrayList<Book> books = new ArrayList<>();

        //Query al Database : Ottengo tutti i documenti
        db.collection("Avventura").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        Book book = documentSnapshot.toObject(Book.class);
                        if (book != null) {
                            book.setRiferimento(documentSnapshot.getReference().getPath());
                        }
                        books.add(book);
                    }
                    libriCategoria.add(books);
                    nomeCategoria.add("Avventura");
                    //Creo l'adapter con i dati ricavati dal database
                    VerticalRecyclerViewAdapter adapter = new VerticalRecyclerViewAdapter(libriCategoria,nomeCategoria,getApplicationContext());
                    mRecyclerView.setAdapter(adapter);
                    //Imposto il listener per il click sulla CardView
                    adapter.SetOnItemClickListener(new HorizontalRecyclerViewAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, Book book, List<View> shared, HashMap<String,String> transitionName) {
                            //Ottengo dall'adapter il libro > passare alla activity specifica per libro
                            //Creo l'intent per passare all'altra activity
                            Intent intent = new Intent(MainActivity.this,BookActivity.class);
                            setIntent(intent);
                            intent.putExtra("LIBRO",book);
                            intent.putExtra("TRANSITION_NAME",transitionName);
                            View statusBar = findViewById(android.R.id.statusBarBackground);
                            View navigationBar = findViewById(android.R.id.navigationBarBackground);
                            List<Pair<View, String>> pairs = new ArrayList<>();
                            if (statusBar != null) {
                                pairs.add(Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME));
                            }
                            if (navigationBar != null) {
                                pairs.add(Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME));
                            }
                            if (toolbar != null) {
                                pairs.add(Pair.create((View)toolbar, toolbar.getTransitionName()));
                            }
                            for (int i=0;i<shared.size();i++){
                                pairs.add(Pair.create(shared.get(i), shared.get(i).getTransitionName()));
                            }
                            startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairs.toArray(new Pair[pairs.size()])).toBundle());
                        }

                        //Al momento non è implementata la pressione prolungata -> aggiungi ai desiderati
                        public void onItemLongClick(View view, Book book) {

                        }
                    });
                }
            }

        });
    }

    private void setupRecyclerView() {
        mRecyclerView = findViewById(R.id.vertical_courses_list);
        RecyclerView.LayoutManager RecyclerViewLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(RecyclerViewLayoutManager);
        mRecyclerView.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public int getItemCount() {
                return 0;
            }
        });
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
    }

    private void setupWindowAnimations() {
        getWindow().setExitTransition(TransitionInflater.from(this).inflateTransition(R.transition.activity_slide));
    }

}