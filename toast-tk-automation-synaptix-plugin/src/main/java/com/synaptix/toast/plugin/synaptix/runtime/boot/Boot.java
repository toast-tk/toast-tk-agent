package com.synaptix.toast.plugin.synaptix.runtime.boot;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Module;
import com.synaptix.toast.core.guice.plugin.ToastPluginBoot;
import com.synaptix.toast.plugin.synaptix.runtime.PluginModule;
import com.synaptix.toast.plugin.synaptix.runtime.handler.SwingCustomWidgetHandler;

public class Boot implements ToastPluginBoot {

	private static final Logger LOG = LoggerFactory.getLogger(Boot.class);

	@Override
	public void boot() {
		LOG.info("Booting Synaptix RedPepper Plugin");
	}

	@Override
	public List<Module> getModules() {
		return Arrays.asList((Module)new PluginModule());
	}

}
