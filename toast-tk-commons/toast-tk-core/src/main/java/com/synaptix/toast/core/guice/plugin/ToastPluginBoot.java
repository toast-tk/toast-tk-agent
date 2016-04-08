package com.synaptix.toast.core.guice.plugin;

import java.util.List;
import java.util.Set;

import com.google.inject.Module;

public interface ToastPluginBoot {

	void boot();

	List<Module> getModules();

	boolean isFrameworkInternalPlugin();

	Set<Class<?>> getCustomActionAdapters();
}
