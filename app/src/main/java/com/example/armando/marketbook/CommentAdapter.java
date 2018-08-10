package com.example.armando.marketbook;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int LAYOUT_ONE= 0;
    private static final int LAYOUT_TWO= 1;
    private String descrizione;
    private List<String> commento;
    private List<String> nomeCommento;
    private OnItemClickListener mItemClickListener;

    public class MyViewComment extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView NomeCommento;
        private TextView Commento;

        MyViewComment(View view) {
            super(view);
            view.setOnClickListener(this);
            NomeCommento = view.findViewById(R.id.nomeCommento);
            Commento = view.findViewById(R.id.commento);
        }

        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(view, getLayoutPosition());
            }
        }

    }

    public class MyViewDescrizione extends RecyclerView.ViewHolder{

        private ExpandableTextView expandableTextView;
        private TextView altreInfo;
        private ImageView freccia;

        MyViewDescrizione(View view) {
            super(view);
            expandableTextView = view.findViewById(R.id.expand_text_view);
            altreInfo = view.findViewById(R.id.altreInfo);
            freccia = view.findViewById(R.id.expand_collapse);
        }
    }

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

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if(holder.getItemViewType()== LAYOUT_ONE)
        {
            final MyViewDescrizione myViewDescrizione = (MyViewDescrizione) holder;
            myViewDescrizione.expandableTextView.setText(descrizione);
            myViewDescrizione.freccia.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
            myViewDescrizione.expandableTextView.setOnExpandStateChangeListener(new ExpandableTextView.OnExpandStateChangeListener() {
                @Override
                public void onExpandStateChanged(TextView textView, boolean isExpanded) {
                    if(isExpanded){
                        myViewDescrizione.altreInfo.setText(R.string.riduci);
                        myViewDescrizione.freccia.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    } else {
                        myViewDescrizione.altreInfo.setText(R.string.altre_info);
                        myViewDescrizione.freccia.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    }
                }
            });
        } else {
            MyViewComment myViewComment = (MyViewComment) holder;
            myViewComment.NomeCommento.setText(nomeCommento.get(position-1));
            myViewComment.Commento.setText(commento.get(position-1));
        }
    }

    @Override
    public int getItemCount() {
        return commento.size()+1;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

}