package me.jfenn.attribouter.wedges

class WebsiteLinkWedge(
        url: String, priority: Int
) : LinkWedge(
        id = "website",
        name = "@string/title_attribouter_website",
        url = url,
        icon = "@drawable/ic_attribouter_link",
        priority = priority
)
