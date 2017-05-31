[![Codacy Badge](https://api.codacy.com/project/badge/Grade/c871d1c99ce74589b8d0e0a634a866d5)](https://www.codacy.com/app/toast-tk/toast-tk-agent?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=toast-tk/toast-tk-agent&amp;utm_campaign=Badge_Grade)
[![License](http://img.shields.io/:license-Apache%202-red.svg)](https://github.com/toast-tk/toast-tk-engine/blob/snapshot/LICENSE.md)

# Toast-Tk Agent

<a href="http://toast-tk.io"><img src="https://github.com/toast-tk/toast-tk-webapp/blob/master/public/images/ToastLogo.png?raw=true" align="left" height="50"></a>
**Toast-tk-agent** is the leightweight desktop application of Toast-Tk framework that records and replays user interactions with the application.

# Contribution

Toast TK is a young ![Open Source Love](https://badges.frapsoft.com/os/v3/open-source.svg?v=103) project.  

For contribution rules and guidelines, See [CONTRIBUTING.md](https://github.com/toast-tk/toast-tk-engine/blob/snapshot/CONTRIBUTING.md)

If you'd like to help, [get in touch](https://gitter.im/toast-tk/toast-tk-engine) and let us know how you'd like to help. We love contributors!! 

# Licence
_Toast TK regroups multiple open source projects licensed under the Apache Software License 2._


# How to debug

To run the agent on an IDE with all the right parameters, you have to create a new java application runner :
* Create a new run configuration (Run > Run configuration > New)
* Set the configurations to :
	- Project : toast-tk-web-agent
	- Main Class : io.vertx.core.Starter
	- Program Arguments : run io.toast.tk.agent.web.RestRecorderService
* Save and run with this configuration