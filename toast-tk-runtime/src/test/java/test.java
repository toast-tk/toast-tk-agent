import java.util.Arrays;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.synaptix.toast.dao.domain.impl.report.Campaign;
import com.synaptix.toast.dao.domain.impl.report.Project;
import com.synaptix.toast.dao.domain.impl.test.TestPage;
import com.synaptix.toast.dao.guice.MongoModule;
import com.synaptix.toast.dao.service.dao.access.project.ProjectDaoService;
import com.synpatix.toast.runtime.core.parse.TestParser;


public class test {

	public static void main(String[] args) {
		Injector in = Guice.createInjector(new MongoModule());

		ProjectDaoService.Factory repoFactory = in.getInstance(ProjectDaoService.Factory.class);
		ProjectDaoService service = repoFactory.create("test_project_db");
		Project p = new Project();
		p.setName("test");
		
		Campaign c = new Campaign();
		c.setName("test");
		StringBuilder b = new StringBuilder();
		b.append("h1. Name:Test Scenario").append("\n");
		b.append("#scenario id:54760c57131400131411e779").append("\n");
		b.append("#scenario driver:connecteurSwing").append("\n");
		b.append("|| scenario || swing ||").append("\n");
		b.append("| Saisir *valeur* dans *ChooseApplicationRusDialog.applicationBox* |").append("\n");
		b.append("| Cliquer sur *ChooseApplicationRusDialog.OK* |").append("\n");	
		
		TestParser par = new TestParser();
		TestPage parseString = par.parseString(b.toString());
		c.setTestCases(Arrays.asList(parseString));
		p.setCampaigns(Arrays.asList(c));
		
		service.saveNewIteration(p);
	}

}
