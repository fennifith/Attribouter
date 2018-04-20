package me.jfenn.attribouter.data.info;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.XmlResourceParser;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.jfenn.attribouter.R;
import me.jfenn.attribouter.data.github.GitHubData;
import me.jfenn.attribouter.data.github.RepositoryData;
import me.jfenn.attribouter.data.info.link.GitHubLinkInfoData;
import me.jfenn.attribouter.data.info.link.LinkInfoData;
import me.jfenn.attribouter.data.info.link.PlayStoreLinkInfoData;
import me.jfenn.attribouter.data.info.link.WebsiteLinkInfoData;
import me.jfenn.attribouter.utils.ResourceUtils;
import me.jfenn.attribouter.utils.UrlClickListener;

public class AppInfoData extends InfoData<AppInfoData.ViewHolder> {

    @Nullable
    private String icon;
    @Nullable
    private String description;
    @Nullable
    private String playStoreUrl;
    @Nullable
    private String websiteUrl;
    @Nullable
    private String gitHubUrl;

    private List<LinkInfoData> links;

    public AppInfoData(XmlResourceParser parser) throws IOException, XmlPullParserException {
        super(R.layout.item_attribouter_app_info);
        icon = parser.getAttributeValue(null, "icon");
        description = parser.getAttributeValue(null, "description");
        playStoreUrl = parser.getAttributeValue(null, "playStoreUrl");
        websiteUrl = parser.getAttributeValue(null, "websiteUrl");
        gitHubUrl = parser.getAttributeValue(null, "gitHubUrl");

        String repo = parser.getAttributeValue(null, "repo");
        if (gitHubUrl == null && repo != null)
            gitHubUrl = "https://github.com/" + repo;

        links = new ArrayList<>();
        if (repo != null || gitHubUrl != null)
            links.add(new GitHubLinkInfoData(gitHubUrl != null ? gitHubUrl : repo, 0, gitHubUrl != null));
        if (websiteUrl != null)
            links.add(new WebsiteLinkInfoData(websiteUrl, 0));
        links.add(new PlayStoreLinkInfoData(playStoreUrl, 0));

        while (parser.next() == XmlPullParser.START_TAG && parser.getName().equals("link")) {
            LinkInfoData link = new LinkInfoData(parser);
            if (links.contains(link))
                links.get(links.indexOf(link)).merge(link);
            else links.add(link);
        }

        addRequest(new RepositoryData(repo));
    }

    @Override
    public void onInit(GitHubData data) {
        if (data instanceof RepositoryData) {
            RepositoryData repository = (RepositoryData) data;
            if ((description == null || !description.startsWith("^")) && repository.description != null)
                description = repository.description;
            if ((gitHubUrl == null || !gitHubUrl.startsWith("^")) && repository.html_url != null)
                gitHubUrl = repository.html_url;
            if ((websiteUrl == null || !websiteUrl.startsWith("^")) && repository.homepage != null) {
                if (repository.homepage.startsWith("https://play.google.com/")) {
                    if (playStoreUrl == null || !playStoreUrl.startsWith("^"))
                        playStoreUrl = repository.homepage;
                } else websiteUrl = repository.homepage;
            }
        }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public void bind(Context context, ViewHolder viewHolder) {
        ApplicationInfo info = context.getApplicationInfo();
        ResourceUtils.setImage(context, icon, info.icon, viewHolder.appIconView);
        viewHolder.nameTextView.setText(info.labelRes);
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(info.packageName, 0);
            viewHolder.versionTextView.setText(String.format(context.getString(R.string.title_attribouter_version), packageInfo.versionName));
            viewHolder.versionTextView.setVisibility(View.VISIBLE);
        } catch (PackageManager.NameNotFoundException e) {
            viewHolder.versionTextView.setVisibility(View.GONE);
        }

        if (description != null) {
            viewHolder.descriptionTextView.setVisibility(View.VISIBLE);
            viewHolder.descriptionTextView.setText(ResourceUtils.getString(context, description));
        } else viewHolder.descriptionTextView.setVisibility(View.GONE);

        UrlClickListener listener = new UrlClickListener("https://play.google.com/store/apps/details?id=" + info.packageName);
        if (playStoreUrl != null)
            listener = new UrlClickListener(ResourceUtils.getString(context, playStoreUrl));

        viewHolder.playStoreButton.setVisibility(View.VISIBLE);
        viewHolder.playStoreButton.setOnClickListener(listener);

        if (websiteUrl != null) {
            viewHolder.websiteButton.setVisibility(View.VISIBLE);
            viewHolder.websiteButton.setOnClickListener(new UrlClickListener(ResourceUtils.getString(context, websiteUrl)));
        } else viewHolder.websiteButton.setVisibility(View.GONE);

        if (gitHubUrl != null) {
            viewHolder.gitHubButton.setVisibility(View.VISIBLE);
            viewHolder.gitHubButton.setOnClickListener(new UrlClickListener(ResourceUtils.getString(context, gitHubUrl)));
        } else viewHolder.gitHubButton.setVisibility(View.GONE);
    }

    static class ViewHolder extends InfoData.ViewHolder {

        ImageView appIconView;
        TextView nameTextView;
        TextView versionTextView;
        TextView descriptionTextView;
        View websiteButton;
        View gitHubButton;
        View playStoreButton;

        private ViewHolder(View v) {
            super(v);
            appIconView = v.findViewById(R.id.appIcon);
            nameTextView = v.findViewById(R.id.appName);
            versionTextView = v.findViewById(R.id.appVersion);
            descriptionTextView = v.findViewById(R.id.description);
            websiteButton = v.findViewById(R.id.appLinkWebsite);
            gitHubButton = v.findViewById(R.id.appLinkGitHub);
            playStoreButton = v.findViewById(R.id.appLinkPlayStore);
        }

    }
}
