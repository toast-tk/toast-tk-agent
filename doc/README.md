Tutorial : Creation of the Installation Agent Program 
--- 
Done by Nathan Cohen
The 20/10/2016
---

To create the installer of the agent, you have to create a AgentInstaller.msi.
This installer has been done with Wix. 

The repository of the installer is "toast-tk-agent/build/".

To update a new version of the AgentInstaller, you have to :
- Install Wix Toolset and add it to the Path directory of your command system
- Update the version in the file AgentInstaller.wxs at this line : "<?define ProductVersion = "0.0.1"?>" (if needed)
- Change/Put the file "agent-1.0-fat.jar" and "server-keystore.jks" in the folder SetupAgent\SetupAgent\resources from the old version to the new one. This files will be put in the right repository thanks to the installer.
- Run the .bat "toast-tk-agent/build/compileMSIAgent.bat"
- The installer "AgentInstaller_32.msi" and "AgentInstaller_64.msi" are updated in the folder "toast-tk-agent/dist/". 

N.B. : 
1/ The installer automaticly install the files in the directory "C:\Users\%USERNAME%\.toast". 
The AgentLauncher.exe is installed in the directory choosed by the user. You can not choose the direcotory "ProgramFiles" (admin rights are needed). 

2/ The AgentLauncher.exe have been compiled from AgentLauncher.bat by Bat To Exe Converter V2.4.5

3/ To customize the interface, you can look into http://wixtoolset.org/documentation/manual/v3/wixui/wixui_customizations.html