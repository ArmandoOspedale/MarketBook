package com.example.armando.marketbook;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AutoreActivity extends AppCompatActivity {

    //View
    private Toolbar toolbar;
    private AnimatedExpandableListView expandableListView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ImageView freccia;

    //Database
    private  FirebaseFirestore db;
    private FirebaseStorage storage;
    private DocumentReference docRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupWindowAnimations();
        setContentView(R.layout.activity_autore);

        //Ottengo i dati passati
        String id = (String) getIntent().getSerializableExtra("ID");
        if(id!=null){
            setupRecyclerView();
            setupToolBar();
            setupProgressBar();
            setupDatabase(id);
            setupInfo();
            setupInfoGenerale();
            setupLibri(id);
        }
    }

    private void setupLibri(String id) {
        final ArrayList<Book> books = new ArrayList<>();
        db.collection("Libri").whereEqualTo("IDAutore",id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                    HorizontalRecyclerViewAdapter adapter = new HorizontalRecyclerViewAdapter(books,getApplicationContext());
                    recyclerView.setAdapter(adapter);
                    adapter.SetOnItemClickListener(new HorizontalRecyclerViewAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, Book book, List<View> shared, HashMap<String, String> transitionName) {
                            Intent intent = new Intent(AutoreActivity.this,BookActivity.class);
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
                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(AutoreActivity.this,pairs.toArray(new Pair[pairs.size()])).toBundle());
                        }

                        //Al momento non è implementata la pressione prolungata -> aggiungi ai desiderati
                        public void onItemLongClick(View view, Book book) {

                        }
                    });
                }
            }

        });
    }

    private void setupInfo() {
        docRef.collection("Info").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("ResourceType")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        InfoAutore infoAutore = documentSnapshot.toObject(InfoAutore.class);
                        assert infoAutore != null;
                        setupInfoSpecifiche(infoAutore);
                    }
                }
            }

        });
    }

    private void setupInfoSpecifiche(InfoAutore infoAutore) {
        ExpandableTextView descrizioneGenerale = findViewById(R.id.expand_text_view);
        freccia = findViewById(R.id.expand_collapse);
        descrizioneGenerale.setText(infoAutore.getGenerale());
        freccia.setImageResource(R.drawable.ic_keyboard_arrow_down_bianca_24dp);
        descrizioneGenerale.setOnExpandStateChangeListener(new ExpandableTextView.OnExpandStateChangeListener() {
            @Override
            public void onExpandStateChanged(TextView textView, boolean isExpanded) {
                if(isExpanded){
                    freccia.setImageResource(R.drawable.ic_keyboard_arrow_up_bianca_24dp);
                } else {
                    freccia.setImageResource(R.drawable.ic_keyboard_arrow_down_bianca_24dp);
                }
            }
        });
        final ExpandAdapter adapter = new ExpandAdapter(getBaseContext(),infoAutore.getInfo());
        expandableListView = findViewById(R.id.mainList);
        expandableListView.setAdapter(adapter);
        expandableListView.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.enter_from_right));
        expandableListView.setGroupIndicator(null);
        expandableListView.setFocusable(false);
        if(adapter.getGroupCount()>0){
            TextView info = findViewById(R.id.TextInfo);
            info.setVisibility(View.VISIBLE);
        }
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (expandableListView.isGroupExpanded(groupPosition)) {
                    adapter.arrowAnimationStart(groupPosition);
                    expandableListView.collapseGroupWithAnimation(groupPosition);
                } else {
                    adapter.arrowAnimationStart(groupPosition);
                    expandableListView.expandGroupWithAnimation(groupPosition);
                }
                return true;
            }
        });
    }

    private void setupInfoGenerale() {
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        final Autore autore = document.toObject(Autore.class);
                        StorageReference storageRef = null;
                        if (autore != null) {
                            storageRef = storage.getReferenceFromUrl("gs://books-c7269.appspot.com").child(autore.getUrlImmagine());
                            try {
                                final File localFile = File.createTempFile("images", "jpg");
                                storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
                                        bitmap = Bitmap.createScaledBitmap(bitmap, Math.round(90 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)),Math.round(90 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)),true);
                                        setupAutoreGenerale(bitmap,autore.getNome());
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                    }
                                });
                            } catch (IOException e ) {}
                        }
                    } else {
                        Log.d("Documento", "Documento non trovato");
                    }
                } else {
                    Log.d("Task", "Task andata male", task.getException());
                }
            }
        });
    }

    private void setupAutoreGenerale(Bitmap bitmap, String nome) {
        TextView nomeAutore = findViewById(R.id.autoreLibro);
        ImageView immagineAutore = findViewById(R.id.immagineAutore);
        nomeAutore.setText(nome);
        immagineAutore.setImageBitmap(bitmap);
        TextView altriDellAutore = findViewById(R.id.AltridellAutore);
        altriDellAutore.setText("Altri di " + nome);
        progressBar.setVisibility(View.GONE);
    }

    private void setupDatabase(String id) {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        docRef = db.collection("Autori").document(id);
    }

    private void setupProgressBar() {
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupToolBar() {
        toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AutoreActivity.this.finishAfterTransition();
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerviewBook);
        LinearLayoutManager RecyclerViewLayout = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        RecyclerViewLayout.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(RecyclerViewLayout);
        //recyclerView.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.exit_out_left));
        recyclerView.setAdapter(new RecyclerView.Adapter() {
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

    private void setupWindowAnimations() {
        getWindow().setEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.autore));
        getWindow().setReturnTransition(TransitionInflater.from(this).inflateTransition(R.transition.autorein));
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}


