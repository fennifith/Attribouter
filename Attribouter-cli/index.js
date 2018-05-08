#!/usr/bin/env node
'use strict';

const _program = require('commander'),
	_inquirer = require('inquirer'),
	_chalk = require('chalk'),
	_exec = require('child_process').exec,
	_fs = require('fs'),
	_path = "src/main/res/values/xml/$.xml",
	_xml = require('xml-parse'),
	_http = require('http'),
	_opn = require('opn'),
	_github = require('github-oauth')({
		githubClient: "eb1b415558aa1491e5b3",
		githubSecret: "1c8f6ec93cf85c622691ea86fbea98979d73503c",
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

function prompt() {
	_inquirer.prompt([
		{
			type: 'input',
			name: 'fileName',
			message: "What is the name of the XML file to create / modify?",
			default: "attribouter.xml",
			filter: function(value) {
				return value.endsWith(".xml") ? value : value + ".xml";
			}
		},
		{
			type: 'input',
			name: 'repository',
			message: "Enter the name of the GitHub repository (format: user/repo) to fetch the data from, or \"null\".",
			default: "null"
		}
	]).then(answers => {
		console.log(answers);
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
	prompt();
});

console.log("> On the next page, please sign in with your GitHub account in order to authenticate requests and bypass the rate limits.");
console.log("> Attempting to open " + _chalk.blue("http://127.0.0.1:8080/login/") + "...");
_opn("http://127.0.0.1:8080/login/");
