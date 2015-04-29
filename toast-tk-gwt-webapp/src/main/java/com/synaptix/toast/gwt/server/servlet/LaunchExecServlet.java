package com.synaptix.toast.gwt.server.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class LaunchExecServlet extends HttpServlet {

	private static final Logger LOG = LogManager.getLogger(LaunchExecServlet.class);
	private static final long serialVersionUID = -1497369902256863496L;
	private static final String COMMAND_PREFIX = "C:\\Windows\\System32\\cmd.exe /k \"\"%JAVA_HOME%\\bin\\javaws\" -J-Drus.inspect=true ";
	private static final String COMMAND_SUFFIX = "\""; 
	
	@Inject
	public LaunchExecServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			String command = req.getParameter("cmd");
			if (command == null) {
				LOG.info(String.format("No command to process !"));
				throw new ServletException("no command delcared");
			}
			
			command = COMMAND_PREFIX + command + COMMAND_SUFFIX;
			LOG.info(String.format("Processing command %s !", command));
			Process p = null;
			try {
				p = Runtime.getRuntime().exec(command);
				if (LOG.isDebugEnabled()) {
					BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
					String line = null;
					while ((line = in.readLine()) != null) {
						LOG.debug(line);
					}
				}
				LOG.info(String.format("Command %s executed !", command));
			} catch (Exception e) {
				String out = String.format("Failed to execute cmd: %s",command);
				LOG.info(out, e);
				throw new ServletException(out);
			}finally{
				if(p != null){
					p.destroy();
				}
			}
			
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}
}
