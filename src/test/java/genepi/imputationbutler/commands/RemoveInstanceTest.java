package genepi.imputationbutler.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;

import org.junit.Test;

import genepi.imputationbot.commands.AddInstance;
import genepi.imputationbot.commands.BaseCommand;
import genepi.imputationbot.commands.ListInstances;
import genepi.imputationbot.commands.RemoveInstance;
import genepi.imputationbutler.ImputationServer;
import genepi.io.FileUtil;

public class RemoveInstanceTest {

	@Test
	public void testCorrectId() throws Exception {

		deleteInstances();

		String hostname = ImputationServer.getInstance().getUrl();
		String token = ImputationServer.getInstance().getAdminToken();

		ListInstances listInstancesBefore = new ListInstances();
		// expect error, since no instances set
		assertEquals(1, listInstancesBefore.start());

		AddInstance addInstance = new AddInstance(hostname, token);
		assertEquals(0, addInstance.start());

		ListInstances listInstancesAfter = new ListInstances();
		assertEquals(0, listInstancesAfter.start());
		assertEquals(1, listInstancesAfter.getInstanceList().getAll().size());

		RemoveInstance removeInstance = new RemoveInstance("1");
		assertEquals(0, removeInstance.start());

		listInstancesAfter = new ListInstances();
		// expect error, since no instances set
		assertEquals(1, listInstancesAfter.start());
		assertEquals(0, listInstancesAfter.getInstanceList().getAll().size());

	}

	@Test
	public void testWrongId() throws Exception {

		deleteInstances();

		String hostname = ImputationServer.getInstance().getUrl();
		String token = ImputationServer.getInstance().getAdminToken();

		ListInstances listInstancesBefore = new ListInstances();
		// expect error, since no instances set
		assertEquals(1, listInstancesBefore.start());

		AddInstance addInstance = new AddInstance(hostname, token);
		assertEquals(0, addInstance.start());

		ListInstances listInstancesAfter = new ListInstances();
		assertEquals(0, listInstancesAfter.start());
		assertEquals(1, listInstancesAfter.getInstanceList().getAll().size());

		RemoveInstance removeInstance = new RemoveInstance("2");
		assertNotEquals(0, removeInstance.start());

		removeInstance = new RemoveInstance("0");
		assertNotEquals(0, removeInstance.start());
	}

	private void deleteInstances() {
		// delete instances files
		File file = new File(FileUtil.path(BaseCommand.APP_HOME, BaseCommand.INSTANCES_FILENAME));
		if (file.exists()) {
			file.delete();
		}
	}

}
