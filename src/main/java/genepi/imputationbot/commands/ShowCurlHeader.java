package genepi.imputationbot.commands;

import genepi.imputationbot.client.CloudgeneClientConfig;

public class ShowCurlHeader extends BaseCommand {

	public ShowCurlHeader(String[] args) {
		super(args, false);
	}

	@Override
	public void createParameters() {

	}

	@Override
	public void init() {

	}

	@Override
	public int runAndHandleErrors() throws Exception {

		CloudgeneClientConfig config = getConfig();
		System.out.print("-H \"X-Auth-Token: " + config.getToken() + "\" ");

		return 0;

	}

}
