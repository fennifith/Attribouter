package me.jfenn.attribouter.data.info;

import android.content.Context;
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
import me.jfenn.attribouter.data.info.link.EmailLinkInfoData;
import me.jfenn.attribouter.data.info.link.GitHubLinkInfoData;
import me.jfenn.attribouter.data.info.link.LinkInfoData;
import me.jfenn.attribouter.data.info.link.WebsiteLinkInfoData;
import me.jfenn.attribouter.dialogs.UserDialog;
import me.jfenn.attribouter.utils.ResourceUtils;

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

    ContributorInfoData(XmlResourceParser parser, @Nullable Integer position) throws XmlPullParserException, IOException {
        this(parser.getAttributeValue(null, "login"),
                parser.getAttributeValue(null, "name"),
                parser.getAttributeValue(null, "avatar"),
                parser.getAttributeValue(null, "task"),
                position,
                parser.getAttributeValue(null, "bio"),
                parser.getAttributeValue(null, "blog"),
                parser.getAttributeValue(null, "email"));

        isHidden = parser.getAttributeBooleanValue(null, "hidden", false);

        while (parser.next() != XmlPullParser.END_TAG || parser.getName().equals("link")) {
            if (parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals("link")) {
                LinkInfoData link = new LinkInfoData(parser);
                if (links.contains(link))
                    links.get(links.indexOf(link)).merge(link);
                else links.add(link);
            }
        }
    }

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
            links.add(new GitHubLinkInfoData(login, 1));
        if (blog != null)
            links.add(new WebsiteLinkInfoData(blog, 2));
        if (email != null)
            links.add(new EmailLinkInfoData(email, -1));
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
            if (links.contains(link))
                links.get(links.indexOf(link)).merge(link);
            else links.add(link);
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

        if (ResourceUtils.getString(context, bio) != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new UserDialog(view.getContext(), ContributorInfoData.this)
                            .show();
                }
            });
        } else {
            LinkInfoData importantLink = null;
            for (LinkInfoData link : links) {
                if (!link.isHidden() && (importantLink == null || link.getPriority() > importantLink.getPriority()))
                    importantLink = link;
            }

            viewHolder.itemView.setOnClickListener(importantLink != null ? importantLink.getListener(context) : null);
        }
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
