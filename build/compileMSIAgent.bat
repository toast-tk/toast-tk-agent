cd SetupAgent
if not exist ../../dist/ cd ../../
if not exist ../../dist/ md dist
if not exist ../../dist/ cd build/SetupAgent/

@RD /S /Q jre
cd resources_x82
XCOPY jre ..\jre\ /E /-Y
cd ..
candle  -ext WixUtilExtension AgentInstaller_32.wxs
light -ext WixUIExtension -sice:ICE07 -cultures:en-us -dWixUILicenseRtf=LICENSE.rtf -dWixUIInfoIco=ToastLogo.bmp -dWixUIDialogBmp=BackgroundAgentSetup.bmp -dWixUIBannerBmp=BannerAgentSetup.bmp AgentInstaller_32.wixobj -out AgentInstaller_32.msi
move AgentInstaller_32.msi ../../dist/

@RD /S /Q jre
cd resources_64
XCOPY jre ..\jre\ /E /-Y
cd ..
candle  -ext WixUtilExtension AgentInstaller_64.wxs
light -ext WixUIExtension -sice:ICE07 -cultures:en-us -dWixUILicenseRtf=LICENSE.rtf -dWixUIInfoIco=ToastLogo.bmp -dWixUIDialogBmp=BackgroundAgentSetup.bmp -dWixUIBannerBmp=BannerAgentSetup.bmp AgentInstaller_64.wixobj -out AgentInstaller_64.msi
move AgentInstaller_64.msi ../../dist/

@pause