package com.synaptix.toast.automation.report;

import java.util.Locale;

import org.apache.commons.lang.LocaleUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import com.synaptix.toast.dao.domain.impl.test.TestPage;
import com.synaptix.toast.dao.domain.impl.test.block.CommentBlock;

/**
 * http://www.thymeleaf.org/doc/tutorials/2.1/usingthymeleaf.html
 * 
 * @author Sallah Kokaina <sallah.kokaina@gmail.com>
 *
 */
public class ThymeLeafHTMLReporter {

	public static void main(String[] args) {
		TemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setTemplateMode("HTML5");
		templateResolver.setCharacterEncoding("UTF-8");
		TemplateEngine templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
		Locale locale = LocaleUtils.toLocale("fr");
		final Context ctx = new Context(locale);
		TestPage test = new TestPage();
		CommentBlock block = new CommentBlock();
		block.addLine("Test Comment");
		test.setName("Script Name");
		test.setTechnicalErrorNumber(20);
		test.addBlock(block);
		ctx.setVariable("test", test);
		
		String process = templateEngine.process("test_report_template.html", ctx);
		System.out.println(process);
	}

}
