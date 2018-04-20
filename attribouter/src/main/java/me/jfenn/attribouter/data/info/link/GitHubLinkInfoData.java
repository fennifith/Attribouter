package me.jfenn.attribouter.data.info.link;

import android.support.annotation.NonNull;

public class GitHubLinkInfoData extends LinkInfoData {

    public GitHubLinkInfoData(@NonNull String name, int priority) {
        this(name, priority, false);
    }

    public GitHubLinkInfoData(@NonNull String item, int priority, boolean isFullUrl) {
        super("github", "@string/title_attribouter_github", isFullUrl ? item : "https://github.com/" + item, "@drawable/ic_attribouter_github", priority);
    }

}
