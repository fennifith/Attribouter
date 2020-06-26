package me.jfenn.attribouter.wedges

class RepoLinkWedge(
        name: String = "@string/attribouter_title_git",
        url: String,
        icon: String = "@drawable/attribouter_ic_git",
        priority: Int
) : LinkWedge(
        id = "git",
        name = name,
        url = url,
        icon = icon,
        priority = priority
)
