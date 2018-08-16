---
title: Custom Themes
---

## Activity / Dialog Themes

By default, Attribouter uses an extension of `Theme.AppCompat.Light.NoActionBar` as its activity theme. The contributor and license dialogs use `ThemeOverlay.AppCompat.Dialog`, and fullscreen dialogs (overflow screens) use an extension of the activity theme with a window animation.

There are multiple ways that you can change these themes, but I know that this one works, so I am going to do this. If you are using Attribouter as a fragment, you can skip the first part since it will use whatever theme you have set for the activity that the fragment is in.

### Overriding the Activity Theme

First, add a new style resource to your `styles.xml` titled "AttribouterTheme". This will override the theme defined by Attribouter that it uses by default. The theme's parent should be "Theme.AppCompat.something", preferably ending with ".NoActionBar" since Attribouter's activity has one of its own. For example, if you wanted to use a dark theme, you would use `Theme.AppCompat.NoActionBar` as follows:

```xml
<resources>
  <style name="AttribouterTheme" parent="Theme.AppCompat.NoActionBar" />
</resources>
```

If you start Attribouter now, it should use a dark theme for the activity and all of its dialogs. What if you want to use a different theme for the dialogs than the activity, though?

### Changing Dialog Themes

There are three custom attributes that Attribouter looks for in the activity theme to tell it what themes to use for its dialogs: `personDialogTheme`, `overflowDialogTheme`, and `licenseDialogTheme`. Let's try changing the contributor dialogs back to a light theme, keeping the rest of Attribouter dark.

```xml
<resources>
  <style name="AttribouterTheme" parent="Theme.AppCompat.NoActionBar" >
    <item name="personDialogTheme">@style/PersonDialogTheme</item>
  </style>
  
  <style name="PersonDialogTheme" parent="ThemeOverlay.AppCompat.Dialog" />
</resources>
```

Yep, that works.

## Overriding Layouts

While this is partially obsolete since [subwedging](./SUBWEDGING.md) is a far cleaner and better solution, it is possible to override some of Attribouter's [layout files](../attribouter/src/main/res/layout/) for small changes (ex: making the circular avatars square). Most of the custom views used in these layout files are only casted to their superclasses (me.jfenn.attribouter.views.CircleImageView is only referenced as an ImageView), so as long as the ids remain the same there should not be any problems.
