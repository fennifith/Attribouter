package me.jfenn.attribouter.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import me.jfenn.attribouter.wedges.Wedge;

public class WedgeAdapter extends RecyclerView.Adapter<Wedge.ViewHolder> {

    private List<Wedge> wedges;

    public WedgeAdapter(List<Wedge> wedges) {
        this.wedges = wedges;
    }

    @NonNull
    @Override
    public Wedge.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Wedge info = wedges.get(viewType);
        return info.getViewHolder(LayoutInflater.from(parent.getContext()).inflate(info.getLayoutRes(), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Wedge.ViewHolder holder, int position) {
        wedges.get(position).bind(holder.itemView.getContext(), holder);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return wedges.size();
    }

}
