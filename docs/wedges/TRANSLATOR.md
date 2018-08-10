---
title: TranslatorWedge
---

The `TranslatorWedge` displays information about a translator in an only slightly more concise layout than the [`ContributorWedge`](./CONTRIBUTOR.md).

## Example

```xml
<me.jfenn.attribouter.wedges.TranslatorWedge
        login="TheAndroidMaster"
        name="James Fenn"
        avatar="https://avatars3.githubusercontent.com/u/13000407"
        locales="en,fr"
        blog="https://jfenn.me/" />
```

## Attributes

|Attribute|Type|Description|
|-----|-----|-----|
|login|String|The GitHub username/login of the translator.|
|name|String / String Resource|The name of the translator.|
|avatar|String (URL) / Drawable Resource|The avatar/profile picture of the translator.|
|locales|String, comma-separated|Locales that have been translated by the translator, separated by a single comma with no whitespace, ex: "en,es,zh".|
|blog|String|The website of the translator.|
|email|String|The email of the translator.|
