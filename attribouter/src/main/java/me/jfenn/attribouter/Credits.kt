package me.jfenn.attribouter

import me.jfenn.attribouter.wedges.ContributorWedge
import me.jfenn.attribouter.wedges.ContributorsWedge
import me.jfenn.attribouter.wedges.LicenseWedge
import me.jfenn.attribouter.wedges.LicensesWedge

fun ContributorsWedge.addDefaults() {
    addChildren(listOf(
            ContributorWedge(
                    login = "github:fennifith",
                    name = "James Fenn",
                    avatarUrl = "https://avatars1.githubusercontent.com/u/13000407",
                    task = "^Library Maintainer",
                    bio = "Enjoys writing software on loud keyboards. Starts too many projects. Consumes food.",
                    websiteUrl = "https://jfenn.me/",
                    email = "dev@jfenn.me"
            ).create(lifecycle),
            ContributorWedge(
                    login = "github:kevttob",
                    name = "Kevin Aguilar",
                    avatarUrl = "https://avatars3.githubusercontent.com/u/16209409",
                    task = "^Designer",
                    websiteUrl = "https://221pxls.com/"
            ).create(lifecycle),
            ContributorWedge(
                    login = "github:rroyGit",
                    name = "Rupam Roy",
                    avatarUrl = "https://avatars2.githubusercontent.com/u/20290568",
                    task = "^Contributor"
            ).create(lifecycle),
            ContributorWedge(
                    login = "github:divadsn",
                    name = "David Sn",
                    avatarUrl = "https://avatars0.githubusercontent.com/u/28547847",
                    task = "^Contributor",
                    websiteUrl = "https://www.codebucket.de/"
            ).create(lifecycle),
            ContributorWedge(
                    login = "github:gcantoni",
                    name = "Giorgio Cantoni",
                    avatarUrl = "https://avatars3.githubusercontent.com/u/30368951",
                    task = "^Contributor",
                    websiteUrl = "https://giorgiocantoni.it/"
            ).create(lifecycle),
            ContributorWedge(
                    login = "github:jahirfiquitiva",
                    name = "Jahir Fiquitiva",
                    avatarUrl = "https://avatars1.githubusercontent.com/u/10360816",
                    task = "^Contributor",
                    websiteUrl = "https://jahir.dev/"
            ).create(lifecycle)
    ))

    requestContributors("github:fennifith/Attribouter")
}

fun LicensesWedge.addDefaults() {
    addChildren(listOf(
            LicenseWedge(
                    repo = "github:fennifith/Attribouter",
                    title = "Attribouter",
                    description = "A lightweight \"about screen\" library to allow quick but customizable attribution in Android apps.",
                    licenseName = "Apache License 2.0",
                    licenseKey = "apache-2.0"
            ).create(lifecycle),
            LicenseWedge(
                    repo = "gitea@code.horrific.dev:james/git-rest-wrapper",
                    title = "^Git REST Wrapper",
                    description = "A 'universal' / normalized API wrapper for git hosting platforms.",
                    licenseName = "Mozilla Public License 2.0",
                    licenseKey = "mpl-2.0"
            ).create(lifecycle),
            LicenseWedge(
                    repo = "github:google/flexbox-layout",
                    title = "FlexBox Layout",
                    description = "FlexboxLayout is a library that brings similar capabilities to the CSS Flexible Box Layout to Android.",
                    licenseName = "Apache License 2.0",
                    licenseKey = "apache-2.0"
            ).create(lifecycle),
            LicenseWedge(
                    repo = "github:bumptech/glide",
                    title = "Glide",
                    description = "An image loading and caching library for Android focused on smooth scrolling",
                    licenseName = "Other",
                    websiteUrl = "https://bumptech.github.io/glide/",
                    licenseUrl = "https://raw.githubusercontent.com/bumptech/glide/master/LICENSE"
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