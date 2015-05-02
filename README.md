[![Build Status](https://jenkins.synaptix-labs.com/buildStatus/icon?job=Toast-tk)](https://jenkins.synaptix-labs.com/job/Toast-tk/)

Build not working, requires xvbf plugin on jenkins to support X11.

# Toast Tk

Toast Toolkit - Test Automation Toolkit.

# What is Toast TK ?

Toast Toolkit aims to ease the collaboration between Business Analysts 
and Developpers to describe and test an application behavior. 

Toast provides:
- Strongly Typed Tests: ...
- Test Refactoring: ...
- Team Synchronization: ...
- Reporting: ...

Toast is a set of tools for recording, replaying test actions for:
- Web Applications.
- Java GUI programs developed using Swing Components. 
- Backend Applications.

Toast consists of an editor (Toast TK WebApp), a recorder and a player (Toast TK Studio). 
Toast records the test cases in a natural language (human readable).
The test cases can be run either through the UI or in batch mode through Eclipse (ref. how to eclipse, eclipse archetype).

# Building Toast TK

You need to clone this repository to compile Toast using maven.

``` java
$ git clone https://gitlab.synaptix-labs.com/synaptix/toast-tk.git
$ cd toast-tk
$ mvn clean install
```

## Toast TK Studio

### JNLP Mode
you should find toast-tk-automation-client-{version}.zip

### Eclipse Mode
Clone the repository.
Install egit/jgit plugins. 
Import the projects from the cloned repository. 
Build All and Enjoy !

## Toast TK WebApp
You need to clone the webapp repository and compile it using sbt.

``` java
$ git clone https://gitlab.synaptix-labs.com/sallah-kokaina/toast-tk-play-webapp.git
$ cd toast-tk-webapp
$ sbt
$ dist
$ cd target/universal
-> you should find redplay-1.0-SNAPSHOT.zip
```

Install and Launch a local Mongo Database

Run Toast TK Webapp
``` java
$ unzip redplay-1.0-SNAPSHOT.zip  
$ cd redplay-1.0-SNAPSHOT/bin
$ redplay
```

# More Information 

... Coming soon ...