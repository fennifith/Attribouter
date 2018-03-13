package me.jfenn.attribouter.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import me.jfenn.attribouter.R;
import me.jfenn.attribouter.data.info.ContributorInfoData;
import me.jfenn.attribouter.utils.ResourceUtils;
import me.jfenn.attribouter.utils.UrlClickListener;

public class UserDialog extends AppCompatDialog {

    private ContributorInfoData contributor;

    public UserDialog(Context context, ContributorInfoData contributor) {
        super(context);
        this.contributor = contributor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_attribouter_user);

        TextView nameView = findViewById(R.id.name);
        TextView taskView = findViewById(R.id.task);
        ImageView imageView = findViewById(R.id.image);
        TextView bioView = findViewById(R.id.description);
        View links = findViewById(R.id.links);
        View websiteButton = findViewById(R.id.website);
        View gitHubButton = findViewById(R.id.gitHub);
        View emailButton = findViewById(R.id.email);

        nameView.setText(ResourceUtils.getString(getContext(), contributor.getName()));
        taskView.setText(ResourceUtils.getString(getContext(), contributor.task));

        String url = ResourceUtils.getString(getContext(), contributor.avatarUrl);
        if (url != null)
            ResourceUtils.setImage(getContext(), contributor.avatarUrl, imageView);
        else imageView.setVisibility(View.GONE);

        bioView.setText(ResourceUtils.getString(getContext(), contributor.bio));
        links.setVisibility(contributor.login != null || contributor.blog != null || contributor.email != null ? View.VISIBLE : View.GONE);

        String blog = ResourceUtils.getString(getContext(), contributor.blog);
        if (blog != null) {
            websiteButton.setVisibility(View.VISIBLE);
            websiteButton.setOnClickListener(new UrlClickListener(blog));
        } else websiteButton.setVisibility(View.GONE);

        if (contributor.login != null) {
            gitHubButton.setVisibility(View.VISIBLE);
            gitHubButton.setOnClickListener(new UrlClickListener("https://github.com/" + contributor.login));
        } else gitHubButton.setVisibility(View.GONE);

        if (contributor.email != null) {
            emailButton.setVisibility(View.VISIBLE);
            emailButton.setOnClickListener(new UrlClickListener("mailto:" + contributor.email));
        } else emailButton.setVisibility(View.GONE);
    }
}
