package me.jfenn.attribouter.data.info;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import me.jfenn.attribouter.data.github.GitHubData;

public abstract class InfoData<T extends InfoData.ViewHolder> implements GitHubData.OnInitListener {

    private int layoutRes;
    private List<GitHubData> requests;

    private OnRequestListener listener;

    public InfoData(@LayoutRes int layoutRes) {
        this.layoutRes = layoutRes;
        requests = new ArrayList<>();
    }

    protected void addRequest(GitHubData request) {
        request.addOnInitListener(this);
        requests.add(request);
        if (listener != null)
            listener.onRequest(this, request);
    }

    public final void setOnRequestListener(OnRequestListener listener) {
        this.listener = listener;
    }

    public final List<GitHubData> getRequests() {
        return requests;
    }

    public final boolean hasRequest(GitHubData request) {
        return requests.contains(request);
    }

    public final int getLayoutRes() {
        return layoutRes;
    }

    public abstract T getViewHolder(View v);

    public abstract void bind(Context context, T viewHolder);

    @Override
    public void onInit(GitHubData data) {
    }

    @Override
    public void onFailure(GitHubData data) {
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View v) {
            super(v);
        }

    }

    public interface OnRequestListener {
        void onRequest(InfoData info, GitHubData request);
    }

}
