package genepi.imputationbot.model;

import java.util.HashMap;

public class ProjectJob {

	private String job;
	
	private HashMap<String, String> params;

	public String getJob() {
		return job;
	}
	
	public void setJob(String job) {
		this.job = job;
	}
	
	public HashMap<String, String> getParams() {
		return params;
	}

	public void setParams(HashMap<String, String> params) {
		this.params = params;
	}
	
}
