package com.omniscien.lsmetric.model;

public class ScoreSummaryOnlyCSModel {

	public ScoreSummaryOnlyCSModel() {
		// TODO Auto-generated constructor stub
	}
	
	

	
	private ResultSummaryCSModel result = new ResultSummaryCSModel();
	private long duration;
	private String startdate;
	private String errortext;
	private int errorno;
	private String enddate;
	private String requestid = "";
	
	
	

	

	public ResultSummaryCSModel getResult() {
		return result;
	}
	public void setResult(ResultSummaryCSModel result) {
		this.result = result;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public String getStartdate() {
		return startdate;
	}
	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}
	public String getErrortext() {
		return errortext;
	}
	public void setErrortext(String errortext) {
		this.errortext = errortext;
	}
	public int getErrorno() {
		return errorno;
	}
	public void setErrorno(int errorno) {
		this.errorno = errorno;
	}
	public String getEnddate() {
		return enddate;
	}
	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}
	public String getRequestid() {
		return requestid;
	}
	public void setRequestid(String requestid) {
		this.requestid = requestid;
	}
	
	

}
