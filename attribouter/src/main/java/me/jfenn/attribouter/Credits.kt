package me.jfenn.attribouter

import me.jfenn.attribouter.wedges.ContributorWedge
import me.jfenn.attribouter.wedges.ContributorsWedge
import me.jfenn.attribouter.wedges.LicenseWedge
import me.jfenn.attribouter.wedges.LicensesWedge

fun ContributorsWedge.addDefaults() {
    addChildren(listOf(
            ContributorWedge(
                    login = "fennifith",
                    name = "James Fenn",
                    avatarUrl = "https://avatars1.githubusercontent.com/u/13000407",
                    task = "^Library Maintainer",
                    bio = "Enjoys writing software on loud keyboards. Starts too many projects. Consumes food.",
                    websiteUrl = "https://jfenn.me/",
                    email = "dev@jfenn.me"
            ).create(lifecycle),
            ContributorWedge(
                    login = "rroyGit",
                    name = "Rupam Roy",
                    avatarUrl = "https://avatars2.githubusercontent.com/u/20290568",
                    task = "^Contributor"
            ).create(lifecycle),
            ContributorWedge(
                    login = "divadsn",
                    name = "David Sn",
                    avatarUrl = "https://avatars0.githubusercontent.com/u/28547847",
                    task = "^Contributor",
                    websiteUrl = "https://www.codebucket.de/"
            ).create(lifecycle),
            ContributorWedge(
                    login = "gcantoni",
                    name = "Giorgio Cantoni",
                    avatarUrl = "https://avatars3.githubusercontent.com/u/30368951",
                    task = "^Contributor",
                    websiteUrl = "https://giorgiocantoni.it/"
            ).create(lifecycle)
    ))

    requestContributors("github:fennifith/Attribouter")
}

fun LicensesWedge.addDefaults() {
    addChildren(listOf(
            LicenseWedge(
                    repo = "fennifith/Attribouter",
                    title = "Attribouter",
                    description = "A lightweight \"about screen\" library to allow quick but customizable attribution in Android apps.",
                    licenseName = "Apache License 2.0",
                    repoUrl = "https://github.com/fennifith/Attribouter",
                    licenseKey = "apache-2.0"
            ).create(lifecycle),
            LicenseWedge(
                    repo = "gitea@code.horrific.dev:james/git-rest-wrapper"
            ).create(lifecycle),
            LicenseWedge(
                    repo = "google/flexbox-layout",
                    title = "FlexBox Layout",
                    description = "FlexboxLayout is a library that brings similar capabilities to the CSS Flexible Box Layout to Android.",
                    licenseName = "Apache License 2.0",
                    repoUrl = "https://github.com/google/flexbox-layout",
                    licenseKey = "apache-2.0"
            ).create(lifecycle),
            LicenseWedge(
                    repo = "bumptech/glide",
                    title = "Glide",
                    description = "An image loading and caching library for Android focused on smooth scrolling",
                    licenseName = "Other",
                    websiteUrl = "https://bumptech.github.io/glide/",
                    repoUrl = "https://github.com/bumptech/glide",
                    licenseUrl = "https://raw.githubusercontent.com/bumptech/glide/master/LICENSE"
            ).create(lifecycle),
            LicenseWedge(
                    repo = "square/retrofit",
                    title = "Retrofit",
                    description = "Type-safe HTTP client for Android and Java by Square, Inc.",
                    licenseName = "Apache License 2.0",
                    websiteUrl = "https://square.github.io/retrofit/",
                    licenseKey = "apache-2.0"
            ).create(lifecycle),
            LicenseWedge(
                    title = "Android Open Source Project",
                    description = "Android is an open source software stack for a wide range of mobile devices and a corresponding open source project led by Google.",
                    licenseName = "Apache License 2.0",
                    licenseUrl = "https://source.android.com/license",
                    websiteUrl = "https://github.com/aosp-mirror",
                    licenseKey = "apache-2.0"
            ).create(lifecycle)
    ))
}