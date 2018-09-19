---
title: Overriding Resources and Providing Translations
---

# Overriding Resources and Providing Translations

## Problem

Currently, Attribouter fetches its information from two different locations: the configuration file that you write, and the GitHub API. However, there will occasionally be some information that is provided by both the file and GitHub. Because Attribouter gets the information from the GitHub API shortly after processing the configuration file, the obvious behavior would be to simply replace the information from the configuration file with its GitHub counterpart. However, that isn't a perfect solution, as shown in the following situations:

- one [`ContributorWedge`](./wedges/CONTRIBUTOR.md) in a [`ContributorsWedge`](./wedges/CONTRIBUTORS.md) does not have a GitHub counterpart, but all of the others do; it would disappear
- a [`ContributorWedge`](./wedges/CONTRIBUTOR.md) has an `email` attribute, but its GitHub counterpart does not provide one; it would disappear
- the [`AppWedge`](./wedges/APP.md)'s `description` attribute is a string resource that is translated into several languages, but the GitHub repositories description is in English; it would always display in English

## Solution

In order to work around these situations, rather than simply replacing duplicate information, Attribouter "merges" them together. Using this method, if an attribute is in the configuration file that doesn't have a GitHub counterpart, it is kept, but all others are overwritten. Likewise, [`ContributorWedge`](./wedges/CONTRIBUTOR.md)s inside of a [`ContributorsWedge`](./wedges/CONTRIBUTORS.md) are merged if their `login` attributes match, and are left alone if they do not (ex: contributors in the config file not present on GitHub are left in the list, and contributors not in the config file but present on GitHub are added as well).

However, you may notice that this solution does not account for the last situation, as it isn't possible to detect this automatically. As a workaround, Attribouter looks for a "^" character at the start of an attribute before merging it. If present, it will ignore its GitHub counterpart. In other words, if the translated string was `@string/app_desc`, the `description` attribute can be set to `^@string/app_desc` to prevent it from being overwritten by the GitHub data.
