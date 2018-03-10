package me.jfenn.attribouter.data.info;

import android.content.Context;
import android.content.res.XmlResourceParser;
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

    private String title;
    private List<LicenseInfoData> licenses;

    public LicensesInfoData(XmlResourceParser parser) throws XmlPullParserException, IOException {
        super(R.layout.item_attribouter_licenses);
        title = parser.getAttributeValue(null, "title");
        licenses = new ArrayList<>();

        while (parser.getEventType() != XmlResourceParser.END_TAG || parser.getName().equals("project")) {
            parser.next();
            if (parser.getEventType() == XmlResourceParser.START_TAG && parser.getName().equals("project")) {
                String projectRepo = parser.getAttributeValue(null, "repo");
                String licenseId = parser.getAttributeValue(null, "license");
                LicenseInfoData license = new LicenseInfoData(
                        projectRepo,
                        parser.getAttributeValue(null, "title"),
                        parser.getAttributeValue(null, "description"),
                        parser.getAttributeValue(null, "licenseName"),
                        parser.getAttributeValue(null, "website"),
                        projectRepo != null ? "https://github.com/" + projectRepo : null,
                        parser.getAttributeValue(null, "licenseUrl"),
                        null,
                        null,
                        null,
                        null,
                        parser.getAttributeValue(null, "licenseBody")
                );

                if (projectRepo != null && !license.hasEverythingGeneric())
                    addRequest(new RepositoryData(projectRepo));
                if (licenseId != null && !license.hasEverythingLicense())
                    addRequest(new LicenseData(licenseId));

                if (!licenses.contains(license))
                    licenses.add(license);
                else licenses.get(licenses.indexOf(license)).merge(license);
            }
        }
    }

    @Override
    public void onInit(GitHubData data) {
        if (data instanceof RepositoryData) {
            RepositoryData repo = (RepositoryData) data;
            if (repo.getTag() != null) {
                LicenseInfoData mergeLicense = new LicenseInfoData(
                        repo.getTag(),
                        null,
                        repo.description,
                        repo.license != null ? repo.license.name : null,
                        repo.homepage,
                        "https://github.com/" + repo.getTag(),
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
                        request.setTag(repo.getTag());
                        addRequest(request);
                    }
                }
            }
        } else if (data instanceof LicenseData) {
            LicenseData license = (LicenseData) data;
            if (license.getTag() != null) {
                LicenseInfoData mergeLicense = new LicenseInfoData(
                        license.getTag(),
                        null,
                        null,
                        license.name,
                        null,
                        "https://github.com/" + license.getTag(),
                        license.html_url,
                        license.permissions,
                        license.conditions,
                        license.limitations,
                        license.description,
                        license.body
                );

                if (licenses.contains(mergeLicense))
                    licenses.get(licenses.indexOf(mergeLicense)).merge(mergeLicense);
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
