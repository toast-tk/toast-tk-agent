#!/bin/bash
if [ -f toast-tk-studio-agent-standalone.jar ]
then
	echo "toast-tk-studio-agent-standalone.jar - building toast env..."
	export TOAST_JRE_HOME="/usr/local/bin/java"
	echo "Setting TOAST_JRE_HOME to: $TOAST_JRE_HOME" 
else
	echo "toast-tk-studio-agent-standalone.jar not found !"
fi