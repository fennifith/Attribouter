package me.jfenn.attribouter.fragments;

import android.content.res.XmlResourceParser;
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
import me.jfenn.attribouter.adapters.WedgeAdapter;
import me.jfenn.attribouter.provider.data.RequestProvider;
import me.jfenn.attribouter.provider.data.github.GitHubService;
import me.jfenn.attribouter.provider.wedge.XMLWedgeProvider;
import me.jfenn.attribouter.wedges.Wedge;

public class AboutFragment extends Fragment {

    private RecyclerView recycler;
    private WedgeAdapter adapter;

    private List<Wedge> wedges;
    private String gitHubToken;

    private RequestProvider[] providers;

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

        providers = new RequestProvider[]{
                GitHubService.Companion.withToken(gitHubToken).create()
        };

        wedges = new ArrayList<>();
        XmlResourceParser parser = getResources().getXml(fileRes);
        wedges.addAll(new XMLWedgeProvider(parser).getAllWedges());
        parser.close();

        adapter = new WedgeAdapter(wedges);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.addItemDecoration(new DividerItemDecoration(recycler.getContext(), DividerItemDecoration.VERTICAL));
        recycler.setAdapter(adapter);

        return recycler;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        for (RequestProvider provider : providers) {
            provider.destroy();
        }
    }
}
