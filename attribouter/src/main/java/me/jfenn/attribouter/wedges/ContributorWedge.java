package me.jfenn.attribouter.wedges;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import androidx.annotation.Nullable;
import me.jfenn.attribouter.R;
import me.jfenn.attribouter.data.github.GitHubData;
import me.jfenn.attribouter.data.github.UserData;
import me.jfenn.attribouter.dialogs.UserDialog;
import me.jfenn.attribouter.interfaces.Mergeable;
import me.jfenn.attribouter.utils.ResourceUtils;
import me.jfenn.attribouter.wedges.link.EmailLinkWedge;
import me.jfenn.attribouter.wedges.link.GitHubLinkWedge;
import me.jfenn.attribouter.wedges.link.LinkWedge;
import me.jfenn.attribouter.wedges.link.WebsiteLinkWedge;

public class ContributorWedge extends Wedge<ContributorWedge.ViewHolder> implements Mergeable<ContributorWedge> {

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

    private boolean isHidden;

    public ContributorWedge(XmlResourceParser parser) throws XmlPullParserException, IOException {
        this(parser.getAttributeValue(null, "login"),
                parser.getAttributeValue(null, "name"),
                parser.getAttributeValue(null, "avatar"),
                parser.getAttributeValue(null, "task"),
                parser.getAttributeIntValue(null, "position", -1),
                parser.getAttributeValue(null, "bio"),
                parser.getAttributeValue(null, "blog"),
                parser.getAttributeValue(null, "email"));

        isHidden = parser.getAttributeBooleanValue(null, "hidden", false);
        addChildren(parser);
    }

    protected ContributorWedge(@Nullable String login, @Nullable String name, @Nullable String avatarUrl, @Nullable String task, @Nullable Integer position, @Nullable String bio, @Nullable String blog, @Nullable String email) {
        super(R.layout.item_attribouter_contributor);
        this.login = login;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.task = task;
        this.position = position != null && position >= 0 ? position : null;
        this.bio = bio;
        this.blog = blog;
        this.email = email;

        if (login != null)
            addChild(new GitHubLinkWedge(login, 1));
        if (blog != null)
            addChild(new WebsiteLinkWedge(blog, 2));
        if (email != null)
            addChild(new EmailLinkWedge(email, -1));

        if (login != null && !hasAll())
            addRequest(new UserData(login));
    }

    @Override
    public void onInit(GitHubData data) {
        if (data instanceof UserData) {
            UserData user = (UserData) data;
            merge(new ContributorWedge(
                    user.login,
                    user.name,
                    user.avatar_url,
                    task == null ? "Contributor" : null,
                    null,
                    user.bio,
                    user.blog,
                    user.email
            ));
        }
    }

    @Nullable
    public String getName() {
        return name != null ? name : login;
    }

    public ContributorWedge merge(ContributorWedge contributor) {
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

        for (Wedge child : contributor.getChildren())
            addChild(child);

        return this;
    }

    @Override
    public boolean hasAll() {
        return name != null && name.startsWith("^") && bio != null && bio.startsWith("^") && blog != null && blog.startsWith("^") && email != null && email.startsWith("^");
    }

    @Override
    public boolean isHidden() {
        return isHidden;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ContributorWedge) {
            ContributorWedge contributor = (ContributorWedge) obj;
            return (login != null && contributor.login != null && login.toLowerCase().equals(contributor.login.toLowerCase())) || super.equals(obj);
        } else return super.equals(obj);
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
                    new UserDialog(view.getContext(), ContributorWedge.this)
                            .show();
                }
            });
        } else {
            LinkWedge importantLink = null;
            View.OnClickListener clickListener = null;
            for (LinkWedge link : getChildren(LinkWedge.class)) {
                if (!link.isHidden() && (importantLink == null || link.getPriority() > importantLink.getPriority())) {
                    View.OnClickListener listener = link.getListener(context);
                    if (listener != null) {
                        importantLink = link;
                        clickListener = listener;
                    }
                }
            }

            viewHolder.itemView.setOnClickListener(clickListener);
        }
    }

    protected static class ViewHolder extends Wedge.ViewHolder {

        protected ImageView imageView;
        protected TextView nameView;
        protected TextView taskView;

        protected ViewHolder(View v) {
            super(v);
            imageView = v.findViewById(R.id.image);
            nameView = v.findViewById(R.id.name);
            taskView = v.findViewById(R.id.task);
        }
    }
}
