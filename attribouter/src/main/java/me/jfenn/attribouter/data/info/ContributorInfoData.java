package me.jfenn.attribouter.data.info;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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
        if ((bio == null || !bio.startsWith("^")) && contributor.bio != null)
            bio = contributor.bio;
        if ((blog == null || !blog.startsWith("^")) && contributor.blog != null)
            blog = contributor.blog;
        if ((task == null || !task.startsWith("^")) && contributor.task != null)
            task = contributor.task;
    }

    public boolean hasEverything() {
        return name != null && name.startsWith("^") && bio != null && bio.startsWith("^") && blog != null && blog.startsWith("^");
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
        Glide.with(context).load(avatarUrl).into(viewHolder.imageView);
        viewHolder.nameView.setText(ResourceUtils.getString(context, getName()));
        if (task != null) {
            viewHolder.taskView.setVisibility(View.VISIBLE);
            viewHolder.taskView.setText(ResourceUtils.getString(context, task));
        } else viewHolder.taskView.setVisibility(View.GONE);

        String blog = ResourceUtils.getString(context, this.blog);
        if (bio != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new UserDialog(view.getContext(), ContributorInfoData.this)
                            .show();
                }
            });
        } else if (blog != null) {
            viewHolder.itemView.setOnClickListener(new UrlClickListener(blog));
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
