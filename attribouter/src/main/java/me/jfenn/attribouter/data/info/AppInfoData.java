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

import me.jfenn.attribouter.R;
import me.jfenn.attribouter.utils.UrlClickListener;

public class AppInfoData extends InfoData<AppInfoData.ViewHolder> {

    private String description;
    private String playStoreUrl;
    private String websiteUrl;
    private String gitHubUrl;

    private boolean isPlayStore;

    public AppInfoData(XmlResourceParser parser, @Nullable String repo) {
        super(R.layout.item_attribouter_app_info);
        description = parser.getAttributeValue(null, "description");
        playStoreUrl = parser.getAttributeValue(null, "playStoreUrl");
        isPlayStore = parser.getAttributeBooleanValue(null, "showPlayStoreUrl", true);
        websiteUrl = parser.getAttributeValue(null, "websiteUrl");
        gitHubUrl = parser.getAttributeValue(null, "gitHubUrl");

        if (gitHubUrl == null && repo != null)
            gitHubUrl = "https://github.com/" + repo;
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public void bind(Context context, ViewHolder viewHolder) {
        ApplicationInfo info = context.getApplicationInfo();
        viewHolder.appIconView.setImageResource(info.icon);
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
            viewHolder.descriptionTextView.setText(description);
        } else viewHolder.descriptionTextView.setVisibility(View.GONE);

        if (isPlayStore) {
            UrlClickListener listener = new UrlClickListener("https://play.google.com/store/apps/details?id=" + info.packageName);
            if (playStoreUrl != null)
                listener = new UrlClickListener(playStoreUrl);

            viewHolder.playStoreButton.setVisibility(View.VISIBLE);
            viewHolder.playStoreButton.setOnClickListener(listener);
        } else viewHolder.playStoreButton.setVisibility(View.GONE);

        if (websiteUrl != null) {
            viewHolder.websiteButton.setVisibility(View.VISIBLE);
            viewHolder.websiteButton.setOnClickListener(new UrlClickListener(websiteUrl));
        } else viewHolder.websiteButton.setVisibility(View.GONE);

        if (gitHubUrl != null) {
            viewHolder.gitHubButton.setVisibility(View.VISIBLE);
            viewHolder.gitHubButton.setOnClickListener(new UrlClickListener(gitHubUrl));
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
