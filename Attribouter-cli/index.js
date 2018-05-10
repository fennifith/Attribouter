#!/usr/bin/env node
'use strict';

const _program = require('commander'),
	_inquirer = require('inquirer'),
	_chalk = require('chalk'),
	_exec = require('child_process').exec,
	_fs = require('fs'),
	_path = "src/main/res/xml/$",
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
var isNewConfig = true;
var defaultRepo = null;
var fileName = "attribouter.xml";

function formatTitle(repo) {
	if (repo.indexOf("/") > 0)
		repo = repo.substring(repo.indexOf("/") + 1);

	repo = repo.charAt(0).toUpperCase() + repo.substring(1);
	repo = repo.replace(/([a-z])([A-Z])/g, "$1 $2");
	repo = repo.replace(/([A-Z])([A-Z][a-z])/g, "$1 $2");
	return repo;
}

function addClosings(root) {
	if (!root.closing)
		root.closingChar = '/';

	for (let i = 0; root.childNodes && root.childNodes.length > i; i++) {
		if (root.childNodes[i].type != "element")
			root.childNodes.splice(i, 1);
		else addClosings(root.childNodes[i]);
	}
}

function nextThing(data) {
	_inquirer.prompt([{
		type: 'list',
		name: 'element',
		message: "Which item would you like to edit?",
		choices: ["appInfo", "contributors", "licenses", "[done]"]
	}]).then(answers => {
		if (answers.element == "appInfo") {
			let appInfoChoices = function() {
				let items = [];
				for (let i = 0; i < data.childNodes.length; i++) {
					if (data.childNodes[i].attributes && data.childNodes[i].tagName == "appInfo")
						items.push("index: " + i + ", title: " + data.childNodes[i].attributes.title + ", repo: " + data.childNodes[i].attributes.repo);
				}
				items.push("Create a new one.");
				return items;
			};
			
			_inquirer.prompt([{
				type: 'list',
				name: 'index',
				message: "There are multiple <appInfo> elements in your file. Which one would you like to edit?",
				default: appInfoChoices()[0],
				choices: appInfoChoices,
				when: appInfoChoices().length > 1
			},{
				type: 'input',
				name: 'repo',
				message: "What repository (format: \"login/repo\") would you like to fetch your app\'s info from?",
				default: function(answers) {
					if (answers.index && answers.index.startsWith("index: "))
						return answers.index.substring(answers.index.indexOf("repo: ") + 6);
					else return defaultRepo;
				},
				validate: function(value) {
					return (value.indexOf("/") > 1 && !value.endsWith("/")) || "Please specify the repository name in the format \"login/repo\".";
				}
			}]).then((answers) => {
				if (answers.repo) {
					defaultRepo = answers.repo;

					console.log("> Fetching GitHub data...");
					_request({
						url: "https://api.github.com/repos/" + answers.repo,
						headers: {
							Authorization: (gitHubToken ? "token " + gitHubToken : null),
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

						if (answers.index && answers.index.startsWith("index: ")) {
							answers.index = Number.parseInt(answers.index.substring(7, answers.index.indexOf(",")));
							console.log("> " + _chalk.blue.bold("Modifying the <appInfo> element at [" + answers.index + "]."));
						} else {
							console.log("> " + _chalk.blue.bold("Creating a new <appInfo> element."));
							answers.index = data.childNodes.length;
							data.childNodes.push({
								type: "element",
								tagName: "appInfo",
								attributes: {},
								childNodes: [],
								closing: false,
								closingChar: '/'
							});
						}

						let prompts = [];
						let map = {
							"full_name": "repo",
							"description": "description",
							"homepage": function() {
								return jsonBody.homepage && jsonBody.homepage.startsWith("https://play.google.com/") ? "playStoreUrl" : "websiteUrl";
							},
							"gitHubUrl": "html_url"
						}

						for (let key in map) {
							let name = typeof map[key] === "function" ? map[key]() : map[key];
							let val = data.childNodes[answers.index].attributes[name];
							if (jsonBody[key] && jsonBody[key].length > 0 && jsonBody[key] != val) {
								prompts.push({
									type: 'input',
									name: name,
									message: "Change " + name + " attribute from \"" + val + "\" to...",
									default: jsonBody[key]
								});
							}
						}

						_inquirer.prompt(prompts).then((answers2) => {
							for (let key in answers2) {
								data.childNodes[answers.index].attributes[key] = answers2[key];
							}
							
							console.log("> " + _chalk.green.bold("appInfo element updated."));
							nextThing(data);
						});						
					});
				} else nextThing(data);
			});
		} else if (answers.element == "contributors") {
				let contributorsChoices = function() {
					let items = [];
					for (let i = 0; i < data.childNodes.length; i++) {
						if (data.childNodes[i].attributes && data.childNodes[i].tagName == "contributors")
							items.push("index: " + i + ", title: " + data.childNodes[i].attributes.title + ", repo: " + data.childNodes[i].attributes.repo);
					}
					items.push("Create a new one.");
					return items;
				};
		
				_inquirer.prompt([{
					type: 'list',
					name: 'index',
					message: "There are multiple <contributors> elements in your file. Which one would you like to edit?",
					default: contributorsChoices()[0],
					choices: contributorsChoices,
					when: contributorsChoices().length > 1
				},{
					type: 'input',
					name: 'repo',
					message: "What repository (format: \"login/repo\") would you like to fetch contributors from?",
					default: function(answers) {
						if (answers.index && answers.index.startsWith("index: "))
							return answers.index.substring(answers.index.indexOf("repo: ") + 6);
						else return defaultRepo;
					},
					validate: function(value) {
						return (value.indexOf("/") > 1 && !value.endsWith("/")) || "Please specify the repository name in the format \"login/repo\".";
					}
				}]).then((answers) => {
					if (answers.repo) {
						defaultRepo = answers.repo;

						console.log("> Fetching GitHub data...");
						_request({
							url: "https://api.github.com/repos/" + answers.repo + "/contributors?per_page=1000",
							headers: {
								Authorization: (gitHubToken ? "token " + gitHubToken : null),
								"User-Agent": "Attribouter-cli"
							}
						}, (err, res, body) => {
							if (err) {
								console.log("> Error fetching data from https://api.github.com/repos/" + answers.repo + "/contributors");
								console.error(err);
								nextThing(data);
								return;
							}

							let jsonBody = JSON.parse(body);

							if (answers.index && answers.index.startsWith("index: ")) {
								answers.index = Number.parseInt(answers.index.substring(7, answers.index.indexOf(",")));
								console.log("> " + _chalk.blue.bold("Modifying the <contributors> element at [" + answers.index + "]."));
							} else {
								console.log("> " + _chalk.blue.bold("Creating a new <contributors> element."));
								answers.index = data.childNodes.length;
								data.childNodes.push({
									type: "element",
									tagName: "contributors",
									attributes: {
										repo: answers.repo
									},
									childNodes: [],
									closing: true,
									closingChar: null
								});
							}

							if (jsonBody.length > 0)
								nextContributor(data, answers.index, jsonBody);
							else {
								console.log("> No contributors were returned from GitHub.");
								nextThing(data);
								return;
							}
						});
					} else nextThing(data);
				});
		} else if (answers.element == "licenses") {
			let licensesChoices = function() {
				let items = [];
				for (let i = 0; i < data.childNodes.length; i++) {
					if (data.childNodes[i].attributes && data.childNodes[i].tagName == "licenses")
						items.push("index: " + i + ", title: " + data.childNodes[i].attributes.title);
				}
				items.push("Create a new one.");
				return items;		
			};
		
			_inquirer.prompt([{
				type: 'list',
				name: 'index',
				message: "There are multiple <licenses> elements in your file. Which would you like to edit?",
				default: licensesChoices()[0],
				choices: licensesChoices,
				when: licensesChoices().length > 1
			}]).then((answers) => {
				if (answers.index && answers.index.startsWith("index: ")) {
					answers.index = Number.parseInt(answers.index.substring(7, answers.index.indexOf(",")));
					console.log("> " + _chalk.blue.bold("Modifying the <licenses> element at [" + answers.index + "]."));
				} else {
					console.log("> " + _chalk.blue.bold("Creating a new <licenses> element."));
					answers.index = data.childNodes.length;
					data.childNodes.push({
						type: "element",
						tagName: "licenses",
						attributes: {},
						childNodes: [],
						closing: true,
						closingChar: null
					});
				}

				nextLicense(data, answers.index);
			});
		} else {
			_inquirer.prompt([{
				type: 'confirm',
				name: 'save',
				message: 'Would you like to save the file?',
				default: true
			}]).then((answers) => {	
				if (answers.save === true) {
					console.log("> generating XML...");

					addClosings(data);
					let xml = _xml.stringify([
						{
							type: 'element',
							tagName: '?xml',
							attributes: {
								version: '1.0',
								encoding: 'UTF-8'
							},
							childNodes: [],
							innerXML: '>',
							closing: false,
							closingChar: '?'
						},
						data
					], 4);
					
					let path = ".";
					let paths = _path.split("/");
					for (let i = 0; i < paths.length - 1; i++) {
						path += "/" + paths[i];
						if (!_fs.existsSync(path)) {
							_fs.mkdirSync(path);
							return;
						}
					}

					console.log("> writing to file...");
					_fs.writeFile(_path.replace('$', fileName), xml, function(err) {
						if (err) {
							console.log("> Unable to write to file.");
							console.error(err);
						} else console.log("> " + _chalk.green.bold("Successfully saved to file."));

						process.exit();
					});
				} else process.exit();
			});
		}
	});
}

function nextContributor(data, index, contributors) {
	if (contributors[0]) {
		console.log("> Fetching more GitHub data...");
		
		_request({
			url: "https://api.github.com/users/" + contributors[0].login,
			headers: {
				Authorization: (gitHubToken ? "token " + gitHubToken : null),
				"User-Agent": "Attribouter-cli"
			}
		}, (err, res, body) => {
			if (err) {
				console.log("> Error fetching data from https://api.github.com/users/" + contributors[csIndex].login);
				console.error(err);
				contributors.splice(0, 1);
				nextContributor(data, index, contributors);
				return;
			}

			let jsonBody = JSON.parse(body);
			let contributorChoices = function() {
				let items = [];
				for (let i = 0; i < data.childNodes[index].childNodes.length; i++) {
					if (data.childNodes[index].childNodes[i].attributes)
						items.push("index: " + i + ", login: " + data.childNodes[index].childNodes[i].attributes.login + ", name: " + data.childNodes[index].childNodes[i].attributes.name);
				}
				items.push("Create a new one.");
				return items;
			};

			_inquirer.prompt([{
				type: 'list',
				name: 'index',
				message: "Which contributor would you like to modify with the data from [" + jsonBody.login + "]?",
				choices: contributorChoices,
				default: function() {
					let choices = contributorChoices();
					for (let i = 0; i < choices.length; i++) {
						if (choices[i].substring(16 + (i + "").length).startsWith(jsonBody.login))
							return choices[i];
					}

					return choices[choices.length - 1];
				},
				when: contributorChoices().length > 1
			}]).then((answers) => {
				if (answers.index && answers.index.startsWith("index: ")) {
					answers.index = Number.parseInt(answers.index.substring(7, answers.index.indexOf(",")));
					console.log("> " + _chalk.blue.bold("Modifying <contributor> at [" + answers.index + "] for [" + jsonBody.login + "]"));
				} else {
					console.log("> " + _chalk.blue.bold("Creating a new <contributor> for [" + jsonBody.login + "]"));
					answers.index = data.childNodes[index].childNodes.length;
					data.childNodes[index].childNodes.push({
						type: "element",
						tagName: "contributor",
						attributes: {},
						childNodes: [],
						closing: false,
						closingChar: '/'
					});
				}

				let prompts = [];
				let map = {
					"login": "login",
					"name": "name",
					"avatar_url": "avatar",
					"bio": "bio",
					"blog": "blog",
					"email": "email"
				};

				for (let key in map) {
					let val = data.childNodes[index].childNodes[answers.index].attributes[map[key]];
					if (jsonBody[key] && jsonBody[key].length > 0 && jsonBody[key] != val) {
						prompts.push({
							type: 'input',
							name: map[key],
							message: "Change " + map[key] + " attribute from \"" + val + "\" to...",
							default: jsonBody[key]
						});
					}
				}
				
				_inquirer.prompt(prompts).then((answers2) => {
					for (let key in answers2) {
						data.childNodes[index].childNodes[answers.index].attributes[key] = answers2[key];
					}
												
					console.log("> " + _chalk.green("contributor element for [" + jsonBody.login + "] updated."));
					contributors.splice(0, 1);
					nextContributor(data, index, contributors);
				});
			});
		});
	} else {
		console.log("> " + _chalk.green.bold("contributors element updated."));
		nextThing(data);
	}
}

function nextLicense(data, index) {
	_inquirer.prompt([{
		type: 'list',
		name: 'index',
		message: "There are multiple <project> tags in this element. Which one would you like to edit?",
		choices: function() {
			let items = [];
			for (let i = 0; i < data.childNodes[index].childNodes.length; i++) {
				if (data.childNodes[index].childNodes[i].attributes)
					items.push("index: " + i + ", title: " + data.childNodes[index].childNodes[i].attributes.title + ", repo: " + data.childNodes[index].childNodes[i].attributes.repo);
			}
			items.push("Create a new one.");
			items.push("[done]");
			return items;
		}
	},{
		type: 'input',
		name: 'repo',
		message: "What repository (format: \"login/repo\") would you like to fetch license information from?",
		default: function(answers) {
			if (answers.index.startsWith("index: "))
				return answers.index.substring(answers.index.indexOf("repo: ") + 6);
			else return null;
		},
		validate: function(value) {
			return (value.indexOf("/") > 1 && !value.endsWith("/")) || "Please specify the repository name in the format \"login/repo\".";
		},
		when: function(answers) {
			return !answers.index.startsWith("[");
		}
	}]).then((answers) => {
		if (answers.index.startsWith("[")) {
			nextThing(data);
			return;
		}
	
		if (answers.index && answers.index.startsWith("index: ")) {
			answers.index = Number.parseInt(answers.index.substring(7, answers.index.indexOf(",")));
			console.log("> " + _chalk.blue.bold("Modifying <project> at [" + answers.index + "] for [" + answers.repo + "]"));
		} else {
			console.log("> " + _chalk.blue.bold("Creating a new <project> for [" + answers.repo + "]"));
			answers.index = data.childNodes[index].childNodes.length;
			data.childNodes[index].childNodes.push({
				type: "element",
				tagName: "project",
				attributes: {},
				childNodes: [],
				closing: false,
				closingChar: '/'
			});
		}

		console.log("> Fetching GitHub data...");
		_request({
			url: "https://api.github.com/repos/" + answers.repo,
			headers: {
				Authorization: (gitHubToken ? "token " + gitHubToken : null),
				"User-Agent": "Attribouter-cli"
			}
		}, (err, res, body) => {
			if (err) {
				console.log("> Error fetching data from https://api.github.com/repos/" + answers.repo);
				console.error(err);
				nextLicense(data, index);
				return;
			}

			let jsonBody = JSON.parse(body);

			let prompts = [{
				type: 'input',
				name: 'title',
				message: "Change title attribute from \"" + data.childNodes[index].childNodes[answers.index].attributes.title + "\" to...",
				default: formatTitle(jsonBody.full_name),
				when: jsonBody.full_name && formatTitle(jsonBody.full_name) != data.childNodes[index].childNodes[answers.index].attributes.title
			}];
			
			let map = {
				"full_name": "repo",
				"description": "description",
				"homepage": "website"
			};
			
			for (let key in map) {
				let val = data.childNodes[index].childNodes[answers.index].attributes[map[key]];
				if (jsonBody[key] && jsonBody[key].length > 0 && jsonBody[key] != val) {
					prompts.push({
						type: 'input',
						name: map[key],
						message: "Change " + map[key] + " attribute from \"" + val + "\" to...",
						default: jsonBody[key]
					});
				}
			}

			if (jsonBody.license) {
				prompts.push({
					type: 'input',
					name: 'license',
					message: "Change license attribute from \"" + data.childNodes[index].childNodes[answers.index].attributes.license + "\" to...",
					default: jsonBody.license.key,
					when: jsonBody.license.key && jsonBody.license.key.length > 0 && jsonBody.license.key != data.childNodes[index].childNodes[answers.index].attributes.license
				});
				prompts.push({
					type: 'input',
					name: 'licenseName',
					message: "Change licenseName attribute from \"" + data.childNodes[index].childNodes[answers.index].attributes.licenseName + "\" to...",
					default: jsonBody.license.name,
					when: jsonBody.license.name && jsonBody.license.name.length > 0 && jsonBody.license.name != data.childNodes[index].childNodes[answers.index].attributes.licenseName
				});
			}
							
			_inquirer.prompt(prompts).then((answers2) => {
				for (let key in answers2) {
					data.childNodes[index].childNodes[answers.index].attributes[key] = answers2[key];
				}
												
				console.log("> " + _chalk.green("license element for [" + answers.repo + "] updated."));
				nextLicense(data, index);
			});
		});
	});
}

function prompt(token) {
	_inquirer.prompt([{
		type: 'input',
		name: 'fileName',
		message: "What is the name of the XML file to create / modify?",
		default: fileName,
		filter: function(value) {
			return value.endsWith(".xml") ? value : value + ".xml";
		}
	}]).then(answers => {
		fileName = answers.fileName;
		let data = null;
		if (_fs.existsSync(_path.replace("$", answers.fileName))) {
			console.log("> reading file structure...");
			data = _xml.parse(_fs.readFileSync(_path.replace("$", fileName), 'utf8').replace(/[\n]/g, ""));
			console.log(data[1].childNodes);
		}
		
		if (data) {
			console.log("> " + _chalk.blue.bold("An existing config file has been found, this tool will now attempt to update the existing data."));
			isNewConfig = false;
			for (let i = 0; i < data.length; i++) {
				if (data[i].tagName == "about") {
					data = data[i];
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
		console.log("> " + _chalk.blue.bold("Verifying auth token..."));
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
console.log("> Attempting to open " + _chalk.bold("http://127.0.0.1:8080/login/") + "...");
_opn("http://127.0.0.1:8080/login/");
