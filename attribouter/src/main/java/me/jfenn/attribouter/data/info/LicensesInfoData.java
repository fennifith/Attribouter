package me.jfenn.attribouter.data.info;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.jfenn.attribouter.R;
import me.jfenn.attribouter.adapters.InfoAdapter;
import me.jfenn.attribouter.data.github.GitHubData;
import me.jfenn.attribouter.data.github.LicenseData;
import me.jfenn.attribouter.data.github.RepositoryData;
import me.jfenn.attribouter.utils.ResourceUtils;

public class LicensesInfoData extends InfoData<LicensesInfoData.ViewHolder> {

    @Nullable
    private String title;
    private List<LicenseInfoData> licenses;

    public LicensesInfoData(XmlResourceParser parser) throws XmlPullParserException, IOException {
        super(R.layout.item_attribouter_licenses);
        title = parser.getAttributeValue(null, "title");
        licenses = new ArrayList<>();

        while (parser.getEventType() != XmlResourceParser.END_TAG || parser.getName().equals("project")) {
            parser.next();
            if (parser.getEventType() == XmlResourceParser.START_TAG && parser.getName().equals("project")) {
                LicenseInfoData license = new LicenseInfoData(parser);

                if (!licenses.contains(license))
                    licenses.add(license);
                else licenses.get(licenses.indexOf(license)).merge(license);
            }
        }

        licenses.add(new LicenseInfoData(
                "TheAndroidMaster/Attribouter",
                "Attribouter",
                "A lightweight \"about screen\" library to allow quick but customizable attribution in Android apps.",
                "Apache License 2.0",
                null,
                "https://github.com/TheAndroidMaster/Attribouter",
                null,
                null,
                null,
                null,
                null,
                null,
                "apache-2.0"
        ));

        licenses.add(new LicenseInfoData(
                "google/gson",
                "Gson",
                "A Java serialization/deserialization library to convert Java Objects into JSON and back",
                "Apache License 2.0",
                null,
                "https://github.com/google/gson",
                null,
                null,
                null,
                null,
                null,
                null,
                "apache-2.0"
        ));

        licenses.add(new LicenseInfoData(
                "google/flexbox-layout",
                "FlexBox Layout",
                "FlexboxLayout is a library that brings similar capabilities to the CSS Flexible Box Layout to Android.",
                "Apache License 2.0",
                null,
                "https://github.com/google/flexbox-layout",
                null,
                null,
                null,
                null,
                null,
                null,
                "apache-2.0"
        ));

        licenses.add(new LicenseInfoData(
                "bumptech/glide",
                "Glide",
                "An image loading and caching library for Android focused on smooth scrolling",
                "Other",
                "https://bumptech.github.io/glide/",
                "https://github.com/bumptech/glide",
                "https://raw.githubusercontent.com/bumptech/glide/master/LICENSE",
                null,
                null,
                null,
                null,
                null,
                null
        ));

        licenses.add(new LicenseInfoData(
                null,
                "Android Open Source Project",
                "Android is an open source software stack for a wide range of mobile devices and a corresponding open source project led by Google.",
                "Apache License 2.0",
                "https://source.android.com/license",
                "https://github.com/aosp-mirror",
                null,
                null,
                null,
                null,
                null,
                null,
                "apache-2.0"
        ));

        for (LicenseInfoData license : licenses) {
            if (license.repo != null && !license.hasEverythingGeneric())
                addRequest(new RepositoryData(license.repo));
            if (license.licenseKey != null && (license.repo != null || license.title != null) && !license.hasEverythingLicense()) {
                LicenseData request = new LicenseData(license.licenseKey);
                request.addTag(license.token);
                addRequest(request);
            }
        }
    }

    @Override
    public void onInit(GitHubData data) {
        if (data instanceof RepositoryData) {
            RepositoryData repo = (RepositoryData) data;
            for (String tag : repo.getTags()) {
                LicenseInfoData mergeLicense = new LicenseInfoData(
                        tag,
                        null,
                        repo.description,
                        repo.license != null ? repo.license.name : null,
                        repo.homepage,
                        "https://github.com/" + tag,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                );

                if (licenses.contains(mergeLicense)) {
                    LicenseInfoData license = licenses.get(licenses.indexOf(mergeLicense));
                    license.merge(mergeLicense);
                    if (repo.license != null && repo.license.key != null && !license.hasEverythingLicense()) {
                        LicenseData request = new LicenseData(repo.license.key);
                        request.addTag(tag);
                        addRequest(request);
                    }

                    break;
                }
            }
        } else if (data instanceof LicenseData) {
            LicenseData license = (LicenseData) data;
            for (LicenseInfoData licenseInfo : licenses) {
                if (license.getTags().contains(licenseInfo.token)) {
                    licenseInfo.merge(new LicenseInfoData(
                            null,
                            null,
                            null,
                            license.name,
                            null,
                            "https://github.com/" + licenseInfo.repo,
                            license.html_url,
                            license.permissions,
                            license.conditions,
                            license.limitations,
                            license.description,
                            license.body,
                            license.key
                    ));
                }
            }
        }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public void bind(Context context, ViewHolder viewHolder) {
        if (title != null)
            viewHolder.titleView.setText(ResourceUtils.getString(context, title));

        viewHolder.recycler.setLayoutManager(new LinearLayoutManager(context));
        viewHolder.recycler.setAdapter(new InfoAdapter(new ArrayList<InfoData>(licenses)));
    }

    static class ViewHolder extends InfoData.ViewHolder {

        private TextView titleView;
        private RecyclerView recycler;

        ViewHolder(View v) {
            super(v);
            titleView = v.findViewById(R.id.title);
            recycler = v.findViewById(R.id.recycler);
        }
    }
}
