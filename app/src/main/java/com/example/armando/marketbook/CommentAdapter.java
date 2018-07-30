package com.example.armando.marketbook;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.List;


// Per creare un'adapter utilizzabile da RecyclerView, è necessario estendere RecyclerView.Adapter
// Stesso pattern di progettazione di view holder, ovvero è possibile definire una classe personalizzata che estende
// RecyclerView.ViewHolder

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int LAYOUT_ONE= 0;
    private static final int LAYOUT_TWO= 1;
    private String descrizione; //Descrizione libro
    private List<String> commento; //Commenti
    private List<String> nomeCommento; //Commenti
    private OnItemClickListener mItemClickListener;

    public class MyViewComment extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView NomeCommento;
        private TextView Commento;

        MyViewComment(View view) {

            super(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

            // Inizializzo le visualizzazioni che appartengono alla RecyclerView Orinzontale
            NomeCommento = view.findViewById(R.id.nomeCommento);
            Commento = view.findViewById(R.id.commento);

        }

        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(view, getLayoutPosition());
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

    public class MyViewDescrizione extends RecyclerView.ViewHolder{
        private ExpandableTextView expandableTextView;

        MyViewDescrizione(View view) {
            super(view);

            // Inizializzo le visualizzazioni che appartengono alla RecyclerView Orinzontale
            expandableTextView = view.findViewById(R.id.expand_text_view);

        }
    }

    // Costruttore personalizzato, puntatore per i dati visualizzati
    CommentAdapter(List<String> commento, List<String> Nomecommento, String Descrizione) {
        this.commento = commento;
        this.nomeCommento = Nomecommento;
        this.descrizione = Descrizione;
    }

    @Override
    public int getItemViewType(int position)
    {
        if(position==0)
            return LAYOUT_ONE;
        else
            return LAYOUT_TWO;
    }


    //Viene chiamato quando ViewHolder personalizzato ha bisogno di essere inizializzato
    //Specifico il layout che ogni elemento del RecyclerView deve utilizzare
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =null;
        RecyclerView.ViewHolder viewHolder = null;
        if(viewType==LAYOUT_ONE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.descrizione_libro,parent,false);
            viewHolder = new MyViewDescrizione(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_section,parent,false);
            viewHolder= new MyViewComment(view);
        }
        return viewHolder;
    }

    //Specifica il contenuto di ciascun elemento della RecyclerView
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if(holder.getItemViewType()== LAYOUT_ONE)
        {
            MyViewDescrizione myViewDescrizione = (MyViewDescrizione) holder;
            myViewDescrizione.expandableTextView.setText(descrizione);

        }
        else {
            MyViewComment myViewComment = (MyViewComment) holder;
            myViewComment.NomeCommento.setText(nomeCommento.get(position-1));
            myViewComment.Commento.setText(commento.get(position-1));
        }

    }

    // Restituisce il numero di elementi presenti nei dati
    @Override
    public int getItemCount() {
        return commento.size()+1;
    }

    // La recyclerView non ha setOnItemClickListener quindi bisogna implementarlo
    // Interfaccia della chiamata OnItemClickListener
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }

    // Per pressione lunga e corta
    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

}