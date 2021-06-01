package com.omniscien.lsmetric.model;

public class ResultPerLineOnlyModel {

	public ResultPerLineOnlyModel() {
		// TODO Auto-generated constructor stub
	}

	private String metric = "";
	private int sourcelines = 0;
	private int targetlines = 0;
	private SummaryModel summary = new SummaryModel();
	private DetailNotIncludeTextModel detail = new DetailNotIncludeTextModel();
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
	public DetailNotIncludeTextModel getDetail() {
		return detail;
	}
	public void setDetail(DetailNotIncludeTextModel detail) {
		this.detail = detail;
	}
	
	
}
