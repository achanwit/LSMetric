package com.omniscien.lsmetrix.test.perl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.omniscien.lsmetric.util.StreamGobblerWithOutput;
import com.omniscien.lsmetrix.test.perl.TestBLEU.WorkerStatus;

public class TestTERP {
	
	private static Process _proc = null;
	private static StreamGobblerWithOutput _errorStreamGobbler;
	private static StreamGobblerWithOutput _inputStreamGobbler;
	private static WorkerStatus _workerStatus;
	private static ExecutorService _executor;

	public TestTERP() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception {
		boolean casSen = false;
		
		String javaStr1 = "java";
		String javaStr2 = "-jar";
		String terpJar = "/omniscien/tools/wfs/metrics/terp/tercom.7.25.jar";
		String caseSenOption = "-s";
		String rOption = "-r";
		String refFile = "/home/chanwit/Documents/LSMetrix/INTEGRATION/TERP/n_ref.txt";

		String hOption = "-h";
		String candFile = "/home/chanwit/Documents/LSMetrix/INTEGRATION/TERP/n_cand.txt";

		
		

		ExecutorService _executor = Executors.newSingleThreadExecutor();
		StringBuilder sbError = new StringBuilder();
		StringBuilder sbResult = new StringBuilder();
//		
//		String[] commandsCaseSensitive = {javaStr1, javaStr2, terpJar, "-N", "-s", rOption, refFile, hOption, candFile, "-n", "output", ">", "output"};
//		String[] commandsIncaseSensitive = {javaStr1, javaStr2, terpJar, rOption, refFile, hOption, candFile, "-n", "/home/chanwit/Documents/LSMetrix/INTEGRATION/TERP/output/home/chanwit/Documents/LSMetrix/INTEGRATION/TERP/output", ">", "/home/chanwit/Documents/LSMetrix/INTEGRATION/TERP/output"};
		String[] commands = {javaStr1, javaStr2, terpJar,rOption, refFile, hOption, candFile, "-o", "sum_nbest","-n","/home/chanwit/Documents/LSMetrix/INTEGRATION/TERP/yo"};		
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
		
		String rawResult = rawOutput.toString();

//		System.out.println(rawResult);
		
		//Read Temp File
				List resultList = new ArrayList<String>();
				FileInputStream InputStream = new FileInputStream("/home/chanwit/Documents/LSMetrix/INTEGRATION/TERP/yo"+".sum_nbest");
				DataInputStream in = new DataInputStream(InputStream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				StringBuffer inputBuf = new StringBuffer();

				try {
					String line = null;
					while ((line = br.readLine()) != null){
						
						
						String terpScoreStr = line.substring(60).trim();
//						System.out.println(terpScoreStr);
						double terpScore = Double.valueOf(terpScoreStr);
						DecimalFormat df = new DecimalFormat("#.##");
						terpScoreStr = df.format(terpScore);
						terpScore = Double.valueOf(terpScoreStr);
						System.out.println(terpScore);
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

	private static double normalizeROUGEOutputSummary(String rawResult) {
		String lineStr = "^Total TER:.*";
		
		Pattern linePattern = Pattern.compile(lineStr, Pattern.MULTILINE);
		Matcher lineMatcher = linePattern.matcher(rawResult);
		String line = "";
		while (lineMatcher.find()) {
			line = lineMatcher.group(0);
//			String[] linePieces = line.split("\t");
			System.out.println(line);
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

}
