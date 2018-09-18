package me.jfenn.attribouter.wedges.link;

import android.content.Context;
import android.content.res.XmlResourceParser;
import androidx.annotation.Nullable;
import android.view.View;

import me.jfenn.attribouter.utils.UrlClickListener;

public class PlayStoreLinkWedge extends LinkWedge {

    public PlayStoreLinkWedge(XmlResourceParser parser) {
        this(null, 0);

        String priorityString = parser.getAttributeValue(null, "priority");
        if (priorityString != null)
            priority = Integer.parseInt(priorityString);
    }

    public PlayStoreLinkWedge(@Nullable String url, int priority) {
        super("playStore", "@string/title_attribouter_rate", url, "@drawable/ic_attribouter_rate", false, priority);
    }

    @Nullable
    @Override
    public View.OnClickListener getListener(Context context) {
        return new UrlClickListener(getUrl() != null ? getUrl() : "https://play.google.com/store/apps/details?id=" + context.getApplicationInfo().packageName);
    }
}
