package com.omniscien.lsmetric.controller;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.ao.ter.TERcalc;
import com.omniscien.lsmetric.process.LevenshteinDistance;
import com.omniscien.lsmetric.util.Constant;
import com.omniscien.lsmetric.util.ReadProp;

import opennlp.tools.util.eval.FMeasure;

public class Metrics {
	
	//General Variable
	private long begintime = 0;
	private long endtime = 0;
	private long totaltime =0;
	private String startTimeStr = "";
	private String endTimeStr = "";
	
	//Constant Metric Type
	public static final String METRIC_BLEU = "BLEU";
	public static final String METRIC_FMEASURE = "FMEASURE";
	public static final String METRIC_LEVENSHTEIN = "LEVENSHTEIN";
	public static final String METRIC_TER = "TER";
	public static final String METRIC_WER = "WER";
	public static final String METRIC_METEOR = "METEOR";
	public static final String METRIC_ROUGE = "ROUGE";
	public static final String METRIC_TERP = "TERP";
	public static final String METRIC_RIBES = "RIBES";
	public static final String METRIC_BEER = "BEER";
	public static final String METRIC_HLEPOR = "HLEPOR";	
	public static final String[] MetricArr = {METRIC_BLEU, METRIC_FMEASURE, METRIC_LEVENSHTEIN, METRIC_TER, METRIC_WER, METRIC_METEOR, METRIC_ROUGE, METRIC_TERP, METRIC_RIBES, METRIC_BEER, METRIC_HLEPOR};
	private static final List metricList = Arrays.asList(MetricArr);
	
	private ReadProp rp = null;
	public Metrics() {
		
	}
	
	
	
	public ReadProp getRp() {
		return rp;
	}



	public void setRp(ReadProp rp) {
		this.rp = rp;
	}



	public void propertiesSetting(String filePath) {
		rp = new ReadProp(filePath);
		
	}
	
	public String Metric(String MetricType, int InputType, String InputReference, String InputCandidate, int CaseSensitive, String MetricOptions,int JSONOutputFormat,
			int FileOutputFormat, String FileOutputPath) throws Exception {
		
		
		
		//Start Time
		begintime = System.currentTimeMillis();
		startTimeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss:SSS")).toString();
		
		
		
		String result = null;
		try {
			//Validate InputType
			if(InputType > 3 || InputType < 1) {
				//10047: "Invalid parameters."
				return getErrorJSON(10047);
			}
			//Validate Input
			if(InputType == 1) {
				boolean checkInputRef = new File(InputReference).exists();
				boolean checkInputCan = new File(InputCandidate).exists();
				if(!checkInputRef || !checkInputCan) {
					//10101: "Input file is not found."
					return getErrorJSON(10101);
				}
			}
			//Validate CaseSensitive
			if(CaseSensitive > 3 || CaseSensitive < 1) {
				//10047: "Invalid parameters."
				return getErrorJSON(10047);
			}
			//Validate OutputFormat
			if(CaseSensitive < 1 || CaseSensitive > 5) {
				//10047: "Invalid parameters."
				return getErrorJSON(10047);
			}
			//Validate MetricType
			if(!metricList.contains(MetricType)) {
				//10047: "Invalid parameters."
				return getErrorJSON(10047);
			}
			
			
			if(FileOutputFormat != 0) {
				if(FileOutputPath == null || FileOutputPath.equals("")) {
					return getErrorJSON(10047); 
				}
			}
			
			
			//*** BLEU Metric ***//
			if(MetricType.equals(METRIC_BLEU)) {
				com.omniscien.lsmetric.process.MultiBleuScorer bleu = new com.omniscien.lsmetric.process.MultiBleuScorer();
				//Input as File
				if(InputType == 1) {
					
					result = bleu.getScoreFromTextFile(CaseSensitive, InputReference, InputCandidate, JSONOutputFormat, begintime, startTimeStr, rp);
					
					if(FileOutputFormat != 0) {
						String resultForWrite = bleu.getScoreFromTextFile(CaseSensitive, InputReference, InputCandidate, FileOutputFormat, begintime, startTimeStr, rp);
						writeFile(resultForWrite, FileOutputPath);
						
					}
				}
				// Input as Single line
				else if (InputType == 2) {
					if(rp == null) {
						rp = new ReadProp("/omniscien/tools/wfs/metrics/metricConfig.json");
					}
					//CheckDirectory
					String tempDirectory = rp.getProp(Constant.temp_Foleder);
					File directory = new File(tempDirectory);
					if (! directory.exists()){
				        directory.mkdir();
				    }
					
					//Prepare Candidate File
					String CanFileName = tempDirectory+generateID();
					String[] canData = InputCandidate.split("\n");
					for(int i = 0; i<canData.length; i++ ) {
						writeFile(canData[i], CanFileName);
					}
					
					//Prepare Ref File
					String RefFileName = tempDirectory+generateID();
					String[] refData = InputReference.split("\n");
					for(int i = 0;i< refData.length; i++) {
						writeFile(refData[i], RefFileName);
					}
					result = bleu.getScoreFromTextFile(CaseSensitive, RefFileName, CanFileName, JSONOutputFormat, begintime, startTimeStr, rp);
					
					if(FileOutputFormat != 0) {
						String resultForWrite = bleu.getScoreFromTextFile(CaseSensitive, RefFileName, CanFileName, FileOutputFormat, begintime, startTimeStr, rp);
						writeFile(resultForWrite, FileOutputPath);
						
					}
					
					//Delete Temp File
					File fileCan = new File(CanFileName); 
					fileCan.delete();
					File fileRef = new File(RefFileName);
					fileRef.delete();
					

					
					
				}
				
				
				
			}else if(MetricType.equals(METRIC_LEVENSHTEIN)) {
				com.omniscien.lsmetric.process.LevenshteinDistance leven = new com.omniscien.lsmetric.process.LevenshteinDistance();
				//Input as File
				if(InputType == 1) {
					result = leven.getScoreFromTextFile(CaseSensitive, InputReference, InputCandidate, JSONOutputFormat, begintime, startTimeStr, rp);
					

					if(FileOutputFormat != 0) {
						String resultForWrite = leven.getScoreFromTextFile(CaseSensitive, InputReference, InputCandidate, FileOutputFormat, begintime, startTimeStr, rp);
						writeFile(resultForWrite, FileOutputPath);
						
					}
				}
				// Input as Single line
				else if (InputType == 2) {
					if(rp == null) {
						rp = new ReadProp("/omniscien/tools/wfs/metrics/metricConfig.json");
					}
					//CheckDirectory
					String tempDirectory = rp.getProp(Constant.temp_Foleder);
					File directory = new File(tempDirectory);
					if (! directory.exists()){
				        directory.mkdir();
				    }
					
					//Prepare Candidate File
					String CanFileName = tempDirectory+generateID();
					String[] canData = InputCandidate.split("\n");
					for(int i = 0; i<canData.length; i++ ) {
						writeFile(canData[i], CanFileName);
					}
					
					//Prepare Ref File
					String RefFileName = tempDirectory+generateID();
					String[] refData = InputReference.split("\n");
					for(int i = 0;i< refData.length; i++) {
						writeFile(refData[i], RefFileName);
					}
					result = leven.getScoreFromTextFile(CaseSensitive, RefFileName, CanFileName, JSONOutputFormat, begintime, startTimeStr, rp);
					
					if(FileOutputFormat != 0) {
						String resultForWrite = leven.getScoreFromTextFile(CaseSensitive, RefFileName, CanFileName, FileOutputFormat, begintime, startTimeStr, rp);
						writeFile(resultForWrite, FileOutputPath);
						
					}
					//Delete Temp File
					File fileCan = new File(CanFileName); 
					fileCan.delete();
					File fileRef = new File(RefFileName);
					fileRef.delete();
					

					
				}
			}else if(MetricType.equals(METRIC_WER)) {
				com.omniscien.lsmetric.process.WordErrorRate wer = new com.omniscien.lsmetric.process.WordErrorRate();
				if(InputType == 1) {
					result = wer.getScoreFromTextFile(CaseSensitive, InputReference, InputCandidate, JSONOutputFormat, begintime, startTimeStr, rp);
					if(FileOutputFormat != 0) {
						String resultForWrite = wer.getScoreFromTextFile(CaseSensitive, InputReference, InputCandidate, FileOutputFormat, begintime, startTimeStr, rp);
						writeFile(resultForWrite, FileOutputPath);
						
					}
					
				}
				// Input as Single line
				else if (InputType == 2) {
					//CheckDirectory
					if(rp == null) {
						rp = new ReadProp("/omniscien/tools/wfs/metrics/metricConfig.json");
					}
					String tempDirectory = rp.getProp(Constant.temp_Foleder);
					File directory = new File(tempDirectory);
					if (! directory.exists()){
				        directory.mkdir();
				    }
					
					//Prepare Candidate File
					String CanFileName = tempDirectory+generateID();
					String[] canData = InputCandidate.split("\n");
					for(int i = 0; i<canData.length; i++ ) {
						writeFile(canData[i], CanFileName);
					}
					
					//Prepare Ref File
					String RefFileName = tempDirectory+generateID();
					String[] refData = InputReference.split("\n");
					for(int i = 0;i< refData.length; i++) {
						writeFile(refData[i], RefFileName);
					}
					
					result = wer.getScoreFromTextFile(CaseSensitive, RefFileName, CanFileName, JSONOutputFormat, begintime, startTimeStr, rp);
					
					if(FileOutputFormat != 0) {
						String resultForWrite = wer.getScoreFromTextFile(CaseSensitive, RefFileName, CanFileName, FileOutputFormat, begintime, startTimeStr, rp);
						writeFile(resultForWrite, FileOutputPath);
						
					}
					
					//Delete Temp File
					File fileCan = new File(CanFileName); 
					fileCan.delete();
					File fileRef = new File(RefFileName);
					fileRef.delete();
					
					
				}
			}else if(MetricType.equals(METRIC_TER)) {
				com.omniscien.lsmetric.process.TranslationErrorScore ter = new com.omniscien.lsmetric.process.TranslationErrorScore();
				if(InputType == 1) {
					result = ter.getScoreFromTextFile(CaseSensitive, InputReference, InputCandidate, JSONOutputFormat, begintime, startTimeStr, rp);
					
					if(FileOutputFormat != 0) {
						String resultForWrite = ter.getScoreFromTextFile(CaseSensitive, InputReference, InputCandidate, FileOutputFormat, begintime, startTimeStr, rp);
						writeFile(resultForWrite, FileOutputPath);
						
					}
				}
				// Input as Single line
				else if (InputType == 2) {
					//CheckDirectory
					if(rp == null) {
						rp = new ReadProp("/omniscien/tools/wfs/metrics/metricConfig.json");
					}
					String tempDirectory = rp.getProp(Constant.temp_Foleder);
					File directory = new File(tempDirectory);
					if (! directory.exists()){
				        directory.mkdir();
				    }
					
					//Prepare Candidate File
					String CanFileName = tempDirectory+generateID();
					String[] canData = InputCandidate.split("\n");
					for(int i = 0; i<canData.length; i++ ) {
						writeFile(canData[i], CanFileName);
					}
					
					//Prepare Ref File
					String RefFileName = tempDirectory+generateID();
					String[] refData = InputReference.split("\n");
					for(int i = 0;i< refData.length; i++) {
						writeFile(refData[i], RefFileName);
					}
					
					result = ter.getScoreFromTextFile(CaseSensitive, RefFileName, CanFileName, JSONOutputFormat, begintime, startTimeStr, rp);
					
					if(FileOutputFormat != 0) {
						String resultForWrite = ter.getScoreFromTextFile(CaseSensitive, RefFileName, CanFileName, FileOutputFormat, begintime, startTimeStr, rp);
						writeFile(resultForWrite, FileOutputPath);
						
					}
					
					//Delete Temp File
					File fileCan = new File(CanFileName); 
					fileCan.delete();
					File fileRef = new File(RefFileName);
					fileRef.delete();
					
					
					
				}
			}else if(MetricType.equals(METRIC_ROUGE)) {
				com.omniscien.lsmetric.process.RougeScore rouge = new com.omniscien.lsmetric.process.RougeScore();
				if(InputType == 1) {
					result = rouge.getScoreFromTextFile(CaseSensitive, InputReference, InputCandidate, JSONOutputFormat, begintime, startTimeStr, rp);
					
					if(FileOutputFormat != 0) {
						String resultForWrite = rouge.getScoreFromTextFile(CaseSensitive, InputReference, InputCandidate, FileOutputFormat, begintime, startTimeStr, rp);
						writeFile(resultForWrite, FileOutputPath);
						
					}
				}
				// Input as Single line
				else if (InputType == 2) {
					//CheckDirectory
					if(rp == null) {
						rp = new ReadProp("/omniscien/tools/wfs/metrics/metricConfig.json");
					}
					String tempDirectory = rp.getProp(Constant.temp_Foleder);
					File directory = new File(tempDirectory);
					if (! directory.exists()){
				        directory.mkdir();
				    }
					
					//Prepare Candidate File
					String CanFileName = tempDirectory+generateID();
					String[] canData = InputCandidate.split("\n");
					for(int i = 0; i<canData.length; i++ ) {
						writeFile(canData[i], CanFileName);
					}
					
					//Prepare Ref File
					String RefFileName = tempDirectory+generateID();
					String[] refData = InputReference.split("\n");
					for(int i = 0;i< refData.length; i++) {
						writeFile(refData[i], RefFileName);
					}
					
					result = rouge.getScoreFromTextFile(CaseSensitive, RefFileName, CanFileName, JSONOutputFormat, begintime, startTimeStr, rp);
					
					if(FileOutputFormat != 0) {
						String resultForWrite = rouge.getScoreFromTextFile(CaseSensitive, RefFileName, CanFileName, FileOutputFormat, begintime, startTimeStr, rp);
						writeFile(resultForWrite, FileOutputPath);
						
					}
					
					//Delete Temp File
					File fileCan = new File(CanFileName); 
					fileCan.delete();
					File fileRef = new File(RefFileName);
					fileRef.delete();
				
				}
			}else if(MetricType.equals(METRIC_RIBES)) {
				com.omniscien.lsmetric.process.RibesScore ribes = new com.omniscien.lsmetric.process.RibesScore();
				if(InputType == 1) {
					result = ribes.getScoreFromTextFile(CaseSensitive, InputReference, InputCandidate, JSONOutputFormat, begintime, startTimeStr, rp);
					
					if(FileOutputFormat != 0) {
						String resultForWrite = ribes.getScoreFromTextFile(CaseSensitive, InputReference, InputCandidate, FileOutputFormat, begintime, startTimeStr, rp);
						writeFile(resultForWrite, FileOutputPath);
						
					}
				}else if (InputType == 2) {
					//CheckDirectory
					if(rp == null) {
						rp = new ReadProp("/omniscien/tools/wfs/metrics/metricConfig.json");
					}
					String tempDirectory = rp.getProp(Constant.temp_Foleder);
					File directory = new File(tempDirectory);
					if (! directory.exists()){
				        directory.mkdir();
				    }
					
					//Prepare Candidate File
					String CanFileName = tempDirectory+generateID();
					String[] canData = InputCandidate.split("\n");
					for(int i = 0; i<canData.length; i++ ) {
						writeFile(canData[i], CanFileName);
					}
					
					//Prepare Ref File
					String RefFileName = tempDirectory+generateID();
					String[] refData = InputReference.split("\n");
					for(int i = 0;i< refData.length; i++) {
						writeFile(refData[i], RefFileName);
					}
					
					result = ribes.getScoreFromTextFile(CaseSensitive, RefFileName, CanFileName, JSONOutputFormat, begintime, startTimeStr, rp);
					
					if(FileOutputFormat != 0) {
						String resultForWrite = ribes.getScoreFromTextFile(CaseSensitive, RefFileName, CanFileName, FileOutputFormat, begintime, startTimeStr, rp);
						writeFile(resultForWrite, FileOutputPath);
						
					}
					
					//Delete Temp File
					File fileCan = new File(CanFileName); 
					fileCan.delete();
					File fileRef = new File(RefFileName);
					fileRef.delete();
					

				}
			}else if (MetricType.equals(METRIC_BEER)) {
				com.omniscien.lsmetric.process.BeerScore beer = new com.omniscien.lsmetric.process.BeerScore();
				if(InputType == 1) {
					result = beer.getScoreFromTextFile(CaseSensitive, InputReference, InputCandidate, JSONOutputFormat, begintime, startTimeStr, rp);
					
					if(FileOutputFormat != 0) {
						String resultForWrite = beer.getScoreFromTextFile(CaseSensitive, InputReference, InputCandidate, FileOutputFormat, begintime, startTimeStr, rp);
						writeFile(resultForWrite, FileOutputPath);
						
					}
				}else if (InputType == 2) {
					//CheckDirectory
					if(rp == null) {
						rp = new ReadProp("/omniscien/tools/wfs/metrics/metricConfig.json");
					}
					String tempDirectory = rp.getProp(Constant.temp_Foleder);
					File directory = new File(tempDirectory);
					if (! directory.exists()){
				        directory.mkdir();
				    }
					
					//Prepare Candidate File
					String CanFileName = tempDirectory+generateID();
					String[] canData = InputCandidate.split("\n");
					for(int i = 0; i<canData.length; i++ ) {
						writeFile(canData[i], CanFileName);
					}
					
					//Prepare Ref File
					String RefFileName = tempDirectory+generateID();
					String[] refData = InputReference.split("\n");
					for(int i = 0;i< refData.length; i++) {
						writeFile(refData[i], RefFileName);
					}
					
					result = beer.getScoreFromTextFile(CaseSensitive, RefFileName, CanFileName, JSONOutputFormat, begintime, startTimeStr, rp);
					if(FileOutputFormat != 0) {
						String resultForWrite = beer.getScoreFromTextFile(CaseSensitive, RefFileName, CanFileName, FileOutputFormat, begintime, startTimeStr, rp);
						writeFile(resultForWrite, FileOutputPath);
						
					}
					
					//Delete Temp File
					File fileCan = new File(CanFileName); 
					fileCan.delete();
					File fileRef = new File(RefFileName);
					fileRef.delete();
					

				}
			}else if (MetricType.equals(METRIC_HLEPOR)) {
				com.omniscien.lsmetric.process.HleporScore hlepor = new com.omniscien.lsmetric.process.HleporScore();
				if(InputType == 1) {
					result = hlepor.getScoreFromTextFile(CaseSensitive, InputReference, InputCandidate, JSONOutputFormat, begintime, startTimeStr, rp);
					
					if(FileOutputFormat != 0) {
						String resultForWrite = hlepor.getScoreFromTextFile(CaseSensitive, InputReference, InputCandidate, FileOutputFormat, begintime, startTimeStr, rp);
						writeFile(resultForWrite, FileOutputPath);
						
					}
				}else if (InputType == 2) {
					//CheckDirectory
					if(rp == null) {
						rp = new ReadProp("/omniscien/tools/wfs/metrics/metricConfig.json");
					}
					String tempDirectory = rp.getProp(Constant.temp_Foleder);
					File directory = new File(tempDirectory);
					if (! directory.exists()){
				        directory.mkdir();
				    }
					
					//Prepare Candidate File
					String CanFileName = tempDirectory+generateID();
					String[] canData = InputCandidate.split("\n");
					for(int i = 0; i<canData.length; i++ ) {
						writeFile(canData[i], CanFileName);
					}
					
					//Prepare Ref File
					String RefFileName = tempDirectory+generateID();
					String[] refData = InputReference.split("\n");
					for(int i = 0;i< refData.length; i++) {
						writeFile(refData[i], RefFileName);
					}
					
					result = hlepor.getScoreFromTextFile(CaseSensitive, RefFileName, CanFileName, JSONOutputFormat, begintime, startTimeStr, rp);
					if(FileOutputFormat != 0) {
						String resultForWrite = hlepor.getScoreFromTextFile(CaseSensitive, RefFileName, CanFileName, FileOutputFormat, begintime, startTimeStr, rp);
						writeFile(resultForWrite, FileOutputPath);
						
					}
					//Delete Temp File
					File fileCan = new File(CanFileName); 
					fileCan.delete();
					File fileRef = new File(RefFileName);
					fileRef.delete();
					

				}
			}else if (MetricType.equals(METRIC_METEOR)) {
				com.omniscien.lsmetric.process.MeteorScore meteor = new com.omniscien.lsmetric.process.MeteorScore();
				if(InputType == 1) {
					result = meteor.getScoreFromTextFile(CaseSensitive, InputReference, InputCandidate, JSONOutputFormat, begintime, startTimeStr, rp);
					
					if(FileOutputFormat != 0) {
						String resultForWrite = meteor.getScoreFromTextFile(CaseSensitive, InputReference, InputCandidate, FileOutputFormat, begintime, startTimeStr, rp);
						writeFile(resultForWrite, FileOutputPath);
						
					}
				}else if (InputType == 2) {
					//CheckDirectory
					if(rp == null) {
						rp = new ReadProp("/omniscien/tools/wfs/metrics/metricConfig.json");
					}
					String tempDirectory = rp.getProp(Constant.temp_Foleder);
					File directory = new File(tempDirectory);
					if (! directory.exists()){
				        directory.mkdir();
				    }
					
					//Prepare Candidate File
					String CanFileName = tempDirectory+generateID();
					String[] canData = InputCandidate.split("\n");
					for(int i = 0; i<canData.length; i++ ) {
						writeFile(canData[i], CanFileName);
					}
					
					//Prepare Ref File
					String RefFileName = tempDirectory+generateID();
					String[] refData = InputReference.split("\n");
					for(int i = 0;i< refData.length; i++) {
						writeFile(refData[i], RefFileName);
					}
					
					result = meteor.getScoreFromTextFile(CaseSensitive, RefFileName, CanFileName, JSONOutputFormat, begintime, startTimeStr, rp);
					
					if(FileOutputFormat != 0) {
						String resultForWrite = meteor.getScoreFromTextFile(CaseSensitive, RefFileName, CanFileName, FileOutputFormat, begintime, startTimeStr, rp);
						writeFile(resultForWrite, FileOutputPath);
						
					}
					
					//Delete Temp File
					File fileCan = new File(CanFileName); 
					fileCan.delete();
					File fileRef = new File(RefFileName);
					fileRef.delete();
					

				}
			}else if (MetricType.equals(METRIC_FMEASURE)) {
				com.omniscien.lsmetric.process.FmeasureScore fmeasure = new com.omniscien.lsmetric.process.FmeasureScore();
				
				
				if(InputType == 1) {
					result = fmeasure.getScoreFromTextFile(CaseSensitive, InputReference, InputCandidate, JSONOutputFormat, begintime, startTimeStr, rp);
					
					if(FileOutputFormat != 0) {
						String resultForWrite = fmeasure.getScoreFromTextFile(CaseSensitive, InputReference, InputCandidate, FileOutputFormat, begintime, startTimeStr, rp);
						writeFile(resultForWrite, FileOutputPath);
						
					}
				}else if (InputType == 2) {
					//CheckDirectory
					if(rp == null) {
						rp = new ReadProp("/omniscien/tools/wfs/metrics/metricConfig.json");
					}
					String tempDirectory = rp.getProp(Constant.temp_Foleder);
					File directory = new File(tempDirectory);
					if (! directory.exists()){
				        directory.mkdir();
				    }
					
					//Prepare Candidate File
					String CanFileName = tempDirectory+generateID();
					String[] canData = InputCandidate.split("\n");
					for(int i = 0; i<canData.length; i++ ) {
						writeFile(canData[i], CanFileName);
					}
					
					//Prepare Ref File
					String RefFileName = tempDirectory+generateID();
					String[] refData = InputReference.split("\n");
					for(int i = 0;i< refData.length; i++) {
						writeFile(refData[i], RefFileName);
					}
					
					result = fmeasure.getScoreFromTextFile(CaseSensitive, RefFileName, CanFileName, JSONOutputFormat, begintime, startTimeStr, rp);
					
					if(FileOutputFormat != 0) {
						String resultForWrite = fmeasure.getScoreFromTextFile(CaseSensitive, RefFileName, CanFileName, FileOutputFormat, begintime, startTimeStr, rp);
						writeFile(resultForWrite, FileOutputPath);
						
					}
					
					//Delete Temp File
					File fileCan = new File(CanFileName); 
					fileCan.delete();
					File fileRef = new File(RefFileName);
					fileRef.delete();
					

				}
			}else if (MetricType.equals(METRIC_TERP)) {
				//CheckDirectory
				if(rp == null) {
					rp = new ReadProp("/omniscien/tools/wfs/metrics/metricConfig.json");
				}
				String tempDirectory = rp.getProp(Constant.temp_Foleder);
				File directory = new File(tempDirectory);
				if (! directory.exists()){
			        directory.mkdir();
			    }
				com.omniscien.lsmetric.process.TerpScore terp = new com.omniscien.lsmetric.process.TerpScore();
				
				if(InputType == 1) {
					String CanFileName = tempDirectory+generateID();
					String RefFileName = tempDirectory+generateID();
					normalizeTerpFileInput(InputReference, CanFileName);
					normalizeTerpFileInput(InputCandidate, RefFileName);
					
					result = terp.getScoreFromTextFile(CaseSensitive, RefFileName, CanFileName, JSONOutputFormat, begintime, startTimeStr, rp);
				
					//Delete Temp File
					File fileCan = new File(CanFileName); 
					fileCan.delete();
					File fileRef = new File(RefFileName);
					fileRef.delete();
					
					if(FileOutputFormat != 0) {
						String resultForWrite = terp.getScoreFromTextFile(CaseSensitive, InputReference, InputCandidate, FileOutputFormat, begintime, startTimeStr, rp);
						writeFile(resultForWrite, FileOutputPath);
						
					}
				}else if (InputType == 2) {
					
					
					//Prepare Candidate File
					String CanFileName = tempDirectory+generateID();
					File canFile = new File(CanFileName);
					String[] canData = InputCandidate.split("\n");
					for(int i = 0; i<canData.length; i++ ) {
						int lineNum = i+1;
//						writeFile(canData[i], CanFileName);
						writeLineinFileForTERP(canData[i], CanFileName, canFile, lineNum);
					}
					
					//Prepare Ref File
					String RefFileName = tempDirectory+generateID();
					File refFile = new File(RefFileName);
					String[] refData = InputReference.split("\n");
					for(int i = 0;i< refData.length; i++) {
//						writeFile(refData[i], RefFileName);
						int lineNum = i+1;
						writeLineinFileForTERP(refData[i], RefFileName, refFile, lineNum);
					}
					
					result = terp.getScoreFromTextFile(CaseSensitive, RefFileName, CanFileName, JSONOutputFormat, begintime, startTimeStr, rp);
					
					if(FileOutputFormat != 0) {
						String resultForWrite = terp.getScoreFromTextFile(CaseSensitive, RefFileName, CanFileName, FileOutputFormat, begintime, startTimeStr, rp);
						writeFile(resultForWrite, FileOutputPath);
						
					}
					
					//Delete Temp File
					File fileCan = new File(CanFileName); 
					fileCan.delete();
					File fileRef = new File(RefFileName);
					fileRef.delete();
					

				}
			}
			
			
		}catch(Exception e) {
			throw e;
		}
		return result;
	}
	private void normalizeTerpFileInput(String inputRawFile, String newFileName) throws IOException {
		File newFile = new File(newFileName);
		FileInputStream InputStream = new FileInputStream(inputRawFile);
		DataInputStream in = new DataInputStream(InputStream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		StringBuffer inputBuf = new StringBuffer();
		try {
			String line = null;
			int lineNum = 0;
			while ((line = br.readLine()) != null){
				lineNum++;
				writeLineinFileForTERP(line, newFileName, newFile, lineNum);
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
		
	}

	/*** Common method for create new file ***/
	public void writeFile(String result, String outFilePath) {
		File file = new File(outFilePath);	
		try {
			if(file.createNewFile()) {
				writeLineinFile(result, outFilePath, file);
			}else {
				writeLineinFile(result, outFilePath, file);
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	/*** Common method for write file ***/
	private void writeLineinFile(String result, String outFilePath, File file) {
		
		FileWriter writer;
		try {
			writer = new FileWriter(file, true);
			writer.write(result+"\n");
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	private void writeLineinFileForTERP(String result, String outFilePath, File file, int lineNum) {
		
		FileWriter writer;
		try {
			writer = new FileWriter(file, true);
			writer.write(result+" (LINE"+lineNum+")"+"\n");
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	/*** Common generate id ***/
	public String generateID() {
		String idStr = new String();
		idStr = UUID.randomUUID().toString();
		return idStr;
	}
	
	private String getErrorJSON(int errorNo) {
		endtime = System.currentTimeMillis();
		totaltime = endtime-begintime;
		endTimeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss:SSS")).toString();
		
		String ErrorMSG = com.omniscien.lib.util.Message.getMessage(errorNo);
		String result = "{\"result\":\"\","
			
				+ "\"duration\":\""+totaltime+"\","
				+ "\"startdate\":\""+startTimeStr+"\","
				+ "\"errortext\":\""+ErrorMSG+"\","
				+ "\"errorno\":\""+errorNo+"\","
				+ "\"enddate\":\""+endTimeStr+"\","
				+ "\"requestid\":\"\"}";
		
		return result;
	}

//	public double calculateBleuString(String ref, String cand, boolean isCaseSensitive) {
//		double result = 0;
//		if (!isCaseSensitive) {
//			ref = ref.toLowerCase();
//			cand = cand.toLowerCase();
//		}
//		com.omniscien.lsmetric.process.MultiBleuScorer mb = new com.omniscien.lsmetric.process.MultiBleuScorer();
//
//		// Get Result
//		try {
//			result = mb.getScore(ref, cand) * 100;
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return result;
//	}
//
//	public int calculateLevenString(String ref, String cand, boolean isCaseSensitive) {
//		if (!isCaseSensitive) {
//			ref = ref.toLowerCase();
//			cand = cand.toLowerCase();
//		}
//		com.omniscien.lsmetric.process.LevenshteinDistance lv = new com.omniscien.lsmetric.process.LevenshteinDistance();
//		int leven = 0;
//		lv.setS1(ref);
//		lv.setS2(cand);
//		leven = lv.getDistance();
//		return leven;
//
//	}
//
//	public double calculateTERString(String ref, String cand, boolean isCaseSensitive) {
//		
//		double result = 999;
//		
//		if (!isCaseSensitive) {
//			ref = ref.toLowerCase();
//			cand = cand.toLowerCase();
//		}
//
//		TERcalc.setCase(isCaseSensitive);
//
//		com.ao.ter.TERalignment terAlign = TERcalc.TER(cand, ref);
//
//		Double dScore = terAlign.score();
//
//		if (dScore >= 1) {
//			result = 100;
//		} else {
//			result = dScore * 100;
//		}
//		return result;
//	}
//	
//	public double calculateWERString(String ref, String cand, boolean isCaseSensitive) {
//		
//		if (!isCaseSensitive) {
//			ref = ref.toLowerCase();
//			cand = cand.toLowerCase();
//		}
//		com.omniscien.lsmetric.process.WordErrorRate wer = new com.omniscien.lsmetric.process.WordErrorRate(ref, cand);
//		
//		Double dScore = wer.getScore();
//		if (dScore >= 1) {
//			return 100;
//		}else {
//			return dScore * 100;
//		}
//		
//		
//	}
//	
//	public double calFMeasurementString(String ref, String cand,
//			boolean isCaseSensitive) throws Exception {
//
//		if (!isCaseSensitive) {
//			ref = ref.toLowerCase();
//			cand = cand.toLowerCase();
//		}
//		double cumulativeF1 = 0;
//		Set<String> refSet = new HashSet<String>();
//		Set<String> candSet = new HashSet<String>();
//		String[] splitRefArray,splitCandArray ;
//
//		splitRefArray = ref.split("\\s+");
//		splitCandArray = cand.split("\\s+");
//		refSet.addAll(Arrays.asList(splitRefArray));
//		candSet.addAll(Arrays.asList(splitCandArray));
//
//		FMeasure fm = new FMeasure();
//		fm.updateScores(refSet.toArray(), candSet.toArray());
//		cumulativeF1 = fm.getFMeasure();
//		return cumulativeF1;
//	}

}
