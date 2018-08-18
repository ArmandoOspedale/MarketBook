package com.example.armando.marketbook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class downloadFragment extends Fragment {

    private DownloadAdapter adapter;


    public static downloadFragment newInstance() {
        return new downloadFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.download_layout, container, false);
        final RecyclerView downloads = rootView.findViewById(R.id.downloads);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Utenti").document("Anonimo").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    List<String> list = (List<String>) documentSnapshot.get("Downloads");
                    RecyclerView.LayoutManager RecyclerViewLayoutManager = new LinearLayoutManager(getContext());
                    downloads.setHasFixedSize(true);
                    downloads.setLayoutManager(RecyclerViewLayoutManager);
                    adapter = new DownloadAdapter(getContext(),list);
                    downloads.setAdapter(adapter);
                    adapter.SetOnItemClickListener(new DownloadAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, Book book) {
                            Toast.makeText(view.getContext(),"Al momento non è possibile visionare ancora il libro" , Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        FloatingActionButton add = rootView.findViewById(R.id.aggiungiLibro);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = IntentIntegrator.forSupportFragment(downloadFragment.this);
                integrator.setBeepEnabled(false);
                integrator.setTimeout(180000);
                integrator.initiateScan();
            }
        });
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        // Gestione risultato scansione barcode con fotocamera
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        final Context context = getContext();
        if (result != null) {
            //verifica disponibilità codice
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            if(result.getContents()!=null){
                final String documento = result.getContents();
                db.collection("Codici").document(documento).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            final Map<String, Object> dati = documentSnapshot.getData();
                            try{
                                if(dati.get("Valido")!=null && dati.get("IDLibro")!=null){
                                    boolean verifica = (boolean) dati.get("Valido");
                                    if(verifica){
                                        Map<String, Object> update = new HashMap<>();
                                        update.put("Valido", false);
                                        db.collection("Codici").document(documento).set(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Map<String, Object> update = new HashMap<>();
                                                db.collection("Utenti").document("Anonimo").update("Downloads", FieldValue.arrayUnion((String)dati.get("IDLibro"))).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            Toast.makeText(context,"Il libro è stato correttamente aggiunto alla tua libreria" , Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(context,"Qualcosa è andato storto" , Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context,"Errore comunicazione database" , Toast.LENGTH_SHORT).show();
                                            }});
                                    }else {
                                        Toast.makeText(context, "Spiacenti, codice già riscattato", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(context, "Ops, qualcosa nella cattura non è andato a buon fine, riprova", Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e){
                                Toast.makeText(context,"Codice non valido" , Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            } else {
                Toast.makeText(context, "Spiacenti, nessun codice trovato. Riprova", Toast.LENGTH_LONG).show();

            }
        }else{
            Toast.makeText(context, "Spiacenti, nessun codice trovato. Riprova", Toast.LENGTH_LONG).show();
        }
    }

}