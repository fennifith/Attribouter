Attribouter
[![](https://jitpack.io/v/me.jfenn/Attribouter.svg)](https://jitpack.io/#me.jfenn/Attribouter)
[![Build Status](https://github.com/fennifith/Attribouter/workflows/Gradle%20Build/badge.svg)](https://github.com/fennifith/Alarmio/actions)
[![Discord](https://img.shields.io/discord/514625116706177035.svg?logo=discord&colorB=7289da)](https://discord.jfenn.me/)
[![Liberapay](https://img.shields.io/badge/liberapay-donate-yellow.svg?logo=liberapay)](https://liberapay.com/fennifith/donate)
=====

Attribouter is a lightweight "about screen" for Android apps, built to allow developers to easily give credit to a project's contributors and open source libraries, while matching the style of their app and saving the largest amount of time and effort possible. It is meant to use GitHub's [REST API](https://developer.github.com/v3/) to fetch and display information about open source projects and contributors, but it allows you to define some or all of its data in its configuration file in your app as well.

### Screenshots

| Contributors | Contributor | Licenses | License |
|--------------|-------------|----------|---------|
| ![img](https://jfenn.me/images/screenshots/Attribouter-Main.png) | ![img](https://jfenn.me/images/screenshots/Attribouter-Contributor.png) | ![img](https://jfenn.me/images/screenshots/Attribouter-Licenses.png) | ![img](https://jfenn.me/images/screenshots/Attribouter-License.png) |

### APK

For demonstration and experimentation, an apk of the sample project can be downloaded [here](../../releases/).

## Usage

This project is published on [JitPack](https://jitpack.io), which you can add to your project by copying the following to your root build.gradle at the end of "repositories".

```gradle
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

To add the dependency, copy this line into your app module's build.gradle file.

```gradle
implementation 'me.jfenn:Attribouter:0.1.6'
```

### Starting an Activity
This is pretty simple.

``` kotlin
Attribouter.from(context).show();
```

### Creating a Fragment
This is also pretty simple.

``` kotlin
val fragment = Attribouter.from(context).toFragment();
```

## Things to Note

### Request Limits

This library does not use an auth key for the GitHub API by default. It does cache data to avoid crossing GitHub's [rate limits](https://developer.github.com/v3/rate_limit/), but if your project has more than a few contributors and libraries *or* you want it to have access to a private repository, you will need to provide an auth token by calling `.withGitHubToken(token)` on your instance of `Attribouter`.

_Be careful not to include this token with your source code._ There are other methods of providing your token at build-time, such as using a [BuildConfig field](https://developer.android.com/studio/build/gradle-tips#share-custom-fields-and-resource-values-with-your-app-code) with an environment variable, that can prevent this from being an issue. These tokens aren't especially dangerous without any scopes/permissions, but GitHub will automatically deactivate them if they show up in any commits/files on their services, which could cause problems for Attribouter.

### Configuration

By default, Attribouter will use the configuration file at [res/xml/attribouter.xml](./attribouter/src/main/res/xml/attribouter.xml). You can either name your configuration file "attribouter.xml" to override the resource, or name it differently and call `.withFile(R.xml.[name])` on your instance of `Attribouter` instead.

The configuration file consists of a single root element, `<about>`, with many child elements that can be placed any amount of times in any order, the same as views in a layout file. These elements, called "wedges" in this library for no apparent reason, are created by Attribouter and added to the page in the order and heirarchy that they are defined in. To create your configuration file, you can either use the [file from the sample project](./app/src/main/res/xml/about.xml) as a template or use [the documentation](https://jfenn.me/projects/attribouter/wiki) to write your own.

### Proguard / Minification

For those using the R8 compiler, Attribouter's [proguard rules](./attribouter/consumer-rules.pro) should be conveniently bundled with the library already - otherwise, you will need to add them to your app's `proguard-rules.pro` file yourself to prevent running into any issues with `minifyEnabled` and the like.

Unfortunately, Attribouter still doesn't behave well with `shrinkResources`, as the compiler cannot detect references from Attribouter's config file and will exclude them from compilation. There is a [workaround](https://developer.android.com/studio/build/shrink-code#shrink-resources) to this, however - create a `<resources>` tag somewhere in your project, and specify `tools:keep="@{resource}"` for all of the strings and drawables referenced by your config file. For all of Attribouter's own resources, this has already been done - if you are not referencing any resources in your configuration, then there shouldn't be an issue.

## Used in

- [Lawnchair Launcher](https://github.com/LawnchairLauncher/Lawnchair)
- [EtchDroid](https://github.com/EtchDroid/EtchDroid)
- [Status](https://github.com/fennifith/Status)
- [Alarmio](https://github.com/fennifith/Alarmio)

If you're using Attribouter in your project, feel free to reach out / make a PR to add it to this list!

## Contributing

See [CONTRIBUTING.md](./.github/CONTRIBUTING.md) for information on how to contribute to this project.
