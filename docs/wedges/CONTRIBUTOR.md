|Attribute|Type|Description|
|-----|-----|-----|
|login|String|The GitHub username/login of the contributor (especially useful for overriding specific attributes of certain contributors.|
|name|String / String Resource|The name of the contributor.|
|avatar|String (URL) / Drawable Resource|The "profile picture" of the contributor.|
|task|String / String Resource|A short phrase describing the contributor's role in the project ("Icon Designer", "Founder", etc).|
|bio|String / String Resource|The biography of the contributor.|
|blog|String / String Resource (URL)|The contributor's website.|
|email|String / String Resource|The email of the contributor.|
|position|Integer|If this attribute is given to three contributors with values between 1 and 3 (one each), they will be displayed in a row at the top of the list (1 in the middle, slightly bigger, 2 on the left, 3 on the right).|
|hidden|Boolean|Whether to remove the contributor from the list. This is only really useful if you want to remove certain contributors that are fetched from GitHub. Default value is 'false', obviously.|

Auto-generated links for the `<contributor>` tag are as follows:

|ID|Description|Required Attributes|
|-----|-----|-----|
|github|The github profile of the contributor.|`login`|
|website|The website of the contributor.|`blog` or `login` (if the user has a blog defined on their GitHub profile)|
|email|The email of the contributor.|`email` or `login` (if the user has a public email on their GitHub profile)|
