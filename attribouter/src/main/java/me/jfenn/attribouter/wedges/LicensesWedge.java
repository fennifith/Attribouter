package me.jfenn.attribouter.wedges;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.view.View;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.jfenn.attribouter.R;
import me.jfenn.attribouter.adapters.InfoAdapter;
import me.jfenn.attribouter.data.github.GitHubData;
import me.jfenn.attribouter.data.github.LicenseData;
import me.jfenn.attribouter.data.github.RepositoryData;
import me.jfenn.attribouter.dialogs.OverflowDialog;
import me.jfenn.attribouter.utils.ResourceUtils;

public class LicensesWedge extends Wedge<LicensesWedge.ViewHolder> {

    @Nullable
    private String title;
    private int overflow;

    public LicensesWedge(XmlResourceParser parser) throws XmlPullParserException, IOException {
        super(R.layout.item_attribouter_licenses);
        title = parser.getAttributeValue(null, "title");
        if (title == null)
            title = "@string/title_attribouter_licenses";
        boolean showDefaults = parser.getAttributeBooleanValue(null, "showDefaults", true);
        overflow = parser.getAttributeIntValue(null, "overflow", -1);

        addChildren(parser);

        if (showDefaults) {
            addChild(new LicenseWedge(
                            "fennifith/Attribouter",
                            "Attribouter",
                            "A lightweight \"about screen\" library to allow quick but customizable attribution in Android apps.",
                            "Apache License 2.0",
                            null,
                            "https://github.com/fennifith/Attribouter",
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            "apache-2.0"));
            addChild(new LicenseWedge(
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
                            "apache-2.0"));
            addChild(new LicenseWedge(
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
                            "apache-2.0"));
            addChild(new LicenseWedge(
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
                            null));
            addChild(new LicenseWedge(
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
                            "apache-2.0"));
        }
    }

    @Override
    public void onInit(GitHubData data) {
        if (data instanceof RepositoryData) {
            RepositoryData repo = (RepositoryData) data;
            for (String tag : repo.getTags()) {
                LicenseWedge mergeLicense = new LicenseWedge(
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

                if (getChildren().contains(mergeLicense)) {
                    Wedge info = getChildren().get(getChildren().indexOf(mergeLicense));
                    if (info instanceof LicenseWedge) {
                        LicenseWedge license = (LicenseWedge) info;
                        license.merge(mergeLicense);
                        if (repo.license != null && repo.license.key != null && !license.hasAllLicense()) {
                            LicenseData request = new LicenseData(repo.license.key);
                            request.addTag(tag);
                            addRequest(request);
                        }
                    }

                    break;
                }
            }
        } else if (data instanceof LicenseData) {
            LicenseData license = (LicenseData) data;
            for (Wedge info : getChildren()) {
                if (info instanceof LicenseWedge) {
                    LicenseWedge licenseInfo = (LicenseWedge) info;

                    if (license.getTags().contains(licenseInfo.token)) {
                        licenseInfo.merge(new LicenseWedge(
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
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public void bind(Context context, ViewHolder viewHolder) {
        if (overflow == 0) {
            viewHolder.titleView.setVisibility(View.GONE);
            viewHolder.recycler.setVisibility(View.GONE);
            viewHolder.expand.setVisibility(View.GONE);

            viewHolder.overflow.setVisibility(View.VISIBLE);
            viewHolder.overflow.setText(ResourceUtils.getString(context, title));

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new OverflowDialog(v.getContext(), title, getChildren()).show();
                }
            });
            return;
        } else {
            viewHolder.titleView.setVisibility(View.VISIBLE);
            viewHolder.recycler.setVisibility(View.VISIBLE);
            viewHolder.expand.setVisibility(View.VISIBLE);
            viewHolder.overflow.setVisibility(View.GONE);
            viewHolder.itemView.setOnClickListener(null);
        }

        if (title != null)
            viewHolder.titleView.setText(ResourceUtils.getString(context, title));

        viewHolder.recycler.setLayoutManager(new LinearLayoutManager(context));
        viewHolder.recycler.setAdapter(new InfoAdapter(getChildren().subList(0, overflow > getChildren().size() || overflow < 0 ? getChildren().size() : overflow)));

        if (overflow > 0 && overflow < getChildren().size()) {
            viewHolder.expand.setVisibility(View.VISIBLE);
            viewHolder.expand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new OverflowDialog(v.getContext(), title, getChildren()).show();
                }
            });
        } else viewHolder.expand.setVisibility(View.GONE);
    }

    protected static class ViewHolder extends Wedge.ViewHolder {

        protected TextView titleView;
        protected RecyclerView recycler;
        protected View expand;
        protected TextView overflow;

        protected ViewHolder(View v) {
            super(v);
            titleView = v.findViewById(R.id.title);
            recycler = v.findViewById(R.id.recycler);
            expand = v.findViewById(R.id.expand);
            overflow = v.findViewById(R.id.overflow);
        }
    }
}
