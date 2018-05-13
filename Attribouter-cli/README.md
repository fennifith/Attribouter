# Attribouter CLI

This is a CLI written in nodejs to somewhat automate the creation of [Attribouter](https://jfenn.me/redirects/?t=github&d=Attribouter)'s config file from a GitHub project.

## Dependencies

- [Chalk](https://npmjs.com/packages/chalk)
- [Commander](https://npmjs.com/packages/commander)
- [GitHub-OAuth](https://npmjs.com/packages/github-oauth)
- [Request](https://npmjs.com/packages/request)
- [Opn](https://npmjs.com/packages/opn)
- [Inquirer](https://npmjs.com/packages/inquirer)
- [XML-Parse](https://npmjs.com/packages/xml-parse)

## Installation

Either do:

```bash
sudo npm install -g attribouter-cli
```

or:

```bash
git clone https://github.com/TheAndroidMaster/Attribouter
cd Attribouter/Attribouter-cli
npm install
sudo npm link
```

## Usage

### Step 1. 

Open the terminal and `cd` into the directory of the module that you want to create the file in. Most of the time, this will be the folder titled "app" in the root of your project. If an error message is not output after step 2, you've done it right.

### Step 2. 

Run `attribouter`. It will request that you sign into GitHub. You should probably do that. It will also ask you to pick a file name. If you don't already have a file, you can just press 'enter' in most cases. This should only be different if you want to use multiple about screens in your app.

### Step 3. 

Use the up/down arrow keys to navigate menus. When the CLI wants to change a value, it will ask you to change it with the new value in parentheses. Pressing 'enter' will apply that value, and typing something else will use that value instead.

### Step 4. 

When you are done, select 'done' in the root manu and save the file. If you terminate the program before saving the file, then the file will not be saved. You may want to open the file in Android Studio to fix the formatting and check for errors (the xml parser sometimes doesn't escape values) before using it.

