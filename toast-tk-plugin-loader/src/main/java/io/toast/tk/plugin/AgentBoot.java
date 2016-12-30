package io.toast.tk.plugin;

import java.lang.instrument.Instrumentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.toast.tk.agent.config.AgentConfigProvider;


public class AgentBoot {

	private static final Logger LOG = LoggerFactory.getLogger(AgentBoot.class);

	private static Instrumentation instrumentation;

	/**
	 * JVM hook to statically load the javaagent at startup.
	 * 
	 * After the Java Virtual Machine (JVM) has initialized, the premain method
	 * will be called. Then the real application main method will be called.
	 * 
	 */
	public static void premain(
		String args,
		Instrumentation inst)
		throws Exception {
		LOG.info("Premain method invoked with args: {} and inst: {}", args, inst);
		instrumentation = inst;
		final String pluginsPath = new AgentConfigProvider().get().getPluginDir();
		LOG.info("Premain method invoked with pluginDir: {} ", pluginsPath);
		//PluginLoader.loadPlugins(pluginsPath);
	}

	public static void agentmain(
		String agentArgs,
		Instrumentation inst) {
		System.out.println("Main method invoked with args: {} and inst: {}");
		instrumentation = inst;
		//PluginLoader.loadPlugins(null);
	}

}