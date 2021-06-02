package com.omniscien.lsmetric.process;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

public class LevenshteinDistance {
	
	private long begintime = 0;
	private long endtime = 0;
	private long totaltime =0;
	private String startTimeStr = "";
	private String endTimeStr = "";
	
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
	
	private String s1;
	private String s2;

	public void setS1(String s1) {
		this.s1 = s1;
	}

	public void setS2(String s2) {
		this.s2 = s2;
	}

	public LevenshteinDistance(String s1, String s2) {
		this.s1 = s1;
		this.s2 = s2;
	}

	public LevenshteinDistance() {
		// Properties
		if (rp == null) {
			rp = new ReadProp("/omniscien/tools/wfs/metrics/bleu/metricConfig.properties");
		}
	}
	
	/**
	 * Compute the Levenshtein distance between the two strings <code>s1</code> and
	 * <code>s2</code>. This is defined to be the minimum number of edits
	 * (insertion, deletion, substitution) that is required to transform
	 * <code>s1</code> to <code>s2</code>.
	 * 
	 * Adapted <code>computeEditDistance</code> to use less space, in the order of
	 * O(min(m, n)), instead of O(m*n). Also, the input strings <code>s1</code> and
	 * <code>s2</code> are normalized to all uppercases.
	 * 
	 * @return The edit distance between <code>s1</code> and <code>s2</code>
	 */
	public int getDistance(String s1, String s2) {

		// check preconditions
		if (s1 == null)
			s1 = "";
		if (s2 == null)
			s2 = "";
		int m = s1.length();
		int n = s2.length();
		if (m == 0) {
			return n; // some simple heuristics
		} else if (n == 0) {
			return m; // some simple heuristics
		} else if (m > n) {
			String tempString = s1; // swap m with n to get O(min(m, n)) space
			s1 = s2;
			s2 = tempString;
			int tempInt = m;
			m = n;
			n = tempInt;
		}

//		// normalize case
//		s1 = s1.toUpperCase();
//		s2 = s2.toUpperCase();

		// Instead of a 2d array of space O(m*n) such as int d[][] = new int[m +
		// 1][n + 1], only the previous row and current row need to be stored at
		// any one time in prevD[] and currD[]. This reduces the space
		// complexity to O(min(m, n)).
		int prevD[] = new int[n + 1];
		int currD[] = new int[n + 1];
		int temp[]; // temporary pointer for swapping

		// the distance of any second string to an empty first string
		for (int j = 0; j < n + 1; j++) {
			prevD[j] = j;
		}

		// for each row in the distance matrix
		for (int i = 0; i < m; i++) {

			// the distance of any first string to an empty second string
			currD[0] = i + 1;
			char ch1 = s1.charAt(i);

			// for each column in the distance matrix
			for (int j = 1; j <= n; j++) {

				char ch2 = s2.charAt(j - 1);
				if (ch1 == ch2) {
					currD[j] = prevD[j - 1];
				} else {
					currD[j] = minOfThreeNumbers(prevD[j] + 1, currD[j - 1] + 1, prevD[j - 1] + 1);
				}

			}

			temp = prevD;
			prevD = currD;
			currD = temp;

		}

		// after swapping, the final answer is now in the last column of prevD
		return prevD[prevD.length - 1];

	}
	
	/**
	 * Returns the minimum of three numbers
	 * 
	 * @param num1 The first number
	 * @param num2 The second number
	 * @param num3 The third number
	 * @return The minimum of first, second and third numbers
	 */
	private int minOfThreeNumbers(int num1, int num2, int num3) {
		return Math.min(num1, Math.min(num2, num3));
	}

	public String getScoreFromTextFile(int CaseSensitive, String InputReference, String InputCandidate,
			int OutputFormat, long begintime, String startTimeStr) throws Exception {
		String jsonResult = null;
		
		if (OutputFormat == 2 || OutputFormat == 3 || OutputFormat == 4) {
			if (OutputFormat == 2) {
				if (CaseSensitive == 1) {
					scoreSummaryOnlyModel = new ScoreSummaryOnlyModel();
					resultSummaryModel = new ResultSummaryModel();
					scoreSummaryOnlyModel.setStartdate(startTimeStr);
					resultSummaryModel.setMetric(Constant.METRIC_LEVENSHTEIN);
				}else if(CaseSensitive == 2) {
					scoreSummaryOnlyCSModel = new ScoreSummaryOnlyCSModel();
					resultSummaryCSModel = new ResultSummaryCSModel();
					scoreSummaryOnlyCSModel.setStartdate(startTimeStr);
					resultSummaryCSModel.setMetric(Constant.METRIC_LEVENSHTEIN);
				}else if(CaseSensitive == 3) {
					scoreSummaryOnlyCIModel = new ScoreSummaryOnlyCIModel();
					resultSummaryCIModel = new ResultSummaryCIModel();
					scoreSummaryOnlyCIModel.setStartdate(startTimeStr);
					resultSummaryCIModel.setMetric(Constant.METRIC_LEVENSHTEIN);
				}

			} else if (OutputFormat == 3) {
				if (CaseSensitive == 1) {
					scorePerLine = new ScorePerLineModel();
					resultPerLineOnlyModel = new ResultPerLineOnlyModel();
					detailNotIncludeTextModel = new DetailNotIncludeTextModel();
					resultPerLineOnlyModel.setMetric(Constant.METRIC_LEVENSHTEIN);
					scorePerLine.setStartdate(startTimeStr);
				}else if(CaseSensitive == 2) {
					scorePerLineCSModel = new ScorePerLineCSModel();
					resultPerLineOnlyCSModel = new ResultPerLineOnlyCSModel();
					detailNotIncludeTextCSModel = new DetailNotIncludeTextCSModel();
					resultPerLineOnlyCSModel.setMetric(Constant.METRIC_LEVENSHTEIN);
					scorePerLineCSModel.setStartdate(startTimeStr);
				}else if(CaseSensitive == 3) {
					scorePerLineCIModel = new ScorePerLineCIModel();
					resultPerLineOnlyCIModel = new ResultPerLineOnlyCIModel();
					detailNotIncludeTextCIModel = new DetailNotIncludeTextCIModel();
					resultPerLineOnlyCIModel.setMetric(Constant.METRIC_LEVENSHTEIN);
					scorePerLineCIModel.setStartdate(startTimeStr);
				}
			} else if (OutputFormat == 4) {
				if (CaseSensitive == 1) {
					scoreTotal = new ScoreTotalModel();
					resultTotalModel = new ResultModel();
					scoreTotal.setStartdate(startTimeStr);
					resultTotalModel.setMetric(Constant.METRIC_LEVENSHTEIN);
					
				}else if(CaseSensitive == 2) {
					scoreTotalCSModel = new ScoreTotalCSModel();
					resultCSModel = new ResultCSModel();
					scoreTotalCSModel.setStartdate(startTimeStr);
					resultCSModel.setMetric(Constant.METRIC_LEVENSHTEIN);
				}
				else if(CaseSensitive == 3) {
					scoreTotalCIModel = new ScoreTotalCIModel();
					resultCIModel = new ResultCIModel();
					scoreTotalCIModel.setStartdate(startTimeStr);
					resultCIModel.setMetric(Constant.METRIC_LEVENSHTEIN);
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
						resultRespondOnly.metric = Constant.METRIC_LEVENSHTEIN;
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
						resultRespondOnly.metric = Constant.METRIC_LEVENSHTEIN;
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
					
					if (OutputFormat == 2) {
						if (CaseSensitive == 1) {
							double scoreSummaryCS = getScoreSummary(InputReferenceList, InputCandidateList, true);
							double scoreSummaryCI = getScoreSummary(InputReferenceList, InputCandidateList, false);
							summary.setCs(scoreSummaryCS);
							summary.setCi(scoreSummaryCI);
							resultSummaryModel.setSummary(summary);
							scoreSummaryOnlyModel.setResult(resultSummaryModel);
							jsonResult = getJSONScoreSummary(scoreSummaryOnlyModel);
						}else if(CaseSensitive == 2) {
							double scoreSummaryCS = getScoreSummary(InputReferenceList, InputCandidateList, true);
							summaryCS.setCs(scoreSummaryCS);
							resultSummaryCSModel.setSummary(summaryCS);
							scoreSummaryOnlyCSModel.setResult(resultSummaryCSModel);
							jsonResult = getJSONScoreSummaryCS(scoreSummaryOnlyCSModel);
						}else if(CaseSensitive == 3) {
							double scoreSummaryCI = getScoreSummary(InputReferenceList, InputCandidateList, false);
							summaryCI.setCi(scoreSummaryCI);
							resultSummaryCIModel.setSummary(summaryCI);
							scoreSummaryOnlyCIModel.setResult(resultSummaryCIModel);
							jsonResult = getJSONScoreSummaryCI(scoreSummaryOnlyCIModel);
						}
					}else if(OutputFormat == 3) {
						if (CaseSensitive == 1) {
							double ScoreSumCS = 0;
							double ScoreSumCI = 0;
							List<Double> cs = new ArrayList<Double>();
							List<Double> ci = new ArrayList<Double>();
							for(int i = 0;i<InputReferenceList.size();i++) {
								double scoreCS = getDistance(InputReferenceList.get(i), InputCandidateList.get(i));
								double scoreCI = getDistance(InputReferenceList.get(i).toLowerCase(), InputCandidateList.get(i).toLowerCase());
								cs.add(scoreCS);
								ci.add(scoreCI);
								
								ScoreSumCS = ScoreSumCS+scoreCS;
								ScoreSumCI = ScoreSumCI+scoreCI;
							}
							double scoreSummaryCS = ScoreSumCS/InputReferenceList.size();
							double scoreSummaryCI = ScoreSumCI/InputReferenceList.size();
							
							summary.setCs(scoreSummaryCS);
							summary.setCi(scoreSummaryCI);
							resultPerLineOnlyModel.setSummary(summary);
							detailNotIncludeTextModel.setCi(ci);
							detailNotIncludeTextModel.setCs(cs);
							resultPerLineOnlyModel.setDetail(detailNotIncludeTextModel);
							scorePerLine.setResult(resultPerLineOnlyModel);
							
							jsonResult = getJSONScorePerLine(scorePerLine);
						}else if(CaseSensitive == 2) {
							double ScoreSumCS = 0;
							List<Double> cs = new ArrayList<Double>();
							for(int i = 0;i<InputReferenceList.size();i++) {
								double scoreCS = getDistance(InputReferenceList.get(i), InputCandidateList.get(i));
								cs.add(scoreCS);
								ScoreSumCS = ScoreSumCS+scoreCS;
							}
							double scoreSummaryCS = ScoreSumCS/InputReferenceList.size();
							summaryCS.setCs(scoreSummaryCS);
							resultPerLineOnlyCSModel.setSummary(summaryCS);
							detailNotIncludeTextCSModel.setCs(cs);
							resultPerLineOnlyCSModel.setDetail(detailNotIncludeTextCSModel);
							scorePerLineCSModel.setResult(resultPerLineOnlyCSModel);
							jsonResult = getJSONScorePerLineCS(scorePerLineCSModel);
						}else if(CaseSensitive == 3) {
							double ScoreSumCI = 0;
							List<Double> ci = new ArrayList<Double>();
							for(int i = 0;i<InputReferenceList.size();i++) {
								double scoreCI = getDistance(InputReferenceList.get(i).toLowerCase(), InputCandidateList.get(i).toLowerCase());
								ci.add(scoreCI);
								ScoreSumCI = ScoreSumCI+scoreCI;
							}
							double scoreSummaryCI = ScoreSumCI/InputReferenceList.size();
							summaryCI.setCi(scoreSummaryCI);
							resultPerLineOnlyCIModel.setSummary(summaryCI);
							detailNotIncludeTextCIModel.setCi(ci);
							resultPerLineOnlyCIModel.setDetail(detailNotIncludeTextCIModel);
							scorePerLineCIModel.setResult(resultPerLineOnlyCIModel);
							jsonResult = getJSONScorePerLineCI(scorePerLineCIModel);
						}
						
					}else if (OutputFormat == 4) {
						List<DetailIncludeTextModel> listDetail = new ArrayList<DetailIncludeTextModel>();
						List<DetailIncludeTextCSModel> listCSDetail = new ArrayList<DetailIncludeTextCSModel>();
						List<DetailIncludeTextCIModel> listCIDetail = new ArrayList<DetailIncludeTextCIModel>();
						if (CaseSensitive == 1) {
							double ScoreSumCS = 0;
							double ScoreSumCI = 0;

							for(int i = 0;i<InputReferenceList.size();i++) {
								detail = new DetailIncludeTextModel();
								detail.setCan(InputCandidateList.get(i));
								detail.setRef(InputReferenceList.get(i));
								double scoreCS = getDistance(InputReferenceList.get(i), InputCandidateList.get(i));
								double scoreCI = getDistance(InputReferenceList.get(i).toLowerCase(), InputCandidateList.get(i).toLowerCase());
								detail.setCi(scoreCI);
								detail.setCs(scoreCS);
								
								listDetail.add(detail);
								ScoreSumCS = ScoreSumCS+scoreCS;
								ScoreSumCI = ScoreSumCI+scoreCI;
							}
							double scoreSummaryCS = ScoreSumCS/InputReferenceList.size();
							double scoreSummaryCI = ScoreSumCI/InputReferenceList.size();
							
							resultTotalModel.setDetail(listDetail);
							summary.setCi(scoreSummaryCI);
							summary.setCs(scoreSummaryCS);
							resultTotalModel.setSummary(summary);
							scoreTotal.setResult(resultTotalModel);
							jsonResult = getJSONOutput(scoreTotal);
						}else if (CaseSensitive == 2) {
							double ScoreSumCS = 0;
							List<Double> cs = new ArrayList<Double>();
							for(int i = 0;i<InputReferenceList.size();i++) {
								detailIncludeTextCSModel = new DetailIncludeTextCSModel();
								detailIncludeTextCSModel.setCan(InputCandidateList.get(i));
								detailIncludeTextCSModel.setRef(InputReferenceList.get(i));
								double scoreCS = getDistance(InputReferenceList.get(i), InputCandidateList.get(i));
								detailIncludeTextCSModel.setCs(scoreCS);
								
								listCSDetail.add(detailIncludeTextCSModel);
								ScoreSumCS = ScoreSumCS+scoreCS;
							}
							double scoreSummaryCS = ScoreSumCS/InputReferenceList.size();
							resultCSModel.setDetail(listCSDetail);
							summaryCS.setCs(scoreSummaryCS);
							resultCSModel.setSummary(summaryCS);
							scoreTotalCSModel.setResult(resultCSModel);
							jsonResult = getJSONOutputCS(scoreTotalCSModel);
						}else if (CaseSensitive == 3) {
							double ScoreSumCI = 0;
							List<Double> ci = new ArrayList<Double>();
							for(int i = 0;i<InputReferenceList.size();i++) {
								detailIncludeTextCIModel = new DetailIncludeTextCIModel();
								detailIncludeTextCIModel.setCan(InputCandidateList.get(i));
								detailIncludeTextCIModel.setRef(InputReferenceList.get(i));
								double scoreCI = getDistance(InputReferenceList.get(i).toLowerCase(), InputCandidateList.get(i).toLowerCase());
								detailIncludeTextCIModel.setCi(scoreCI);
								listCIDetail.add(detailIncludeTextCIModel);
								ScoreSumCI = ScoreSumCI+scoreCI;
							}
							double scoreSummaryCI = ScoreSumCI/InputReferenceList.size();
							resultCIModel.setDetail(listCIDetail);
							summaryCI.setCi(scoreSummaryCI);
							resultCIModel.setSummary(summaryCI);
							scoreTotalCIModel.setResult(resultCIModel);
							
							jsonResult = getJSONOutputCI(scoreTotalCIModel);
						}
					}else if(OutputFormat == 5){
						List<Double> cs = new ArrayList<Double>();
						List<Double> ci = new ArrayList<Double>();
						for(int i = 0;i<InputReferenceList.size();i++) {
							double scoreCS = getDistance(InputReferenceList.get(i), InputCandidateList.get(i));
							double scoreCI = getDistance(InputReferenceList.get(i).toLowerCase(), InputCandidateList.get(i).toLowerCase());
							cs.add(scoreCS);
							ci.add(scoreCI);	
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
						List<Double> cs = new ArrayList<Double>();
						List<Double> ci = new ArrayList<Double>();
						for(int i = 0;i<InputReferenceList.size();i++) {
							double scoreCS = getDistance(InputReferenceList.get(i), InputCandidateList.get(i));
							double scoreCI = getDistance(InputReferenceList.get(i).toLowerCase(), InputCandidateList.get(i).toLowerCase());
							cs.add(scoreCS);
							ci.add(scoreCI);	
						}
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

	private double getScoreSummary(List<String> inputReferenceList, List<String> inputCandidateList, boolean caseSensitive) {
		double scoreSum = 0;
		if(caseSensitive) {
			for(int i = 0; i < inputReferenceList.size(); i++) {
				String ref = inputReferenceList.get(i);
				String can = inputCandidateList.get(i);
				double score = getDistance(ref, can);
				scoreSum = scoreSum+score;
			}			
		}else {
			for(int i = 0; i < inputReferenceList.size(); i++) {
				String ref = inputReferenceList.get(i).toLowerCase();
				String can = inputCandidateList.get(i).toLowerCase();
				double score = getDistance(ref, can);
				scoreSum = scoreSum+score;
			}
		}
	
		return scoreSum/inputReferenceList.size();
	}

	private String getResponeOnlyJSONOutput(ScoreResponseOnlyModel scoreResponseOnly) throws JsonProcessingException {
		String jsonResult = null;

		ObjectMapper Obj = new ObjectMapper();
		String jsonStr = Obj.writeValueAsString(scoreResponseOnly);

		return jsonStr;
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

}
