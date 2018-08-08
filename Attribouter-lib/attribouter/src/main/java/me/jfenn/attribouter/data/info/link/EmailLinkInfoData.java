package me.jfenn.attribouter.data.info.link;

import android.content.res.XmlResourceParser;
import android.support.annotation.NonNull;

public class EmailLinkInfoData extends LinkInfoData {

    public EmailLinkInfoData(XmlResourceParser parser) {
        this(parser.getAttributeValue(null, "email"), 0);

        String priorityString = parser.getAttributeValue(null, "priority");
        if (priorityString != null)
            priority = Integer.parseInt(priorityString);
    }

    public EmailLinkInfoData(@NonNull String address, int priority) {
        super("email", "@string/title_attribouter_email", "mailto:" + address, "@drawable/ic_attribouter_email", false, priority);
    }

}
