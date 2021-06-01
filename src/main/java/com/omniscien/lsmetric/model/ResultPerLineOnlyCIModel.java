package com.omniscien.lsmetric.model;

public class ResultPerLineOnlyCIModel {

	public ResultPerLineOnlyCIModel() {
		// TODO Auto-generated constructor stub
	}

	private String metric = "";
	private int sourcelines = 0;
	private int targetlines = 0;
	private SummaryCIModel summary = new SummaryCIModel();
	private DetailNotIncludeTextCIModel detail = new DetailNotIncludeTextCIModel();
	public String getMetric() {
		return metric;
	}
	public void setMetric(String metric) {
		this.metric = metric;
	}
	public int getSourcelines() {
		return sourcelines;
	}
	public void setSourcelines(int sourcelines) {
		this.sourcelines = sourcelines;
	}
	public int getTargetlines() {
		return targetlines;
	}
	public void setTargetlines(int targetlines) {
		this.targetlines = targetlines;
	}

	
	public SummaryCIModel getSummary() {
		return summary;
	}
	public void setSummary(SummaryCIModel summary) {
		this.summary = summary;
	}
	public DetailNotIncludeTextCIModel getDetail() {
		return detail;
	}
	public void setDetail(DetailNotIncludeTextCIModel detail) {
		this.detail = detail;
	}

	
	
}
