package com.example.armando.marketbook;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private OnItemClickListener mItemClickListener;
    private Context context;

    public class MyView extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView Titolo;
        private TextView Autore;
        private ImageView Copertina;
        private ProgressBar ProgressBar;
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
        }

        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                List<View> shared = new ArrayList<>();
                HashMap<String,String> transitionName= new HashMap<>();
                shared.add(Copertina);
                transitionName.put(Copertina.getResources().getResourceName(Copertina.getId()),Copertina.getTransitionName());
                shared.add(Titolo);
                transitionName.put(Titolo.getResources().getResourceName(Titolo.getId()),Titolo.getTransitionName());
                shared.add(Autore);
                transitionName.put(Autore.getResources().getResourceName(Autore.getId()),Autore.getTransitionName());
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

        final Book oggetto = books.get(position);
        holder.Titolo.setText(oggetto.getTitolo());
        holder.Autore.setText(oggetto.getAutore());
        ViewCompat.setTransitionName(holder.Immagine,oggetto.getGenere());
        ViewCompat.setTransitionName(holder.Titolo,oggetto.getTitolo());
        ViewCompat.setTransitionName(holder.Autore,oggetto.getAutore());
        ViewCompat.setTransitionName(holder.Copertina,oggetto.getURLCopertina());

        // Creo istanza di FirebaseStorage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://books-c7269.appspot.com").child(oggetto.getURLCopertina());
        try {
            final File localFile = File.createTempFile((String) oggetto.getTitolo(), "png");
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
                    bitmap = Bitmap.createScaledBitmap(bitmap, Math.round(130 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)),Math.round(170 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)),true);
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
    }

    // Restituisce il numero di elementi presenti nei dati
    @Override
    public int getItemCount() {
        return books.size();
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

}