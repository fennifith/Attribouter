---
title: Automating CrowdIn Translations
---

As stated in [issue #23](https://jfenn.me/redirects/?t=github&d=Attribouter/issues/23), no kind of "proper" support for CrowdIn will be added to Attribouter for a while because [their API does not look very fun](https://support.crowdin.com/api/api-integration-setup/). However, [@deletescape](https://github.com/deletescape) has written [a python script](https://github.com/deletescape/dscripts/blob/8b261226deda604df7405708e6e7ae67b6d2e480/gettranslators.py) (below, slightly modified to update the syntax) that uses their API to fetch translators and output them to a file in the correct syntax, which you may find useful for handling large amounts of translators.

```python
import requests
import pycountry
import csv
import codecs
import os
from config import CONFIG

project = CONFIG['CROWDIN']['PROJECT_NAME']
projectkey = CONFIG['CROWDIN']['PROJECT_KEY']

r = requests.post(
    f'https://api.crowdin.com/api/project/{project}/reports/top-members/export?json&key={projectkey}&format=csv')

filehash = r.json()['hash']

r = requests.get(
    f'https://api.crowdin.com/api/project/{project}/reports/top-members/download?key={projectkey}&hash={filehash}')
if not os.path.exists('tmp'):
    os.makedirs('tmp')
with codecs.open(f'tmp/{filehash}.csv', 'w', 'utf-8') as f:
    f.write(r.text)

translators = []

csv.register_dialect('comma', delimiter=',', quoting=csv.QUOTE_MINIMAL)
with codecs.open(f'tmp/{filehash}.csv', 'r', 'utf-8') as csvfile:
    reader = csv.DictReader(csvfile, dialect='comma')
    for row in reader:
        translators.append(
            {'name': row['Name'], 'languages': row['Languages']})

xml = ''
for translator in translators:
    languages = ''
    counter = 0
    for lang in translator['languages'].split('; '):
        if counter >= 5:
            break
        try:
            languages += pycountry.languages.get(name=lang).alpha_2
            languages += ','
            counter += 1
        except Exception as e:
            if lang == 'Portuguese, Brazilian':
                languages += 'pt,'
            elif lang == 'Chinese Simplified':
                languages += 'zh,'
            elif lang == 'Chinese Traditional':
                languages += 'zh,'
            elif lang == 'Greek':
                languages += 'el,'
            elif lang == 'Serbian (Cyrillic)':
                languages += 'sr,'
            elif lang == 'Pirate English':
                pass
            elif lang != "":
                print(f'Unknown language: "{lang}"')
    languages = languages[:-1]
    name = translator['name']

    if languages == "":
        continue

    xml += f'''<me.jfenn.attribouter.wedges.TranslatorWedge
avatar="https://i2.wp.com/crowdin.com/images/user-picture.png"
name="{name}"
locales="{languages}" />
'''

with codecs.open('translators.xml', 'w', 'utf-8') as f:
    f.write(xml)
```
