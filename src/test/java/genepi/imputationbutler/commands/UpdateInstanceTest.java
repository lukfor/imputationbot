package genepi.imputationbutler.commands;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import genepi.imputationbot.commands.AddInstance;
import genepi.imputationbot.commands.BaseCommand;
import genepi.imputationbot.commands.ListInstances;
import genepi.imputationbot.commands.UpdateInstance;
import genepi.imputationbutler.ImputationServer;
import genepi.io.FileUtil;

public class UpdateInstanceTest {

	@Test
	public void testInvalidTokenAndEmtpyList() {

		deleteInstances();

		UpdateInstance updateInstance = new UpdateInstance("1", "INVALID-TOKEN");
		int result = updateInstance.start();
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

		AddInstance addInstance = new AddInstance(hostname, token);
		assertEquals(0, addInstance.start());

		ListInstances listInstancesAfter = new ListInstances();
		assertEquals(0, listInstancesAfter.start());
		assertEquals(1, listInstancesAfter.getInstanceList().getAll().size());

		UpdateInstance updateInstance = new UpdateInstance("1", token);
		assertEquals(0, updateInstance.start());

		listInstancesAfter = new ListInstances();
		assertEquals(0, listInstancesAfter.start());
		assertEquals(1, listInstancesAfter.getInstanceList().getAll().size());

	}

	@Test
	public void testUpdateExpiredToken() throws Exception {

		deleteInstances();

		String hostname = ImputationServer.getInstance().getUrl();
		String token = ImputationServer.getInstance().getAdminToken();

		AddInstance addInstance = new AddInstance(hostname, token);
		assertEquals(0, addInstance.start());

		ListInstances listInstancesAfter = new ListInstances();
		assertEquals(0, listInstancesAfter.start());
		assertEquals(1, listInstancesAfter.getInstanceList().getAll().size());

		ImputationServer.getInstance().revokeTokenForUser("admin", "admin1978");

		UpdateInstance updateInstance = new UpdateInstance("1", token);
		assertEquals(1, updateInstance.start());

		listInstancesAfter = new ListInstances();
		assertEquals(0, listInstancesAfter.start());
		assertEquals(1, listInstancesAfter.getInstanceList().getAll().size());
		assertEquals(token, listInstancesAfter.getInstanceList().getByHostname(hostname).getToken());
		
		String newToken = ImputationServer.getInstance().createTokenForUser("admin", "admin1978");		
		updateInstance = new UpdateInstance("1", newToken);
		assertEquals(0, updateInstance.start());
		
		listInstancesAfter = new ListInstances();
		assertEquals(0, listInstancesAfter.start());
		assertEquals(1, listInstancesAfter.getInstanceList().getAll().size());
		assertEquals(newToken, listInstancesAfter.getInstanceList().getByHostname(hostname).getToken());

	}

	@Test
	public void testInvalidToken() throws Exception {

		deleteInstances();

		String hostname = ImputationServer.getInstance().getUrl();
		String token = ImputationServer.getInstance().getAdminToken();

		AddInstance addInstance = new AddInstance(hostname, token);
		assertEquals(0, addInstance.start());

		ListInstances listInstancesAfter = new ListInstances();
		assertEquals(0, listInstancesAfter.start());
		assertEquals(1, listInstancesAfter.getInstanceList().getAll().size());

		UpdateInstance updateInstance = new UpdateInstance("1", "INVALID-TOKEN");
		assertEquals(1, updateInstance.start());

		listInstancesAfter = new ListInstances();
		assertEquals(0, listInstancesAfter.start());
		assertEquals(1, listInstancesAfter.getInstanceList().getAll().size());
		assertEquals(token, listInstancesAfter.getInstanceList().getByHostname(hostname).getToken());

	}

	@Test
	public void testInvalidId() throws Exception {

		deleteInstances();

		String hostname = ImputationServer.getInstance().getUrl();
		String token = ImputationServer.getInstance().getAdminToken();

		AddInstance addInstance = new AddInstance(hostname, token);
		assertEquals(0, addInstance.start());

		ListInstances listInstancesAfter = new ListInstances();
		assertEquals(0, listInstancesAfter.start());
		assertEquals(1, listInstancesAfter.getInstanceList().getAll().size());

		UpdateInstance updateInstance = new UpdateInstance("2", token);
		assertEquals(1, updateInstance.start());

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
