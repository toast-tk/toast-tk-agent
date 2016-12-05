package io.toast.tk.agent.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;


public class TestRecorder {
	
	public static void main(
		String[] args) throws IOException {
		RestRecorderService service = new RestRecorderService();
		service.start();
		//injectRecordScript();
	}
	
	private static void injectRecordScript() throws IOException {
        WebDriver driver = new ChromeDriver();
        driver.get("http://localhost:9000/");
        File file = new File("C:\\TEMP\\test.js");
		FileInputStream openInputStream = FileUtils.openInputStream(file);
		String script = IOUtils.toString(openInputStream);
		String recordScript = "\"<script>\\\n"+script.replace("\r\n", "\\\r\n")+"\\\n</script>\"";
		String subscript = "$('head').append("+recordScript+");";
    	JavascriptExecutor executor = ((JavascriptExecutor) driver);
    	executor.executeScript(subscript);
	}
	
}
