package me.jfenn.attribouter.data.info.link;

import android.support.annotation.NonNull;

public class WebsiteLinkInfoData extends LinkInfoData {

    public WebsiteLinkInfoData(@NonNull String url, int priority) {
        super("website", "@string/title_attribouter_website", url, "@drawable/ic_attribouter_link", false, priority);
    }

}
