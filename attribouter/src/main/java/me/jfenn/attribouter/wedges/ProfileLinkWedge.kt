package me.jfenn.attribouter.wedges

class ProfileLinkWedge(
        url: String, priority: Int
) : LinkWedge(
        id = "profile",
        name = "@string/attribouter_title_profile",
        url = url,
        icon = "@drawable/attribouter_ic_person",
        priority = priority
)
