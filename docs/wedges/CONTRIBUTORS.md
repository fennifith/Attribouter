Shows a list of the contributors of a project on github, merged with a list of child `<contributor>` elements defined in the configuration file. For example, if a user with the login "TheAndroidMaster" is both in GitHub and the configuration file, its attributes will be merged so that any attributes beginning with a "^" character will override the information from GitHub, and any attributes not beginning with a "^" character will be used while the GitHub information is loading, or if the information from GitHub is not present or unavailable.

|Attribute|Type|Description|
|-----|-----|-----|
|repo|String (name/repository)|The GitHub repository to fetch contributors from.|
|title|String / String Resource|The title to show above the contributors (defaults to @string/title_attribouter_contributors / "Contributors").|
|overflow|Integer (>= -1)|The maximum number of contributors to display in the list. Additional contributors are displayed in a dialog. If the number is 0, the entire list is replaced with a button titled "View %s" ('%s' being the title attribute). If the number is -1, all of the contributors are displayed.|
|showDefaults|Boolean|Whether or not to show the default contributors in this list (me and the contributors to this repository).|