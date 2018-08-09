A list of the open source licenses used by the project. Child elements are `<project>` tags with attributes that function similar to the `<contributor>`s ("^" overrides GitHub values, otherwise they're replaced by the GitHub data), except the repository is defined by a `repo` attribute (starting a value with "^" is obsolete if the `repo` attribute is not defined).


|Attribute|Type|Description|
|-----|-----|-----|
|title|String / String Resource|The title to show above the licenses (defaults to @string/title_attribouter_licenses / "Open Source Licenses")|
|overflow|Integer (>= -1)|The maximum number of projects to display in the list. Additional projects are displayed in a dialog. If the number is 0, the entire list is replaced with a button titled "View %s" ('%s' being the title attribute). If the number is -1, all of the projects are displayed.|
|showDefaults|Boolean|Whether or not to show the default projects in this list (this library and the projects used by it).|