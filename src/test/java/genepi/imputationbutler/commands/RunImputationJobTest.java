package genepi.imputationbutler.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import genepi.imputationbot.client.CloudgeneClient;
import genepi.imputationbot.client.CloudgeneException;
import genepi.imputationbot.client.CloudgeneInstance;
import genepi.imputationbot.client.CloudgeneInstanceList;
import genepi.imputationbot.client.CloudgeneJob;
import genepi.imputationbot.client.CloudgeneJobList;
import genepi.imputationbot.commands.AddInstance;
import genepi.imputationbot.commands.BaseCommand;
import genepi.imputationbot.commands.ListJobs;
import genepi.imputationbot.commands.ListProjects;
import genepi.imputationbot.commands.RunImputationJob;
import genepi.imputationbot.model.Project;
import genepi.imputationbutler.ImputationServer;
import genepi.io.FileUtil;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.util.Zip4jConstants;

public class RunImputationJobTest {

	public static final String VCF = "test-data/chr20.R50.merged.1.330k.recode.small.vcf.gz";

	public static final String VCF_SMALL = "test-data/small.vcf.gz";

	public static final String VCF_SMALL_CHR1 = "test-data/small.chr1.vcf.gz";

	public static final String VCF_SMALL_CHR2 = "test-data/small.chr2.vcf.gz";

	public static final String VCF_SMALL_CHR3 = "test-data/small.chr3.vcf.gz";

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
		assertNull(runImputationJob.getProject());
	}

	@Test
	public void testRunImputationWithoutWait() throws IOException, CloudgeneException, InterruptedException {

		deleteInstances();

		String hostname = ImputationServer.getInstance().getUrl();
		String token = ImputationServer.getInstance().getAdminToken();

		AddInstance addInstance = new AddInstance(hostname, token);
		assertEquals(0, addInstance.start());

		String[] args = new String[] { "--refpanel", "hapmap-2", "--population", "eur", "--files", VCF };

		RunImputationJob runImputationJob = new RunImputationJob(args);
		int result = runImputationJob.start();
		assertEquals(0, result);

		CloudgeneJob job = runImputationJob.getJob();
		assertNotNull(job);

		CloudgeneClient client = new CloudgeneClient(getInstances());
		client.waitForJob(job);

		job = client.getJobDetails(job.getId());
		assertTrue(job.isSuccessful());
		assertFalse(job.isRetired());
		assertFalse(job.isRunning());
		assertTrue(job.getExecutionTime() > 0);
		assertEquals(hostname, job.getInstance().getHostname());
		assertNull(runImputationJob.getProject());
	}

	@Test
	public void testAutoDownloadWithPassword() throws Exception {

		String password = "test-password";

		deleteInstances();

		String hostname = ImputationServer.getInstance().getUrl();
		String token = ImputationServer.getInstance().getAdminToken();

		AddInstance addInstance = new AddInstance(hostname, token);
		assertEquals(0, addInstance.start());

		RunImputationJob runImputationJob = new RunImputationJob("--refpanel", "hapmap-2", "--population", "eur",
				"--files", VCF, "--autoDownload", "--password", password, "--name", "test-job");
		int result = runImputationJob.start();
		assertEquals(0, result);

		CloudgeneJob job = runImputationJob.getJob();
		assertNotNull(job);
		assertTrue(job.isSuccessful());

		String output = job.getId() + "-test-job";

		assertTrue(new File(output).exists());
		assertTrue(new File(output + "/local/chr_20.zip").exists());
		assertTrue(new File(output + "/local/chr20.dose.vcf.gz").exists());
		assertTrue(new File(output + "/local/chr20.info.gz").exists());

		FileUtil.deleteDirectory(job.getId());

		ZipFile file = new ZipFile(output + "/local/chr_20.zip");
		assertTrue(file.isValidZipFile());
		assertTrue(file.isEncrypted());

		// check if zip file is not AES encrypted
		for (Object o : file.getFileHeaders()) {
			FileHeader h = (FileHeader) o;
			assertEquals(true, h.isEncrypted());
			assertNotEquals(Zip4jConstants.ENC_METHOD_AES, h.getEncryptionMethod());
		}
	}
	
	@Test
	public void testAutoDownloadWithoutPassword() throws Exception {

		deleteInstances();

		String hostname = ImputationServer.getInstance().getUrl();
		String token = ImputationServer.getInstance().getAdminToken();

		AddInstance addInstance = new AddInstance(hostname, token);
		assertEquals(0, addInstance.start());

		RunImputationJob runImputationJob = new RunImputationJob("--refpanel", "hapmap-2", "--population", "eur",
				"--files", VCF, "--autoDownload", "--name", "test-job");
		int result = runImputationJob.start();
		assertEquals(0, result);

		CloudgeneJob job = runImputationJob.getJob();
		assertNotNull(job);
		assertTrue(job.isSuccessful());

		String output = job.getId() + "-test-job";

		assertTrue(new File(output).exists());
		assertTrue(new File(output + "/local/chr_20.zip").exists());
		assertFalse(new File(output + "/local/chr20.dose.vcf.gz").exists());
		assertFalse(new File(output + "/local/chr20.info.gz").exists());

		FileUtil.deleteDirectory(job.getId());

		ZipFile file = new ZipFile(output + "/local/chr_20.zip");
		assertTrue(file.isValidZipFile());
		assertTrue(file.isEncrypted());

		// check if zip file is not AES encrypted
		for (Object o : file.getFileHeaders()) {
			FileHeader h = (FileHeader) o;
			assertEquals(true, h.isEncrypted());
			assertNotEquals(Zip4jConstants.ENC_METHOD_AES, h.getEncryptionMethod());
		}
	}
	
	@Test
	public void testRunImputationAndAddToProject() throws IOException, CloudgeneException, InterruptedException {

		deleteInstances();
		deleteProjects();

		String hostname = ImputationServer.getInstance().getUrl();
		String token = ImputationServer.getInstance().getAdminToken();

		AddInstance addInstance = new AddInstance(hostname, token);
		assertEquals(0, addInstance.start());

		String[] args = new String[] { "--refpanel", "hapmap-2", "--population", "eur", "--files", VCF, "--project",
				"test-project" };

		RunImputationJob runImputationJob1 = new RunImputationJob(args);
		int result1 = runImputationJob1.start();
		assertEquals(0, result1);

		CloudgeneJob job1 = runImputationJob1.getJob();
		assertNotNull(job1);

		Project project1 = runImputationJob1.getProject();
		assertNotNull(project1);
		assertTrue(project1.containsJob(job1.getId()));
		assertEquals("test-project", project1.getName());
		assertEquals(1, project1.getJobs().size());

		String[] args2 = new String[] { "--refpanel", "hapmap-2", "--population", "eur", "--files", VCF_SMALL,
				"--project", "test-project" };

		RunImputationJob runImputationJob2 = new RunImputationJob(args2);
		int result2 = runImputationJob2.start();
		assertEquals(0, result2);

		CloudgeneJob job2 = runImputationJob2.getJob();
		assertNotNull(job2);

		Project project2 = runImputationJob2.getProject();
		assertNotNull(project2);
		assertTrue(project2.containsJob(job1.getId()));
		assertTrue(project2.containsJob(job2.getId()));
		assertEquals("test-project", project2.getName());
		assertEquals(2, project2.getJobs().size());

		CloudgeneClient client = new CloudgeneClient(getInstances());
		client.waitForProject(project2);

		CloudgeneJobList jobs = client.getJobs(project2);
		jobs.sortById();

		assertFalse(jobs.get(0).isSuccessful());
		assertFalse(jobs.get(0).isRetired());
		assertFalse(jobs.get(0).isRunning());
		assertEquals(hostname, jobs.get(0).getInstance().getHostname());

		assertTrue(jobs.get(1).isSuccessful());
		assertFalse(jobs.get(1).isRetired());
		assertFalse(jobs.get(1).isRunning());
		assertEquals(hostname, jobs.get(1).getInstance().getHostname());

		ListJobs listJobs = new ListJobs("test-project");
		int result3 = listJobs.start();
		assertEquals(0, result3);

		ListProjects listProjects = new ListProjects();
		int result4 = listProjects.start();
		assertEquals(0, result4);

		assertEquals(1, listProjects.getData().length);
		assertEquals(3, listProjects.getData()[0].length);
		assertEquals("test-project", listProjects.getData()[0][0]);
		assertTrue(listProjects.getData()[0][2].contains(job1.getId()));
		assertTrue(listProjects.getData()[0][2].contains(job2.getId()));
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
		assertNull(runImputationJob.getProject());
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
		assertNull(runImputationJob.getProject());

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
		assertNull(runImputationJob.getProject());
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
		assertNull(runImputationJob.getProject());
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
		assertNull(runImputationJob.getProject());
	}

	@Test
	public void testRunImputationWithMultipleFiles() throws IOException, CloudgeneException, InterruptedException {

		deleteInstances();

		String hostname = ImputationServer.getInstance().getUrl();
		String token = ImputationServer.getInstance().getAdminToken();

		AddInstance addInstance = new AddInstance(hostname, token);
		assertEquals(0, addInstance.start());

		String[] args = new String[] { "--refpanel", "hapmap-2", "--population", "eur", "--files", VCF_SMALL_CHR1,
				VCF_SMALL_CHR2, VCF_SMALL_CHR3 };

		RunImputationJob runImputationJob = new RunImputationJob(args);
		int result = runImputationJob.start();
		assertEquals(0, result);

		CloudgeneJob job = runImputationJob.getJob();
		assertNotNull(job);

		CloudgeneClient client = new CloudgeneClient(getInstances());
		client.waitForJob(job);
		job = client.getJobDetails(job.getId());
		assertTrue(job.isSuccessful());
		assertFalse(job.isRetired());
		assertFalse(job.isRunning());
		assertTrue(job.getExecutionTime() > 0);
		assertEquals(hostname, job.getInstance().getHostname());
		assertNull(runImputationJob.getProject());
		
		//check three result files
		JSONArray outputs = job.getOutputs();
		JSONObject files = outputs.getJSONObject(2);
		assertEquals("Imputation Results", files.getString("description"));
		assertEquals(3, files.getJSONArray("files").length());
	}

	// TODO: test optional parameters: build, r2 filter, password, aesEncryption?

	private void deleteInstances() {
		// delete instances files
		File file = new File(FileUtil.path(BaseCommand.APP_HOME, BaseCommand.INSTANCES_FILENAME));
		if (file.exists()) {
			file.delete();
		}
	}

	private void deleteProjects() {
		// delete instances files
		File file = new File(FileUtil.path(BaseCommand.APP_HOME, BaseCommand.PROJECTS_FILENAME));
		if (file.exists()) {
			file.delete();
		}
	}

	private List<CloudgeneInstance> getInstances() throws IOException {
		return new Vector<CloudgeneInstance>(CloudgeneInstanceList
				.load(FileUtil.path(BaseCommand.APP_HOME, BaseCommand.INSTANCES_FILENAME)).getAll());

	}

}
