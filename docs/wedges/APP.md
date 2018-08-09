Displays the app icon, name, version, and links to the project on github and its website if available.

|Attribute|Type|Description|
|-----|-----|-----|
|repo|String (name/repository)|The github repository to fetch data from.|
|icon|String (URL) / Drawable Resource|The app icon to display.|
|description|String / String Resource|A short description of the app/project.|
|playStoreUrl|String / String Resource (URL)|The URL of the app on the Play Store (generated from the package name by default).|
|websiteUrl|String / String Resource (URL)| The website of the project.|
|gitHubUrl|String / String Resource (URL)|The URL for the open source GitHub project. You do not need to define this if it is the same as `repo`.|

Auto-generated links for the `<appInfo>` tag are as follows:

|ID|Description|Required Attributes|
|-----|-----|-----|
|github|The github repository of the project.|`repo` or `gitHubUrl`|
|website|The website of the project.|`repo` (if the repo has a website assigned) or `websiteUrl`|
|playStore|A 'rate' button that opens the app in the play store.|`playStoreUrl` or `repo` (if the repository url is the play store url)|