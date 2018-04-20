package me.jfenn.attribouter.data.info;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.jfenn.attribouter.R;
import me.jfenn.attribouter.dialogs.UserDialog;
import me.jfenn.attribouter.utils.ResourceUtils;
import me.jfenn.attribouter.utils.UrlClickListener;

public class ContributorInfoData extends InfoData<ContributorInfoData.ViewHolder> {

    @Nullable
    public String login;
    @Nullable
    public String name;
    @Nullable
    public String avatarUrl;
    @Nullable
    public String bio;
    @Nullable
    public String blog;
    @Nullable
    public String email;
    @Nullable
    Integer position;
    @Nullable
    public String task;
    public List<LinkInfoData> links;

    boolean isHidden;

    ContributorInfoData(@Nullable String login, @Nullable String name, @Nullable String avatarUrl, @Nullable String task, @Nullable Integer position, @Nullable String bio, @Nullable String blog, @Nullable String email) {
        super(R.layout.item_attribouter_contributor);
        this.login = login;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.task = task;
        this.position = position;
        this.bio = bio;
        this.blog = blog;
        this.email = email;

        links = new ArrayList<>();
        if (login != null)
            links.add(new LinkInfoData("@string/title_attribouter_github", "https://github.com/" + login, "@drawable/ic_attribouter_github"));
        if (blog != null)
            links.add(new LinkInfoData("@string/title_attribouter_website", blog, "@drawable/ic_attribouter_link"));
        if (email != null)
            links.add(new LinkInfoData("@string/title_attribouter_email", "mailto:" + email, "@drawable/ic_attribouter_email"));
    }

    @Nullable
    public String getName() {
        return name != null ? name : login;
    }

    public void merge(ContributorInfoData contributor) {
        if ((name == null || !name.startsWith("^")) && contributor.name != null)
            name = contributor.name;
        if ((avatarUrl == null || !avatarUrl.startsWith("^")) && contributor.avatarUrl != null)
            avatarUrl = contributor.avatarUrl;
        if ((bio == null || !bio.startsWith("^")) && contributor.bio != null && !contributor.bio.isEmpty())
            bio = contributor.bio;
        if ((blog == null || !blog.startsWith("^")) && contributor.blog != null && !contributor.blog.isEmpty())
            blog = contributor.blog;
        if ((email == null || !email.startsWith("^")) && contributor.email != null && !contributor.email.isEmpty())
            email = contributor.email;
        if ((task == null || !task.startsWith("^")) && contributor.task != null)
            task = contributor.task;

        for (LinkInfoData link : contributor.links) {
            if (!links.contains(link))
                links.add(link);
        }
    }

    public boolean hasEverything() {
        return name != null && name.startsWith("^") && bio != null && bio.startsWith("^") && blog != null && blog.startsWith("^") && email != null && email.startsWith("^");
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ContributorInfoData && (login != null ? login.equals(((ContributorInfoData) obj).login) : super.equals(obj));
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public void bind(Context context, ViewHolder viewHolder) {
        ResourceUtils.setImage(context, avatarUrl, viewHolder.imageView);
        viewHolder.nameView.setText(ResourceUtils.getString(context, getName()));
        if (task != null) {
            viewHolder.taskView.setVisibility(View.VISIBLE);
            viewHolder.taskView.setText(ResourceUtils.getString(context, task));
        } else viewHolder.taskView.setVisibility(View.GONE);

        String blog = ResourceUtils.getString(context, this.blog);
        if (ResourceUtils.getString(context, bio) != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new UserDialog(view.getContext(), ContributorInfoData.this)
                            .show();
                }
            });
        } else if (blog != null) {
            viewHolder.itemView.setOnClickListener(new UrlClickListener(blog));
        } else if (login != null) {
            viewHolder.itemView.setOnClickListener(new UrlClickListener("https://github.com/" + login));
        } else viewHolder.itemView.setOnClickListener(null);
    }

    static class ViewHolder extends InfoData.ViewHolder {

        private ImageView imageView;
        private TextView nameView;
        private TextView taskView;

        ViewHolder(View v) {
            super(v);
            imageView = v.findViewById(R.id.image);
            nameView = v.findViewById(R.id.name);
            taskView = v.findViewById(R.id.task);
        }
    }
}
