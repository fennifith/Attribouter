package me.jfenn.attribouter.data.info.link;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;

import me.jfenn.attribouter.utils.UrlClickListener;

public class PlayStoreLinkInfoData extends LinkInfoData {

    public PlayStoreLinkInfoData(@Nullable String url, int priority) {
        super("playStore", "@string/title_attribouter_rate", url, "@drawable/ic_attribouter_rate", priority);
    }

    @Nullable
    @Override
    public View.OnClickListener getListener(Context context) {
        return new UrlClickListener(getUrl() != null ? getUrl() : "https://play.google.com/store/apps/details?id=" + context.getApplicationInfo().packageName);
    }
}
