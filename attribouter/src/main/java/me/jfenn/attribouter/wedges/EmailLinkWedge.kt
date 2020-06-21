package me.jfenn.attribouter.wedges

class EmailLinkWedge(
        address: String, priority: Int
) : LinkWedge(
        id = "email",
        name = "@string/attribouter_title_email",
        url = "mailto:$address",
        icon = "@drawable/attribouter_ic_email",
        priority = priority
)
