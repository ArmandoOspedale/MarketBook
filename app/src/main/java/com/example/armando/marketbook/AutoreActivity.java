package com.example.armando.marketbook;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
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

import java.io.File;
import java.io.IOException;
import java.util.Objects;


public class AutoreActivity extends AppCompatActivity {

    //View
    private Toolbar toolbar;
    private AnimatedExpandableListView expandableListView;
    private ProgressBar progressBar;
    private TextView nomeAutore;
    private ImageView immagineAutore;
    private TextView descrizioneGenerale;

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
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            String id = (String) extra.getSerializable("ID");
            String transitionName = (String) extra.getSerializable("AUTORE");
            if(id!=null && transitionName!=null){
                setupToolBar(transitionName);
                setupProgressBar();
                setupDatabase(id);
                setupInfoGenerale();
                setupInfo();
            }
        }
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
        descrizioneGenerale = findViewById(R.id.info_generale);
        expandableListView = findViewById(R.id.mainList);
        descrizioneGenerale.setText(infoAutore.getGenerale());
        final ExpandAdapter adapter = new ExpandAdapter(getBaseContext(),infoAutore.getInfo());
        expandableListView.setAdapter(adapter);
        expandableListView.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.enter_from_right));
        expandableListView.setGroupIndicator(null);
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
                        StorageReference storageRef = storage.getReferenceFromUrl("gs://books-c7269.appspot.com").child(autore.getUrlImmagine());
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
        nomeAutore = findViewById(R.id.autoreLibro);
        immagineAutore = findViewById(R.id.immagineAutore);
        nomeAutore.setText(nome);
        immagineAutore.setImageBitmap(bitmap);
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

    private void setupToolBar(String nome) {
        toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setTransitionName(nome);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setupWindowAnimations() {
        Transition slideEntrata = TransitionInflater.from(this).inflateTransition(R.transition.autore);
        getWindow().setEnterTransition(slideEntrata);
    }
}


