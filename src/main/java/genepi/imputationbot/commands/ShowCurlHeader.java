package genepi.imputationbot.commands;

import genepi.imputationbot.client.CloudgeneClientConfig;

public class ShowCurlHeader extends BaseCommand {

	public ShowCurlHeader(String[] args) {
		super(args, false);
	}

	@Override
	public void createParameters() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init() {

	}

	@Override
	public int runAndHandleErrors() throws Exception {

		CloudgeneClientConfig config = readConfig();
		System.out.print("-H \"X-Auth-Token: " + config.getToken() +  "\" ");

		return 0;

	}

}
