package com.synaptix.toast.swing.agent.web;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/*  
 * 
 * http://stackoverflow.com/questions/17385779/how-do-i-load-a-javascript-file-into-the-dom-using-selenium
 * http://stackoverflow.com/questions/9805508/how-to-capture-user-action-on-browser-by-java-code
 * 
 */
public class RecordWebTester {
	
	public static void main(String[] args) {
        WebDriver driver = new FirefoxDriver();
        driver.get("http://10.23.252.131:9000/#/main");
        try {
			Thread.sleep(20000);
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
        String script = "var s=window.document.createElement('script'); s.src='http://code.jquery.com/jquery-2.1.4.min.js'; window.document.head.appendChild(s);";
        ((JavascriptExecutor) driver).executeScript(script);
        System.out.println("Page title is: " + driver.getTitle());
    }
	
}
