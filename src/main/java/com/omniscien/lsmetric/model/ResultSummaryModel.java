package com.omniscien.lsmetric.model;

public class ResultSummaryModel {

	public ResultSummaryModel() {
		// TODO Auto-generated constructor stub
	}
	
	private String metric;
	private int sourcelines;
	private int targetlines;
	private SummaryModel summary = new SummaryModel();
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
	public SummaryModel getSummary() {
		return summary;
	}
	public void setSummary(SummaryModel summary) {
		this.summary = summary;
	}
	
	

}
