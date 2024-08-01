package genepi.imputationbutler.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import genepi.imputationbot.commands.AddInstance;
import genepi.imputationbot.commands.BaseCommand;
import genepi.imputationbot.commands.ListRefPanels;
import genepi.imputationbutler.ImputationServer;
import genepi.io.FileUtil;

public class ListRefPanelsTest {

	@Test
	public void testListRefPanels() {

		deleteInstances();

		String hostname = ImputationServer.getInstance().getUrl();
		String token = ImputationServer.getInstance().getAdminToken();

		AddInstance addInstance = new AddInstance(hostname, token);
		assertEquals(0, addInstance.start());	
		
		ListRefPanels listRefPanels = new ListRefPanels();
		
		assertEquals(0, listRefPanels.start());
		assertEquals(1, listRefPanels.getData().length);
		assertEquals(4, listRefPanels.getData()[0].length);
		
		assertEquals("hapmap-2", listRefPanels.getData()[0][0]);
		assertEquals("HapMap 2 (GRCh37/hg19)", listRefPanels.getData()[0][1]);
		assertTrue(listRefPanels.getData()[0][2].contains("eur"));
		assertTrue(listRefPanels.getData()[0][2].contains("off"));
		assertFalse(listRefPanels.getData()[0][2].contains("afr"));
		assertFalse(listRefPanels.getData()[0][2].contains("amr"));
		assertFalse(listRefPanels.getData()[0][2].contains("sas"));
		assertEquals("Cloudgene", listRefPanels.getData()[0][3]);
		
	}

	private void deleteInstances() {
		// delete instances files
		File file = new File(FileUtil.path(BaseCommand.APP_HOME, BaseCommand.INSTANCES_FILENAME));
		if (file.exists()) {
			file.delete();
		}
	}

}
