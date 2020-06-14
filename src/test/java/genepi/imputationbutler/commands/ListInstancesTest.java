package genepi.imputationbutler.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;

import org.junit.Test;

import genepi.imputationbot.commands.AddInstance;
import genepi.imputationbot.commands.BaseCommand;
import genepi.imputationbot.commands.ListInstances;
import genepi.imputationbutler.ImputationServer;
import genepi.io.FileUtil;

public class ListInstancesTest {

	@Test
	public void testListRefPanels() {

		deleteInstances();

		String hostname = ImputationServer.getInstance().getUrl();
		String token = ImputationServer.getInstance().getAdminToken();

		AddInstance addInstance = new AddInstance(hostname, token);
		assertEquals(0, addInstance.start());

		ListInstances listInstances = new ListInstances();

		assertEquals(0, listInstances.start());

		assertEquals(1, listInstances.getData().length);
		assertEquals(6, listInstances.getData()[0].length);

		assertEquals("1", listInstances.getData()[0][0]);
		assertEquals("Michigan Imputation Server", listInstances.getData()[0][1]);
		assertEquals(ImputationServer.getInstance().getUrl(), listInstances.getData()[0][2]);
		assertEquals("admin", listInstances.getData()[0][3]);
		assertNotEquals("", listInstances.getData()[0][4]);
		assertNotEquals("", listInstances.getData()[0][5]);

	}

	private void deleteInstances() {
		// delete instances files
		File file = new File(FileUtil.path(BaseCommand.APP_HOME, BaseCommand.INSTANCES_FILENAME));
		if (file.exists()) {
			file.delete();
		}
	}

}
