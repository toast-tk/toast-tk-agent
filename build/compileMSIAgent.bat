cd SetupAgent
if not exist ../../dist/ cd ../../
if not exist ../../dist/ md dist
if not exist ../../dist/ cd build/SetupAgent/

candle  -ext WixUtilExtension AgentInstaller_32.wxs
light -ext WixUIExtension -cultures:en-us -dWixUILicenseRtf=LICENSE.rtf -dWixUIInfoIco=ToastLogo.bmp -dWixUIDialogBmp=BackgroundAgentSetup.bmp -dWixUIBannerBmp=BannerAgentSetup.bmp AgentInstaller_32.wixobj -out AgentInstaller_32.msi
move AgentInstaller_32.msi ../../dist/

candle  -ext WixUtilExtension AgentInstaller_64.wxs
light -ext WixUIExtension -cultures:en-us -dWixUILicenseRtf=LICENSE.rtf -dWixUIInfoIco=ToastLogo.bmp -dWixUIDialogBmp=BackgroundAgentSetup.bmp -dWixUIBannerBmp=BannerAgentSetup.bmp AgentInstaller_64.wixobj -out AgentInstaller_64.msi
move AgentInstaller_64.msi ../../dist/

@pause