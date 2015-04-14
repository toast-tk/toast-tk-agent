package com.synaptix.toast.plugin.synaptix.runtime.handler;

import java.awt.Component;
import java.awt.MouseInfo;
import java.awt.Point;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.sncf.fret.rus.client.service.SendServiceDispatcher;
import com.synaptix.service.IServiceFactory;
import com.synaptix.service.ServicesManager;
import com.synaptix.service.client.ClientServiceFactory;
import com.synaptix.service.client.IReWaitResult;
import com.synaptix.service.client.IServiceCommunicator;
import com.synaptix.smackx.service.SendXmppTimedServiceManager;
import com.synaptix.smackx.service.ServiceFactoryManager;
import com.synaptix.toast.automation.net.CommandRequest;
import com.synaptix.toast.automation.net.IIdRequest;
import com.synaptix.toast.automation.net.ValueResponse;
import com.synaptix.toast.core.Property;
import com.synaptix.toast.dao.domain.impl.test.ComponentConfigLine;
import com.synaptix.toast.dao.domain.impl.test.block.ConfigBlock;
import com.synaptix.toast.dao.service.dao.access.test.ConfigBlockDaoService;
import com.synaptix.toast.plugin.synaptix.runtime.annotation.ServiceCallHandler;
import com.synaptix.toast.plugin.synaptix.runtime.converter.StringToObjectInstance;
import com.synaptix.toast.plugin.synaptix.runtime.model.ServiceCallIdentifier;
import com.synaptix.toast.plugin.synaptix.runtime.service.ConnectionBuilder;

@ServiceCallHandler
public class ServiceCallCustomHandler extends AbstractCustomFixtureHandler {
	static {
		LOG = LoggerFactory.getLogger(ServiceCallCustomHandler.class);
//		try {
//			new Thread("MouseLocation"){
//				@Override
//				public void run() {
//					while(true){
//						try {
//							Thread.sleep(1000);
//				        	final Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
//				        	System.out.println("("+Integer.valueOf(mouseLocation.x)+","+Integer.valueOf(mouseLocation.y)+")");
//						}
//						catch(final Exception e) {
//							e.printStackTrace();
//						}
//				    }
//				}
//			}.start();
//		}
//		catch(final Exception e) {
//			e.printStackTrace();
//		}
	}

	private static final int NB_NO_ARGS_WORD = 1;

	static final Logger LOG;

	//private ConfigBlockDaoService configService;

	private IServiceCommunicator dispatcher;

	private XMPPConnection connection;
	
	//private ConfigBlock configBlock; 

	private Map<String, Map<String, Object>> allServices;
	
	private Map<String, Class<?>[]> methodDescriptors;
	
	private List<String> whiteList;
	
	@Inject
	public ServiceCallCustomHandler(final ConfigBlockDaoService.Factory configServiceFactory) {
		try {
			//this.configService = configServiceFactory.create("test_project_db");
			this.connection = ConnectionBuilder.connect();
			this.dispatcher = new SendServiceDispatcher(connection);
			//this.configBlock = initConfigService();
			this.whiteList = new ArrayList<String>(1);
			initWhiteList();
			this.allServices = new HashMap<String, Map<String, Object>>(4000);
			this.methodDescriptors = new HashMap<String, Class<?>[]>(20000);
			initializeServiceFactories();
			LOG.info("NB methodDescriptors {}", Integer.valueOf(methodDescriptors.size()));
		}
		catch(final Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private void initWhiteList() {
		whiteList.add("service");
	}
	
	//private ConfigBlock initConfigService() {
		//return configService.loadConfigBlock("RUS");
	//}
	
	private void initializeServiceFactories() throws XMPPException {
		LOG.info("Initialize serviceFactories");
		final String[] factories = ServiceFactoryManager.getServiceFactoryNames(connection);
		final ServicesManager servicesManagerInstance = ServicesManager.getInstance();
		for(final String factory : factories) {
			initializeServiceFactory(servicesManagerInstance, factory);
		}
		ClientServiceFactory.putTimeout(120000);
		SendXmppTimedServiceManager.getInstance(connection).setDefaultReWaitResult(new AllRewaitResult());
		
		inspectServiceFactoriesMethods();
	}

	private void initializeServiceFactory(
			final ServicesManager servicesManagerInstance, 
			final String factory
	) {
		final Map<String, Object> recupServices = new HashMap<String, Object>();
		final IServiceFactory clientServiceFactory = new ClientServiceFactory(dispatcher, factory, recupServices);
		allServices.put(factory, recupServices);
		
		servicesManagerInstance.addServiceFactory(factory, clientServiceFactory);
		clientServiceFactory.addServiceInterceptor(new DebugServiceInterceptor(factory));
		
		LOG.info("Finded servicefactory {}", factory);
	}

	private static class AllRewaitResult implements IReWaitResult {

		public AllRewaitResult() {

		}

		@Override
		public boolean isReWaitResult(final long timeout) throws Exception {
			return true;
		}
	}

	private void inspectServiceFactoriesMethods() {
		final Set<Method> objectMethods = new HashSet<Method>(Arrays.asList(Object.class.getMethods()));
		final Set<String> factoryNames = allServices.keySet();
		for(final String factoryName : factoryNames) {
			inspectServiceFactoryMethods(objectMethods, factoryName);
		}
	}

	private void inspectServiceFactoryMethods(
			final Set<Method> objectMethods,
			final String factoryName
	) {
		final Map<String, Object> services = allServices.get(factoryName);
		final Set<String> serviceNames = services.keySet();
		for(final String serviceName : serviceNames) {
			inspectServiceMethods(objectMethods, factoryName, services, serviceName);
		}
	}

	private void inspectServiceMethods(
			final Set<Method> objectMethods,
			final String factoryName, 
			final Map<String, Object> services,
			final String serviceName
	) {
		final Object service = services.get(serviceName);
		final Method[] serviceMethods = service.getClass().getMethods();
		fillMethodDescriptors(serviceMethods, objectMethods, factoryName, serviceName);
	}
	
	private void fillMethodDescriptors(
			final Method[] serviceMethods,
			final Set<Method> objectMethods,
			final String factoryName,
			final String serviceName
	) {
		final StringBuilder sb = new StringBuilder();
		for(final Method serviceMethod : serviceMethods) {
			if(isNotAnObjectClassMethod(objectMethods, serviceMethod)) {
				final String methodDescriptor = buildMethodDescriptor(factoryName, serviceName, sb, serviceMethod);
				addMethodDescriptor(serviceMethod, methodDescriptor);
				resetStringBuilder(sb);
			}
		}
	}

	private static void resetStringBuilder(final StringBuilder sb) {
		sb.setLength(0);
	}

	private void addMethodDescriptor(
			final Method serviceMethod,
			final String methodDescriptor
	) {
		if(!methodDescriptors.containsKey(methodDescriptor)) {
			final Class<?>[] argTypes = serviceMethod.getParameterTypes();
			methodDescriptors.put(methodDescriptor, argTypes);
		}
		else {
			LOG.error("Multiple method descriptors : {}", methodDescriptor);
		}
	}

	private static String buildMethodDescriptor(
			final String factoryName,
			final String serviceName, 
			final StringBuilder sb,
			final Method serviceMethod
	) {
		final String methodName = serviceMethod.getName();
		return sb.append(factoryName).append('/').append(serviceName).append('/').append(methodName).toString();
	}

	private static boolean isNotAnObjectClassMethod(
			final Set<Method> objectMethods,
			final Method serviceMethod
	) {
		return !objectMethods.contains(serviceMethod);
	}
	
	@Override
	public String processCustomCall(final CommandRequest commandRequest) {
		if ("service".equals(commandRequest.itemType)) {
			try {
				LOG.info("processing command : {}", commandRequest.value);
				final ServiceCallIdentifier serviceCallIdentifier = buildServiceCallIdentifier(commandRequest.value);
				final ValueResponse valueResponse = callService(serviceCallIdentifier, commandRequest.getId());
				return valueResponse.value;
			}
			catch (final Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return null;
	}

	private ServiceCallIdentifier buildServiceCallIdentifier(final String value) {
		final List<String> retrieveMessageAsWord;
		if(value.contains(Property.DEFAULT_PARAM_INPUT_SEPARATOR)){
			String[] initSplit = value.split(Property.DEFAULT_PARAM_INPUT_SEPARATOR);
			retrieveMessageAsWord = new ArrayList<String>(retrieveMessageAsWords(initSplit[0]));
			retrieveMessageAsWord.addAll(retrieveParametersAsWords(initSplit[1]));
		}else{
			retrieveMessageAsWord = new ArrayList<String>(retrieveMessageAsWords(value));
		}
		//final String methodDescriptor = searchInRepos(retrieveMessageAsWord.get(0));
		final String methodDescriptor = retrieveMessageAsWord.get(0);
		final String[] split = methodDescriptor.split("/");
		final String factory = split[0];
		final String serviceName = split[1];
		final String methodName = split[2];
		final int nbArgs = retrieveMessageAsWord.size() - NB_NO_ARGS_WORD;
		LOG.info("nbArgs {}", Integer.valueOf(nbArgs));
		final Class<?>[] argsType = methodDescriptors.get(methodDescriptor);
		final Object[] args = new Object[nbArgs];
		fillArgs(argsType, args, retrieveMessageAsWord);
		LOG.info("finded service to call : {}/{}/{}", factory, serviceName, methodName);
		return new ServiceCallIdentifier(factory, serviceName, methodName, argsType, args);
	}

	private static List<String> retrieveMessageAsWords(final String value) {
		return Arrays.asList(StringUtils.split(value));
	}
	private static List<String> retrieveParametersAsWords(final String value) {
		return Arrays.asList(StringUtils.split(value, Property.DEFAULT_PARAM_SEPARATOR));
	}

	private static void fillArgs(
			final Class<?>[] argsType,
			final Object[] args,
			final List<String> retrieveMessageAsWord
	) {
		final int size = retrieveMessageAsWord.size();
		for(int index = NB_NO_ARGS_WORD; index < size; ++index) {
			final String object = retrieveMessageAsWord.get(index);
			final String param = object != null ? object.trim() : "null";
			final Class<?> classArgs = argsType[index - NB_NO_ARGS_WORD];
			args[index - NB_NO_ARGS_WORD] = computeObject(classArgs, param);
		}
	}

	private static Object computeObject(
			final Class<?> parameterClass,
			final String stringObject
	) {
		return "null".equalsIgnoreCase(stringObject) ? null : StringToObjectInstance.getInstance().toObject(stringObject, parameterClass);
	}

	private static ValueResponse callService(
			final ServiceCallIdentifier serviceCallIdentifier,
			final String id
	) throws Exception {
		final Object service = findService(serviceCallIdentifier);
		final Method serviceMethod = findMethod(serviceCallIdentifier, service);
		try {
			final Object serviceCallResult = serviceMethod.invoke(service, serviceCallIdentifier.args);
			LOG.info("service result {}", serviceCallResult);
			final String stringuifiedResponse = serviceCallResult instanceof Throwable ? "Exception " + String.valueOf(serviceCallResult) : String.valueOf(serviceCallResult);
			return new ValueResponse(id, stringuifiedResponse);
		}
		catch(final InvocationTargetException e) {
			LOG.debug(e.getMessage(), e);
			return handleInvocationTargetException(e, id);
		}
		catch(final UndeclaredThrowableException e) {
			LOG.debug(e.getMessage(), e);
			return handleUndeclaredThrowableException(e, id);
		}
	}

	private static ValueResponse handleInvocationTargetException(
			final InvocationTargetException e,
			final String id
	) {
		final Throwable targetException = e.getTargetException();
		final ValueResponse valueResponse;
		if(targetException != null) {
			valueResponse = new ValueResponse(id, "Exception " + targetException.getMessage());
		}
		else {
			valueResponse = new ValueResponse(id, "Exception " + e.getMessage());
		}
		return valueResponse;
	}
	
	private static ValueResponse handleUndeclaredThrowableException(
			final UndeclaredThrowableException e,
			final String id
	) {
		final Throwable undeclaredThrowable = e.getUndeclaredThrowable();
		final ValueResponse valueResponse;
		if(undeclaredThrowable != null) {
			valueResponse = new ValueResponse(id, "Exception " + undeclaredThrowable.getMessage());
		}
		else {
			valueResponse = new ValueResponse(id, "Exception " + e.getMessage());
		}
		return valueResponse;
	}
	
	private static Object findService(final ServiceCallIdentifier serviceCallIdentifier) throws Exception {
		final IServiceFactory serviceFactory = ServicesManager.getInstance().getServiceFactory(serviceCallIdentifier.factory);
		return serviceFactory.getService(serviceCallIdentifier.serviceName);
	}

	private static Method findMethod(final ServiceCallIdentifier serviceCallIdentifier, final Object service) throws Exception {
		final Class<? extends Object> serviceClass = service.getClass();
		return serviceClass.getMethod(serviceCallIdentifier.methodName, serviceCallIdentifier.argsType);
	}

	public String searchInRepos(final String locator) {
		try {
			/*final List<ComponentConfigLine> componentConfigLines = configBlock.getLines();
			for(final ComponentConfigLine componentConfigLine : componentConfigLines) {
				if(componentConfigLine.getTestName().equals(locator)) {
					return componentConfigLine.getComponentAssociation();
				}
			}
			final String[] classAndAssociation = locator.split(":");
			final String fullClassName = classAndAssociation[0];
			final String classAlias = classAndAssociation[1];
			configBlock.addLine(classAlias, "", fullClassName);*/
			//configService.saveNormal(configBlock);
			//return classAlias;
			return locator;
		}
		catch(final Exception e) {
			LOG.error(e.getMessage(), e);
			return locator;
		}
	}

	@Override
	protected String makeHandleFixtureCall(
			final Component component,
			final IIdRequest request
	) {
		return null;
	}

	@Override
	public String getName() {
		return "STX-PLUGIN-ServiceCallCustomHandler";
	}

	@Override
	public boolean isInterestedIn(Component component) {
		return false;
	}

	@Override
	public List<String> getCommandRequestWhiteList() {
		return whiteList;
	}
	
	public static void main(String[] args) {
		//add in unit test
		String value = "swi-normal/assemblage.prevision/findEstActifPrevisionForTnr 03/02/2015 " +Property.DEFAULT_PARAM_INPUT_SEPARATOR+ " TGVWAGON In Plaza|DFCE";
		String[] initSplit = value.split(Property.DEFAULT_PARAM_INPUT_SEPARATOR);
		final List<String> retrieveMessageHeader = retrieveMessageAsWords(initSplit[0]);
		final List<String> retrieveMessageAsWord = new ArrayList<String>(retrieveMessageHeader);
		retrieveMessageAsWord.addAll(retrieveParametersAsWords(initSplit[1]));
		System.out.println(StringUtils.join(retrieveMessageAsWord, ","));
		
	}
}