package me.jfenn.attribouter.wedges;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.view.View;
import android.widget.TextView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import me.jfenn.attribouter.R;
import me.jfenn.attribouter.adapters.InfoAdapter;
import me.jfenn.attribouter.data.github.GitHubData;
import me.jfenn.attribouter.data.github.LicenseData;
import me.jfenn.attribouter.data.github.RepositoryData;
import me.jfenn.attribouter.interfaces.Mergeable;
import me.jfenn.attribouter.utils.ResourceUtils;
import me.jfenn.attribouter.wedges.link.GitHubLinkWedge;
import me.jfenn.attribouter.wedges.link.LicenseLinkWedge;
import me.jfenn.attribouter.wedges.link.LinkWedge;
import me.jfenn.attribouter.wedges.link.WebsiteLinkWedge;

public class LicenseWedge extends Wedge<LicenseWedge.ViewHolder> implements Mergeable<LicenseWedge> {

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

    public LicenseWedge(XmlResourceParser parser) throws IOException, XmlPullParserException {
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

        addChildren(parser);
    }

    protected LicenseWedge(@Nullable String repo, @Nullable String title, @Nullable String description, @Nullable String licenseName, @Nullable String websiteUrl, @Nullable String gitHubUrl, @Nullable String licenseUrl, @Nullable String[] licensePermissions, @Nullable String[] licenseConditions, @Nullable String[] licenseLimitations, @Nullable String licenseDescription, @Nullable String licenseBody, @Nullable String licenseKey) {
        super(R.layout.item_attribouter_license);
        this.repo = repo;
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

        if (websiteUrl != null && !websiteUrl.isEmpty())
            addChild(new WebsiteLinkWedge(websiteUrl, 2));
        if (repo != null)
            addChild(new GitHubLinkWedge(repo, 1));
        if (licenseBody != null || licenseUrl != null)
            addChild(new LicenseLinkWedge(this, 0));

        if (repo != null && !hasAllGeneric())
            addRequest(new RepositoryData(repo));
        if (licenseKey != null && (repo != null || title != null) && !hasAllLicense()) {
            LicenseData request = new LicenseData(licenseKey);
            request.addTag(token);
            addRequest(request);
        }
    }

    @Override
    public void onInit(GitHubData data) {
        if (data instanceof RepositoryData) {
            RepositoryData repo = (RepositoryData) data;
            merge(new LicenseWedge(
                    null,
                    null,
                    repo.description,
                    repo.license != null ? repo.license.name : null,
                    repo.homepage,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            ));

            if (repo.license != null && repo.license.key != null && !hasAllLicense())
                addRequest(new LicenseData(repo.license.key));
        } else if (data instanceof LicenseData) {
            LicenseData license = (LicenseData) data;
            merge(new LicenseWedge(
                    null,
                    null,
                    null,
                    license.name,
                    null,
                    null,
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LicenseWedge) {
            LicenseWedge license = (LicenseWedge) obj;
            return (repo != null && ((license.repo != null && repo.toLowerCase().equals(license.repo.toLowerCase())) || (license.title != null && repo.toLowerCase().equals(license.title.toLowerCase()))))
                    || (title != null && ((license.repo != null && title.toLowerCase().equals(license.repo.toLowerCase())) || license.title != null && title.toLowerCase().equals(license.title.toLowerCase())))
                    || super.equals(obj);
        } else return super.equals(obj);
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

        List<LinkWedge> links = getChildren(LinkWedge.class);
        if (links.size() > 0) {
            Collections.sort(links, new LinkWedge.Comparator(context));

            List<Wedge> linksList = new ArrayList<>();
            for (LinkWedge link : links) {
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

        LinkWedge importantLink = null;
        View.OnClickListener clickListener = null;
        for (LinkWedge link : links) {
            if (importantLink == null || link.getPriority() > importantLink.getPriority()) {
                View.OnClickListener listener = link.getListener(context);
                if (listener != null) {
                    clickListener = listener;
                    importantLink = link;
                }
            }
        }

        viewHolder.itemView.setOnClickListener(clickListener);
    }

    @Override
    public LicenseWedge merge(LicenseWedge mergee) {
        if ((title == null || !title.startsWith("^")) && mergee.title != null && !mergee.title.isEmpty())
            title = mergee.title;
        if ((description == null || !description.startsWith("^")) && mergee.description != null && !mergee.description.isEmpty())
            description = mergee.description;
        if ((licenseName == null || !licenseName.startsWith("^")) && mergee.licenseName != null)
            licenseName = mergee.licenseName;
        if ((websiteUrl == null || !websiteUrl.startsWith("^")) && mergee.websiteUrl != null && !mergee.websiteUrl.isEmpty())
            websiteUrl = mergee.websiteUrl;
        if ((gitHubUrl == null || !gitHubUrl.startsWith("^")) && mergee.gitHubUrl != null)
            gitHubUrl = mergee.gitHubUrl;
        if ((licenseUrl == null || !licenseUrl.startsWith("^")) && mergee.licenseUrl != null)
            licenseUrl = mergee.licenseUrl;
        if (mergee.licensePermissions != null)
            licensePermissions = mergee.licensePermissions;
        if (mergee.licenseConditions != null)
            licenseConditions = mergee.licenseConditions;
        if (mergee.licenseLimitations != null)
            licenseLimitations = mergee.licenseLimitations;
        if (mergee.licenseDescription != null)
            licenseDescription = mergee.licenseDescription;
        if ((licenseBody == null || !licenseBody.startsWith("^")) && mergee.licenseBody != null)
            licenseBody = mergee.licenseBody;

        for (Wedge child : mergee.getChildren())
            addChild(child);

        return this;
    }

    @Override
    public boolean hasAll() {
        return hasAllGeneric() && hasAllLicense();
    }

    public boolean hasAllGeneric() {
        return description != null && description.startsWith("^") && websiteUrl != null && websiteUrl.startsWith("^") && licenseName != null && licenseName.startsWith("^");
    }

    public boolean hasAllLicense() {
        return licenseName != null && licenseName.startsWith("^") && licenseUrl != null && licenseUrl.startsWith("^") && licenseBody != null && licenseBody.startsWith("^");
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    protected static class ViewHolder extends Wedge.ViewHolder {

        protected TextView titleView;
        protected TextView descriptionView;
        protected TextView licenseView;
        protected RecyclerView links;

        protected ViewHolder(View v) {
            super(v);

            titleView = v.findViewById(R.id.title);
            descriptionView = v.findViewById(R.id.description);
            licenseView = v.findViewById(R.id.license);
            links = v.findViewById(R.id.projectLinks);
        }

    }

}
