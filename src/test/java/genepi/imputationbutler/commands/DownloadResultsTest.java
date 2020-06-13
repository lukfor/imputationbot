package genepi.imputationbutler.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import genepi.imputationbot.client.CloudgeneJob;
import genepi.imputationbot.commands.AddInstance;
import genepi.imputationbot.commands.BaseCommand;
import genepi.imputationbot.commands.DownloadResults;
import genepi.imputationbot.commands.RunImputationJob;
import genepi.imputationbutler.ImputationServer;
import genepi.io.FileUtil;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.util.Zip4jConstants;

public class DownloadResultsTest {

	public static final String VCF = "test-data/chr20.R50.merged.1.330k.recode.small.vcf.gz";

	public static final String VCF_SMALL = "test-data/small.vcf.gz";

	public static final String OUTPUT = "test-data-output";

	@Test
	public void testDownload() throws Exception {

		String password = "test-password";

		deleteInstances();

		String hostname = ImputationServer.getInstance().getUrl();
		String token = ImputationServer.getInstance().getAdminToken();

		AddInstance addInstance = new AddInstance(hostname, token);
		assertEquals(0, addInstance.start());

		RunImputationJob runImputationJob = new RunImputationJob("--refpanel", "hapmap-2", "--population", "eur",
				"--files", VCF, "--wait", "--password", password, "--name", "test-job");
		int result = runImputationJob.start();
		assertEquals(0, result);

		CloudgeneJob job = runImputationJob.getJob();
		assertNotNull(job);
		assertTrue(job.isSuccessful());

		DownloadResults downloadResults = new DownloadResults(job.getId());
		result = downloadResults.start();
		assertEquals(0, result);

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
	public void testDownloadWithRandomPassword() throws Exception {

		deleteInstances();

		String hostname = ImputationServer.getInstance().getUrl();
		String token = ImputationServer.getInstance().getAdminToken();

		AddInstance addInstance = new AddInstance(hostname, token);
		assertEquals(0, addInstance.start());

		RunImputationJob runImputationJob = new RunImputationJob("--refpanel", "hapmap-2", "--population", "eur",
				"--files", VCF, "--wait", "--name", "test-job", "--aesEncryption", "no");
		int result = runImputationJob.start();
		assertEquals(0, result);

		CloudgeneJob job = runImputationJob.getJob();
		assertNotNull(job);
		assertTrue(job.isSuccessful());

		DownloadResults downloadResults = new DownloadResults(job.getId());
		result = downloadResults.start();
		assertEquals(0, result);

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
	public void testDownloadWithAESEncryption() throws Exception {

		String password = "test-password";

		deleteInstances();

		String hostname = ImputationServer.getInstance().getUrl();
		String token = ImputationServer.getInstance().getAdminToken();

		AddInstance addInstance = new AddInstance(hostname, token);
		assertEquals(0, addInstance.start());

		RunImputationJob runImputationJob = new RunImputationJob("--refpanel", "hapmap-2", "--population", "eur",
				"--files", VCF, "--wait", "--password", password, "--name", "test-job", "--aesEncryption", "yes");
		int result = runImputationJob.start();
		assertEquals(0, result);

		CloudgeneJob job = runImputationJob.getJob();
		assertNotNull(job);
		assertTrue(job.isSuccessful());

		DownloadResults downloadResults = new DownloadResults(job.getId());
		result = downloadResults.start();
		assertEquals(0, result);

		String output = job.getId() + "-test-job";

		assertTrue(new File(output).exists());
		assertTrue(new File(output + "/local/chr_20.zip").exists());
		assertFalse(new File(output + "/local/chr20.dose.vcf.gz").exists());
		assertFalse(new File(output + "/local/chr20.info.gz").exists());

		FileUtil.deleteDirectory(job.getId());

		ZipFile file = new ZipFile(output + "/local/chr_20.zip");
		assertTrue(file.isValidZipFile());
		assertTrue(file.isEncrypted());
		// check if zip file is AES encrypted
		for (Object o : file.getFileHeaders()) {
			FileHeader h = (FileHeader) o;
			assertEquals(true, h.isEncrypted());
			assertEquals(Zip4jConstants.ENC_METHOD_AES, h.getEncryptionMethod());
		}
	}

	@Test
	public void testDownloadWithPassword() throws Exception {

		String password = "test-password";

		deleteInstances();

		String hostname = ImputationServer.getInstance().getUrl();
		String token = ImputationServer.getInstance().getAdminToken();

		AddInstance addInstance = new AddInstance(hostname, token);
		assertEquals(0, addInstance.start());

		String[] args = new String[] { "--refpanel", "hapmap-2", "--population", "eur", "--files", VCF, "--wait",
				"--password", password };

		RunImputationJob runImputationJob = new RunImputationJob(args);
		int result = runImputationJob.start();
		assertEquals(0, result);

		CloudgeneJob job = runImputationJob.getJob();
		assertNotNull(job);
		assertTrue(job.isSuccessful());

		FileUtil.deleteDirectory(OUTPUT);

		DownloadResults downloadResults = new DownloadResults(job.getId(), "--output", OUTPUT, "--password", password);
		result = downloadResults.start();
		assertEquals(0, result);

		assertTrue(new File(OUTPUT + "/" + job.getId()).exists());
		assertTrue(new File(OUTPUT + "/" + job.getId() + "/local/chr_20.zip").exists());
		assertTrue(new File(OUTPUT + "/" + job.getId() + "/local/chr20.dose.vcf.gz").exists());
		assertTrue(new File(OUTPUT + "/" + job.getId() + "/local/chr20.info.gz").exists());

		ZipFile file = new ZipFile(OUTPUT + "/" + job.getId() + "/local/chr_20.zip");
		assertTrue(file.isValidZipFile());
		assertTrue(file.isEncrypted());

		// TODO check file content (number of lines 63481)
	}

	@Test
	public void testDownloadFailedJob() throws Exception {

		deleteInstances();

		String hostname = ImputationServer.getInstance().getUrl();
		String token = ImputationServer.getInstance().getAdminToken();

		AddInstance addInstance = new AddInstance(hostname, token);
		assertEquals(0, addInstance.start());

		RunImputationJob runImputationJob = new RunImputationJob("--refpanel", "hapmap-2", "--population", "eur",
				"--files", VCF_SMALL, "--wait", "--password", "test");
		int result = runImputationJob.start();
		assertEquals(0, result);

		CloudgeneJob job = runImputationJob.getJob();
		assertNotNull(job);
		assertFalse(job.isSuccessful());

		FileUtil.deleteDirectory(OUTPUT);

		DownloadResults downloadResults = new DownloadResults(job.getId(), "--output", OUTPUT, "--password", "test");
		result = downloadResults.start();
		assertEquals(0, result);

		assertTrue(new File(OUTPUT + "/" + job.getId()).exists());
		assertFalse(new File(OUTPUT + "/" + job.getId() + "/local/chr_20.zip").exists());
		assertFalse(new File(OUTPUT + "/" + job.getId() + "/local/chr20.dose.vcf.gz").exists());
		assertFalse(new File(OUTPUT + "/" + job.getId() + "/local/chr20.info.gz").exists());
	}

	// TODO: download project

	private void deleteInstances() {
		// delete instances files
		File file = new File(FileUtil.path(BaseCommand.APP_HOME, BaseCommand.INSTANCES_FILENAME));
		if (file.exists()) {
			file.delete();
		}
	}

}
