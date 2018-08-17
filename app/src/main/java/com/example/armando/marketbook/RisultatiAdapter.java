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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RisultatiAdapter extends RecyclerView.Adapter<RisultatiAdapter.MyView> {

    private List<Object> risultati;
    private Context context;
    private OnItemClickListener mItemClickListener;
    private File directory;

    public class MyView extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView Titolo;
        private TextView Prezzo;
        private ImageView Immagine;
        private ProgressBar progressBar;

        MyView(View view) {
            super(view);
            view.setOnClickListener(this);
            Titolo = view.findViewById(R.id.Nome_Titolo);
            Immagine = view.findViewById(R.id.Immagine);
            Prezzo = view.findViewById(R.id.Autore);
            progressBar = view.findViewById(R.id.progressBar);
        }

        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                List<View> shared = new ArrayList<>();
                HashMap<String,String> transitionName= new HashMap<>();
                //shared.add(Immagine);
                //transitionName.put(Immagine.getResources().getResourceName(Immagine.getId()),Immagine.getTransitionName());
                mItemClickListener.onItemClick(view, risultati.get(getLayoutPosition()),shared,transitionName);
            }
        }
    }

    RisultatiAdapter(Context context, List<Object> risultati) {
        this.risultati = risultati;
        this.context = context;
        directory = context.getDir("immagini", Context.MODE_PRIVATE);
        if (!directory.exists())
        {
            directory.mkdirs();
        }
    }

    RisultatiAdapter(Context context){
        directory = context.getDir("immagini", Context.MODE_PRIVATE);
        if (!directory.exists())
        {
            directory.mkdirs();
        }
    }

    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.riga, parent, false);
        return new MyView(itemView);
    }

    //Specifica il contenuto di ciascun elemento della RecyclerView
    @Override
    public void onBindViewHolder(@NonNull final MyView holder, final int position) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef;
        final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        try {
            Book oggetto = (Book) risultati.get(position);
            ViewCompat.setTransitionName(holder.Immagine,oggetto.getIDAutore());
            holder.Titolo.setText(oggetto.getTitolo());
            holder.Prezzo.setText(String.valueOf(oggetto.getPrezzo()) + "\u20ac");
            storageRef = storage.getReferenceFromUrl("gs://books-c7269.appspot.com").child(oggetto.getURLCopertina());
            final File file = new File(directory.getAbsolutePath(), (String) oggetto.getTitolo() + ".png");
            if (!file.exists()) {
                storageRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        bitmap = Bitmap.createScaledBitmap(bitmap, Math.round(48 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)), Math.round(64 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)), true);
                        holder.Immagine.setImageBitmap(bitmap);
                        holder.progressBar.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        exception.printStackTrace();
                    }
                });
            } else {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                bitmap = Bitmap.createScaledBitmap(bitmap, Math.round(48 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)), Math.round(64 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)), true);
                holder.Immagine.setImageBitmap(bitmap);
                holder.progressBar.setVisibility(View.GONE);
            }
        } catch (Exception e) {

        }
        try {
            Autore oggetto = (Autore) risultati.get(position);
            ViewCompat.setTransitionName(holder.Immagine,oggetto.getUrlImmagine());
            holder.Titolo.setText(oggetto.getNome());
            storageRef = storage.getReferenceFromUrl("gs://books-c7269.appspot.com").child(oggetto.getUrlImmagine());
            final File file = new File(directory.getAbsolutePath(), (String) oggetto.getNome() + ".png");
            if (!file.exists()) {
                storageRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        bitmap = Bitmap.createScaledBitmap(bitmap, Math.round(64 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)), Math.round(64 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)), true);
                        holder.Immagine.setImageBitmap(bitmap);
                        holder.progressBar.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            exception.printStackTrace();
                        }
                });
            } else {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                bitmap = Bitmap.createScaledBitmap(bitmap, Math.round(64 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)), Math.round(64 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)), true);
                holder.Immagine.setImageBitmap(bitmap);
                holder.progressBar.setVisibility(View.GONE);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public int getItemCount() {
        return (risultati.size());
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Object object ,List<View> shared, HashMap<String,String> transitionName);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void destroy(){
        File file = new File(directory.getAbsolutePath());
        String[] files;
        files = file.list();
        for (int i=0; i<files.length; i++) {
            File myFile = new File(file, files[i]);
            myFile.delete();
        }
    }

}