Attribouter is a lightweight "about screen" for Android apps, built to allow developers to easily give credit to a project's contributors and open source libraries, while matching the style of their app and saving the largest amount of time and effort possible.

For demonstration and experimentation, an apk of the sample project can be downloaded [here](https://github.com/TheAndroidMaster/Attribouter/releases/).

## Screenshots

This is just sample data. It is not real. Though Jahir is lazy, so that part is (joking).

|Contributors|Contributor|Licenses|License|
|-----|-----|-----|-----|
|![img](https://jfenn.me/images/screenshots/Attribouter-Main.png)|![img](https://jfenn.me/images/screenshots/Attribouter-Contributor.png)|![img](https://jfenn.me/images/screenshots/Attribouter-Licenses.png)|![img](https://jfenn.me/images/screenshots/Attribouter-License.png)|

## Usage

### Setup

The Gradle dependency is available through jCenter, which is used by default in Android Studio. To add the dependency to your project, copy this line into the dependencies section of your app's build.gradle file.
 
```gradle
compile 'me.jfenn:attribouter:0.0.1'
```

#### Starting an Activity
This is pretty simple.

``` java
Attribouter.from(context).show();
```

#### Creating a Fragment
This is also pretty simple.

``` java
Fragment fragment = Attribouter.from(context).toFragment();
```

### Request Limits

This library does not use an auth key for the GitHub API by default. It does cache data for up to 10 days to avoid crossing GitHub's [rate limits](https://developer.github.com/v3/rate_limit/), but if your project has more than a few contributors and libraries *or* you want it to have access to a private repository, you will need to provide an auth token by calling `.withGitHubToken(token)` on your instance of `Attribouter`.

### Configuration
By default, Attribouter will use the configuration file at [res/xml/attribouter.xml](https://github.com/TheAndroidMaster/Attribouter/blob/master/attribouter/src/main/res/xml/attribouter.xml). You can either name your configuration file "attribouter.xml" to override the resource, or name it differently and call `.withFile(R.xml.[name])` on your instance of `Attribouter` instead.

The configuration file consists of a single root element, `<about>`, with several possible child elements that can be added any amount of times in any order. You can either make one by looking at the [configuration file](https://github.com/TheAndroidMaster/Attribouter/blob/master/app/src/main/res/xml/about.xml) of the sample app for an example, or by using the list below.

#### `<appInfo>`
Displays the app icon, name, version, and links to the project on github and its website if available.

|Attribute|Type|Description|
|-----|-----|-----|
|repo|String (name/repository)|The github repository to fetch data from.|
|icon|String (URL) / Drawable Resource|The app icon to display.|
|description|String / String Resource|A short description of the app/project.|
|playStoreUrl|String / String Resource (URL)|The URL of the app on the Play Store (generated from the package name by default).|
|showPlayStoreUrl|Boolean|Whether to display the "rate" button (defaults to true).|
|websiteUrl|String / String Resource (URL)| The website of the project.|
|gitHubUrl|String / String Resource (URL)|The URL for the open source GitHub project. You do not need to define this if it is the same as `repo`.|

#### `<text>`
A block of text.

|Attribute|Type|Description|
|-----|-----|-----|
|text|String / String Resource (HTML)|A string or string resource, can be formatted in html (links work too) that defines the text to display.|
|centered|Boolean|Whether the text should be centered.|

#### `<contributors>`
Shows a list of the contributors of a project on github, merged with a list of child `<contributor>` elements defined in the configuration file. For example, if a user with the login "TheAndroidMaster" is both in GitHub and the configuration file, its attributes will be merged so that any attributes beginning with a "^" character will override the information from GitHub, and any attributes not beginning with a "^" character will be used while the GitHub information is loading, or if the information from GitHub is not present or unavailable.

|Attribute|Type|Description|
|-----|-----|-----|
|repo|String (name/repository)|The GitHub repository to fetch contributors from.|
|title|String / String Resource|The title to show above the contributors (defaults to @string/title_attribouter_contributors / "Contributors").|

##### `<contributor>`

|Attribute|Type|Description|
|-----|-----|-----|
|login|String|The GitHub username/login of the contributor (especially useful for overriding specific attributes of certain contributors.|
|name|String / String Resource|The name of the contributor.|
|avatar|String (URL) / Drawable Resource|The "profile picture" of the contributor.|
|task|String / String Resource|A short phrase describing the contributor's role in the project ("Icon Designer", "Founder", etc).|
|bio|String / String Resource|The biography of the contributor.|
|blog|String / String Resource (URL)|The contributor's website.|
|email|String / String Resource|The email of the contributor.|
|position|Integer|If this attribute is given to three contributors with values between 1 and 3 (one each), they will be displayed in a row at the top of the list (1 in the middle, slightly bigger, 2 on the left, 3 on the right).|
|hidden|Boolean|Whether to remove the contributor from the list. This is only really useful if you want to remove certain contributors that are fetched from GitHub. Default value is 'false', obviously.|

#### `<licenses>`
A list of the open source licenses used by the project. Child elements are `<project>` tags with attributes that function similar to the `<contributor>`s ("^" overrides GitHub values, otherwise they're replaced by the GitHub data), except the repository is defined by a `repo` attribute (starting a value with "^" is obsolete if the `repo` attribute is not defined).


|Attribute|Type|Description|
|-----|-----|-----|
|title|String / String Resource|The title to show above the licenses (defaults to @string/title_attribouter_licenses / "Open Source Licenses")|

##### `<license>`

|Attribute|Type|Description|
|-----|-----|-----|
|repo|String (name/repository)|The GitHub repository to fetch the license's information from.|
|title|String / String Resource|The name of the project (will be generated from the repository name if not present, ex: "TheAndroidMaster/ColorPickerDialog" -> "Color Picker Dialog").|
|description|String / String Resource|A description of what the project contains.|
|website|String / String Resource|A URL of the website for the project.|
|license|String|The "key" of the license the project is under. This will cause Attribouter to fetch information from the [GitHub Licenses API](https://developer.github.com/v3/licenses/).|
|licenseName|String / String Resource|The name of the license.|
|licenseBody|String / String Resource|The content of the license.|
|licenseUrl|String / String Resource|The URL of the license.|

### Things You Can Override

Couple strings, drawables, themes, etc. If you make any translations, feel free to make a PR because I like PRs. PRs are nice.

|The thing|What the thing is|What the thing's supposed to do|
|-----|-----|-----|
|@style/AttribouterTheme|Extension of `Theme.AppCompat.Light.NoActionBar`|Defines colors and stuff.|
|@string/title_attribouter_about|"About"|Toolbar title.|
|@string/title_attribouter_version|"Version %1$s"|The app version thing name thing.|
|@string/title_attribouter_contributors|"Contributors"|Header of the contributors section.|
|@string/title_attribouter_licenses|"Open Source Licenses"|Header of the licenses section.|
|@string/title_attribouter_license|"License"|I can't remember, but this probably does something somewhere.|
|@string/title_attribouter_license_permissions|"Permissions"|Title of the 'permissions' section of the license dialog.|
|@string/title_attribouter_license_conditions|"Conditions"|Title of the 'conditions' section of the license dialog.|
|@string/title_attribouter_license_limitations|"Limitations"|Title of the 'limitations' section of the license dialog.|
|@string/title_attribouter_more_info|"More Info"|Title of the 'more info' button at the bottom of the license dialog.|
|@string/title_attribouter_email|"Email"|Title of the "email" button in the contributor dialog.|
|@string/title_attribouter_website|"Website"|Title of the website buttons on... yeah just about everything.|
|@string/title_attribouter_github|"GitHub"|Title of the GitHub buttons on... also just about everything.|
|@string/title_attribouter_rate|"Rate"|Title of the Rate button.|
|@color/attribouter_licensePermissionsColor|#00C853|Color of the 'permissions' header in the license dialog.|
|@color/attribouter_licenseConditionsColor|#0091EA|Color of the 'conditions' header in the license dialog.|
|@color/attribouter_licenseLimitationsColor|#EF5350|Color of the 'limitations' header in the license dialog.|
|@drawable/ic_attribouter_arrow_back|--|Back arrow used in the dialog.|
|@drawable/ic_attribouter_copyright|--|Copyright icon. Buttons.|
|@drawable/ic_attribouter_email|--|Email icon. Buttons.|
|@drawable/ic_attribouter_github|--|GitHub icon. Buttons.|
|@drawable/ic_attribouter_link|--|Link icon. Buttons.|
|@drawable/ic_attribouter_rate|--|Rate/star icon. Buttons.|
|@drawable/bg_attribouter_emphasized|--|Rounded & slightly darkened background for the license names / text (in dialogs).|
