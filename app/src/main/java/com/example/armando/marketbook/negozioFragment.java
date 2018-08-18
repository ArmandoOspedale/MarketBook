package com.example.armando.marketbook;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

public class negozioFragment extends Fragment {

    private RecyclerView mRecyclerView;


    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public static negozioFragment newInstance() {
        negozioFragment fragment = new negozioFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.negozio_layout, container, false);
        setupRecyclerView(rootView);
        ricercaLibri();
        return rootView;
    }

    private void setupRecyclerView(View view) {
        mRecyclerView = view.findViewById(R.id.vertical_courses_list);
        RecyclerView.LayoutManager RecyclerViewLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(RecyclerViewLayoutManager);
        mRecyclerView.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) { }

            @Override
            public int getItemCount() {
                return 0;
            }
        });
    }

    private void ricercaLibri() {
        db = FirebaseFirestore.getInstance();
        //Query al Database : Ottengo tutti i documenti
        db.collection("Index").document("sZYBGqY7z7gXeFEjodCW").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    List<Object> list = (List<Object>) documentSnapshot.get("Sezioni");
                    VerticalRecyclerViewAdapter adapter = new VerticalRecyclerViewAdapter(list,getContext());
                    mRecyclerView.setAdapter(adapter);
                    adapter.SetOnItemClickListener(new HorizontalRecyclerViewAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, Book book, List<View> shared, HashMap<String,String> transitionName) {
                            //Ottengo dall'adapter il libro > passare alla activity specifica per libro
                            //Creo l'intent per passare all'altra activity
                            Intent intent = new Intent(getActivity(),BookActivity.class);
                            //setIntent(intent);
                            intent.putExtra("LIBRO",book);
                            intent.putExtra("TRANSITION_NAME",transitionName);
                            View statusBar = getActivity().findViewById(android.R.id.statusBarBackground);
                            View navigationBar = getActivity().findViewById(android.R.id.navigationBarBackground);
                            View toolbar = getActivity().findViewById(R.id.toolbar2);
                            List<Pair<View, String>> pairs = new ArrayList<>();
                            if (statusBar != null) {
                                pairs.add(Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME));
                            }
                            if (navigationBar != null) {
                                pairs.add(Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME));
                            }
                            if (toolbar != null) {
                                pairs.add(Pair.create(toolbar, toolbar.getTransitionName()));
                            }
                            for (int i=0;i<shared.size();i++){
                                pairs.add(Pair.create(shared.get(i), shared.get(i).getTransitionName()));
                            }
                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity(),pairs.toArray(new Pair[pairs.size()])).toBundle());
                        }

                        //Al momento non è implementata la pressione prolungata -> aggiungi ai desiderati
                        public void onItemLongClick(View view, Book book) {

                        }
                    });
                }
            }
        });
    }

}