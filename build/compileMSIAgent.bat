cd SetupAgent
candle  -ext WixUtilExtension AgentInstaller.wxs
light -ext WixUIExtension -cultures:en-us -dWixUILicenseRtf=LICENSE.rtf -dWixUIInfoIco=ToastLogo.bmp -dWixUIDialogBmp=BackgroundAgentSetup.bmp -dWixUIBannerBmp=BannerAgentSetup.bmp AgentInstaller.wixobj -out AgentInstaller.msi
if not exist ../../dist/ cd ../../
if not exist ../../dist/ md dist
if not exist ../../dist/ cd build/SetupAgent/
move AgentInstaller.msi ../../dist/
@pause