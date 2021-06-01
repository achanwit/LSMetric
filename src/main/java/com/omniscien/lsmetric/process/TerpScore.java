package com.omniscien.lsmetric.process;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class TerpScore {

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
	
	public TerpScore() {
		// Properties
		if (rp == null) {
			rp = new ReadProp("/omniscien/tools/wfs/metrics/bleu/metricConfig.properties");
		}
		
		
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
					resultSummaryModel.setMetric(Constant.METRIC_TERP);
				}else if(CaseSensitive == 2) {
					scoreSummaryOnlyCSModel = new ScoreSummaryOnlyCSModel();
					resultSummaryCSModel = new ResultSummaryCSModel();
					scoreSummaryOnlyCSModel.setStartdate(startTimeStr);
					resultSummaryCSModel.setMetric(Constant.METRIC_TERP);
				}else if(CaseSensitive == 3) {
					scoreSummaryOnlyCIModel = new ScoreSummaryOnlyCIModel();
					resultSummaryCIModel = new ResultSummaryCIModel();
					scoreSummaryOnlyCIModel.setStartdate(startTimeStr);
					resultSummaryCIModel.setMetric(Constant.METRIC_TERP);
				}

			} else if (OutputFormat == 3) {
				if (CaseSensitive == 1) {
					scorePerLine = new ScorePerLineModel();
					resultPerLineOnlyModel = new ResultPerLineOnlyModel();
					detailNotIncludeTextModel = new DetailNotIncludeTextModel();
					resultPerLineOnlyModel.setMetric(Constant.METRIC_TERP);
					scorePerLine.setStartdate(startTimeStr);
				}else if(CaseSensitive == 2) {
					scorePerLineCSModel = new ScorePerLineCSModel();
					resultPerLineOnlyCSModel = new ResultPerLineOnlyCSModel();
					detailNotIncludeTextCSModel = new DetailNotIncludeTextCSModel();
					resultPerLineOnlyCSModel.setMetric(Constant.METRIC_TERP);
					scorePerLineCSModel.setStartdate(startTimeStr);
				}else if(CaseSensitive == 3) {
					scorePerLineCIModel = new ScorePerLineCIModel();
					resultPerLineOnlyCIModel = new ResultPerLineOnlyCIModel();
					detailNotIncludeTextCIModel = new DetailNotIncludeTextCIModel();
					resultPerLineOnlyCIModel.setMetric(Constant.METRIC_TERP);
					scorePerLineCIModel.setStartdate(startTimeStr);
				}
			} else if (OutputFormat == 4) {
				if (CaseSensitive == 1) {
					scoreTotal = new ScoreTotalModel();
					resultTotalModel = new ResultModel();
					scoreTotal.setStartdate(startTimeStr);
					resultTotalModel.setMetric(Constant.METRIC_TERP);
					
				}else if(CaseSensitive == 2) {
					scoreTotalCSModel = new ScoreTotalCSModel();
					resultCSModel = new ResultCSModel();
					scoreTotalCSModel.setStartdate(startTimeStr);
					resultCSModel.setMetric(Constant.METRIC_TERP);
				}
				else if(CaseSensitive == 3) {
					scoreTotalCIModel = new ScoreTotalCIModel();
					resultCIModel = new ResultCIModel();
					scoreTotalCIModel.setStartdate(startTimeStr);
					resultCIModel.setMetric(Constant.METRIC_TERP);
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
				resultRespondOnly.metric = Constant.METRIC_TER;
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
				resultRespondOnly.metric = Constant.METRIC_TERP;
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
					String rawScoreCSStr = getRawScore(InputReference,InputCandidate,  true);
					String rawScoreCIStr = getRawScore(InputReference, InputCandidate, false);
					double scoreSummaryCS = normalizeROUGEOutputSummary(rawScoreCSStr);
					double scoreSummaryCI = normalizeROUGEOutputSummary(rawScoreCIStr);
					summary.setCs(scoreSummaryCS);
					summary.setCi(scoreSummaryCI);
					resultSummaryModel.setSummary(summary);
					scoreSummaryOnlyModel.setResult(resultSummaryModel);
					jsonResult = getJSONScoreSummary(scoreSummaryOnlyModel);
				}else if(CaseSensitive == 2) {
					String rawScoreCSStr = getRawScore(InputReference,InputCandidate,  true);
					double scoreSummaryCS = normalizeROUGEOutputSummary(rawScoreCSStr);
					summaryCS.setCs(scoreSummaryCS);
					resultSummaryCSModel.setSummary(summaryCS);
					scoreSummaryOnlyCSModel.setResult(resultSummaryCSModel);
					jsonResult = getJSONScoreSummaryCS(scoreSummaryOnlyCSModel);
				}else if(CaseSensitive == 3) {
					String rawScoreCIStr = getRawScore(InputReference, InputCandidate, false);
					double scoreSummaryCI = normalizeROUGEOutputSummary(rawScoreCIStr);
					summaryCI.setCi(scoreSummaryCI);
					resultSummaryCIModel.setSummary(summaryCI);
					scoreSummaryOnlyCIModel.setResult(resultSummaryCIModel);
					jsonResult = getJSONScoreSummaryCI(scoreSummaryOnlyCIModel);
				}
			}else if(OutputFormat == 3) {
				if (CaseSensitive == 1) {
					String rawScoreCSStr = getRawScore(InputReference,InputCandidate,  true);
					String rawScoreCIStr = getRawScore(InputReference, InputCandidate, false);
					double scoreSummaryCS = normalizeROUGEOutputSummary(rawScoreCSStr);
					double scoreSummaryCI = normalizeROUGEOutputSummary(rawScoreCIStr);
					summary.setCs(scoreSummaryCS);
					summary.setCi(scoreSummaryCI);
					resultPerLineOnlyModel.setSummary(summary);
					
					List<Double> cs = new ArrayList<Double>();
					List<Double> ci = new ArrayList<Double>();
					
					cs = getCsoreList(InputReference, InputCandidate, true, cs);
					ci = getCsoreList(InputReference, InputCandidate, false, ci);
					
					detailNotIncludeTextModel.setCi(ci);
					detailNotIncludeTextModel.setCs(cs);
					resultPerLineOnlyModel.setDetail(detailNotIncludeTextModel);
					scorePerLine.setResult(resultPerLineOnlyModel);

					jsonResult = getJSONScorePerLine(scorePerLine);
				}else if(CaseSensitive == 2) {
					String rawScoreCSStr = getRawScore(InputReference,InputCandidate,  true);
					double scoreSummaryCS = normalizeROUGEOutputSummary(rawScoreCSStr);
					summaryCS.setCs(scoreSummaryCS);
					List<Double> cs = new ArrayList<Double>();
					cs = getCsoreList(InputReference, InputCandidate, true, cs);
					detailNotIncludeTextCSModel.setCs(cs);
					resultPerLineOnlyCSModel.setDetail(detailNotIncludeTextCSModel);
					scorePerLineCSModel.setResult(resultPerLineOnlyCSModel);
					jsonResult = getJSONScorePerLineCS(scorePerLineCSModel);
				}else if(CaseSensitive == 3) {
					String rawScoreCIStr = getRawScore(InputReference, InputCandidate, false);
					double scoreSummaryCI = normalizeROUGEOutputSummary(rawScoreCIStr);
					summaryCI.setCi(scoreSummaryCI);
					resultPerLineOnlyCIModel.setSummary(summaryCI);
					List<Double> ci = new ArrayList<Double>();
					ci = getCsoreList(InputReference, InputCandidate, false, ci);
					detailNotIncludeTextCIModel.setCi(ci);
					resultPerLineOnlyCIModel.setDetail(detailNotIncludeTextCIModel);
					scorePerLineCIModel.setResult(resultPerLineOnlyCIModel);
					jsonResult = getJSONScorePerLineCI(scorePerLineCIModel);
				}
			}else if(OutputFormat == 4) {
				List<DetailIncludeTextModel> listDetail = new ArrayList<DetailIncludeTextModel>();
				List<DetailIncludeTextCSModel> listCSDetail = new ArrayList<DetailIncludeTextCSModel>();
				List<DetailIncludeTextCIModel> listCIDetail = new ArrayList<DetailIncludeTextCIModel>();
			
				if (CaseSensitive == 1) {
					String rawScoreCSStr = getRawScore(InputCandidate, InputReference, true);
					String rawScoreCIStr = getRawScore(InputCandidate, InputReference, false);
					
					double scoreSummaryCS = normalizeROUGEOutputSummary(rawScoreCSStr);
					double scoreSummaryCI = normalizeROUGEOutputSummary(rawScoreCIStr);
					
					List<Double> cs = new ArrayList<Double>();
					cs = getCsoreList(InputReference, InputCandidate, true, cs);
					
					List<Double> ci = new ArrayList<Double>();
					ci = getCsoreList(InputReference, InputCandidate, false, ci);
					for (int i = 0; i < InputReferenceList.size(); i++) {
						detail = new DetailIncludeTextModel();
						detail.setCan(InputCandidateList.get(i));
						detail.setRef(InputReferenceList.get(i));
						
						detail.setCs(cs.get(i));
						detail.setCi(ci.get(i));
						listDetail.add(detail);
					}
					resultTotalModel.setDetail(listDetail);
					

					summary.setCi(scoreSummaryCI);
					summary.setCs(scoreSummaryCS);
					resultTotalModel.setSummary(summary);
					scoreTotal.setResult(resultTotalModel);

					jsonResult = getJSONOutput(scoreTotal);
				}else if(CaseSensitive == 2) {
					String rawScoreCSStr = getRawScore(InputCandidate, InputReference, true);
					double scoreSummaryCS = normalizeROUGEOutputSummary(rawScoreCSStr);
					List<Double> cs = new ArrayList<Double>();
					cs = getCsoreList(InputReference, InputCandidate, true, cs);
					for (int i = 0; i < InputReferenceList.size(); i++) {
						detailIncludeTextCSModel = new DetailIncludeTextCSModel();
						detailIncludeTextCSModel.setCan(InputCandidateList.get(i));
						detailIncludeTextCSModel.setRef(InputReferenceList.get(i));
						
						detailIncludeTextCSModel.setCs(cs.get(i));
			
						listCSDetail.add(detailIncludeTextCSModel);
					}
					resultCSModel.setDetail(listCSDetail);
					
					
					summaryCS.setCs(scoreSummaryCS);
					resultCSModel.setSummary(summaryCS);
					scoreTotalCSModel.setResult(resultCSModel);
					jsonResult = getJSONOutputCS(scoreTotalCSModel);
				}else if(CaseSensitive == 3) {
					String rawScoreCIStr = getRawScore(InputCandidate, InputReference, false);
					double scoreSummaryCI = normalizeROUGEOutputSummary(rawScoreCIStr);
					List<Double> ci = new ArrayList<Double>();
					ci = getCsoreList(InputReference, InputCandidate, false, ci);
					for (int i = 0; i < InputReferenceList.size(); i++) {
						detailIncludeTextCIModel = new DetailIncludeTextCIModel();
						detailIncludeTextCIModel.setCan(InputCandidateList.get(i));
						detailIncludeTextCIModel.setRef(InputReferenceList.get(i));
						
						detailIncludeTextCIModel.setCi(ci.get(i));
			
						listCIDetail.add(detailIncludeTextCIModel);
					}
					resultCIModel.setDetail(listCIDetail);
					
					
					summaryCI.setCi(scoreSummaryCI);
					resultCIModel.setSummary(summaryCI);
					scoreTotalCIModel.setResult(resultCIModel);
					
					jsonResult = getJSONOutputCI(scoreTotalCIModel);
				}
			}else if(OutputFormat == 5){
				List<Double> cs = new ArrayList<Double>();
				List<Double> ci = new ArrayList<Double>();
				
				cs = getCsoreList(InputReference, InputCandidate, true, cs);
				ci = getCsoreList(InputReference, InputCandidate, false, ci);
				StringBuffer resultBuf = new StringBuffer();
				for(int i = 0; i<cs.size(); i++) {
					if (i == (cs.size() - 1)) {
						resultBuf = resultBuf.append(cs.get(i)).append("\t").append(ci.get(i));
					}else {
						resultBuf = resultBuf.append(cs.get(i)).append("\t").append(ci.get(i)).append("\n");
					}
				}
				jsonResult = resultBuf.toString();
			}else if(OutputFormat == 6){
				List<Double> cs = new ArrayList<Double>();
				List<Double> ci = new ArrayList<Double>();
				
				cs = getCsoreList(InputReference, InputCandidate, true, cs);
				ci = getCsoreList(InputReference, InputCandidate, false, ci);
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
			e.printStackTrace();
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
		totaltime = endtime - begintime;
		endTimeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss:SSS")).toString();

		scoreTotal.setDuration(totaltime);
		scoreTotal.setEnddate(endTimeStr);
		if (scoreTotal.getErrorno() == 0) {
			scoreTotal.setErrortext("");
		}

		ObjectMapper Obj = new ObjectMapper();
		String jsonStr = Obj.writeValueAsString(scoreTotal);

		return jsonStr;
	}

	private List<Double> getCsoreList(String inputReference, String inputCandidate, boolean casSen, List<Double> cs) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		String javaStr1 = "java";
		String javaStr2 = "-jar";
		String terpJar = rp.getProp(Constant.terp_Jar);
		String caseSenOption = "-s";
		String rOption = "-r";
		String hOption = "-h";
		String oOption = "-o";
		String outputFormat = "sum_nbest";
		String nOption = "-n";
		String tempFolder= rp.getProp(Constant.temp_Terp);
		/******/
		String tempFileName = tempFolder+generateID();
		/*****/
		String[] commands = {javaStr1, 
				javaStr2, 
				terpJar,
				rOption, 
				inputReference, 
				hOption, 
				inputCandidate, oOption, "sum_nbest",nOption,tempFileName};		
		
		ProcessBuilder pBuilder = new ProcessBuilder();
		if (casSen) {
			pBuilder.command(commands);
		} else {
			pBuilder.command(commands);
		}
		
		Process process = pBuilder.start();
		process.waitFor();

		InputStream stdout = new BufferedInputStream(process.getInputStream());
		Scanner outputScanner = new Scanner(stdout);
		StringBuilder rawOutput = new StringBuilder();
		while (outputScanner.hasNext()) {
			String line = outputScanner.nextLine();
			rawOutput.append(line + "\n");
		}

		process.destroy();
		stdout.close();
		outputScanner.close();

		process.destroy();
		
		//Read Temp File
		List resultList = new ArrayList<String>();
		FileInputStream InputStream = new FileInputStream(tempFileName+".sum_nbest");
		DataInputStream in = new DataInputStream(InputStream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		StringBuffer inputBuf = new StringBuffer();

		try {
			String line = null;
			while ((line = br.readLine()) != null){
				String terpScoreStr = line.substring(60).trim();
//				System.out.println(terpScoreStr);
				double terpScore = Double.valueOf(terpScoreStr);
				DecimalFormat df = new DecimalFormat("#.##");
				terpScoreStr = df.format(terpScore);
				terpScore = Double.valueOf(terpScoreStr);
				
				cs.add(terpScore);
				
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
		//Delete Temp File
		File file = new File(tempFileName+".sum_nbest"); 
		file.delete();

		return cs;
		
	}
	
	/*** Common generate id ***/
	public String generateID() {
		String idStr = new String();
		idStr = UUID.randomUUID().toString();
		return idStr;
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

	private double normalizeROUGEOutputSummary(String rawScoreCIStr) {
		String lineStr = "^Total TER:.*";
		
		Pattern linePattern = Pattern.compile(lineStr, Pattern.MULTILINE);
		Matcher lineMatcher = linePattern.matcher(rawScoreCIStr);
		String line = "";
		while (lineMatcher.find()) {
			line = lineMatcher.group(0);
//			String[] linePieces = line.split("\t");
//			System.out.println(line);
		}
		String[] lineArr = line.split(" ");
		String scoreStr = lineArr[2];
		double finalScoreDouble = Double.valueOf(scoreStr);
		finalScoreDouble = finalScoreDouble*100;
		DecimalFormat df = new DecimalFormat("#.##");
		scoreStr = df.format(finalScoreDouble);
		finalScoreDouble = Double.valueOf(scoreStr);
		return finalScoreDouble;
	}

	private String getRawScore(String inputReference, String inputCandidate, boolean casSen) throws IOException, InterruptedException {
		String javaStr1 = "java";
		String javaStr2 = "-jar";
		String terpJar = rp.getProp(Constant.terp_Jar);
		String caseSenOption = "-s";
		String rOption = "-r";
		String hOption = "-h";
		
		ExecutorService _executor = Executors.newSingleThreadExecutor();
		StringBuilder sbError = new StringBuilder();
		StringBuilder sbResult = new StringBuilder();
		
		String[] commandsCaseSensitive = {javaStr1, javaStr2, terpJar, "-N", "-s", rOption, inputReference, hOption, inputCandidate};
		String[] commandsIncaseSensitive = {javaStr1, javaStr2, terpJar, rOption, inputReference, hOption, inputCandidate};
		
		ProcessBuilder pBuilder = new ProcessBuilder();
		
		if (casSen) {
			pBuilder.command(commandsCaseSensitive);
		} else {
			pBuilder.command(commandsIncaseSensitive);
		}
		
		Process process = pBuilder.start();
		process.waitFor();

		InputStream stdout = new BufferedInputStream(process.getInputStream());
		Scanner outputScanner = new Scanner(stdout);
		StringBuilder rawOutput = new StringBuilder();
		while (outputScanner.hasNext()) {
			String line = outputScanner.nextLine();
			rawOutput.append(line + "\n");
		}

		process.destroy();
		stdout.close();
		outputScanner.close();

		process.destroy();
		
		String rawResult = rawOutput.toString();
		return rawResult;
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
