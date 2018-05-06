package me.jfenn.attribouter.data.info.link;

import android.support.annotation.NonNull;

public class EmailLinkInfoData extends LinkInfoData {

    public EmailLinkInfoData(@NonNull String address, int priority) {
        super("email", "@string/title_attribouter_email", "mailto:" + address, "@drawable/ic_attribouter_email", false, priority);
    }

}
