package com.omniscien.lsmetric.model;

import java.util.ArrayList;
import java.util.List;

public class ResultCIModel {

	public ResultCIModel() {
		// TODO Auto-generated constructor stub
	}
	private String metric;
	private int sourcelines;
	private int targetlines;
	
	private SummaryCIModel summary = new SummaryCIModel();
	
	private List<DetailIncludeTextCIModel> detail = new ArrayList<DetailIncludeTextCIModel>();

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

	public List<DetailIncludeTextCIModel> getDetail() {
		return detail;
	}

	public void setDetail(List<DetailIncludeTextCIModel> detail) {
		this.detail = detail;
	}

	
	
	
	

}
