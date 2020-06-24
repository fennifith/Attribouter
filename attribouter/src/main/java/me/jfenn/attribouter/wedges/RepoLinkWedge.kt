package me.jfenn.attribouter.wedges

class RepoLinkWedge(
        item: String, priority: Int
) : LinkWedge(
        id = "git",
        name = "@string/attribouter_title_git",
        url = item,
        icon = "@drawable/attribouter_ic_git",
        priority = priority
)
