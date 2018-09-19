---
title: TranslatorsWedge
---

The `TranslatorsWedge` displays a list of [`TranslatorWedge`](./translator.md)s in sections, sorted by locale. If a translator has translated multiple locales, they will appear under every locale that they have translated.

## Example

```xml
<me.jfenn.attribouter.wedges.TranslatorsWedge
        title="Translators"
        overflow="10" >
  
    <!-- translators -->
  
</me.jfenn.attribouter.wedges.TranslatorsWedge>
```

## Attributes

|Attribute|Type|Description|
|-----|-----|-----|
|title|String / String Resource|The title to show above the translators (defaults to @string/title_attribouter_translators / "Translators")|
|overflow|Integer (>= -1)|The maximum number of translators to display in the list. Additional translators are displayed in a dialog. If the number is 0, the entire list is replaced with a button titled "View %s" ('%s' being the title attribute). If the number is -1, all of the translators are displayed.|
