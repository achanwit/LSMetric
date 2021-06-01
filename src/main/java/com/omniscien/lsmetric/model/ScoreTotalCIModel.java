package com.omniscien.lsmetric.model;

public class ScoreTotalCIModel {

	public ScoreTotalCIModel() {
		// TODO Auto-generated constructor stub
	}
	
	private ResultCIModel result = new ResultCIModel();
	private long duration;
	private String startdate;
	private String errortext;
	private int errorno;
	private String enddate;
	private String requestid = "";

	
	public ResultCIModel getResult() {
		return result;
	}
	public void setResult(ResultCIModel result) {
		this.result = result;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long totaltime) {
		this.duration = totaltime;
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
