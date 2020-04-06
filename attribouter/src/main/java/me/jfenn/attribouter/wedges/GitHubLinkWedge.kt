package me.jfenn.attribouter.wedges

class GitHubLinkWedge(
        item: String, priority: Int, isFullUrl: Boolean = false
) : LinkWedge(
        id = "github",
        name = "@string/title_attribouter_github",
        url = if (isFullUrl) item else "https://github.com/$item",
        icon = "@drawable/ic_attribouter_github",
        priority = priority
)
