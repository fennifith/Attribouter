package me.jfenn.attribouter.fragments;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.jfenn.attribouter.Attribouter;
import me.jfenn.attribouter.R;
import me.jfenn.attribouter.adapters.InfoAdapter;
import me.jfenn.attribouter.data.github.GitHubData;
import me.jfenn.attribouter.data.info.AppInfoData;
import me.jfenn.attribouter.data.info.ContributorsInfoData;
import me.jfenn.attribouter.data.info.InfoData;
import me.jfenn.attribouter.data.info.LicensesInfoData;
import me.jfenn.attribouter.data.info.TextInfoData;
import me.jfenn.attribouter.data.info.TranslatorsInfoData;

public class AboutFragment extends Fragment implements GitHubData.OnInitListener, InfoData.OnRequestListener {

    private RecyclerView recycler;
    private InfoAdapter adapter;

    private List<InfoData> infos;
    private List<GitHubData> requests;
    private String gitHubToken;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        recycler = (RecyclerView) inflater.inflate(R.layout.fragment_attribouter_about, container, false);

        infos = new ArrayList<>();

        Bundle args = getArguments();
        int fileRes = R.xml.attribouter;
        if (args != null) {
            gitHubToken = args.getString(Attribouter.EXTRA_GITHUB_OAUTH_TOKEN, null);
            fileRes = args.getInt(Attribouter.EXTRA_FILE_RES, fileRes);
        }

        XmlResourceParser parser = getResources().getXml(fileRes);
        try {
            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlPullParser.START_TAG) {
                    switch (parser.getName()) {
                        case "appInfo":
                            infos.add(new AppInfoData(parser));
                            break;
                        case "contributors":
                            infos.add(new ContributorsInfoData(parser));
                            break;
                        case "translators":
                            infos.add(new TranslatorsInfoData(parser));
                            break;
                        case "licenses":
                            infos.add(new LicensesInfoData(parser));
                            break;
                        case "text":
                            infos.add(new TextInfoData(parser));
                            break;
                    }
                }
                parser.next();
            }
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }

        parser.close();


        adapter = new InfoAdapter(infos);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.addItemDecoration(new DividerItemDecoration(recycler.getContext(), DividerItemDecoration.VERTICAL));
        recycler.setAdapter(adapter);

        requests = new ArrayList<>();
        for (InfoData info : infos) {
            for (GitHubData request : (List<GitHubData>) info.getRequests()) {
                if (!requests.contains(request))
                    requests.add(request);
                else {
                    int i = requests.indexOf(request);
                    requests.set(i, request.merge(requests.get(i)));
                }
            }

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
        for (int i = 0; i < infos.size(); i++) {
            if (infos.get(i).hasRequest(data))
                adapter.notifyItemChanged(i);
        }

        recycler.smoothScrollToPosition(0);
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
    public void onRequest(InfoData info, GitHubData request) {
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
