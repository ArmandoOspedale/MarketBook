package com.example.armando.marketbook;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


public class downloadFragment extends Fragment {

    public static downloadFragment newInstance() {
        downloadFragment fragment = new downloadFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.download_layout, container, false);
        final RecyclerView downloads = rootView.findViewById(R.id.downloads);
        //prendere tutti i libri
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
                    DownloadAdapter adapter = new DownloadAdapter(getContext(),list);
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
        return rootView;
    }
}