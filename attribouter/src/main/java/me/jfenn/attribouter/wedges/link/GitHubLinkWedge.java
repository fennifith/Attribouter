package me.jfenn.attribouter.wedges.link;

import androidx.annotation.NonNull;

public class GitHubLinkWedge extends LinkWedge {

    public GitHubLinkWedge(@NonNull String name, int priority) {
        this(name, priority, false);
    }

    public GitHubLinkWedge(@NonNull String item, int priority, boolean isFullUrl) {
        super("github", "@string/title_attribouter_github", isFullUrl ? item : "https://github.com/" + item, "@drawable/ic_attribouter_github", false, priority);
    }

}
