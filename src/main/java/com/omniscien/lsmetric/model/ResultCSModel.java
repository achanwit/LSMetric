package com.omniscien.lsmetric.model;

import java.util.ArrayList;
import java.util.List;

public class ResultCSModel {

	public ResultCSModel() {
		// TODO Auto-generated constructor stub
	}
	private String metric;
	private int sourcelines;
	private int targetlines;
	
	private SummaryCSModel summary = new SummaryCSModel();
	
	private List<DetailIncludeTextCSModel> detail = new ArrayList<DetailIncludeTextCSModel>();

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

	public List<DetailIncludeTextCSModel> getDetail() {
		return detail;
	}

	public void setDetail(List<DetailIncludeTextCSModel> detail) {
		this.detail = detail;
	}


	
	

}
