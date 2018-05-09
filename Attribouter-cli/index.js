#!/usr/bin/env node
'use strict';

const _program = require('commander'),
	_inquirer = require('inquirer'),
	_chalk = require('chalk'),
	_exec = require('child_process').exec,
	_fs = require('fs'),
	_path = "src/main/res/values/xml/$",
	_xml = require('xml-parse'),
	_http = require('http'),
	_request = require('request'),
	_opn = require('opn'),
	_private = require("./private"),
	_github = require('github-oauth')({
		githubClient: _private._githubClient,
		githubSecret: _private._githubSecret,
		baseURL: 'http://127.0.0.1:8080',
		loginURI: '/login',
		callbackURI: '/callback',
		scope: ''
	});

console.log(_private._githubClient);

_program.version('1.0.0');
_program.parse(process.argv);

let path = ".";
let paths = _path.split("/");
for (let i = 0; i < paths.length - 2; i++) {
	path += "/" + paths[i];
	if (!_fs.existsSync(path)) {
		console.log("> " + _chalk.red.bold("Unable to find the directory \"" + path + "\""));
		return;
	}
}

var gitHubToken = null;
var appInfoIndexes = [null];
var contributorsIndexes = [null];
var licensesIndexes = [null];
var isNewConfig = true;
var defaultRepo = null;

function nextThing(data) {
	_inquirer.prompt([{
		type: 'rawlist',
		name: 'element',
		message: "Which item would you like to edit?",
		choices: ["appInfo", "contributors", "licenses", "Done."]
	}]).then(answers => {
		if (answers.element == "appInfo") {
			if (appInfoIndexes.length == 1 && appInfoIndexes[0] === null)
				appInfoIndexes.pop();
			
			_inquirer.prompt([{
				type: 'input',
				name: 'repo',
				message: "What repository (format: \"login/repo\", or \"null\") would you like to fetch your app\'s info from?",
				default: defaultRepo,
				validate: function(value) {
					return (value == "null" || (value.indexOf("/") > 1 && !value.endsWith("/"))) || "Please specify the repository name in the format \"login/repo\", or type \"null\".";
				}
			},{
				type: 'rawlist',
				name: 'index',
				message: "There are multiple <appInfo> elements in your file. Which one would you like to edit?",
				default: appInfoIndexes[0],
				choices: function() {
					let items = [];
					for (let i = 0; i < appInfoIndexes.length; i++) {
						items.push("index: " + appInfoIndexes[i] + ", title: " + root.childNodes[appInfoIndexes[i]].attributes.title);
					}
					items.push("Create a new one.");
					return items;
				},
				when: function(answers) {
					return answers.repo && appInfoIndexes.length > 0;
				}
			},{
				type: 'confirm',
				message: "There is no <appInfo> element in your file. Would you like to create one?",
				default: true,
				when: function(answers) {
					return answers.repo && !isNewConfig && appInfoIndexes[0] === null;
				}
			}]).then((answers) => {
				if (answers.repo) {
					defaultRepo = answers.repo;

					_request({
						url: "https://api.github.com/repos/" + answers.repo,
						headers: {
							Authorization: gitHubToken ? "bearer " + gitHubToken : null,
							"User-Agent": "Attribouter-cli"
						}
					}, (err, res, body) => {
						if (err) {
							console.log("> Error fetching data from https://api.github.com/repos/" + answers.repo);
							console.error(err);
							nextThing(data);
							return;
						}

						let jsonBody = JSON.parse(body);

						let index;
						if (answers.index && answers.index.startsWith("index: ")) {
							index = Number.parseInt(answers.index.substring(7, answers.index.indexOf(",")));
						} else {
							index = data.childNodes.length;
							data.childNodes.push({
								type: "element",
								tagName: "appInfo",
								attributes: {},
								childNodes: [],
								closing: true,
								closingChar: null
							});
						}

						_inquirer.prompt([{
							type: 'input',
							name: 'repo',
							message: "Change repo attribute from \"" + data.childNodes[index].attributes["repo"] + "\" to... ",
							default: jsonBody.full_name
						},{
							type: 'input',
							name: 'description',
							message: "Change description attribute from \"" + data.childNodes[index].attributes["description"] + "\" to... ",
							default: jsonBody.description,
							when: jsonBody.description !== null && jsonBody.description.length > 0
						},{
							type: 'input',
							name: 'playStoreUrl',
							message: "Change playStoreUrl attribute from \"" + data.childNodes[index].attributes["playStoreUrl"] + "\" to... ",
							default: jsonBody.homepage,
							when: jsonBody.homepage !== null && jsonBody.homepage.startsWith("https://play.google.com/")
						},{
							type: 'input',
							name: 'websiteUrl',
							message: "Change websiteUrl attribute from \"" + data.childNodes[index].attributes["websiteUrl"] + "\" to... ",
							default: jsonBody.homepage,
							when: jsonBody.homepage !== null && jsonBody.homepage.length > 0 && !jsonBody.homepage.startsWith("https://play.google.com/")
						},{
							type: 'input',
							name: 'gitHubUrl',
							message: "Change gitHubUrl attribute from \"" + data.childNodes[index].attributes["gitHubUrl"] + "\" to... ",
							default: jsonBody.html_url
						}]).then((answers) => {
							for (let key in answers) {
								data.childNodes[index].attributes[key] = answers[key];
							}
							console.log("> " + _chalk.green.bold("appInfo element updated."));
							nextThing(data);
						});						
					});
				} else nextThing(data);
			});
		} else if (answers.element == "contributors") {
			if (contributorsIndexes.length == 1 && contributorsIndexes[0] === null)
				contributorsIndexes.pop();
		} else if (answers.element == "licenses") {
			if (licensesIndexes.length == 1 && licensesIndexes[0] === null)
				licensesIndexes.pop();
		} else {
			//write to file & exit
		}
	});
}

function applyAppInfo(data, index, response) {
	appInfoIndexes.splice(appInfoIndexes.indexOf(index), 1);
}

function applyContributors(data, index, response) {
	contributorsIndexes.splice(contributorsIndexes.indexOf(index), 1);
}

function applyLicenses(data, index, response) {
	licensesIndexes.splice(licensesIndexes.indexOf(index), 1);
}

function prompt(token) {
	_inquirer.prompt([{
		type: 'input',
		name: 'fileName',
		message: "What is the name of the XML file to create / modify?",
		default: "attribouter.xml",
		filter: function(value) {
			return value.endsWith(".xml") ? value : value + ".xml";
		}
	}]).then(answers => {
		var data = _fs.existsSync(_path.replace("$", answers.fileName)) ? _xml.parse(_fs.readFileSync(_path.replace("$", answers.fileName))) : null;
		if (data) {
			console.log("> An existing config file has been found, this tool will now attempt to update the existing data.");
			isNewConfig = false;
			for (let i = 0; i < data.length; i++) {
				if (data[i].tagName == "about") {
					data = data[i];
					for (i = 0; i < data.childNodes.length; i++) {
						if (data.childNodes[i].tagName == "appInfo") {
							if (appInfoIndexes[0] === null)
								appInfoIndexes[0] = i;
							else appInfoIndexes.push(i);
						}
				
						if (data.childNodes[i].tagName == "contributors") {
							if (contributorsIndexes[0] === null)
								contributorsIndexes[0] = i;
							else contributorsIndexes.push(i);
						}
			
						if (data.childNodes[i].tagName == "licenses") {
							if (licensesIndexes[0] === null)
								licensesIndexes[0] = i;
							else licensesIndexes.push(i);
						}
					}
					break;
				}
			}
		} else {
			console.log("> No existing config file has been found, this tool will now begin creating a new file.");
			data = {
				type: 'element',
				tagName: 'about',
				attributes: {},
				childNodes: [],
				closing: true,
				closingChar: null
			};
		}
		
		nextThing(data);
	});
}

const _server = _http.createServer(function(req, res) {
	if (req.url.match("/login") || req.url.match("/login/")) 
		return _github.login(req, res);
		
	if (req.url.match("/callback?") || req.url.match("/callback/")) {
		console.log("> Verifying auth token...");
		return _github.callback(req, res);
	}
});
_server.listen(8080);

_github.on('error', function(error) {
	_server.close();
	console.log("> There was an error signing in. Request limits will be in place, and you run into issues fetching certain information from GitHub.");
	prompt();
});

_github.on('token', function(token, response) {
	_server.close();
	console.log("> " + _chalk.green.bold("You have been successfully signed in. Requests are now authenticated."));
	prompt(token.access_token);
});

console.log("> On the next page, please sign in with your GitHub account in order to authenticate requests and bypass the rate limits.");
console.log("> Attempting to open " + _chalk.blue("http://127.0.0.1:8080/login/") + "...");
_opn("http://127.0.0.1:8080/login/");
