/**Copyright (c) 2013-2015, Synaptix Labs
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Creation date: 5 mars 2015
@author Sallah Kokaina <sallah.kokaina@gmail.com>

*/
package com.synaptix.toast.swing.agent.constant;

import java.awt.Image;
import java.awt.Toolkit;

public class Resource {

	private static final Toolkit kit = Toolkit.getDefaultToolkit();

	private static final String _64pxIconsDirPath = "icons/";

	private static final String _16pxIconsDirPath = "icons/16px/";

	// 64px icons
	public static final Image ICON_IMG = kit.createImage(Resource.class.getClassLoader().getResource("toast.png"));

	public static final Image ICON_CAMERA_IMG = kit.createImage(Resource.class.getClassLoader().getResource(
		_64pxIconsDirPath + "camera113.png"));

	public static final Image ICON_CONF_IMG = kit.createImage(Resource.class.getClassLoader().getResource(
		_64pxIconsDirPath + "configuration1.png"));

	public static final Image ICON_CONF_2_IMG = kit.createImage(Resource.class.getClassLoader().getResource(
		_64pxIconsDirPath + "configure.png"));

	public static final Image ICON_PLAY_IMG = kit.createImage(Resource.class.getClassLoader().getResource(
		_64pxIconsDirPath + "play43.png"));

	public static final Image ICON_SEARCH_IMG = kit.createImage(Resource.class.getClassLoader().getResource(
		_64pxIconsDirPath + "search108.png"));

	public static final Image ICON_SHARE_IMG = kit.createImage(Resource.class.getClassLoader().getResource(
		_64pxIconsDirPath + "share2.png"));

	public static final Image ICON_SETTINGS_IMG = kit.createImage(Resource.class.getClassLoader().getResource(
		_64pxIconsDirPath + "settingsIcon.png"));
	
	public static final Image ICON_WEB_IMG = kit.createImage(Resource.class.getClassLoader().getResource(
		_64pxIconsDirPath + "web.png"));

	public static final Image ICON_JAVA_IMG = kit.createImage(Resource.class.getClassLoader().getResource(
		_64pxIconsDirPath + "java.png"));

	// 16px icons
	public static final Image ICON_CAMERA_16PX_IMG = kit.createImage(Resource.class.getClassLoader().getResource(
		_16pxIconsDirPath + "camera113.png"));

	public static final Image ICON_CONF_16PX_IMG = kit.createImage(Resource.class.getClassLoader().getResource(
		_16pxIconsDirPath + "configuration1.png"));

	public static final Image ICON_CONF_16PX_2_IMG = kit.createImage(Resource.class.getClassLoader().getResource(
		_16pxIconsDirPath + "configure.png"));

	public static final Image ICON_CONF_16PX_3_IMG = kit.createImage(Resource.class.getClassLoader().getResource(
		_16pxIconsDirPath + "tools6.png"));

	public static final Image ICON_SAVE_16PX_IMG = kit.createImage(Resource.class.getClassLoader().getResource(
		_16pxIconsDirPath + "diskette18.png"));

	public static final Image ICON_DATA_16PX_IMG = kit.createImage(Resource.class.getClassLoader().getResource(
		_16pxIconsDirPath + "data39.png"));

	public static final Image ICON_RUN_16PX_IMG = kit.createImage(Resource.class.getClassLoader().getResource(
		_16pxIconsDirPath + "forward26.png"));

	public static final Image ICON_SCAN_16PX_IMG = kit.createImage(Resource.class.getClassLoader().getResource(
		_16pxIconsDirPath + "quick-response-code.png"));

	public static final Image ICON_FILTER_16PX_IMG = kit.createImage(Resource.class.getClassLoader().getResource(
		_16pxIconsDirPath + "data39.png"));

	public static final Image ICON_SEARCH_16PX_IMG = kit.createImage(Resource.class.getClassLoader().getResource(
		_16pxIconsDirPath + "search108.png"));

	public static final Image ICON_STOP_16PX_IMG = kit.createImage(Resource.class.getClassLoader().getResource(
		_16pxIconsDirPath + "stop4.png"));

	public static final Image ICON_START_16PX_IMG = kit.createImage(Resource.class.getClassLoader().getResource(
		_16pxIconsDirPath + "start.png"));

	public static final Image ICON_SHARE_16PX_IMG = kit.createImage(Resource.class.getClassLoader().getResource(
		_16pxIconsDirPath + "share2.png"));

	public static final Image ICON_CLEAR_16PX_IMG = kit.createImage(Resource.class.getClassLoader().getResource(
		_16pxIconsDirPath + "wiping16.png"));

	public static final Image ICON_POWER_16PX_IMG = kit.createImage(Resource.class.getClassLoader().getResource(
		_16pxIconsDirPath + "power6.png"));

	public static final Image ICON_SETTINGS_16PX_IMG = kit.createImage(Resource.class.getClassLoader().getResource(
		_16pxIconsDirPath + "settingsIcon.png"));

	public static final Image ICON_HOME_16PX_IMG = kit.createImage(Resource.class.getClassLoader().getResource(
		_16pxIconsDirPath + "homeIcon.png"));
	
	public static final Image ICON_AMPOULE_16PX_IMG = kit.createImage(Resource.class.getClassLoader().getResource(
		_16pxIconsDirPath + "ampoule_icon.png"));
	
	public static final Image ICON_ARRET_16PX_IMG = kit.createImage(Resource.class.getClassLoader().getResource(
		_16pxIconsDirPath + "arret.png"));
	
	public static final Image ICON_PRISE_16PX_IMG = kit.createImage(Resource.class.getClassLoader().getResource(
		_16pxIconsDirPath + "prise.png"));

	public static final Image ICON_KILL_POISON_16PX_IMG = kit.createImage(Resource.class.getClassLoader().getResource(
			_16pxIconsDirPath + "kill_poison.png"));
	;
}
