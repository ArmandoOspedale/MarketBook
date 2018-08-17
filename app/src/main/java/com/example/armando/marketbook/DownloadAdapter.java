package com.example.armando.marketbook;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.MyView> {

    private Context mContext;
    private List<String> Downloads;
    private FirebaseFirestore db;
    private OnItemClickListener mItemClickListener;
    private FirebaseStorage storage;
    private File directory;

    public class MyView extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView Titolo;
        private TextView Autore;
        private ImageView Immagine;
        private ProgressBar progressBar;

        MyView(View view) {
            super(view);
            view.setOnClickListener(this);
            Titolo = view.findViewById(R.id.Nome_Titolo);
            Immagine = view.findViewById(R.id.Immagine);
            Autore = view.findViewById(R.id.Autore);
            progressBar = view.findViewById(R.id.progressBar);
        }

        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(view, new Book());
            }
        }
    }

    DownloadAdapter(Context c , List<String> downloads) {
        this.mContext = c;
        this.Downloads = downloads;
        this.db = FirebaseFirestore.getInstance();
        this.storage = FirebaseStorage.getInstance();
        directory = mContext.getDir("download", Context.MODE_PRIVATE);
        if (!directory.exists())
        {
            directory.mkdirs();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MyView holder, final int position) {
        db.collection("Libri").document(Downloads.get(position)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    Book book = documentSnapshot.toObject(Book.class);
                    holder.Titolo.setText(book.getTitolo());
                    holder.Autore.setText(book.getAutore());
                    final DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
                    StorageReference storageRef = storage.getReferenceFromUrl("gs://books-c7269.appspot.com").child(book.getURLCopertina());
                    final File file = new File(directory.getAbsolutePath(), (String) book.getTitolo() + ".png");
                    if (!file.exists()) {
                        storageRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                                bitmap = Bitmap.createScaledBitmap(bitmap, Math.round(64 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)), Math.round(80 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)), true);
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
                        bitmap = Bitmap.createScaledBitmap(bitmap, Math.round(64 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)), Math.round(80 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)), true);
                        holder.Immagine.setImageBitmap(bitmap);
                        holder.progressBar.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.download_item, parent, false);
        return new MyView(itemView);
    }

    @Override
    public int getItemCount() {
        return (Downloads.size());
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Book book);
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



