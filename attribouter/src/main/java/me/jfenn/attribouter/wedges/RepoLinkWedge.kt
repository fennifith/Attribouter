package me.jfenn.attribouter.wedges

class RepoLinkWedge(
        item: String, priority: Int, isFullUrl: Boolean = false
) : LinkWedge(
        id = "git",
        name = "@string/attribouter_title_git",
        url = if (isFullUrl) item else "https://github.com/$item",
        icon = "@drawable/attribouter_ic_git",
        priority = priority
)
