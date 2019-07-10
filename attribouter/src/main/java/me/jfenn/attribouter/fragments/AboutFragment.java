package me.jfenn.attribouter.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import me.jfenn.attribouter.Attribouter;
import me.jfenn.attribouter.R;
import me.jfenn.attribouter.adapters.InfoAdapter;
import me.jfenn.attribouter.data.github.GitHubData;
import me.jfenn.attribouter.provider.wedge.XMLWedgeProvider;
import me.jfenn.attribouter.wedges.Wedge;

public class AboutFragment extends Fragment implements GitHubData.OnInitListener, Wedge.OnRequestListener {

    private RecyclerView recycler;
    private InfoAdapter adapter;

    private List<Wedge> wedges;
    private List<GitHubData> requests;
    private String gitHubToken;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        recycler = (RecyclerView) inflater.inflate(R.layout.fragment_attribouter_about, container, false);

        Bundle args = getArguments();
        int fileRes = R.xml.attribouter;
        if (args != null) {
            gitHubToken = args.getString(Attribouter.EXTRA_GITHUB_OAUTH_TOKEN, null);
            fileRes = args.getInt(Attribouter.EXTRA_FILE_RES, fileRes);
        }

        wedges = new XMLWedgeProvider(fileRes).getWedges(getContext());

        adapter = new InfoAdapter(wedges);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.addItemDecoration(new DividerItemDecoration(recycler.getContext(), DividerItemDecoration.VERTICAL));
        recycler.setAdapter(adapter);

        requests = new ArrayList<>();
        for (Wedge info : wedges) {
            info.setOnRequestListener(this);
        }

        for (GitHubData request : requests) {
            request.addOnInitListener(this);
            request.startInit(getContext(), gitHubToken);
        }

        return recycler;
    }

    @Override
    public void onInit(GitHubData data) {
        for (int i = 0; i < wedges.size(); i++) {
            if (wedges.get(i).hasRequest(data))
                adapter.notifyItemChanged(i);
            else notifyChildren(i, wedges.get(i).getChildren(), data);
        }

        recycler.smoothScrollToPosition(0);
    }

    private void notifyChildren(int index, List<Wedge> children, GitHubData data) {
        if (children.size() < 1)
            return;

        for (Wedge child : children) {
            if (child.hasRequest(data)) {
                adapter.notifyItemChanged(index);
                return;
            }

            notifyChildren(index, child.getChildren(), data);
        }
    }

    @Override
    public void onFailure(GitHubData data) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (requests != null) {
            for (GitHubData request : requests)
                request.interruptThread();
        }
    }

    @Override
    public void onRequest(Wedge info, GitHubData request) {
        if (!requests.contains(request)) {
            requests.add(request);
            request.addOnInitListener(this);

            Context context = getContext();
            if (context != null)
                request.startInit(context, gitHubToken);
        } else {
            int i = requests.indexOf(request);
            GitHubData activeRequest = requests.get(i).merge(request);
            if (activeRequest.isInitialized())
                info.onInit(activeRequest);
        }
    }
}
