---
title: The Configuration File
---

# The Configuration File

The great File of Configuration is truly a mighty hurdle to overcome when implementing this library. It contains many confusing and difficult concepts such as "tags" and "attributes". However, when it comes down to it, it is really just a weird form of layout file that specifies the information to display instead of how to display it.
 
For no specific reason, each configuration file must have an `<about>` tag as its root element. Inside of that tag, any amount of children can be defined in any order. When the about screen is opened, these tags will be parsed and their corresponding objects will be created, much like inflating a layout file. The objects are then added to a list to be displayed in a RecyclerView. In order to simplify things, these tags, their corresponding objects, and the views created for them in the RecyclerView will be referred to as "wedges".

Because wedge classes are instantiated from the configruation file, the wedge's tag in the file must be the class name of the wedge that should be instantiated, including the package name. This is the same as adding a custom view to a layout file.

With this information, we can now create a simple configuration file using the [`App`](./wedges/app), [`Contributors`](./wedges/contributors), and [`Licenses`](./wedges/licenses) wedges as follows.

```xml
<about>

  <me.jfenn.attribouter.wedges.AppWedge/>
  
  <me.jfenn.attribouter.wedges.ContributorsWedge/>
  
  <me.jfenn.attribouter.wedges.LicensesWedge/>
  
</about>
```

This will create a screen that displays information about your app, followed by its contributors and finally the open source licenses that you have used. However, the only information that is currently displayed is what Attribouter knows how to find by itself; the `AppWedge` displays your app's icon, name, and a link to rate your app on the Play Store, the `ContributorsWedge` displays the people that have contributed to Attribouter, and the `LicensesWedge` displays the licenses that Attribouter uses. In order to add information to these wedges, you can use one of the following options:

## Option 1: The GitHub API

As stated in their documentation, both the [`AppWedge`](./wedges/app) and the [`ContributorsWedge`](./wedges/contributors) have an attribute titled `repo` that accepts the full name of a GitHub repository. Let's say that the repository for your app is [TheAndroidMaster/Pasta-for-Spotify](https://jfenn.me/redirects/?t=github&d=Pasta-for-Spotify). In that case, you can modify the file as follows:

```xml
<about>

  <me.jfenn.attribouter.wedges.AppWedge
    repo="TheAndroidMaster/Pasta-for-Spotify" />
    
  <me.jfenn.attribouter.wedges.ContributorsWedge
    repo="TheAndroidMaster/Pasta-for-Spotify" />
    
  <me.jfenn.attribouter.wedges.LicensesWedge/>
  
</about>
```

The `AppWedge` now displays the repository's description, a link to the Pasta-for-Spotify repository on GitHub, and a link to the project's homepage ([https://jfenn.me/apps/pasta](https://jfenn.me/apps/pasta/)), and the `ContributorsWedge` includes all of [the people that have contributed to it](https://jfenn.me/redirects/?t=github&d=Pasta-for-Spotify/graphs/contributors).

## Option 2: Attributes and Children

If your project does not have a GitHub repository or you want some information to be available without an internet connection, you should define that information in the configuration file. Using the documentation pages for `AppWedge` and `ContributorsWedge`, the equivalent of Option 1 (without the GitHub link in the `AppWedge`) would be as follows:

```xml
<about>

  <me.jfenn.attribouter.wedges.AppWedge
    description="A material design Spotify client for Android"
    websiteUrl="https://jfenn.me/apps/pasta/" />
    
  <me.jfenn.attribouter.wedges.ContributorsWedge>
  
    <me.jfenn.attribouter.wedges.ContributorWedge
      name="James Fenn"
      avatar="https://avatars3.githubusercontent.com/u/13000407"
      task="Developer"
      bio="Android developer and co-founder of @DoubleDotLabs. Writes Java, C, and HTML. PHP confuses me."
      blog="https://jfenn.me/"
      email="me@jfenn.me" />
      
    <me.jfenn.attribouter.wedges.ContributorWedge
      name="Jan-Lukas Else"
      avatar="https://avatars3.githubusercontent.com/u/8822316"
      task="Contributor"
      blog="https://about.jlelse.de" />
      
    <me.jfenn.attribouter.wedges.ContributorWedge
      name="Alexandre Piveteau"
      avatar="https://avatars1.githubusercontent.com/u/6318990"
      task="Contributor"
      bio="Student in Computer Science @ETHZ. Consulting @ Taktil GmbH"
      blog="https://alexandrepiveteau.ch" />
  
  </me.jfenn.attribouter.wedges.ContributorsWedge>
    
  <me.jfenn.attribouter.wedges.LicensesWedge/>
  
</about>
```

## Option 3: Both

> "This should be fairly easy. All that you need to do is combine the two previous options, right?"

Actually, there is one extra step - if you do not define the `login` attribute on all of the [`ContributorWedge`](./wedges/contributor)s, you will end up with two sets of contributors - one from the configuration file and one from the GitHub API. Similar attributes are used to merge duplicate wedges like [`LinkWedge`](./wedges/link)s and [`LicenseWedge`](./wedges/license)s, so you should check out ["Overriding Resources and Providing Translations"](../resources) if the results of this option confuse you. When you are done, the end result should look something like this:

```xml
<about>

  <me.jfenn.attribouter.wedges.AppWedge
    repo="TheAndroidMaster/Pasta-for-Spotify"
    description="A material design Spotify client for Android"
    websiteUrl="https://jfenn.me/apps/pasta/" />
    
  <me.jfenn.attribouter.wedges.ContributorsWedge
    repo="TheAndroidMaster/Pasta-for-Spotify" >
  
    <me.jfenn.attribouter.wedges.ContributorWedge
      login="TheAndroidMaster"
      name="James Fenn"
      avatar="https://avatars3.githubusercontent.com/u/13000407"
      task="Developer"
      bio="Android developer and co-founder of @DoubleDotLabs. Writes Java, C, and HTML. PHP confuses me."
      blog="https://jfenn.me/"
      email="me@jfenn.me" />
      
    <me.jfenn.attribouter.wedges.ContributorWedge
      login="jlelse"
      name="Jan-Lukas Else"
      avatar="https://avatars3.githubusercontent.com/u/8822316"
      task="Contributor"
      blog="https://about.jlelse.de" />
      
    <me.jfenn.attribouter.wedges.ContributorWedge
      login="alexandrepiveteau"
      name="Alexandre Piveteau"
      avatar="https://avatars1.githubusercontent.com/u/6318990"
      task="Contributor"
      bio="Student in Computer Science @ETHZ. Consulting @ Taktil GmbH"
      blog="https://alexandrepiveteau.ch" />
  
  </me.jfenn.attribouter.wedges.ContributorsWedge>
    
  <me.jfenn.attribouter.wedges.LicensesWedge/>
  
</about>
```
