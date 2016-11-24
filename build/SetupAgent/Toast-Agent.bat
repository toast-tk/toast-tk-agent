echo off
set toastDirectory=C:\Users\%USERNAME%\.toast
set javaDirectory=C:\Users\%USERNAME%\.toast\java

:: We figure out if ToastTk-Agent have been installed
if not exist %javaDirectory%\java.exe goto 1
if not exist %toastDirectory%\agent-1.0-fat.jar goto 1

if exist %javaDirectory%\java.exe if exist %toastDirectory%\agent-1.0-fat.jar goto 2


pause
echo on


goto deb
:1 
echo MsgBox "There is a problem. Please, reinstall ToastTk-Agent."> msgbox.vbs 
cscript msgbox.vbs
:2
%javaDirectory%\javaw.exe -jar %toastDirectory%\agent-1.0-fat.jar