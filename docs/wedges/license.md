---
title: LicenseWedge
---

The `LicenseWedge` is intended to display information about libraries used by your project. Simply displaying this wedge does not guarantee that you are following all of the conditions of the project's license, but it is a good start.

If only the `repo` attribute is specified, this wedge can fetch all of its necessary information from the GitHub API, however it is a good idea to include all of the information in the configuration file as well, so that it is still displayed if the user is offline or there is an issue with the GitHub API.

## Example

```xml
<me.jfenn.attribouter.wedges.LicenseWedge
        repo="TheAndroidMaster/Attribouter"
        description="I'm pickle riiiiiiiiiiiiiiiiiiiiiiiiick!"
        website="https://jfenn.me/about/?Attribouter"
        license="apache-2.0"
        licenseName="Apache License 2.0"
        licenseBody="@string/license_body_apache2"
        licenseUrl="https://choosealicense.com/licenses/apache-2.0/" />
```

## Attributes

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

## Auto-generated Links

The links that are automatically created by this wedge are as follows. See [`LinkWedge`](./link) for more information.

|ID|Description|Required Attributes|
|-----|-----|-----|
|github|The github repo of the project.|`repo`|
|website|The project's website.|`website` or `repo` (if the project has a website defined on their GitHub repo)|
|license|Information about the license that the project is under.|`license`, `licenseUrl`, or `repo` (if the repo's license is supported by the GitHub API)|
