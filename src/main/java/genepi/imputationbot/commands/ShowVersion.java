package genepi.imputationbot.commands;

import genepi.imputationbot.util.OperatingSystem;

public class ShowVersion extends BaseCommand {

	public ShowVersion(String[] args) {
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

		System.out.println("Operating System: " + OperatingSystem.NAME);
		
		return 0;

	}

}
