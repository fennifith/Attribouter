package me.jfenn.attribouter.fragments;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import me.jfenn.attribouter.Attribouter;
import me.jfenn.attribouter.R;
import me.jfenn.attribouter.adapters.InfoAdapter;
import me.jfenn.attribouter.data.github.GitHubData;
import me.jfenn.attribouter.wedges.AppWedge;
import me.jfenn.attribouter.wedges.ContributorsWedge;
import me.jfenn.attribouter.wedges.Wedge;
import me.jfenn.attribouter.wedges.LicensesWedge;
import me.jfenn.attribouter.wedges.TextWedge;
import me.jfenn.attribouter.wedges.TranslatorsWedge;

public class AboutFragment extends Fragment implements GitHubData.OnInitListener, Wedge.OnRequestListener {

    private RecyclerView recycler;
    private InfoAdapter adapter;

    private List<Wedge> infos;
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
                    try {
                        Class<?> classy = Class.forName(parser.getName());
                        Constructor<?> constructor = classy.getConstructor(XmlResourceParser.class);
                        infos.add((Wedge) constructor.newInstance(parser));
                        parser.next();
                        continue;
                    } catch (ClassNotFoundException e) {
                        Log.e("Attribouter", "Class name \"" + parser.getName() + "\" not found - you should probably check your configuration file for typos.");
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        Log.e("Attribouter", "Class \"" + parser.getName() + "\" definitely exists, but doesn't have the correct constructor. Check that you have defined one with a single argument - \'android.content.res.XmlResourceParser\'");
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (java.lang.InstantiationException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (ClassCastException e) {
                        Log.e("Attribouter", "Class \"" + parser.getName() + "\" has been instantiated correctly, but it must extend \'me.jfenn.attribouter.data.info.InfoData\' to be worthy of the great RecyclerView adapter.");
                        e.printStackTrace();
                    }

                    //TODO: here only for backwards compatibility - remove once obsolete
                    switch (parser.getName()) {
                        case "appInfo":
                            infos.add(new AppWedge(parser));
                            break;
                        case "contributors":
                            infos.add(new ContributorsWedge(parser));
                            break;
                        case "translators":
                            infos.add(new TranslatorsWedge(parser));
                            break;
                        case "licenses":
                            infos.add(new LicensesWedge(parser));
                            break;
                        case "text":
                            infos.add(new TextWedge(parser));
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
        for (Wedge info : infos) {
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
            else notifyChildren(i, infos.get(i).getChildren(), data);
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
