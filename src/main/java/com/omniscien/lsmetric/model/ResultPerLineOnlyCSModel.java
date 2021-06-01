package com.omniscien.lsmetric.model;

public class ResultPerLineOnlyCSModel {

	public ResultPerLineOnlyCSModel() {
		// TODO Auto-generated constructor stub
	}

	private String metric = "";
	private int sourcelines = 0;
	private int targetlines = 0;
	private SummaryCSModel summary = new SummaryCSModel();
	private DetailNotIncludeTextCSModel detail = new DetailNotIncludeTextCSModel();
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

	
	public SummaryCSModel getSummary() {
		return summary;
	}
	public void setSummary(SummaryCSModel summary) {
		this.summary = summary;
	}
	public DetailNotIncludeTextCSModel getDetail() {
		return detail;
	}
	public void setDetail(DetailNotIncludeTextCSModel detail) {
		this.detail = detail;
	}

	
	
	
}
