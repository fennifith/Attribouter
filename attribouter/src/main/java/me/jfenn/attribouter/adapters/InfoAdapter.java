package me.jfenn.attribouter.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import me.jfenn.attribouter.wedges.Wedge;

public class InfoAdapter extends RecyclerView.Adapter<Wedge.ViewHolder> {

    private List<Wedge> infos;

    public InfoAdapter(List<Wedge> infos) {
        this.infos = infos;
    }

    @NonNull
    @Override
    public Wedge.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Wedge info = infos.get(viewType);
        return info.getViewHolder(LayoutInflater.from(parent.getContext()).inflate(info.getLayoutRes(), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Wedge.ViewHolder holder, int position) {
        infos.get(position).bind(holder.itemView.getContext(), holder);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return infos.size();
    }

}
