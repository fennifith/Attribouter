package me.jfenn.attribouter.data.info;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.jfenn.attribouter.R;
import me.jfenn.attribouter.adapters.InfoAdapter;
import me.jfenn.attribouter.data.info.link.GitHubLinkInfoData;
import me.jfenn.attribouter.data.info.link.LicenseLinkInfoData;
import me.jfenn.attribouter.data.info.link.LinkInfoData;
import me.jfenn.attribouter.data.info.link.WebsiteLinkInfoData;
import me.jfenn.attribouter.utils.ResourceUtils;

public class LicenseInfoData extends InfoData<LicenseInfoData.ViewHolder> {

    @Nullable
    String token;
    @Nullable
    String repo;
    @Nullable
    String title;
    @Nullable
    String description;
    @Nullable
    public String licenseName;
    @Nullable
    String websiteUrl;
    @Nullable
    String gitHubUrl;
    @Nullable
    public String licenseUrl;
    @Nullable
    String[] licensePermissions;
    @Nullable
    String[] licenseConditions;
    @Nullable
    String[] licenseLimitations;
    @Nullable
    public String licenseDescription;
    @Nullable
    public String licenseBody;
    @Nullable
    String licenseKey;
    List<LinkInfoData> links;

    public LicenseInfoData(XmlResourceParser parser) throws IOException, XmlPullParserException {
        this(parser.getAttributeValue(null, "repo"),
                parser.getAttributeValue(null, "title"),
                parser.getAttributeValue(null, "description"),
                parser.getAttributeValue(null, "licenseName"),
                parser.getAttributeValue(null, "website"),
                parser.getAttributeValue(null, "gitHubUrl"),
                parser.getAttributeValue(null, "licenseUrl"),
                null,
                null,
                null,
                null,
                parser.getAttributeValue(null, "licenseBody"),
                parser.getAttributeValue(null, "license"));

        while (parser.next() != XmlPullParser.END_TAG || parser.getName().equals("link")) {
            if (parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals("link")) {
                LinkInfoData link = new LinkInfoData(parser);
                if (links.contains(link))
                    links.get(links.indexOf(link)).merge(link);
                else links.add(link);
            }
        }
    }

    public LicenseInfoData(@Nullable String repo, @Nullable String title, @Nullable String description, @Nullable String licenseName, @Nullable String websiteUrl, @Nullable String gitHubUrl, @Nullable String licenseUrl, @Nullable String[] licensePermissions, @Nullable String[] licenseConditions, @Nullable String[] licenseLimitations, @Nullable String licenseDescription, @Nullable String licenseBody, @Nullable String licenseKey) {
        super(R.layout.item_attribouter_license);
        this.repo = repo != null ? repo.toLowerCase() : repo;
        this.title = title;
        this.description = description;
        this.licenseName = licenseName;
        this.websiteUrl = websiteUrl;
        this.gitHubUrl = gitHubUrl;
        this.licenseUrl = licenseUrl;
        this.licensePermissions = licensePermissions;
        this.licenseConditions = licenseConditions;
        this.licenseLimitations = licenseLimitations;
        this.licenseDescription = licenseDescription;
        this.licenseBody = licenseBody;
        this.licenseKey = licenseKey;

        if (repo != null)
            token = repo;
        else token = title;

        links = new ArrayList<>();
        if (websiteUrl != null && !websiteUrl.isEmpty())
            links.add(new WebsiteLinkInfoData(websiteUrl, 2));
        if (repo != null)
            links.add(new GitHubLinkInfoData(repo, 1));
        if (licenseBody != null || licenseUrl != null)
            links.add(new LicenseLinkInfoData(this, 0));
    }

    public String getName() {
        if (title != null)
            return title;
        else if (repo != null) {
            String name = repo;
            if (name.contains("/")) {
                String[] names = name.split("/");
                if (names.length > 1 && names[1].length() > 0)
                    name = names[1];
                else name = names[0];
            }

            name = name.replace('-', ' ')
                    .replace('_', ' ')
                    .replaceAll("([a-z])([A-Z])", "$1 $2")
                    .replaceAll("([A-Z])([A-Z][a-z])", "$1 $2")
                    .trim();

            StringBuffer nameBuffer = new StringBuffer();
            Pattern pattern = Pattern.compile("\\b(\\w)");
            Matcher matcher = pattern.matcher(name);
            while (matcher.find())
                matcher.appendReplacement(nameBuffer, matcher.group(1).toUpperCase());

            return matcher.appendTail(nameBuffer).toString();
        } else return null;
    }

    public String getLicensePermissions() {
        if (licensePermissions == null)
            return null;
        else {
            StringBuilder builder = new StringBuilder();
            for (String permission : licensePermissions) {
                if (permission.length() > 1) {
                    builder.append(String.valueOf(permission.charAt(0)).toUpperCase())
                            .append(permission.replace('-', ' ').substring(1))
                            .append("\n");
                }
            }

            return builder.substring(0, builder.length() - 1);
        }
    }

    public String getLicenseConditions() {
        if (licenseConditions == null)
            return null;
        else {
            StringBuilder builder = new StringBuilder();
            for (String condition : licenseConditions) {
                if (condition.length() > 1) {
                    builder.append(String.valueOf(condition.charAt(0)).toUpperCase())
                            .append(condition.replace('-', ' ').substring(1))
                            .append("\n");
                }
            }

            return builder.substring(0, builder.length() - 1);
        }
    }

    public String getLicenseLimitations() {
        if (licenseLimitations == null)
            return null;
        else {
            StringBuilder builder = new StringBuilder();
            for (String limitation : licenseLimitations) {
                if (limitation.length() > 1) {
                    builder.append(String.valueOf(limitation.charAt(0)).toUpperCase())
                            .append(limitation.replace('-', ' ').substring(1))
                            .append("\n");
                }
            }

            return builder.substring(0, builder.length() - 1);
        }
    }

    public void merge(LicenseInfoData license) {
        if ((title == null || !title.startsWith("^")) && license.title != null && !license.title.isEmpty())
            title = license.title;
        if ((description == null || !description.startsWith("^")) && license.description != null && !license.description.isEmpty())
            description = license.description;
        if ((licenseName == null || !licenseName.startsWith("^")) && license.licenseName != null)
            licenseName = license.licenseName;
        if ((websiteUrl == null || !websiteUrl.startsWith("^")) && license.websiteUrl != null && !license.websiteUrl.isEmpty())
            websiteUrl = license.websiteUrl;
        if ((gitHubUrl == null || !gitHubUrl.startsWith("^")) && license.gitHubUrl != null)
            gitHubUrl = license.gitHubUrl;
        if ((licenseUrl == null || !licenseUrl.startsWith("^")) && license.licenseUrl != null)
            licenseUrl = license.licenseUrl;
        if (license.licensePermissions != null)
            licensePermissions = license.licensePermissions;
        if (license.licenseConditions != null)
            licenseConditions = license.licenseConditions;
        if (license.licenseLimitations != null)
            licenseLimitations = license.licenseLimitations;
        if (license.licenseDescription != null)
            licenseDescription = license.licenseDescription;
        if ((licenseBody == null || !licenseBody.startsWith("^")) && license.licenseBody != null)
            licenseBody = license.licenseBody;

        for (LinkInfoData link : license.links) {
            if (links.contains(link))
                links.get(links.indexOf(link)).merge(link);
            else links.add(link);
        }
    }

    public boolean hasEverythingGeneric() {
        return description != null && description.startsWith("^") && websiteUrl != null && websiteUrl.startsWith("^") && licenseName != null && licenseName.startsWith("^");
    }

    public boolean hasEverythingLicense() {
        return licenseName != null && licenseName.startsWith("^") && licenseUrl != null && licenseUrl.startsWith("^") && licenseBody != null && licenseBody.startsWith("^");
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LicenseInfoData && (repo != null ? repo.equals(((LicenseInfoData) obj).repo) || repo.equals(((LicenseInfoData) obj).title) : (title != null ? title.equals(((LicenseInfoData) obj).repo) : super.equals(obj)));
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public void bind(Context context, ViewHolder viewHolder) {
        viewHolder.titleView.setText(ResourceUtils.getString(context, getName()));
        viewHolder.descriptionView.setText(ResourceUtils.getString(context, description));

        if (licenseName != null) {
            viewHolder.licenseView.setVisibility(View.VISIBLE);
            viewHolder.licenseView.setText(ResourceUtils.getString(context, licenseName));
        } else viewHolder.licenseView.setVisibility(View.GONE);

        if (links.size() > 0) {
            Collections.sort(links, new LinkInfoData.Comparator(context));

            List<InfoData> linksList = new ArrayList<>();
            for (LinkInfoData link : links) {
                if (!link.isHidden())
                    linksList.add(link);
            }

            viewHolder.links.setVisibility(View.VISIBLE);

            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(context);
            layoutManager.setFlexDirection(FlexDirection.ROW);
            layoutManager.setJustifyContent(JustifyContent.FLEX_START);
            viewHolder.links.setLayoutManager(layoutManager);
            viewHolder.links.setAdapter(new InfoAdapter(linksList));
        } else viewHolder.links.setVisibility(View.GONE);

        LinkInfoData importantLink = null;
        for (LinkInfoData link : links) {
            if (importantLink == null || link.getPriority() > importantLink.getPriority())
                importantLink = link;
        }

        viewHolder.itemView.setOnClickListener(importantLink != null ? importantLink.getListener(context) : null);
    }

    static class ViewHolder extends InfoData.ViewHolder {

        private TextView titleView;
        private TextView descriptionView;
        private TextView licenseView;
        private RecyclerView links;

        ViewHolder(View v) {
            super(v);

            titleView = v.findViewById(R.id.title);
            descriptionView = v.findViewById(R.id.description);
            licenseView = v.findViewById(R.id.license);
            links = v.findViewById(R.id.projectLinks);
        }

    }

}
