package com.example.armando.marketbook;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

public class VerticalRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ArrayList<Book>> books;
    private ArrayList<String> categoria;
    private SparseIntArray listPosition = new SparseIntArray();
    private HorizontalRecyclerViewAdapter.OnItemClickListener mItemClickListener;
    private Context mContext;

    private class CellViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView HorizontalRecyclerView;
        private TextView Categoria;

        CellViewHolder(View itemView) {
            super(itemView);
            HorizontalRecyclerView = itemView.findViewById(R.id.recyclerview1);
            Categoria = itemView.findViewById(R.id.Categoria);
        }
    }

    VerticalRecyclerViewAdapter(ArrayList<ArrayList<Book>> verticalList, ArrayList<String> nomeCategoria, Context context) {
        this.books = verticalList;
        this.categoria = nomeCategoria;
        this.mContext = context;
    }

    @NonNull
    @Override
    public CellViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_layout, parent, false);
        return new CellViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CellViewHolder cellViewHolder = (CellViewHolder) holder;
        cellViewHolder.HorizontalRecyclerView.setHasFixedSize(true);
        LinearLayoutManager RecyclerViewLayout = new LinearLayoutManager(mContext);
        RecyclerViewLayout.setOrientation(LinearLayoutManager.HORIZONTAL);
        cellViewHolder.HorizontalRecyclerView.setLayoutManager( RecyclerViewLayout);
        HorizontalRecyclerViewAdapter adapter = new HorizontalRecyclerViewAdapter(books.get(position),mContext);
        cellViewHolder.HorizontalRecyclerView.setAdapter(adapter);
        cellViewHolder.Categoria.setText(categoria.get(position));
        adapter.SetOnItemClickListener(mItemClickListener);
        int lastSeenFirstPosition = listPosition.get(position, 0);
        if (lastSeenFirstPosition >= 0) {
            cellViewHolder.HorizontalRecyclerView.scrollToPosition(lastSeenFirstPosition);
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder viewHolder) {
        final int position = viewHolder.getAdapterPosition();
        CellViewHolder cellViewHolder = (CellViewHolder) viewHolder;
        LinearLayoutManager layoutManager = ((LinearLayoutManager) cellViewHolder.HorizontalRecyclerView.getLayoutManager());
        int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
        listPosition.put(position, firstVisiblePosition);
        super.onViewRecycled(viewHolder);
    }

    @Override
    public int getItemCount() {
        if (books == null)
            return 0;
        return books.size();
    }

    public void SetOnItemClickListener(final HorizontalRecyclerViewAdapter.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

}