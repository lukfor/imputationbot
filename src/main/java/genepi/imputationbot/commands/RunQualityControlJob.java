package genepi.imputationbot.commands;

public class RunQualityControlJob extends AbstractRunJob {

	public RunQualityControlJob(String[] args) {
		super(args, AbstractRunJob.QC_JOB);
	}

}
