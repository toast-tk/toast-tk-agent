package com.synaptix.toast.swing.agent.swing;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.cli.ParseException;
import org.xml.sax.SAXException;

import com.synaptix.toast.swing.agent.runtime.SutRunnerAsExec;

public class JNLPEnvInitializer {

	public static void main(String[] args) throws ParseException, IllegalAccessException, SAXException, IOException, ParserConfigurationException {
		SutRunnerAsExec runner = SutRunnerAsExec.FromArgs(args);
		runner.init("JNLP", true);
	}
	
}
