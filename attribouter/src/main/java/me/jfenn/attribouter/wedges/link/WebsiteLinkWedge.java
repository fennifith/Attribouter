package me.jfenn.attribouter.wedges.link;

import androidx.annotation.NonNull;

public class WebsiteLinkWedge extends LinkWedge {

    public WebsiteLinkWedge(@NonNull String url, int priority) {
        super("website", "@string/title_attribouter_website", url, "@drawable/ic_attribouter_link", false, priority);
    }

}
