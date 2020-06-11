package genepi.imputationbutler.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import genepi.imputationbot.client.CloudgeneJob;
import genepi.imputationbot.commands.AddInstance;
import genepi.imputationbot.commands.BaseCommand;
import genepi.imputationbot.commands.RunImputationJob;
import genepi.imputationbutler.ImputationServer;
import genepi.io.FileUtil;

public class RunImputationJobTest {

	public static final String VCF = "test-data/chr20.R50.merged.1.330k.recode.small.vcf.gz";

	@Test
	public void testRunImputationAndWait() throws IOException {

		deleteInstances();

		String hostname = ImputationServer.getInstance().getUrl();
		String token = ImputationServer.getInstance().getAdminToken();

		AddInstance addInstance = new AddInstance(hostname, token);
		assertEquals(0, addInstance.start());

		String[] args = new String[] { "--refpanel", "hapmap-2", "--population", "eur", "--files", VCF, "--wait" };

		RunImputationJob runImputationJob = new RunImputationJob(args);
		int result = runImputationJob.start();
		assertEquals(0, result);

		CloudgeneJob job = runImputationJob.getJob();
		assertNotNull(job);
		assertTrue(job.isSuccessful());
		assertFalse(job.isRetired());
		assertFalse(job.isRunning());
		assertEquals(hostname, job.getInstance().getHostname());

	}

	@Test
	public void testRunImputationWrongRefPanel() throws IOException {

		deleteInstances();

		String hostname = ImputationServer.getInstance().getUrl();
		String token = ImputationServer.getInstance().getAdminToken();

		AddInstance addInstance = new AddInstance(hostname, token);
		assertEquals(0, addInstance.start());

		String[] args = new String[] { "--refpanel", "test-panel", "--population", "eur", "--files", VCF, "--wait" };

		RunImputationJob runImputationJob = new RunImputationJob(args);
		int result = runImputationJob.start();
		assertNotEquals(0, result);

		CloudgeneJob job = runImputationJob.getJob();
		assertNull(job);

	}

	@Test
	public void testRunImputationWrongPopulation() throws IOException {

		deleteInstances();

		String hostname = ImputationServer.getInstance().getUrl();
		String token = ImputationServer.getInstance().getAdminToken();

		AddInstance addInstance = new AddInstance(hostname, token);
		assertEquals(0, addInstance.start());

		String[] args = new String[] { "--refpanel", "hapmap-2", "--population", "test-pop", "--files", VCF, "--wait" };

		RunImputationJob runImputationJob = new RunImputationJob(args);
		int result = runImputationJob.start();
		assertNotEquals(0, result);

		CloudgeneJob job = runImputationJob.getJob();
		assertNull(job);

	}

	@Test
	public void testRunImputationMissingPopulation() throws IOException {

		deleteInstances();

		String hostname = ImputationServer.getInstance().getUrl();
		String token = ImputationServer.getInstance().getAdminToken();

		AddInstance addInstance = new AddInstance(hostname, token);
		assertEquals(0, addInstance.start());

		String[] args = new String[] { "--refpanel", "hapmap-2", "--files", VCF, "--wait" };

		RunImputationJob runImputationJob = new RunImputationJob(args);
		int result = runImputationJob.start();
		assertNotEquals(0, result);

		CloudgeneJob job = runImputationJob.getJob();
		assertNull(job);

	}

	@Test
	public void testRunImputationMissingRefPanel() throws IOException {

		deleteInstances();

		String hostname = ImputationServer.getInstance().getUrl();
		String token = ImputationServer.getInstance().getAdminToken();

		AddInstance addInstance = new AddInstance(hostname, token);
		assertEquals(0, addInstance.start());

		String[] args = new String[] { "--population", "eur", "--files", VCF, "--wait" };

		RunImputationJob runImputationJob = new RunImputationJob(args);
		int result = runImputationJob.start();
		assertNotEquals(0, result);

		CloudgeneJob job = runImputationJob.getJob();
		assertNull(job);

	}

	@Test
	public void testRunImputationWrongFile() throws IOException {

		deleteInstances();

		String hostname = ImputationServer.getInstance().getUrl();
		String token = ImputationServer.getInstance().getAdminToken();

		AddInstance addInstance = new AddInstance(hostname, token);
		assertEquals(0, addInstance.start());

		String[] args = new String[] { "--refpanel", "hapmap-2", "--files", "wrong.vcf.gz", "--population", "eur",
				"--wait" };

		RunImputationJob runImputationJob = new RunImputationJob(args);
		int result = runImputationJob.start();
		assertNotEquals(0, result);

		CloudgeneJob job = runImputationJob.getJob();
		assertNull(job);
	}

	// TODO: test optional parameters: build, r2 filter, password, aesEncryption?

	// TODO: test project

	private void deleteInstances() {
		// delete instances files
		File file = new File(FileUtil.path(BaseCommand.APP_HOME, BaseCommand.INSTANCES_FILENAME));
		if (file.exists()) {
			file.delete();
		}
	}

}
