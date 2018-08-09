A list of the translators of this app, sorted into sections by the language(s) that they have translated. Child elements are `<translator>` tags.

|Attribute|Type|Description|
|-----|-----|-----|
|title|String / String Resource|The title to show above the translators (defaults to @string/title_attribouter_translators / "Translators")|
|overflow|Integer (>= -1)|The maximum number of translators to display in the list. Additional translators are displayed in a dialog. If the number is 0, the entire list is replaced with a button titled "View %s" ('%s' being the title attribute). If the number is -1, all of the translators are displayed.|