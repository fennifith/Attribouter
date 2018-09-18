package me.jfenn.attribouter.wedges.link;

import android.content.res.XmlResourceParser;
import androidx.annotation.NonNull;

public class EmailLinkWedge extends LinkWedge {

    public EmailLinkWedge(XmlResourceParser parser) {
        this(parser.getAttributeValue(null, "email"), 0);

        String priorityString = parser.getAttributeValue(null, "priority");
        if (priorityString != null)
            priority = Integer.parseInt(priorityString);
    }

    public EmailLinkWedge(@NonNull String address, int priority) {
        super("email", "@string/title_attribouter_email", "mailto:" + address, "@drawable/ic_attribouter_email", false, priority);
    }

}
