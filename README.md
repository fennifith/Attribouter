Attribouter is a lightweight "about screen" for Android apps, built to allow developers to easily give credit to a project's contributors and open source libraries, while matching the style of their app and saving the largest amount of time and effort possible.

## Usage

### Setup

The gradle dependency will soon be available through jCenter.

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

### Configuration
By default, Attribouter will use the configuration file at [res/xml/attribouter.xml](https://github.com/TheAndroidMaster/Attribouter/blob/master/attribouter/src/main/res/xml/attribouter.xml). You can either name your configuration file "attribouter.xml" to override the resource, or name it differently and call `.withFile(R.xml.[name])` on your instance of `Attribouter` instead.

The configuration file consists of a single root element, `<about>`, with several possible child elements that can be added any amount of times in any order. You can either make one by looking at the [configuration file](https://github.com/TheAndroidMaster/Attribouter/blob/master/app/src/main/res/xml/about.xml) of the sample app for an example, or by using the list below.

#### `<appInfo>`
Displays the app icon, name, version, and links to the project on github and its website if available.

#### `<text>`
A block of text. Has two possible attributes, `text` (a string or string resource, can be formatted in html (links work too)) which defines the text to display, and `centered` (a boolean) which either centers the text (it is left aligned by default).

#### `<contributors>`
Shows a list of the contributors of a project on github, merged with a list of child `<contributor>` elements defined in the configuration file. For example, if a user with the login "TheAndroidMaster" is both in GitHub and the configuration file, its attributes will be merged so that any attributes beginning with a "^" character will override the information from GitHub, and any attributes not beginning with a "^" character will be used while the GitHub information is loading, or if the information from GitHub is not present or unavailable.

##### `<contributor>`

|Attribute|Type|Description|
|-----|-----|-----|
|login|String|The GitHub username/login of the contributor (especially useful for overriding specific attributes of certian contributors.|
|name|String|The name of the contributor.|
|avatar|String (URL) / Drawable|The "profile picture" of the contributor.|
|task|String|A short phrase describing the contributor's role in the project ("Icon Designer", "Founder", etc).|
|bio|String|The biography of the contributor.|
|blog|String (URL)|The contributor's website.|
|email|String|The email of the contributor.|
|position|Integer|If this attribute is given to three contributors with values between 1 and 3 (one each), they will be displayed in a row at the top of the list (1 in the middle, slightly bigger, 2 on the left, 3 on the right).|

#### `<licenses>`
A list of the open source licenses used by the project. Child elements are `<project>` tags with attributes that function similar to the `<contributor>`s ("^" overrides GitHub values, otherwise they're replaced by the GitHub data), except the repository is defined by a `repo` attribute (starting a value with "^" is obsolete if the `repo` attribute is not defined).

##### `<license>`

|Attribute|Type|Description|
|-----|-----|-----|
|repo|String (name/repository)|The GitHub repository to fetch the license's information from.|
|title|String|The name of the project (will be generated from the repository name if not present, ex: "TheAndroidMaster/ColorPickerDialog" -> "Color Picker Dialog").|
|description|String|A description of what the project contains.|
|website|String|A URL of the website for the project.|
|license|String|The "key" of the license the project is under. This will cause Attribouter to fetch information from the [GitHub Licenses API](https://developer.github.com/v3/licenses/).|
|licenseName|String|The name of the license.|
|licenseBody|String|The content of the license.|
|licenseUrl|String|The URL of the license.|
