@echo off
set toastDirectory=C:\Users\%USERNAME%\.toast\
set javaDirectory=Java\jre\bin\
cd %toastDirectory%
%javaDirectory%\java.exe -jar %toastDirectory%agent-1.0-fat.jar
pause