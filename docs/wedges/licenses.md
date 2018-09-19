---
title: LicensesWedge
---

The `LicensesWedge` displays a list of child [`LicenseWedge`](./LICENSE.md)s with a title at the top. If the `showDefaults` attribute is undefined or `true`, this list will be merged with the licenses used by Attribouter. See ["Overriding Resources and Providing Translations"](../RESOURCES.md) for more information on how merges work.

## Example

```xml
<me.jfenn.attribouter.wedges.LicensesWedge
        title="OSS Licenses"
        overflow="-1" >
  
    <!-- licenses -->
  
</me.jfenn.attribouter.wedges.LicensesWedge>
```

## Attributes

|Attribute|Type|Description|
|-----|-----|-----|
|title|String / String Resource|The title to show above the licenses (defaults to @string/title_attribouter_licenses / "Open Source Licenses")|
|overflow|Integer (>= -1)|The maximum number of projects to display in the list. Additional projects are displayed in a dialog. If the number is 0, the entire list is replaced with a button titled "View %s" ('%s' being the title attribute). If the number is -1, all of the projects are displayed.|
|showDefaults|Boolean|Whether or not to show the default projects in this list (this library and the projects used by it).|
