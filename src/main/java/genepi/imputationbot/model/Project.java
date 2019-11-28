package genepi.imputationbot.model;

import java.util.Date;
import java.util.List;
import java.util.Vector;

public class Project {

	private String name;
	
	private List<ProjectJob> jobs = new Vector<ProjectJob>();

	private Date created = new Date();

	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setJobs(List<ProjectJob> jobs) {
		this.jobs = jobs;
	}

	public List<ProjectJob> getJobs() {
		return jobs;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getCreated() {
		return created;
	}
	
	public boolean containsJob(String id) {
		for (ProjectJob job: jobs) {
			if (job.getJob().equals(id)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
