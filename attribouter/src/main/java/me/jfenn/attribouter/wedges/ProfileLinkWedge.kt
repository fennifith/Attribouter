package me.jfenn.attribouter.wedges

class ProfileLinkWedge(
        name: String = "@string/attribouter_title_profile",
        url: String,
        icon: String = "@drawable/attribouter_ic_person",
        priority: Int
) : LinkWedge(
        id = "profile",
        name = name,
        url = url,
        icon = icon,
        priority = priority
)
