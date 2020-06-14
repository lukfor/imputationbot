package genepi.imputationbot.commands;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;

import genepi.imputationbot.client.CloudgeneInstance;
import genepi.imputationbot.util.FlipTable;

public class ListRefPanels extends BaseCommand {

	public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private String[][] data;

	public ListRefPanels(String... args) {
		super(args);
	}

	@Override
	public void createParameters() {
	}

	@Override
	public void init() {

	}

	@Override
	public int runAndHandleErrors() throws Exception {

		String[] header = new String[4];
		header[0] = "ID";
		header[1] = "Name";
		header[2] = "Populations";
		header[3] = "Instance";

		data = new String[0][4];

		for (CloudgeneInstance instance : getInstanceList().getAll()) {
			String[][] dataRefPanel = instance.getReferencePanelsWithDetails();
			data = concatenate(data, dataRefPanel);
		}

		String table = FlipTable.of(header, data) + "\n";
		System.out.println(table);

		println();

		return 0;
	}

	public <T> T[] concatenate(T[] a, T[] b) {
		int aLen = a.length;
		int bLen = b.length;

		@SuppressWarnings("unchecked")
		T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);

		return c;
	}

	public String[][] getData() {
		return data;
	}

}
