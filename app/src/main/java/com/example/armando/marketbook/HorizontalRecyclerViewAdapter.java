package com.example.armando.marketbook;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

/**
 * Per creare un'adapter utilizzabile da RecyclerView, è necessario estendere RecyclerView.Adapter
    Stesso pattern di progettazione di view holder, ovvero è possibile definire una classe personalizzata che estende
    RecyclerView.ViewHolder
 */
public class HorizontalRecyclerViewAdapter extends RecyclerView.Adapter<HorizontalRecyclerViewAdapter.MyView> {

    private List<Book> books; //Libri contenuti all'interno di una categoria
    private List<String> lista;
    private OnItemClickListener mItemClickListener;
    private Context context;
    private int lastPosition = -1;
    private boolean animation;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    public class MyView extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView Titolo;
        private TextView Autore;
        private ImageView Copertina;
        private ProgressBar ProgressBar;
        private TextView Prezzo;
        private View Immagine;

        MyView(View view) {
            super(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

            // Inizializzo le visualizzazioni che appartengono alla RecyclerView Orinzontale
            Copertina = view.findViewById(R.id.Copertina);
            Titolo = view.findViewById(R.id.titololibro);
            Autore = view.findViewById(R.id.autoreLibro);
            ProgressBar = view.findViewById(R.id.progressBar);
            Immagine = view.findViewById(R.id.cardview);
            Prezzo = view.findViewById(R.id.Autore);
        }

        @Override
        public void onClick(final View view) {
            if (mItemClickListener != null) {
                final List<View> shared = new ArrayList<>();
                final HashMap<String,String> transitionName= new HashMap<>();
                shared.add(Copertina);
                transitionName.put(Copertina.getResources().getResourceName(Copertina.getId()),Copertina.getTransitionName());
                //shared.add(Titolo);
                //transitionName.put(Titolo.getResources().getResourceName(Titolo.getId()),Titolo.getTransitionName());
                //shared.add(Autore);
                //transitionName.put(Autore.getResources().getResourceName(Autore.getId()),Autore.getTransitionName());
                shared.add(Immagine);
                transitionName.put(Immagine.getResources().getResourceName(Immagine.getId()),Immagine.getTransitionName());
                mItemClickListener.onItemClick(view, books.get(getLayoutPosition()),shared,transitionName);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemLongClick(view, books.get(getLayoutPosition()));
                return true;
            }
            return false;
        }

    }

    // Costruttore personalizzato, puntatore per i dati visualizzati
    HorizontalRecyclerViewAdapter(List<Book> horizontalList, Context mcontext) {
        this.books = horizontalList;
        this.context = mcontext;
        this.animation=false;
        this.lista=null;
        storage = FirebaseStorage.getInstance();
    }

    HorizontalRecyclerViewAdapter(List<Book> horizontalList, Context mcontext, boolean animation) {
        this.books = horizontalList;
        this.context = mcontext;
        this.animation = animation;
        this.lista=null;
        storage = FirebaseStorage.getInstance();
    }

    HorizontalRecyclerViewAdapter(List<String> list, Context mcontext, boolean animation , boolean x) {
        this.lista = list;
        this.books = new ArrayList<>();
        for (int i = 0; i < lista.size(); i++) books.add(null);
        this.context = mcontext;
        this.animation = animation;
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

    }

    //Viene chiamato quando ViewHolder personalizzato ha bisogno di essere inizializzato
    //Specifico il layout che ogni elemento del RecyclerView deve utilizzare
    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_item, parent, false);
        return new MyView(itemView);
    }

    //Specifica il contenuto di ciascun elemento della RecyclerView
    @Override
    public void onBindViewHolder(@NonNull final MyView holder, final int position) {
        if(lista==null){
            final Book oggetto = books.get(position);
            holder.Titolo.setText(oggetto.getTitolo());
            holder.Autore.setText(oggetto.getAutore());
            holder.Prezzo.setText(String.valueOf(oggetto.getPrezzo()) + "\u20ac");
            ViewCompat.setTransitionName(holder.Immagine,oggetto.getIDAutore());
            ViewCompat.setTransitionName(holder.Copertina,oggetto.getURLCopertina());
            StorageReference storageRef = storage.getReferenceFromUrl("gs://books-c7269.appspot.com").child(oggetto.getURLCopertina());
            try {
                final File localFile = File.createTempFile((String) oggetto.getTitolo(), "png");
                storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
                        bitmap = Bitmap.createScaledBitmap(bitmap, Math.round(120 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)),Math.round(160 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)),true);
                        holder.Copertina.setImageBitmap(bitmap);
                        holder.ProgressBar.setVisibility(View.GONE);
                        oggetto.setPath(localFile.getAbsolutePath());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                });
            } catch (IOException e ) {
                holder.Copertina.setImageResource(R.drawable.ic_image_black_24dp);
            }
        }else{
            db.collection("Libri").document(lista.get(position)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            final Book book = documentSnapshot.toObject(Book.class);
                            book.setRiferimento(documentSnapshot.getReference().getPath());
                            books.set(position, book);
                            holder.Titolo.setText(book.getTitolo());
                            holder.Autore.setText(book.getAutore());
                            holder.Prezzo.setText(String.valueOf(book.getPrezzo()) + "\u20ac");
                            ViewCompat.setTransitionName(holder.Immagine,book.getIDAutore());
                            ViewCompat.setTransitionName(holder.Copertina,book.getURLCopertina());
                            StorageReference storageRef = storage.getReferenceFromUrl("gs://books-c7269.appspot.com").child(book.getURLCopertina());
                            try {
                                final File localFile = File.createTempFile((String) book.getTitolo(), "png");
                                storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
                                        bitmap = Bitmap.createScaledBitmap(bitmap, Math.round(120 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)),Math.round(160 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)),true);
                                        holder.Copertina.setImageBitmap(bitmap);
                                        holder.ProgressBar.setVisibility(View.GONE);
                                        book.setPath(localFile.getAbsolutePath());
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                    }
                                });
                            } catch (IOException e ) {
                                holder.Copertina.setImageResource(R.drawable.ic_image_black_24dp);
                            }
                        } else {
                            Log.d("Documento", "Documento non trovato");
                        }
                    }
                }
            });
        }
        if(animation){
            setAnimation(holder.itemView,position);
        }
    }

    // Restituisce il numero di elementi presenti nei dati
    @Override
    public int getItemCount() {
        if (lista!=null){
            return  lista.size();
        } else {
            return books.size();
        }
    }

    // La recyclerView non ha setOnItemClickListener quindi bisogna implementarlo
    // Interfaccia della chiamata OnItemClickListener
    public interface OnItemClickListener {
        void onItemClick(View view, Book book , List<View> shared, HashMap<String,String> transitionName);
        void onItemLongClick(View view, Book book);
    }

    // Per pressione lunga e corta
    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.item);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

}