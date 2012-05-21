package configurationslicing.parameters;

import hudson.model.Job;

public class JobParameter {

	@SuppressWarnings("unchecked")
	private Job job;
	private String parameterName;
	private String parameterValue;
	
	@SuppressWarnings("unchecked")
	public JobParameter(Job job, String parameterName, String parameterValue) {
		this.job = job;
		this.parameterName = parameterName;
		this.parameterValue = parameterValue;
	}

	@SuppressWarnings("unchecked")
	public Job getJob() {
		return job;
	}
	public String getParameterName() {
		return parameterName;
	}
	public String getParameterValue() {
		return parameterValue;
	}
	
}
