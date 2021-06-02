package com.omniscien.lsmetric.process;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.omniscien.lsmetric.model.DetailIncludeTextCIModel;
import com.omniscien.lsmetric.model.DetailIncludeTextCSModel;
import com.omniscien.lsmetric.model.DetailIncludeTextModel;
import com.omniscien.lsmetric.model.DetailNotIncludeTextCIModel;
import com.omniscien.lsmetric.model.DetailNotIncludeTextCSModel;
import com.omniscien.lsmetric.model.DetailNotIncludeTextModel;
import com.omniscien.lsmetric.model.ResultCIModel;
import com.omniscien.lsmetric.model.ResultCSModel;
import com.omniscien.lsmetric.model.ResultModel;
import com.omniscien.lsmetric.model.ResultPerLineOnlyCIModel;
import com.omniscien.lsmetric.model.ResultPerLineOnlyCSModel;
import com.omniscien.lsmetric.model.ResultPerLineOnlyModel;
import com.omniscien.lsmetric.model.ResultSummaryCIModel;
import com.omniscien.lsmetric.model.ResultSummaryCSModel;
import com.omniscien.lsmetric.model.ResultSummaryModel;
import com.omniscien.lsmetric.model.ScorePerLineCIModel;
import com.omniscien.lsmetric.model.ScorePerLineCSModel;
import com.omniscien.lsmetric.model.ScorePerLineModel;
import com.omniscien.lsmetric.model.ScoreResponseOnlyModel;
import com.omniscien.lsmetric.model.ScoreSummaryOnlyCIModel;
import com.omniscien.lsmetric.model.ScoreSummaryOnlyCSModel;
import com.omniscien.lsmetric.model.ScoreSummaryOnlyModel;
import com.omniscien.lsmetric.model.ScoreTotalCIModel;
import com.omniscien.lsmetric.model.ScoreTotalCSModel;
import com.omniscien.lsmetric.model.ScoreTotalModel;
import com.omniscien.lsmetric.model.SummaryCIModel;
import com.omniscien.lsmetric.model.SummaryCSModel;
import com.omniscien.lsmetric.model.SummaryModel;
import com.omniscien.lsmetric.util.Constant;
import com.omniscien.lsmetric.util.ReadProp;
import com.omniscien.lsmetric.util.StreamGobblerWithOutput;

public class MultiBleuScorer {
	
	private String sBlankLine = "LSBLANKLINELS";
	private long begintime = 0;
	private long endtime = 0;
	private long totaltime =0;
	private String startTimeStr = "";
	private String endTimeStr = "";
	
	//Model
	private ResultModel resultTotalModel;
	private ResultSummaryModel resultSummaryModel;
	private ResultPerLineOnlyModel resultPerLineOnlyModel;
	private DetailIncludeTextModel detail;
	private DetailNotIncludeTextModel detailNotIncludeTextModel;
	private SummaryModel summary;
	private SummaryCSModel summaryCS;
	private SummaryCIModel summaryCI;
	private ScoreTotalModel scoreTotal;
	private ScorePerLineModel scorePerLine;
	private ScoreResponseOnlyModel scoreResponseOnly;
	private ScoreSummaryOnlyModel scoreSummaryOnlyModel;
	
	private ScoreSummaryOnlyCSModel scoreSummaryOnlyCSModel;
	private ResultSummaryCSModel resultSummaryCSModel;
	
	
	private ScoreSummaryOnlyCIModel scoreSummaryOnlyCIModel;
	private ResultSummaryCIModel resultSummaryCIModel;
	
	private ScorePerLineCSModel scorePerLineCSModel;
	private ResultPerLineOnlyCSModel resultPerLineOnlyCSModel;
	private DetailNotIncludeTextCSModel detailNotIncludeTextCSModel;
	
	private ScorePerLineCIModel scorePerLineCIModel;
	private ResultPerLineOnlyCIModel resultPerLineOnlyCIModel;
	private DetailNotIncludeTextCIModel detailNotIncludeTextCIModel;
	
	private ScoreTotalCSModel scoreTotalCSModel;
	private ResultCSModel resultCSModel;
	
	private ScoreTotalCIModel scoreTotalCIModel;
	private ResultCIModel resultCIModel;
	
	private DetailIncludeTextCSModel detailIncludeTextCSModel;
	private DetailIncludeTextCIModel detailIncludeTextCIModel;
	
	private ReadProp rp = null;

	public MultiBleuScorer() {
		//Properties
		if(rp == null) {
			rp = new ReadProp("/omniscien/tools/wfs/metrics/metricConfig.properties");
		}
	}
	
	public String getScoreFromTextFile(int CaseSensitive, String InputReference, String InputCandidate, int OutputFormat, long begintime, String startTimeStr) throws Exception{
		String jsonResult = null;
		
		
		if (OutputFormat == 2 || OutputFormat == 3 || OutputFormat == 4) {
			if (OutputFormat == 2) {
				if (CaseSensitive == 1) {
					scoreSummaryOnlyModel = new ScoreSummaryOnlyModel();
					resultSummaryModel = new ResultSummaryModel();
					scoreSummaryOnlyModel.setStartdate(startTimeStr);
					resultSummaryModel.setMetric(Constant.METRIC_BLEU);
				}else if(CaseSensitive == 2) {
					scoreSummaryOnlyCSModel = new ScoreSummaryOnlyCSModel();
					resultSummaryCSModel = new ResultSummaryCSModel();
					scoreSummaryOnlyCSModel.setStartdate(startTimeStr);
					resultSummaryCSModel.setMetric(Constant.METRIC_BLEU);
				}else if(CaseSensitive == 3) {
					scoreSummaryOnlyCIModel = new ScoreSummaryOnlyCIModel();
					resultSummaryCIModel = new ResultSummaryCIModel();
					scoreSummaryOnlyCIModel.setStartdate(startTimeStr);
					resultSummaryCIModel.setMetric(Constant.METRIC_BLEU);
				}

			} else if (OutputFormat == 3) {
				if (CaseSensitive == 1) {
					scorePerLine = new ScorePerLineModel();
					resultPerLineOnlyModel = new ResultPerLineOnlyModel();
					detailNotIncludeTextModel = new DetailNotIncludeTextModel();
					resultPerLineOnlyModel.setMetric(Constant.METRIC_BLEU);
					scorePerLine.setStartdate(startTimeStr);
				}else if(CaseSensitive == 2) {
					scorePerLineCSModel = new ScorePerLineCSModel();
					resultPerLineOnlyCSModel = new ResultPerLineOnlyCSModel();
					detailNotIncludeTextCSModel = new DetailNotIncludeTextCSModel();
					resultPerLineOnlyCSModel.setMetric(Constant.METRIC_BLEU);
					scorePerLineCSModel.setStartdate(startTimeStr);
				}else if(CaseSensitive == 3) {
					scorePerLineCIModel = new ScorePerLineCIModel();
					resultPerLineOnlyCIModel = new ResultPerLineOnlyCIModel();
					detailNotIncludeTextCIModel = new DetailNotIncludeTextCIModel();
					resultPerLineOnlyCIModel.setMetric(Constant.METRIC_BLEU);
					scorePerLineCIModel.setStartdate(startTimeStr);
				}
			} else if (OutputFormat == 4) {
				if (CaseSensitive == 1) {
					scoreTotal = new ScoreTotalModel();
					resultTotalModel = new ResultModel();
					scoreTotal.setStartdate(startTimeStr);
					resultTotalModel.setMetric(Constant.METRIC_BLEU);
					
				}else if(CaseSensitive == 2) {
					scoreTotalCSModel = new ScoreTotalCSModel();
					resultCSModel = new ResultCSModel();
					scoreTotalCSModel.setStartdate(startTimeStr);
					resultCSModel.setMetric(Constant.METRIC_BLEU);
				}
				else if(CaseSensitive == 3) {
					scoreTotalCIModel = new ScoreTotalCIModel();
					resultCIModel = new ResultCIModel();
					scoreTotalCIModel.setStartdate(startTimeStr);
					resultCIModel.setMetric(Constant.METRIC_BLEU);
				}
			}
			
			//Set Summary Model
			if (CaseSensitive == 1) {
				summary = new SummaryModel();
			}else if(CaseSensitive == 2) {
				summaryCS = new SummaryCSModel();
			}else if(CaseSensitive == 3) {
				summaryCI = new SummaryCIModel();
			}
		}
		
		
		
		
		
		
		
		//Set Start Time
		this.begintime = begintime;
		this.startTimeStr =  startTimeStr;		
		
		List<String> InputReferenceList = new ArrayList<String>();
		List<String> InputCandidateList = new ArrayList<String>();
		
		InputReferenceList = getListStringFromFile(InputReference);
		InputCandidateList = getListStringFromFile(InputCandidate);
		try {
			
			
			
			//Validate line number 
			if(InputReferenceList.size() != InputCandidateList.size()) {
				
				scoreResponseOnly = new ScoreResponseOnlyModel();
				scoreResponseOnly.setStartdate(startTimeStr);
				com.omniscien.lsmetric.model.ResultRespondOnlyModel resultRespondOnly = new com.omniscien.lsmetric.model.ResultRespondOnlyModel();
				resultRespondOnly.metric = Constant.METRIC_BLEU;
				resultRespondOnly.sourcelines = InputReferenceList.size();
				resultRespondOnly.targetlines = InputCandidateList.size();
				scoreResponseOnly.setResult(resultRespondOnly);
				
				//10436 : The Number of line between input data and output data does not match.
				int ErrorNo = 10436;
				scoreResponseOnly.setErrorno(ErrorNo);
				
				String ErrorMSG = com.omniscien.lib.util.Message.getMessage(ErrorNo);
				scoreResponseOnly.setErrortext(ErrorMSG);
				
				endtime = System.currentTimeMillis();
				totaltime = endtime-begintime;
				endTimeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss:SSS")).toString();
				
				scoreResponseOnly.setEnddate(endTimeStr);
				scoreResponseOnly.setDuration(totaltime);
				
				return getResponeOnlyJSONOutput(scoreResponseOnly);
			}else {
				if(OutputFormat ==2) {
					if (CaseSensitive == 1) {
						resultSummaryModel.setSourcelines(InputReferenceList.size());
						resultSummaryModel.setTargetlines(InputCandidateList.size());
					}else if(CaseSensitive == 2) {
						resultSummaryCSModel.setSourcelines(InputReferenceList.size());
						resultSummaryCSModel.setTargetlines(InputCandidateList.size());
					}else if(CaseSensitive == 3) {
						resultSummaryCIModel.setSourcelines(InputReferenceList.size());
						resultSummaryCIModel.setTargetlines(InputCandidateList.size());
					}
				}else if (OutputFormat == 3) {
					if (CaseSensitive == 1) {
						resultPerLineOnlyModel.setSourcelines(InputReferenceList.size());
						resultPerLineOnlyModel.setTargetlines(InputReferenceList.size());
					}else if(CaseSensitive == 2) {
						resultPerLineOnlyCSModel.setSourcelines(InputReferenceList.size());
						resultPerLineOnlyCSModel.setTargetlines(InputReferenceList.size());
					}else if(CaseSensitive == 3) {
						resultPerLineOnlyCIModel.setSourcelines(InputReferenceList.size());
						resultPerLineOnlyCIModel.setTargetlines(InputReferenceList.size());
					}
				
				}else if(OutputFormat == 4) {
					if (CaseSensitive == 1) {
						resultTotalModel.setSourcelines(InputReferenceList.size());
						resultTotalModel.setTargetlines(InputReferenceList.size());
					}else if(CaseSensitive == 2) {
						resultCSModel.setSourcelines(InputReferenceList.size());
						resultCSModel.setTargetlines(InputReferenceList.size());
					}else if(CaseSensitive == 3) {
						resultCIModel.setSourcelines(InputReferenceList.size());
						resultCIModel.setTargetlines(InputReferenceList.size());
					}
					
				}
				
			}
			
			if(OutputFormat == 1) {
				scoreResponseOnly = new ScoreResponseOnlyModel();
				com.omniscien.lsmetric.model.ResultRespondOnlyModel resultRespondOnly = new com.omniscien.lsmetric.model.ResultRespondOnlyModel();
				resultRespondOnly.metric = Constant.METRIC_BLEU;
				resultRespondOnly.sourcelines = InputReferenceList.size();
				resultRespondOnly.targetlines = InputCandidateList.size();
				scoreResponseOnly.setStartdate(startTimeStr);
				scoreResponseOnly.setResult(resultRespondOnly);
				scoreResponseOnly.setErrorno(0);
				scoreResponseOnly.setErrortext("");
				
				endtime = System.currentTimeMillis();
				totaltime = endtime-begintime;
				endTimeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss:SSS")).toString();
				
				scoreResponseOnly.setEnddate(endTimeStr);
				scoreResponseOnly.setDuration(totaltime);
				
				return getResponeOnlyJSONOutput(scoreResponseOnly);
				
			}
			
			//Process Score
	
				if (OutputFormat == 2) {
					if (CaseSensitive == 1) {
						double scoreSummaryCS = getScoreSummary(InputReference, InputCandidate, "");
						double scoreSummaryCI = getScoreSummary(InputReference, InputCandidate, "-lc");
						summary.setCs(scoreSummaryCS);
						summary.setCi(scoreSummaryCI);
						resultSummaryModel.setSummary(summary);
						scoreSummaryOnlyModel.setResult(resultSummaryModel);
						jsonResult = getJSONScoreSummary(scoreSummaryOnlyModel);
					}else if(CaseSensitive == 2) {
						double scoreSummaryCS = getScoreSummary(InputReference, InputCandidate, "");
						summaryCS.setCs(scoreSummaryCS);
						resultSummaryCSModel.setSummary(summaryCS);
						scoreSummaryOnlyCSModel.setResult(resultSummaryCSModel);
						jsonResult = getJSONScoreSummaryCS(scoreSummaryOnlyCSModel);
					}else if(CaseSensitive == 3) {
						double scoreSummaryCI = getScoreSummary(InputReference, InputCandidate, "-lc");
						summaryCI.setCi(scoreSummaryCI);
						resultSummaryCIModel.setSummary(summaryCI);
						scoreSummaryOnlyCIModel.setResult(resultSummaryCIModel);
						jsonResult = getJSONScoreSummaryCI(scoreSummaryOnlyCIModel);
					}
					
				}else if(OutputFormat == 3) {
					if (CaseSensitive == 1) {
						double scoreSummaryCS = getScoreSummary(InputReference, InputCandidate, "");
						double scoreSummaryCI = getScoreSummary(InputReference, InputCandidate, "-lc");
						summary.setCs(scoreSummaryCS);
						summary.setCi(scoreSummaryCI);
						resultPerLineOnlyModel.setSummary(summary);
						List<Double> cs = new ArrayList<Double>();
						List<Double> ci = new ArrayList<Double>();
						if(InputReferenceList.size() > 1) {
							for (int i = 0; i < InputReferenceList.size(); i++) {
								double scoreCI = getScorePerline(InputReferenceList.get(i).toLowerCase(),
										InputCandidateList.get(i).toLowerCase());
								double scoreCS = getScorePerline(InputReferenceList.get(i), InputCandidateList.get(i));

								scoreCI = scoreCI * 100;
								DecimalFormat df = new DecimalFormat("#.##");
								String scoreCIStr = df.format(scoreCI);

								scoreCS = scoreCS * 100;
								String scoreCSStr = df.format(scoreCS);

								cs.add(Double.valueOf(scoreCSStr));
								ci.add(Double.valueOf(scoreCIStr));
							}
						}else {
							cs.add(scoreSummaryCS);
							ci.add(scoreSummaryCI);
						}

						detailNotIncludeTextModel.setCi(ci);
						detailNotIncludeTextModel.setCs(cs);
						resultPerLineOnlyModel.setDetail(detailNotIncludeTextModel);
						scorePerLine.setResult(resultPerLineOnlyModel);

						jsonResult = getJSONScorePerLine(scorePerLine);
					}else if(CaseSensitive == 2) {
						double scoreSummaryCS = getScoreSummary(InputReference, InputCandidate, "");
						summaryCS.setCs(scoreSummaryCS);
						resultPerLineOnlyCSModel.setSummary(summaryCS);
						List<Double> cs = new ArrayList<Double>();
						DecimalFormat df = new DecimalFormat("#.##");
						if(InputReferenceList.size() > 1) {
							for (int i = 0; i < InputReferenceList.size(); i++) {
								double scoreCS = getScorePerline(InputReferenceList.get(i), InputCandidateList.get(i));
								scoreCS = scoreCS * 100;
								String scoreCSStr = df.format(scoreCS);
								cs.add(Double.valueOf(scoreCSStr));
							}
						}else {
							cs.add(scoreSummaryCS);
						}
						detailNotIncludeTextCSModel.setCs(cs);
						resultPerLineOnlyCSModel.setDetail(detailNotIncludeTextCSModel);
						scorePerLineCSModel.setResult(resultPerLineOnlyCSModel);
						jsonResult = getJSONScorePerLineCS(scorePerLineCSModel);
					}else if(CaseSensitive == 3) {
						double scoreSummaryCI = getScoreSummary(InputReference, InputCandidate, "-lc");
						summaryCI.setCi(scoreSummaryCI);
						resultPerLineOnlyCIModel.setSummary(summaryCI);
						List<Double> ci = new ArrayList<Double>();
						DecimalFormat df = new DecimalFormat("#.##");
						if (InputReferenceList.size() > 1) {
							for (int i = 0; i < InputReferenceList.size(); i++) {
								double scoreCI = getScorePerline(InputReferenceList.get(i).toLowerCase(),
										InputCandidateList.get(i).toLowerCase());
								scoreCI = scoreCI * 100;
								String scoreCIStr = df.format(scoreCI);
								ci.add(Double.valueOf(scoreCIStr));
							}
						}else {
							ci.add(scoreSummaryCI);
						}
						detailNotIncludeTextCIModel.setCi(ci);
						resultPerLineOnlyCIModel.setDetail(detailNotIncludeTextCIModel);
						scorePerLineCIModel.setResult(resultPerLineOnlyCIModel);
						jsonResult = getJSONScorePerLineCI(scorePerLineCIModel);
					}
				}else 
				if (OutputFormat == 4) {
					List<DetailIncludeTextModel> listDetail = new ArrayList<DetailIncludeTextModel>();
					List<DetailIncludeTextCSModel> listCSDetail = new ArrayList<DetailIncludeTextCSModel>();
					List<DetailIncludeTextCIModel> listCIDetail = new ArrayList<DetailIncludeTextCIModel>();
					if (CaseSensitive == 1) {
						
						double scoreSummaryCI = getScoreSummary(InputReference, InputCandidate, "-lc");
						double scoreSummaryCS = getScoreSummary(InputReference, InputCandidate, "");
						for (int i = 0; i < InputReferenceList.size(); i++) {
							detail = new DetailIncludeTextModel();
							detail.setCan(InputCandidateList.get(i));
							detail.setRef(InputReferenceList.get(i));
							double scoreCI = 0;
							double scoreCS = 0;
							String scoreCIStr = "";
							String scoreCSStr = "";
							
							
							
							if (InputReferenceList.size() > 1) {
								scoreCI = getScorePerline(InputReferenceList.get(i).toLowerCase(),
										InputCandidateList.get(i).toLowerCase());
								scoreCS = getScorePerline(InputReferenceList.get(i), InputCandidateList.get(i));
								scoreCI = scoreCI * 100;
								DecimalFormat df = new DecimalFormat("#.##");
								 scoreCIStr = df.format(scoreCI);

								scoreCS = scoreCS * 100;
								 scoreCSStr = df.format(scoreCS);
								 
								 detail.setCi(Double.valueOf(scoreCIStr));
									detail.setCs(Double.valueOf(scoreCSStr));
							}else {
								detail.setCi(scoreSummaryCI);
								detail.setCs(scoreSummaryCS);
							}
							

							
							listDetail.add(detail);
						}
						resultTotalModel.setDetail(listDetail);
						

						summary.setCi(scoreSummaryCI);
						summary.setCs(scoreSummaryCS);
						resultTotalModel.setSummary(summary);
						scoreTotal.setResult(resultTotalModel);

						jsonResult = getJSONOutput(scoreTotal);
					}else if (CaseSensitive == 2) {
						
						double scoreSummaryCS = getScoreSummary(InputReference, InputCandidate, "");
						for (int i = 0; i < InputReferenceList.size(); i++) {
							detailIncludeTextCSModel = new DetailIncludeTextCSModel();
							detailIncludeTextCSModel.setCan(InputCandidateList.get(i));
							detailIncludeTextCSModel.setRef(InputReferenceList.get(i));
							if (InputReferenceList.size() > 1) {
								double scoreCS = getScorePerline(InputReferenceList.get(i), InputCandidateList.get(i));

								DecimalFormat df = new DecimalFormat("#.##");

								scoreCS = scoreCS * 100;
								String scoreCSStr = df.format(scoreCS);
								detailIncludeTextCSModel.setCs(Double.valueOf(scoreCSStr));
							}else {
								detailIncludeTextCSModel.setCs(scoreSummaryCS);
							}
							listCSDetail.add(detailIncludeTextCSModel);
						}
						resultCSModel.setDetail(listCSDetail);
						
						
						summaryCS.setCs(scoreSummaryCS);
						resultCSModel.setSummary(summaryCS);
						scoreTotalCSModel.setResult(resultCSModel);
						jsonResult = getJSONOutputCS(scoreTotalCSModel);
					}else if (CaseSensitive == 3) {
						double scoreSummaryCI = getScoreSummary(InputReference, InputCandidate, "-lc");
						for (int i = 0; i < InputReferenceList.size(); i++) {
							detailIncludeTextCIModel = new DetailIncludeTextCIModel();
							detailIncludeTextCIModel.setCan(InputCandidateList.get(i));
							detailIncludeTextCIModel.setRef(InputReferenceList.get(i));
							if (InputReferenceList.size() > 1) {
								double scoreCI = getScorePerline(InputReferenceList.get(i).toLowerCase(),
										InputCandidateList.get(i).toLowerCase());

								DecimalFormat df = new DecimalFormat("#.##");

								scoreCI = scoreCI * 100;
								String scoreCIStr = df.format(scoreCI);
								detailIncludeTextCIModel.setCi(Double.valueOf(scoreCIStr));
							}else {
								detailIncludeTextCIModel.setCi(scoreSummaryCI);
							}
							listCIDetail.add(detailIncludeTextCIModel);
						}
						resultCIModel.setDetail(listCIDetail);
						
						
						summaryCI.setCi(scoreSummaryCI);
						resultCIModel.setSummary(summaryCI);
						scoreTotalCIModel.setResult(resultCIModel);
						
						jsonResult = getJSONOutputCI(scoreTotalCIModel);
					}
				}else if(OutputFormat == 5){
					double scoreSummaryCS = getScoreSummary(InputReference, InputCandidate, "");
					double scoreSummaryCI = getScoreSummary(InputReference, InputCandidate, "-lc");
					
					List<Double> cs = new ArrayList<Double>();
					List<Double> ci = new ArrayList<Double>();
					if(InputReferenceList.size() > 1) {
						for (int i = 0; i < InputReferenceList.size(); i++) {
							double scoreCI = getScorePerline(InputReferenceList.get(i).toLowerCase(),
									InputCandidateList.get(i).toLowerCase());
							double scoreCS = getScorePerline(InputReferenceList.get(i), InputCandidateList.get(i));

							scoreCI = scoreCI * 100;
							DecimalFormat df = new DecimalFormat("#.##");
							String scoreCIStr = df.format(scoreCI);

							scoreCS = scoreCS * 100;
							String scoreCSStr = df.format(scoreCS);

							cs.add(Double.valueOf(scoreCSStr));
							ci.add(Double.valueOf(scoreCIStr));
						}
					}else {
						cs.add(scoreSummaryCS);
						ci.add(scoreSummaryCI);
					}
					
					StringBuffer resultBuf = new StringBuffer();
					for(int i = 0; i<cs.size(); i++) {
						if (i == (cs.size() - 1)) {
							resultBuf = resultBuf.append(cs.get(i)).append("\t").append(ci.get(i));
						}else {
							resultBuf = resultBuf.append(cs.get(i)).append("\t").append(ci.get(i)).append("\n");
						}
					}
					jsonResult = resultBuf.toString();
				}else if(OutputFormat == 6) {
					double scoreSummaryCS = getScoreSummary(InputReference, InputCandidate, "");
					double scoreSummaryCI = getScoreSummary(InputReference, InputCandidate, "-lc");
					
					List<Double> cs = new ArrayList<Double>();
					List<Double> ci = new ArrayList<Double>();
					if(InputReferenceList.size() > 1) {
						for (int i = 0; i < InputReferenceList.size(); i++) {
							double scoreCI = getScorePerline(InputReferenceList.get(i).toLowerCase(),
									InputCandidateList.get(i).toLowerCase());
							double scoreCS = getScorePerline(InputReferenceList.get(i), InputCandidateList.get(i));

							scoreCI = scoreCI * 100;
							DecimalFormat df = new DecimalFormat("#.##");
							String scoreCIStr = df.format(scoreCI);

							scoreCS = scoreCS * 100;
							String scoreCSStr = df.format(scoreCS);

							cs.add(Double.valueOf(scoreCSStr));
							ci.add(Double.valueOf(scoreCIStr));
						}
					}else {
						cs.add(scoreSummaryCS);
						ci.add(scoreSummaryCI);
					}
//					InputReferenceList = getListStringFromFile(InputReference);
//					InputCandidateList = getListStringFromFile(InputCandidate);
					StringBuffer resultBuf = new StringBuffer();
					for(int i = 0; i<cs.size(); i++) {
						if (i == (cs.size() - 1)) {
							resultBuf = resultBuf.append(InputReferenceList.get(i)).append("\t").append(InputCandidateList.get(i)).append("\t").append(cs.get(i)).append("\t").append(ci.get(i));
						}else {
							resultBuf = resultBuf.append(InputReferenceList.get(i)).append("\t").append(InputCandidateList.get(i)).append("\t").append(cs.get(i)).append("\t").append(ci.get(i)).append("\n");
						}
					}
					jsonResult = resultBuf.toString();
				}
			

			
		
		
		}catch(Exception e) {
		throw e;
	}
		return jsonResult;
	}
	
	private String getJSONOutputCI(ScoreTotalCIModel scoreTotalCIModel2) throws JsonProcessingException {
		endtime = System.currentTimeMillis();
		totaltime = endtime-begintime;
		endTimeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss:SSS")).toString();
		
		scoreTotalCIModel2.setDuration(totaltime);
		scoreTotalCIModel2.setEnddate(endTimeStr);
		if(scoreTotalCIModel2.getErrorno() == 0) {
			scoreTotalCIModel2.setErrortext("");
		}
		
		ObjectMapper Obj = new ObjectMapper(); 
		String jsonStr = Obj.writeValueAsString(scoreTotalCIModel2); 
		
		return jsonStr;
	}

	private String getJSONOutputCS(ScoreTotalCSModel scoreTotalCSModel2) throws JsonProcessingException {
		endtime = System.currentTimeMillis();
		totaltime = endtime-begintime;
		endTimeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss:SSS")).toString();
		
		scoreTotalCSModel2.setDuration(totaltime);
		scoreTotalCSModel2.setEnddate(endTimeStr);
		if(scoreTotalCSModel2.getErrorno() == 0) {
			scoreTotalCSModel2.setErrortext("");
		}
		
		ObjectMapper Obj = new ObjectMapper(); 
		String jsonStr = Obj.writeValueAsString(scoreTotalCSModel2); 
		
		return jsonStr;
	}

	private String getJSONScorePerLineCI(ScorePerLineCIModel scorePerLineCIModel2) throws JsonProcessingException {
		endtime = System.currentTimeMillis();
		totaltime = endtime-begintime;
		endTimeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss:SSS")).toString();
		
		scorePerLineCIModel2.setDuration(totaltime);
		scorePerLineCIModel2.setEnddate(endTimeStr);
		scorePerLineCIModel2.setErrortext("");
		
		ObjectMapper Obj = new ObjectMapper(); 
		String jsonStr = Obj.writeValueAsString(scorePerLineCIModel2); 
		
		return jsonStr;
	}

	private String getJSONScorePerLineCS(ScorePerLineCSModel scorePerLineCSModel2) throws JsonProcessingException {
		endtime = System.currentTimeMillis();
		totaltime = endtime-begintime;
		endTimeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss:SSS")).toString();
		
		scorePerLineCSModel2.setDuration(totaltime);
		scorePerLineCSModel2.setEnddate(endTimeStr);
		scorePerLineCSModel2.setErrortext("");
		
		ObjectMapper Obj = new ObjectMapper(); 
		String jsonStr = Obj.writeValueAsString(scorePerLineCSModel2); 
		
		return jsonStr;
	}

	private String getJSONScoreSummaryCI(ScoreSummaryOnlyCIModel scoreSummaryOnlyCIModel2) throws JsonProcessingException {
		endtime = System.currentTimeMillis();
		totaltime = endtime-begintime;
		endTimeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss:SSS")).toString();
		
		scoreSummaryOnlyCIModel2.setDuration(totaltime);
		scoreSummaryOnlyCIModel2.setEnddate(endTimeStr);
		scoreSummaryOnlyCIModel2.setErrortext("");
		
		ObjectMapper Obj = new ObjectMapper(); 
		String jsonStr = Obj.writeValueAsString(scoreSummaryOnlyCIModel2); 
		
		
		return jsonStr;
	}

	private String getJSONScoreSummaryCS(ScoreSummaryOnlyCSModel scoreSummaryOnlyCSModel2) throws JsonProcessingException {
		endtime = System.currentTimeMillis();
		totaltime = endtime-begintime;
		endTimeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss:SSS")).toString();
		
		scoreSummaryOnlyCSModel2.setDuration(totaltime);
		scoreSummaryOnlyCSModel2.setEnddate(endTimeStr);
		scoreSummaryOnlyCSModel2.setErrortext("");
		
		ObjectMapper Obj = new ObjectMapper(); 
		String jsonStr = Obj.writeValueAsString(scoreSummaryOnlyCSModel2); 
		
		
		return jsonStr;
	}

	private String getJSONScorePerLine(ScorePerLineModel scorePerLine2) throws JsonProcessingException {
		endtime = System.currentTimeMillis();
		totaltime = endtime-begintime;
		endTimeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss:SSS")).toString();
		
		scorePerLine2.setDuration(totaltime);
		scorePerLine2.setEnddate(endTimeStr);
		scorePerLine2.setErrortext("");
		
		ObjectMapper Obj = new ObjectMapper(); 
		String jsonStr = Obj.writeValueAsString(scorePerLine2); 
		
		return jsonStr;
	}

	private double getScoreSummary(String inputReference, String inputCandidate, String lowercase) throws IOException, InterruptedException {
		double finalResult =0;
		StreamGobblerWithOutput _errorStreamGobbler;
		StreamGobblerWithOutput _inputStreamGobbler;
		String shellFilePath = rp.getProp(Constant.shell_File_Path);
		String perlFilePath = rp.getProp(Constant.perl_File_Path);

		ExecutorService _executor = Executors.newSingleThreadExecutor();
		
		StringBuilder sbError = new StringBuilder();
		StringBuilder sbResult = new StringBuilder();
		String[] commands = {shellFilePath, perlFilePath, lowercase, inputCandidate, inputReference};
		
		ProcessBuilder proc = new ProcessBuilder(commands);
		proc.redirectErrorStream(false);
		
		Process _proc = proc.start();
		
		_inputStreamGobbler = new StreamGobblerWithOutput("InputStreamST", _proc.getInputStream(), _proc.getOutputStream(), "test\teating");
		_errorStreamGobbler = new StreamGobblerWithOutput("ErrorStreamST", _proc.getErrorStream());
		
		Thread tInput = new Thread(_inputStreamGobbler);
		Thread tError = new Thread(_errorStreamGobbler);
		
		tError.start();
		tInput.start();
		tInput.join();
		
		sbResult =_inputStreamGobbler.getOutputBuffer();
		
		String result = sbResult.toString().substring(6,12).trim().replace(",", "");
		
		finalResult = Double.valueOf(result);
		
		
		return finalResult;
	}

	private String writeFileTemp(String content, String type) {
		// TODO Auto-generated method stub
		return null;
	}

	private String getJSONScoreSummary(ScoreSummaryOnlyModel scoreSummaryOnlyModel2) throws JsonProcessingException {
		
		endtime = System.currentTimeMillis();
		totaltime = endtime-begintime;
		endTimeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss:SSS")).toString();
		
		scoreSummaryOnlyModel2.setDuration(totaltime);
		scoreSummaryOnlyModel2.setEnddate(endTimeStr);
		scoreSummaryOnlyModel2.setErrortext("");
		
		ObjectMapper Obj = new ObjectMapper(); 
		String jsonStr = Obj.writeValueAsString(scoreSummaryOnlyModel2); 
		
		
		return jsonStr;
	}

	private String getJSONOutput(ScoreTotalModel scoreTotal) throws JsonProcessingException {
		
		endtime = System.currentTimeMillis();
		totaltime = endtime-begintime;
		endTimeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss:SSS")).toString();
		
		scoreTotal.setDuration(totaltime);
		scoreTotal.setEnddate(endTimeStr);
		if(scoreTotal.getErrorno() == 0) {
			scoreTotal.setErrortext("");
		}
		
		ObjectMapper Obj = new ObjectMapper(); 
		String jsonStr = Obj.writeValueAsString(scoreTotal); 
		
		return jsonStr;
	}
	
	private String getResponeOnlyJSONOutput(ScoreResponseOnlyModel scoreResponseOnly) throws JsonProcessingException {
		String jsonResult = null;
		
		
		ObjectMapper Obj = new ObjectMapper(); 
		String jsonStr = Obj.writeValueAsString(scoreResponseOnly); 
		
		return jsonStr;
	}
	
//	private String getJSONOutput(int outputFormat, MetricScoreModel metricScoreModel2) {
//		String result = null;
//		if(outputFormat == 1) {
//			result = getJSONSummaryOnly(metricScoreModel2);
//		} else if(outputFormat == 2) {
//			result = getJSONPerLineOnly(metricScoreModel2);
//		} else if(outputFormat == 3) {
//			result = getJSONPerLineIncludeText(metricScoreModel2);
//		} else if(outputFormat == 4) {
//			result = getJSONSummaryAndRow(metricScoreModel2);
//		} else if(outputFormat == 5) {
//			result = getJSONSummaryAndRowIncludeText(metricScoreModel2);
//		}
//		return result;
//	}
//
//	private String getJSONSummaryAndRowIncludeText(MetricScoreModel metricScoreModel2) {
//		String result = null;
//		
//		endtime = System.currentTimeMillis();
//		totaltime = endtime-begintime;
//		endTimeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss:SSS")).toString();
//		
//		result = "{\"result\":{"
//				+ "\"metric\":\""+metricScoreModel2.getMetric()+"\","
//				+ "\"totalline\":\""+metricScoreModel2.getTotalLine()+"\","
//				+ "\"casesensitive\":\""+metricScoreModel2.getCaseSensitive()+"\","
//				+ "\"scoresummary\":\""+metricScoreModel2.getScoreSummary()+"\","
//				+ "\"scoreperline\":{"+getScorePerLineInclueText(metricScoreModel2)+"}},"
//				+ "\"duration\":\""+totaltime+"\","
//				+ "\"startdate\":\""+startTimeStr+"\","
//				+ "\"errortext\":\"\","
//				+ "\"errorno\":\"\","
//				+ "\"enddate\":\""+endTimeStr+"\","
//				+ "\"requestid\":\"\"}";
//		
//		return result;
//	}
//
//	private String getJSONSummaryAndRow(MetricScoreModel metricScoreModel2) {
//		String result = null;
//		
//		endtime = System.currentTimeMillis();
//		totaltime = endtime-begintime;
//		endTimeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss:SSS")).toString();
//		
//
//		result = "{\"result\":{"
//				+ "\"metric\":\""+metricScoreModel2.getMetric()+"\","
//				+ "\"totalline\":\""+metricScoreModel2.getTotalLine()+"\","
//				+ "\"casesensitive\":\""+metricScoreModel2.getCaseSensitive()+"\","
//				+ "\"scoresummary\":\""+metricScoreModel2.getScoreSummary()+"\","
//				+ "\"scoreperline\":{"+getScorePerLineOnly(metricScoreModel2.getScorePerLine())+"}},"
//				+ "\"duration\":\""+totaltime+"\","
//				+ "\"startdate\":\""+startTimeStr+"\","
//				+ "\"errortext\":\"\","
//				+ "\"errorno\":\"\","
//				+ "\"enddate\":\""+endTimeStr+"\","
//				+ "\"requestid\":\"\"}";
//		
//		return result;
//	}
//
//	private String getJSONPerLineIncludeText(MetricScoreModel metricScoreModel2) {
//		String result = null;
//		
//		endtime = System.currentTimeMillis();
//		totaltime = endtime-begintime;
//		endTimeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss:SSS")).toString();
//		
//		result = "{\"result\":{"
//				+ "\"metric\":\""+metricScoreModel2.getMetric()+"\","
//				+ "\"totalline\":\""+metricScoreModel2.getTotalLine()+"\","
//				+ "\"casesensitive\":\""+metricScoreModel2.getCaseSensitive()+"\","
//				+ "\"scoreperline\":{"+getScorePerLineInclueText(metricScoreModel2)+"}},"
//				+ "\"duration\":\""+totaltime+"\","
//				+ "\"startdate\":\""+startTimeStr+"\","
//				+ "\"errortext\":\"\","
//				+ "\"errorno\":\"\","
//				+ "\"enddate\":\""+endTimeStr+"\","
//				+ "\"requestid\":\"\"}";
//		
//		return result;
//	}
//
//	private String getScorePerLineInclueText(MetricScoreModel metricScoreModel2) {
//		String result = null;
//		
//		StringBuffer scoreBuff = new StringBuffer();
//		
//		int line = 0;
//		for(int i = 0; i< metricScoreModel2.getScorePerLine().size(); i++) {
//			line = line+1;
//			String can = metricScoreModel2.getScorePerLine().get(i).getCandidate();
//			String ref = metricScoreModel2.getScorePerLine().get(i).getReference();
//			String score = metricScoreModel2.getScorePerLine().get(i).getScoreOfThisLine();
//			
//			if(i <(metricScoreModel2.getScorePerLine().size()-1)) {
//				scoreBuff = scoreBuff.append("\""+line+"\":{\"can\":\""+can+"\",\"ref\":\""+ref+"\",\"score\":\""+score+"\"},");
//			}else {
//				scoreBuff = scoreBuff.append("\""+line+"\":{\"can\":\""+can+"\",\"ref\":\""+ref+"\",\"score\":\""+score+"\"}");
//			}
//		}
//		return scoreBuff.toString();
//	}
//
//	private String getJSONPerLineOnly(MetricScoreModel metricScoreModel2) {
//		String result = null;
//		
//		endtime = System.currentTimeMillis();
//		totaltime = endtime-begintime;
//		endTimeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss:SSS")).toString();
//		
//		result = "{\"result\":{"
//				+ "\"metric\":\""+metricScoreModel2.getMetric()+"\","
//				+ "\"totalline\":\""+metricScoreModel2.getTotalLine()+"\","
//				+ "\"casesensitive\":\""+metricScoreModel2.getCaseSensitive()+"\","
//
//				+ "\"scoreperline\":{"+getScorePerLineOnly(metricScoreModel2.getScorePerLine())+"}},"
//				+ "\"duration\":\""+totaltime+"\","
//				+ "\"startdate\":\""+startTimeStr+"\","
//				+ "\"errortext\":\"\","
//				+ "\"errorno\":\"\","
//				+ "\"enddate\":\""+endTimeStr+"\","
//				+ "\"requestid\":\"\"}";
//		
//		return result;
//	}
//
//	private String getScorePerLineOnly(List<ScorePerLineModel> scorePerLine) {
//		StringBuffer scoreBuffer = new StringBuffer();
//		for(int i =0; i < scorePerLine.size(); i++ ) {
//			int line = i+1;
//			if(i<(scorePerLine.size()-1)) {
//				scoreBuffer = scoreBuffer.append("\""+line+"\":\""+scorePerLine.get(i)+"\",");
//			}else {
//				scoreBuffer = scoreBuffer.append("\""+line+"\":\""+scorePerLine.get(i)+"\"");
//			}
//		}
//		return scoreBuffer.toString();
//	}
//
//	private String getJSONSummaryOnly(MetricScoreModel metricScoreModel2) {
//		String result = null;
//		
//		endtime = System.currentTimeMillis();
//		totaltime = endtime-begintime;
//		endTimeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss:SSS")).toString();
//		
//		result = "{\"result\":{"
//				+ "\"metric\":\""+metricScoreModel2.getMetric()+"\","
//				+ "\"totalline\":\""+metricScoreModel2.getTotalLine()+"\","
//				+ "\"casesensitive\":\""+metricScoreModel2.getCaseSensitive()+"\","
//				+ "\"scoresummary\":\""+metricScoreModel2.getScoreSummary()+"\"},"
//				+ "\"duration\":\""+totaltime+"\","
//				+ "\"startdate\":\""+startTimeStr+"\","
//				+ "\"errortext\":\"\","
//				+ "\"errorno\":\"\","
//				+ "\"enddate\":\""+endTimeStr+"\","
//				+ "\"requestid\":\"\"}";
//		
//		return result;
//	}

	private double calculateScoreSummaryCI(List<DetailIncludeTextModel> detail) {
		double scoreCal = 0;
		double scoreAvg = 0;
		for(int i = 0; i< detail.size(); i++) {
			double score = detail.get(i).getCi();
			scoreCal = scoreCal+score;
		}
		scoreAvg = scoreCal/detail.size();
		
		return scoreAvg;
	}
	
	private double calculateScoreSummaryCS(List<DetailIncludeTextModel> detail) {
		double scoreCal = 0;
		double scoreAvg = 0;
		for(int i = 0; i< detail.size(); i++) {
			double score = detail.get(i).getCs();
			scoreCal = scoreCal+score;
		}
		scoreAvg = scoreCal/detail.size();
		
		return scoreAvg;
	}

	private List getListStringFromFile(String inputFilePath) throws IOException {
		List resultList = new ArrayList<String>();
		FileInputStream InputStream = new FileInputStream(inputFilePath);
		DataInputStream in = new DataInputStream(InputStream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		StringBuffer inputBuf = new StringBuffer();
		try {
			String line = null;
			while ((line = br.readLine()) != null){
				resultList.add(line);
			}
		}catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {

			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
				throw ex;
			}
		}
		return resultList;
		
	}
	
//	private String getErrorJSON(int errorNo) {
//		endtime = System.currentTimeMillis();
//		totaltime = endtime-begintime;
//		endTimeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss:SSS")).toString();
//		
//		String ErrorMSG = com.omniscien.lib.util.Message.getMessage(errorNo);
//		String result = "{\"result\":"
//				+ "\"\""
//				+ "\"duration\":\""+totaltime+"\","
//				+ "\"startdate\":\""+startTimeStr+"\","
//				+ "\"errortext\":\""+ErrorMSG+"\","
//				+ "\"errorno\":\""+errorNo+"\","
//				+ "\"enddate\":\""+endTimeStr+"\","
//				+ "\"requestid\":\"\"}";
//		
//		return result;
//	}

	private double getScorePerline(String ref, String cand) throws IOException {

		// Normalize linefeed
		cand = NormalizeInput(cand);
		ref = NormalizeInput(ref);
		/**/

		// Split linefeed
		String[] arcand = cand.split("\n");
		String[] arref = ref.split("\n");

		boolean bFile = false;
		if (arcand.length > 1)
			bFile = true;

		int[] CORRECT = new int[] { 0, 0, 0, 0, 0 };
		int[] TOTAL = new int[] { 0, 0, 0, 0, 0 };
		int length_translation = 0, length_reference = 0;
		int s = 0;
		for (int i = 0; i < arcand.length; i++) {

			String candidate = arcand[i];// $_ = lc if $lowercase;
			String reference = arref[i];
			// check blank line
			if (candidate.equalsIgnoreCase(sBlankLine) || reference.equalsIgnoreCase(sBlankLine)
					|| candidate.trim().length() == 0 || reference.trim().length() == 0) {
				continue;
			}
			String[] WORDC = candidate.split(" ");// my @WORD = split;

			Hashtable<String, Integer> REF_NGRAM = new Hashtable<String, Integer>();// my %REF_NGRAM = ();
			int length_translation_this_sentence = WORDC.length;// my $length_translation_this_sentence = scalar(@WORD);
			int closest_diff = 9999;// my ($closest_diff,$closest_length) = (9999,9999);
			int closest_length = 9999;

			// foreach my $reference (@{$REF[$s]}) {
			// String reference = arref[i]; //$reference = lc($reference) if $lowercase;
			String[] WORDR = reference.split(" ");// my @WORD = split(' ',$reference);
			int length = WORDR.length;// my $length = scalar(@WORD);
			int diff = Math.abs(length_translation_this_sentence - length);// my $diff =
																			// abs($length_translation_this_sentence-$length);

			if (diff < closest_diff) // if ($diff < $closest_diff) {
			{
				closest_diff = diff; // $closest_diff = $diff;
				closest_length = length; // $closest_length = $length;
			} else if (diff == closest_diff) // } elsif ($diff == $closest_diff) {
			{
				if (length < closest_length) // $closest_length = $length if $length < $closest_length;
					closest_length = length;
			}

			for (int n = 1; n <= 4; n++) // for(my $n=1;$n<=4;$n++) {
			{
				Hashtable<String, Integer> REF_NGRAM_N = new Hashtable<String, Integer>();// my %REF_NGRAM_N = ();
				for (int start = 0; start <= (WORDR.length - 1) - (n - 1); start++) // for(my
																					// $start=0;$start<=$#WORD-($n-1);$start++)
																					// {
				{
					String ngram = String.valueOf(n);// my $ngram = "$n";
					for (int w = 0; w < n; w++) {// for(my $w=0;$w<$n;$w++) {
						ngram += " " + WORDR[start + w];// $ngram .= " ".$WORD[$start+$w];
					}
					// $REF_NGRAM_N{$ngram}++;
					if (!REF_NGRAM_N.containsKey(ngram))
						REF_NGRAM_N.put(ngram, 1);
					else
						REF_NGRAM_N.put(ngram, REF_NGRAM_N.get(ngram) + 1);
				}
				Set<String> keys = REF_NGRAM_N.keySet();
				for (String ngram : keys) { // foreach my $ngram (keys %REF_NGRAM_N)
					if (!REF_NGRAM.containsKey(ngram) || REF_NGRAM.get(ngram) < REF_NGRAM_N.get(ngram)) // if
																										// (!defined($REF_NGRAM{$ngram})
																										// ||
																										// $REF_NGRAM{$ngram}
																										// <
																										// $REF_NGRAM_N{$ngram})
					{
						REF_NGRAM.put(ngram, REF_NGRAM_N.get(ngram));// $REF_NGRAM{$ngram} = $REF_NGRAM_N{$ngram};
					}
				}
			}
			// }

			length_translation += length_translation_this_sentence;// $length_translation +=
																	// $length_translation_this_sentence;
			length_reference += closest_length;// $length_reference += $closest_length;

			for (int n = 1; n <= 4; n++) // for(my $n=1;$n<=4;$n++) {
			{
				Hashtable<String, Integer> T_NGRAM = new Hashtable<String, Integer>();// my %T_NGRAM = ();
				for (int start = 0; start <= (WORDC.length - 1) - (n - 1); start++) // for(my
																					// $start=0;$start<=$#WORD-($n-1);$start++)
				{
					String ngram = String.valueOf(n);// my $ngram = "$n";
					for (int w = 0; w < n; w++) {// for(my $w=0;$w<$n;$w++) {
						ngram += " " + WORDC[start + w];// $ngram .= " ".$WORD[$start+$w];
					}
					// $T_NGRAM{$ngram}++;
					if (!T_NGRAM.containsKey(ngram))
						T_NGRAM.put(ngram, 1);
					else
						T_NGRAM.put(ngram, T_NGRAM.get(ngram) + 1);
				}
				Set<String> keys = T_NGRAM.keySet();
				for (String ngram : keys) { // foreach my $ngram (keys %T_NGRAM) {
					// $ngram =~ /^(\d+) /;
					// my $n = $1;
					int ng = Integer.parseInt(ngram.split(" ")[0]);
					TOTAL[ng] += T_NGRAM.get(ngram);// $TOTAL[$n] += $T_NGRAM{$ngram};
					if (REF_NGRAM.containsKey(ngram)) // if (defined($REF_NGRAM{$ngram})) {
					{
						if (REF_NGRAM.get(ngram) >= T_NGRAM.get(ngram)) // if ($REF_NGRAM{$ngram} >= $T_NGRAM{$ngram}) {
						{
							CORRECT[ng] += T_NGRAM.get(ngram);// $CORRECT[$n] += $T_NGRAM{$ngram};
						} else {
							CORRECT[ng] += REF_NGRAM.get(ngram);// $CORRECT[$n] += $REF_NGRAM{$ngram};
						}
					}
				}
			}
			s++;// $s++;
		}

		double brevity_penalty = 1;// my $brevity_penalty = 1;
		float bleu = 0; // my $bleu = 0;

		// my @bleu=();
		double[] arbleu = new double[] { 0, 0, 0, 0, 0 };

		for (int n = 1; n <= 4; n++) {// for(my $n=1;$n<=4;$n++) {
			if (bFile) {
				if (TOTAL[n] > 0) {// if (defined ($TOTAL[$n])){
					arbleu[n] = CORRECT[n] / (double) TOTAL[n];// $bleu[$n]=($TOTAL[$n])?$CORRECT[$n]/$TOTAL[$n]:0;
				} else {
					arbleu[n] = 0;// $bleu[$n]=0;
				}
			} else {
				// add new rule for line as Phillip suggest --> BLEU+1
				arbleu[n] = (CORRECT[n] + 1) / ((double) TOTAL[n] + 1);// $bleu[$n]=($TOTAL[$n])?$CORRECT[$n]/$TOTAL[$n]:0;
			}
		}

		if (length_reference == 0)// if ($length_reference==0){
		{
			// String printf = "BLEU = 0, 0/0/0/0 (BP=0, ratio=0, hyp_len=0, ref_len=0)";
			// System.out.println(printf);
			return 0.0;
		}

		if (length_translation < length_reference) // if ($length_translation<$length_reference) {
		{
			brevity_penalty = Math.exp(1 - (length_reference / (double) length_translation));// $brevity_penalty =
																								// exp(1-$length_reference/$length_translation);
		}

		// $bleu = $brevity_penalty * exp((my_log( $bleu[1] ) +my_log( $bleu[2] )
		// +my_log( $bleu[3] ) +my_log( $bleu[4] ) ) / 4) ;
		bleu = (float) (brevity_penalty * (Math
				.exp((Math.log(arbleu[1]) + Math.log(arbleu[2]) + Math.log(arbleu[3]) + Math.log(arbleu[4])) / 4)));

		/*
		 * String printf = String.
		 * format("BLEU = %.2f, %.1f/%.1f/%.1f/%.1f (BP=%.3f, ratio=%.3f, hyp_len=%d, ref_len=%d)"
		 * , 100*bleu, 100*arbleu[1], 100*arbleu[2], 100*arbleu[3], 100*arbleu[4],
		 * brevity_penalty, length_translation / (double)length_reference,
		 * length_translation, length_reference);
		 * 
		 * System.out.println(printf);
		 */

		// ref :
		// https://github.com/moses-smt/mosesdecoder/blob/master/scripts/generic/multi-bleu.perl

		return bleu;
	}

	private String NormalizeInput(String text) {
		text = text.replace("\\r\\n", "\r\n").replace("\\r", "\r").replace("\\n", "\n").replace("\\t", "\t");
		return text.replace("\r\n", "\n").replace("\r", "\n");
	}
}
