package io.toast.tk.agent.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DriverFactory {

	private static final Logger LOG = LogManager.getLogger(DriverFactory.class);
	private static DRIVER selected;
	
	public enum DRIVER {
		CHROME_32("-  Chrome 32", "CHROME"), CHROME_64("-  Chrome 64", "CHROME"), 
		FIREFOX_32("-  Firefox 32", "FIREFOX"), FIREFOX_64("-  Firefox 64", "FIREFOX"), 
		IE_32("-  IE 32", "IE"), IE_64("-  IE 64", "IE");

	    private final String driverName;
	    private final String driverType;

	    private DRIVER(String name, String type) {
	    	driverName = name;
	    	driverType = type;
	    }
	    
	    public String toString() {
	    	return driverName;
	    }

	    public String getName() {
	        return driverName;
	    }

	    public String getType() {
	        return driverType;
	    }
	    
	    public String getValue() {
	        return getDriverValue(this.toString());
	    }
	}

	public static void setSelected(String driver) {
		DRIVER value = getDriver(driver);
		setSelected(value);
	}
	public static void setSelected(DRIVER value) {
		selected = value;
	}
	
	public static DRIVER getSelected() {
		return selected;
	}
	
	public static DRIVER getDriver(String driver) {
		switch(driver) {
		case "-  Chrome 32" : 
			return DRIVER.CHROME_32;
		case "-  Chrome 64" : 
			return DRIVER.CHROME_64;
		case "-  Firefox 32" : 
			return DRIVER.FIREFOX_32;
		case "-  Firefox 64" : 
			return DRIVER.FIREFOX_64;
		case "-  IE 32" : 
			return DRIVER.IE_32;
		case "-  IE 64" : 
			return DRIVER.IE_64;
		default : 
			LOG.warn("Can't match the value " + driver);
			return DRIVER.CHROME_64;
		}
	}

	public static String getDriverValue() {
		return getDriverValue(getSelected().toString());
	}
	public static String getDriverValue(String driver) {
		DRIVER value = getDriver(driver);
		
		switch(value) {
		case CHROME_32 : 
			return AgentConfigProvider.TOAST_CHROMEDRIVER_32_PATH;
		case CHROME_64 : 
			return AgentConfigProvider.TOAST_CHROMEDRIVER_64_PATH;
		case FIREFOX_32 : 
			return AgentConfigProvider.TOAST_FIREFOXDRIVER_32_PATH;
		case FIREFOX_64 : 
			return AgentConfigProvider.TOAST_FIREFOXDRIVER_64_PATH;
		case IE_32 : 
			return AgentConfigProvider.TOAST_IEDRIVER_32_PATH;
		case IE_64 : 
			return AgentConfigProvider.TOAST_IEDRIVER_64_PATH;
		default : 
			return AgentConfigProvider.TOAST_CHROMEDRIVER_64_PATH;
		}
	}
}
