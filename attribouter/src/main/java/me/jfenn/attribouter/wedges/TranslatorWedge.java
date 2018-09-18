package me.jfenn.attribouter.wedges;

import android.content.Context;
import android.content.res.XmlResourceParser;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import me.jfenn.attribouter.R;
import me.jfenn.attribouter.data.github.GitHubData;
import me.jfenn.attribouter.data.github.UserData;
import me.jfenn.attribouter.interfaces.Mergeable;
import me.jfenn.attribouter.utils.ResourceUtils;
import me.jfenn.attribouter.utils.UrlClickListener;

public class TranslatorWedge extends Wedge<TranslatorWedge.ViewHolder> implements Mergeable<TranslatorWedge> {

    @Nullable
    public String login;
    @Nullable
    public String name;
    @Nullable
    public String avatarUrl;
    @Nullable
    public String blog;
    @Nullable
    public String email;
    @Nullable
    public String locales;

    public TranslatorWedge(XmlResourceParser parser) throws IOException, XmlPullParserException {
        this(parser.getAttributeValue(null, "login"),
                parser.getAttributeValue(null, "name"),
                parser.getAttributeValue(null, "avatar"),
                parser.getAttributeValue(null, "locales"),
                parser.getAttributeValue(null, "blog"),
                parser.getAttributeValue(null, "email"));
    }

    protected TranslatorWedge(@Nullable String login, @Nullable String name, @Nullable String avatarUrl, @Nullable String locales, @Nullable String blog, @Nullable String email) {
        super(R.layout.item_attribouter_translator);
        this.login = login;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.locales = locales;
        this.blog = blog;
        this.email = email;

        if (login != null && !hasEverything())
            addRequest(new UserData(login));
    }

    @Override
    public void onInit(GitHubData data) {
        if (data instanceof UserData) {
            UserData user = (UserData) data;
            merge(new TranslatorWedge(
                    user.login,
                    user.name,
                    user.avatar_url,
                    null,
                    user.blog,
                    user.email
            ));
        }
    }

    @Nullable
    public String getName() {
        return name != null ? name : login;
    }

    @Override
    public TranslatorWedge merge(TranslatorWedge contributor) {
        if ((name == null || !name.startsWith("^")) && contributor.name != null)
            name = contributor.name;
        if ((avatarUrl == null || !avatarUrl.startsWith("^")) && contributor.avatarUrl != null)
            avatarUrl = contributor.avatarUrl;
        if ((blog == null || !blog.startsWith("^")) && contributor.blog != null && !contributor.blog.isEmpty())
            blog = contributor.blog;
        if ((email == null || !email.startsWith("^")) && contributor.email != null && !contributor.email.isEmpty())
            email = contributor.email;
        if ((locales == null || !locales.startsWith("^")) && contributor.locales != null)
            locales = contributor.locales;

        return this;
    }

    @Override
    public boolean hasAll() {
        return false;
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    public boolean hasEverything() {
        return name != null && name.startsWith("^") && blog != null && blog.startsWith("^");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TranslatorWedge) {
            TranslatorWedge translator = (TranslatorWedge) obj;
            return (login != null && translator.login != null && login.toLowerCase().equals(translator.login.toLowerCase())) || super.equals(obj);
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

        String blog = ResourceUtils.getString(context, this.blog);
        if (blog != null) {
            viewHolder.itemView.setOnClickListener(new UrlClickListener(blog));
        } else if (login != null) {
            viewHolder.itemView.setOnClickListener(new UrlClickListener("https://github.com/" + login));
        } else viewHolder.itemView.setOnClickListener(null);
    }

    protected static class ViewHolder extends Wedge.ViewHolder {

        protected ImageView imageView;
        protected TextView nameView;

        protected ViewHolder(View v) {
            super(v);
            imageView = v.findViewById(R.id.image);
            nameView = v.findViewById(R.id.name);
        }
    }
}
