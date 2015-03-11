package com.synaptix.toast.gwt.server.guice;

import java.lang.reflect.Type;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.plugins.guice.GuiceResourceFactory;
import org.jboss.resteasy.plugins.server.servlet.ListenerBootstrap;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.GetRestful;

import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class WebAppServletContextListener extends GuiceServletContextListener implements HttpSessionListener, HttpSessionActivationListener {

	private final static Logger logger = Logger.getLogger(WebAppServletContextListener.class);

	protected Injector injector;

	protected ResteasyDeployment resteasyDeployment;

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		super.contextInitialized(servletContextEvent);

		ServletContext servletContext = servletContextEvent.getServletContext();

		injector = (Injector) servletContext.getAttribute(Injector.class.getName());

		// Resteasy
		ListenerBootstrap config = new ListenerBootstrap(servletContext);
		resteasyDeployment = config.createDeployment();
		resteasyDeployment.start();

		servletContext.setAttribute(ResteasyProviderFactory.class.getName(), resteasyDeployment.getProviderFactory());
		servletContext.setAttribute(Dispatcher.class.getName(), resteasyDeployment.getDispatcher());
		servletContext.setAttribute(Registry.class.getName(), resteasyDeployment.getRegistry());

		processInjector(resteasyDeployment.getRegistry(), resteasyDeployment.getProviderFactory(), injector);
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		super.contextDestroyed(servletContextEvent);

		// Resteasy
		if (resteasyDeployment != null) {
			resteasyDeployment.stop();
		}

	}

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(new BootServerServletModule());
	}

	private void processInjector(Registry registry, ResteasyProviderFactory providerFactory, Injector injector) {
		for (final Binding<?> binding : injector.getBindings().values()) {
			final Type type = binding.getKey().getTypeLiteral().getType();
			if (type instanceof Class) {
				final Class<?> beanClass = (Class<?>) type;
				if (GetRestful.isRootResource(beanClass)) {
					final ResourceFactory resourceFactory = new GuiceResourceFactory(binding.getProvider(), beanClass);
					logger.info("registering factory for {0}", beanClass.getName());
					registry.addResourceFactory(resourceFactory);
				}
				if (beanClass.isAnnotationPresent(Provider.class)) {
					logger.info("registering provider instance for {0}", beanClass.getName());
					providerFactory.registerProviderInstance(binding.getProvider().get());
				}
			}
		}
	}

	@Override
	public void sessionCreated(HttpSessionEvent event) {
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
	}

	@Override
	public void sessionDidActivate(HttpSessionEvent event) {
	}

	@Override
	public void sessionWillPassivate(HttpSessionEvent event) {
	}
}
