package me.jfenn.attribouter.data.info;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.XmlResourceParser;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.jfenn.attribouter.R;
import me.jfenn.attribouter.adapters.InfoAdapter;
import me.jfenn.attribouter.data.github.GitHubData;
import me.jfenn.attribouter.data.github.RepositoryData;
import me.jfenn.attribouter.data.info.link.GitHubLinkInfoData;
import me.jfenn.attribouter.data.info.link.LinkInfoData;
import me.jfenn.attribouter.data.info.link.PlayStoreLinkInfoData;
import me.jfenn.attribouter.data.info.link.WebsiteLinkInfoData;
import me.jfenn.attribouter.utils.ResourceUtils;

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

        while (parser.next() != XmlPullParser.END_TAG || parser.getName().equals("link")) {
            if (parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals("link")) {
                LinkInfoData link = new LinkInfoData(parser);
                if (links.contains(link))
                    links.get(links.indexOf(link)).merge(link);
                else links.add(link);
            }
        }

        addRequest(new RepositoryData(repo));
    }

    @Override
    public void onInit(GitHubData data) {
        if (data instanceof RepositoryData) {
            RepositoryData repository = (RepositoryData) data;
            if ((description == null || !description.startsWith("^")) && repository.description != null)
                description = repository.description;

            List<LinkInfoData> newLinks = Arrays.asList(
                    new GitHubLinkInfoData(repository.html_url, 0, true),
                    repository.homepage.startsWith("https://play.google.com/")
                            ? new PlayStoreLinkInfoData(repository.homepage, 0)
                            : new WebsiteLinkInfoData(repository.homepage, 0)
            );

            for (LinkInfoData link : newLinks) {
                if (links.contains(link))
                    links.get(links.indexOf(link)).merge(link);
                else links.add(link);
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

        if (links.size() > 0) {
            viewHolder.links.setVisibility(View.VISIBLE);

            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(context);
            layoutManager.setFlexDirection(FlexDirection.ROW);
            layoutManager.setJustifyContent(JustifyContent.CENTER);
            viewHolder.links.setLayoutManager(layoutManager);
            viewHolder.links.setAdapter(new InfoAdapter(new ArrayList<InfoData>(links)));
        } else viewHolder.links.setVisibility(View.GONE);
    }

    static class ViewHolder extends InfoData.ViewHolder {

        ImageView appIconView;
        TextView nameTextView;
        TextView versionTextView;
        TextView descriptionTextView;
        RecyclerView links;

        private ViewHolder(View v) {
            super(v);
            appIconView = v.findViewById(R.id.appIcon);
            nameTextView = v.findViewById(R.id.appName);
            versionTextView = v.findViewById(R.id.appVersion);
            descriptionTextView = v.findViewById(R.id.description);
            links = v.findViewById(R.id.appLinks);
        }

    }
}
