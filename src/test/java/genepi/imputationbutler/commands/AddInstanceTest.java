package genepi.imputationbutler.commands;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import genepi.imputationbot.commands.AddInstance;
import genepi.imputationbot.commands.BaseCommand;
import genepi.imputationbot.commands.ListInstances;
import genepi.imputationbutler.ImputationServer;
import genepi.io.FileUtil;

public class AddInstanceTest {

	@Test
	public void testInvalidToken() {

		deleteInstances();

		String hostname = ImputationServer.getInstance().getUrl();
		String token = "INVALID-TOKEN";

		AddInstance addInstance = new AddInstance(hostname, token);
		int result = addInstance.start();
		assertEquals(1, result);

		// expect error, since no instances set
		ListInstances listInstances = new ListInstances();
		assertEquals(1, listInstances.start());
	}

	@Test
	public void testInvalidHostname() {

		deleteInstances();

		String hostname = "http://forer.it";
		String token = ImputationServer.getInstance().getAdminToken();

		AddInstance addInstance = new AddInstance(hostname, token);
		int result = addInstance.start();
		assertEquals(1, result);

		// expect error, since no instances set
		ListInstances listInstances = new ListInstances();
		assertEquals(1, listInstances.start());

	}

	@Test
	public void testCorrectToken() throws Exception {

		deleteInstances();

		String hostname = ImputationServer.getInstance().getUrl();
		String token = ImputationServer.getInstance().getAdminToken();

		System.out.println("Test token on " + hostname);

		ListInstances listInstancesBefore = new ListInstances();
		// expect error, since no instances set
		assertEquals(1, listInstancesBefore.start());

		AddInstance addInstance = new AddInstance(hostname, token);
		assertEquals(0, addInstance.start());

		ListInstances listInstancesAfter = new ListInstances();
		assertEquals(0, listInstancesAfter.start());
		assertEquals(1, listInstancesAfter.getInstanceList().getAll().size());

		addInstance = new AddInstance(hostname, token);
		assertEquals(0, addInstance.start());

		listInstancesAfter = new ListInstances();
		assertEquals(0, listInstancesAfter.start());
		assertEquals(1, listInstancesAfter.getInstanceList().getAll().size());
	}

	private void deleteInstances() {
		// delete instances files
		File file = new File(FileUtil.path(BaseCommand.APP_HOME, BaseCommand.INSTANCES_FILENAME));
		if (file.exists()) {
			file.delete();
		}
	}

}
