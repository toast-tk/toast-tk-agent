package io.toast.tk.agent.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DriverFactory {

	private static final Logger LOG = LogManager.getLogger(DriverFactory.class);
	private static DRIVER selected;
	
	public enum DRIVER {

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
	        return getDriver(this.toString());
	    }
	}

	public static void setSelected(String driver) {
		DRIVER value = DRIVER.CHROME_64;
		try {
			value = DRIVER.valueOf(driver);
		} catch(IllegalArgumentException e) {
			LOG.warn(e.getMessage());
		}
		setSelected(value);
	}
	public static void setSelected(DRIVER value) {
		selected = value;
	}
	
	public static DRIVER getSelected() {
		return selected;
	}

	public static String getDriver() {
		return getDriver(getSelected().toString());
	}
	public static String getDriver(String driver) {
		DRIVER value = DRIVER.CHROME_64;
		try {
			value = DRIVER.valueOf(driver);
		} catch(IllegalArgumentException e) {
			LOG.warn(e.getMessage());
		}
		
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
