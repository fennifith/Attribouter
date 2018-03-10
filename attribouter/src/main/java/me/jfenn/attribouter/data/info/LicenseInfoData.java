package me.jfenn.attribouter.data.info;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import me.jfenn.attribouter.R;
import me.jfenn.attribouter.utils.ResourceUtils;
import me.jfenn.attribouter.utils.UrlClickListener;

public class LicenseInfoData extends InfoData<LicenseInfoData.ViewHolder> {

    @Nullable
    String repo;
    @Nullable
    String title;
    @Nullable
    String description;
    @Nullable
    String licenseName;
    @Nullable
    String websiteUrl;
    @Nullable
    String gitHubUrl;
    @Nullable
    String licenseUrl;

    public LicenseInfoData(@Nullable String repo, @Nullable String title, @Nullable String description, @Nullable String licenseName, @Nullable String websiteUrl, @Nullable String gitHubUrl, @Nullable String licenseUrl) {
        super(R.layout.item_attribouter_license);
        this.repo = repo;
        this.title = title;
        this.description = description;
        this.licenseName = licenseName;
        this.websiteUrl = websiteUrl;
        this.gitHubUrl = gitHubUrl;
        this.licenseUrl = licenseUrl;
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

            return name.replace('-', ' ')
                    .replace('_', ' ')
                    .replaceAll("/([A-Z])/g", " $1")
                    .trim();
        } else return null;
    }

    public void merge(LicenseInfoData license) {
        if ((title == null || !title.startsWith("^")) && license.title != null)
            title = license.title;
        if ((description == null || !description.startsWith("^")) && license.description != null)
            description = license.description;
        if ((licenseName == null || !licenseName.startsWith("^")) && license.licenseName != null)
            licenseName = license.licenseName;
        if ((websiteUrl == null || !websiteUrl.startsWith("^")) && license.websiteUrl != null)
            websiteUrl = license.websiteUrl;
        if ((gitHubUrl == null || !gitHubUrl.startsWith("^")) && license.gitHubUrl != null)
            gitHubUrl = license.gitHubUrl;
        if ((licenseUrl == null || !licenseUrl.startsWith("^")) && license.licenseUrl != null)
            licenseUrl = license.licenseUrl;
    }

    public boolean hasEverythingGeneric() {
        return description != null && description.startsWith("^") && websiteUrl != null && websiteUrl.startsWith("^") && licenseName != null && licenseName.startsWith("^");
    }

    public boolean hasEverythingLicense() {
        return licenseName != null && licenseName.startsWith("^") && licenseUrl != null && licenseUrl.startsWith("^");
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LicenseInfoData && (repo != null ? repo.equals(((LicenseInfoData) obj).repo) : super.equals(obj));
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

        viewHolder.links.setVisibility(websiteUrl != null || gitHubUrl != null || licenseUrl != null ? View.VISIBLE : View.GONE);

        if (websiteUrl != null) {
            viewHolder.websiteButton.setVisibility(View.VISIBLE);
            viewHolder.websiteButton.setOnClickListener(new UrlClickListener(ResourceUtils.getString(context, websiteUrl)));
        } else viewHolder.websiteButton.setVisibility(View.GONE);

        if (gitHubUrl != null) {
            viewHolder.gitHubButton.setVisibility(View.VISIBLE);
            viewHolder.gitHubButton.setOnClickListener(new UrlClickListener(ResourceUtils.getString(context, gitHubUrl)));
        } else viewHolder.gitHubButton.setVisibility(View.GONE);

        if (licenseUrl != null) {
            viewHolder.licenseButton.setVisibility(View.VISIBLE);
            viewHolder.licenseButton.setOnClickListener(new UrlClickListener(ResourceUtils.getString(context, licenseUrl))); //TODO: create dialog to show license info
        } else viewHolder.licenseButton.setVisibility(View.GONE);
    }

    static class ViewHolder extends InfoData.ViewHolder {

        private TextView titleView;
        private TextView descriptionView;
        private TextView licenseView;
        private View links;
        private View websiteButton;
        private View gitHubButton;
        private View licenseButton;

        ViewHolder(View v) {
            super(v);

            titleView = v.findViewById(R.id.title);
            descriptionView = v.findViewById(R.id.description);
            licenseView = v.findViewById(R.id.license);
            links = v.findViewById(R.id.projectLinks);
            websiteButton = v.findViewById(R.id.websiteButton);
            gitHubButton = v.findViewById(R.id.gitHubButton);
            licenseButton = v.findViewById(R.id.licenseButton);
        }

    }

}
