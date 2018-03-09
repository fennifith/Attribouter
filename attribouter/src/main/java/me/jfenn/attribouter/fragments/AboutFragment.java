package me.jfenn.attribouter.fragments;

import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import me.jfenn.attribouter.data.info.AppInfoData;
import me.jfenn.attribouter.data.info.ContributorsInfoData;
import me.jfenn.attribouter.data.info.InfoData;
import me.jfenn.attribouter.data.info.LicensesInfoData;
import me.jfenn.attribouter.data.info.TextInfoData;

public class AboutFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerView recycler = (RecyclerView) inflater.inflate(R.layout.fragment_attribouter_about, container, false);

        List<InfoData> infos = new ArrayList<>();
        String repo = null;

        Bundle args = getArguments();
        if (args != null && args.containsKey(Attribouter.EXTRA_FILE_RES)) {
            XmlResourceParser parser = getResources().getXml(args.getInt(Attribouter.EXTRA_FILE_RES, -1));
            try {
                while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                    if (parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals("about")) {
                        repo = parser.getAttributeValue(null, "repo");
                    } else if (parser.getEventType() == XmlPullParser.START_TAG) {
                        switch (parser.getName()) {
                            case "appInfo":
                                infos.add(new AppInfoData(parser));
                                break;
                            case "contributors":
                                infos.add(new ContributorsInfoData(parser));
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
        } else {
            //TODO: throw exception or something
        }

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(new InfoAdapter(infos));

        return recycler;
    }
}
