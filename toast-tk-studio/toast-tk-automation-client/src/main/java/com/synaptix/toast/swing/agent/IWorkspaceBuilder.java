package com.synaptix.toast.swing.agent;

import java.io.File;
import java.util.Properties;

public interface IWorkspaceBuilder {

	void initWorkspace();

	String getRuntimeType();

	void openConfigDialog();

	File getToastPropertiesFile();

	File getToastWebPropertiesFile();

	void propertiesChanged();

	Properties getProperties();

	Properties getSwingProperties();

	Properties getWebProperties();
}
