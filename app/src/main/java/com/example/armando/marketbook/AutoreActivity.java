package com.example.armando.marketbook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AutoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autore);

        //Ottengo i dati passati
        Intent intent = getIntent();
        String id = (String) intent.getSerializableExtra("ID");

        // Creo istanza di FirebaseStorage e FirebaseFirestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseStorage storage = FirebaseStorage.getInstance();

        // Punto alla toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Aggiungo il tasto indietro
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Impostosto i riferimenti alle view
        final TextView nome = findViewById(R.id.autoreLibro);
        final ImageView immagineAutore = findViewById(R.id.immagineAutore);


        ExpandableListView expandableListView = findViewById(R.id.infoAutore);

        HashMap<String, List<String>> expandableListDetail = ExpandableListDataPump.getData();
        List<String> expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
        ExpandableListAdapter expandableListAdapter = new InfoAutoreAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);

        DocumentReference docRef = db.collection("Autori").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Autore autore = document.toObject(Autore.class);
                        nome.setText(autore.getNome());
                        StorageReference storageRef = storage.getReferenceFromUrl("gs://books-c7269.appspot.com").child(autore.getUrlImmagine());
                        try {
                            final File localFile = File.createTempFile("images", "jpg");
                            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                    bitmap = Bitmap.createScaledBitmap(bitmap, 90,90,true);
                                    immagineAutore.setImageBitmap(bitmap);

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


        //if(id!=null){
            //Setto autore
            //autore.setText(libro);
       // }

    }

}

