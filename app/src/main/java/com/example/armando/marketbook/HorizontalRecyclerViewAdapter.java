package com.example.armando.marketbook;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

// Per creare un'adapter utilizzabile da RecyclerView, è necessario estendere RecyclerView.Adapter
// Stesso pattern di progettazione di view holder, ovvero è possibile definire una classe personalizzata che estende
// RecyclerView.ViewHolder
public class HorizontalRecyclerViewAdapter extends RecyclerView.Adapter<HorizontalRecyclerViewAdapter.MyView> {

    private List<Items> book; //Libri contenuti all'interno di una categoria
    private OnItemClickListener mItemClickListener;

    public class MyView extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView Titolo;
        private TextView Autore;
        private CardView cv;

        MyView(View view) {
            super(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

            // Inizializzo le visualizzazioni che appartengono alla RecyclerView Orinzontale
            cv = view.findViewById(R.id.cardview);
            Titolo = view.findViewById(R.id.Titolo);
            Autore = view.findViewById(R.id.Autore);
        }

        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(view, book.get(getLayoutPosition()));
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemLongClick(view, getLayoutPosition());
                return true;
            }
            return false;
        }

    }

    // Costruttore personalizzato, puntatore per i dati visualizzati
    HorizontalRecyclerViewAdapter(List<Items> horizontalList) {
        this.book = horizontalList;
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
        holder.Titolo.setText(book.get(position).getTitolo());
        holder.Autore.setText(book.get(position).getAutore());
    }

    // Restituisce il numero di elementi presenti nei dati
    @Override
    public int getItemCount() {
        return book.size();
    }

    // La recyclerView non ha setOnItemClickListener quindi bisogna implementarlo
    // Interfaccia della chiamata OnItemClickListener
    public interface OnItemClickListener {
        void onItemClick(View view, Items book);
        void onItemLongClick(View view, int position);
    }

    // Per pressione lunga e corta
    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

}