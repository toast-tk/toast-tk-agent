package com.synaptix.toast.core.guice.plugin;

import java.util.List;

import com.google.inject.Module;

public interface ToastPluginBoot {

	void boot();

	List<Module> getModules();
}
