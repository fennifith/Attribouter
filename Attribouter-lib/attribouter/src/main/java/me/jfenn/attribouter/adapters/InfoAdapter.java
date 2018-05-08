package me.jfenn.attribouter.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import me.jfenn.attribouter.data.info.InfoData;

public class InfoAdapter extends RecyclerView.Adapter<InfoData.ViewHolder> {

    private List<InfoData> infos;

    public InfoAdapter(List<InfoData> infos) {
        this.infos = infos;
    }

    @NonNull
    @Override
    public InfoData.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        InfoData info = infos.get(viewType);
        return info.getViewHolder(LayoutInflater.from(parent.getContext()).inflate(info.getLayoutRes(), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull InfoData.ViewHolder holder, int position) {
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
