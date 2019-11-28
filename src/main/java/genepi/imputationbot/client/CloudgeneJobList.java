package genepi.imputationbot.client;

import java.text.SimpleDateFormat;
import java.util.Vector;

import genepi.imputationbot.util.FlipTable;

public class CloudgeneJobList extends Vector<CloudgeneJob> {

	private static final long serialVersionUID = 1L;

	public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private int limit = 10;

	public void setLimit(int limit) {
		this.limit = limit;
	}

	@Override
	public synchronized String toString() {

		if (size() == 0) {
			return "No jobs found.\n";
		}

		String[] header = new String[6];
		header[0] = "";
		header[1] = "Queue";
		header[1] = "Status";
		header[2] = "Job-Name";
		header[3] = "Job-ID";
		header[4] = "Submitted On";
		header[5] = "Execution Time";

		int length = limit;
		if (length == -1 || size() < length) {
			length = size();
		}

		String[][] data = new String[length][header.length];

		for (int i = 0; i < length; i++) {
			CloudgeneJob job = get(i);
			data[i][0] = "#" + (size() - i);
			data[i][1] = job.getQueuePosition() + "";
			data[i][1] = job.getJobStateAsText();
			data[i][2] = job.getName();
			data[i][3] = job.getId();
			data[i][4] = DATE_FORMAT.format(job.getSubmittedOn());
			data[i][5] = job.getExecutionTime() + " sec";
		}

		String content = FlipTable.of(header, data) + "\n";
		content += "Showing " + 1 + " to " + length + " of " + size() + " jobs.\n\n\n";
		return content;
	}

}
