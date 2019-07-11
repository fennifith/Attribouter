package me.jfenn.attribouter.wedges.link

class EmailLinkWedge(
        address: String, priority: Int
) : LinkWedge(
        id = "email",
        name = "@string/title_attribouter_email",
        url = "mailto:$address",
        icon = "@drawable/ic_attribouter_email",
        priority = priority
)
