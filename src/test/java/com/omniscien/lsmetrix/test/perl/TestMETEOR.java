package com.omniscien.lsmetrix.test.perl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.omniscien.lsmetric.util.Constant;
import com.omniscien.lsmetric.util.StreamGobblerWithOutput;

public class TestMETEOR {

	public TestMETEOR() throws IOException, InterruptedException {
		
	}

	public static void main(String[] args) throws Exception {
		boolean casSen = false;
		
		String javaStr1 = "java";
		String javaStr2 = "-jar";
		String meteorJar = "/omniscien/tools/wfs/metrics/meteor/meteor-1.5.jar";
		String caseOption1 = "-l";
		String caseOption2 = "other";
		String inputCandidate = "/home/chanwit/Documents/LSMetrix/D.Test/1_BLEU/Can.txt";
		String inputReference = "/home/chanwit/Documents/LSMetrix/D.Test/1_BLEU/Ref.txt";
		
			
		
		
		ExecutorService _executor = Executors.newSingleThreadExecutor();
		StringBuilder sbError = new StringBuilder();
		StringBuilder sbResult = new StringBuilder();
		
		String[] commandsIncaseSensitive = {javaStr1, javaStr2, meteorJar, inputCandidate, inputReference, caseOption1, caseOption2};
		String[] commandsCaseSensitive = {javaStr1, javaStr2, meteorJar, inputCandidate, inputReference };
//		ProcessBuilder proc = new ProcessBuilder(commands);
//		proc.redirectErrorStream(false); // setting  true
//		
//		Process _proc = proc.start();
//		
//		StreamGobblerWithOutput _errorStreamGobbler = new StreamGobblerWithOutput("ErrorStreamST", _proc.getErrorStream());
//		StreamGobblerWithOutput _inputStreamGobbler = new StreamGobblerWithOutput("InputStreamST", _proc.getInputStream(), _proc.getOutputStream(), "test\teating");
//		
//		Thread tInput = new Thread(_inputStreamGobbler);
//		Thread tError = new Thread(_errorStreamGobbler);
//		tError.start();
//		tInput.start();
//		tInput.join();
//		
//		
//		sbError = _errorStreamGobbler.getOutputBuffer();
//		sbResult =_inputStreamGobbler.getOutputBuffer();
//		
//		
//
//		String result = sbResult.toString();
//		System.out.println(sbResult.toString());
		
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
//		return rawOutput.toString();
//		System.out.println(rawOutput.toString());
		
		String rawResult = rawOutput.toString();
		
		double SumScore = normalizeROUGEOutputSummary(rawResult);
//		System.out.println(SumScore);
	}

	private static double normalizeROUGEOutputSummary(String rawResult) {
//		String totalStr = "^Final score:.*";
//		Pattern totalPattern = Pattern.compile(totalStr, Pattern.MULTILINE);
//		Matcher totalMatcher = totalPattern.matcher(rawResult);
//		String total = "";
//		while (totalMatcher.find()) {
//			 total = totalMatcher.group(0);
//			
//		}
//		total = total.replace(" ", "");
////		System.out.println("total: "+total);
//		String[] totalArr = total.split(":");
//		String finalScoreStr = totalArr[1];
//		
//		double finalScoreDouble = Double.valueOf(finalScoreStr);
//		finalScoreDouble = finalScoreDouble*100;
//		finalScoreStr = String.valueOf(finalScoreDouble);
//		DecimalFormat df = new DecimalFormat("#.##");
//		String scoreFormat = df.format(Double.valueOf(finalScoreStr));
//		finalScoreDouble = Double.valueOf(scoreFormat);
//		return finalScoreDouble;
		
		String lineStr = "^Segment.*";
		
		Pattern linePattern = Pattern.compile(lineStr, Pattern.MULTILINE);
		Matcher lineMatcher = linePattern.matcher(rawResult);
		while (lineMatcher.find()) {
			String line = lineMatcher.group(0);
			String[] linePieces = line.split("\t");
			System.out.println(linePieces[1]);
		}
		
		return 0;
	}
}
