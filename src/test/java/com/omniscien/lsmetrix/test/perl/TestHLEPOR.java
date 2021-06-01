package com.omniscien.lsmetrix.test.perl;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.omniscien.lsmetric.util.StreamGobblerWithOutput;

public class TestHLEPOR {

	public TestHLEPOR() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		String hleporScript = "/omniscien/tools/wfs/metrics/hlepor/hlepor_stdin.pl";
		String refOption = "-ref";
		String refFile = "/home/chanwit/Documents/LSMetrix/D.Test/1_BLEU/Ref.txt";
		String canOption = "-cand";
		String candFile = "/home/chanwit/Documents/LSMetrix/D.Test/1_BLEU/Can.txt";
		
		ExecutorService _executor = Executors.newSingleThreadExecutor();
		StringBuilder sbError = new StringBuilder();
		StringBuilder sbResult = new StringBuilder();
		String[] commands = {hleporScript, refOption, refFile, canOption, candFile};
		
		ProcessBuilder proc = new ProcessBuilder(commands);
		proc.redirectErrorStream(false); // setting  true
		
		Process _proc = proc.start();
		
		StreamGobblerWithOutput _errorStreamGobbler = new StreamGobblerWithOutput("ErrorStreamST", _proc.getErrorStream());
		StreamGobblerWithOutput _inputStreamGobbler = new StreamGobblerWithOutput("InputStreamST", _proc.getInputStream(), _proc.getOutputStream(), "test\teating");
		
		Thread tInput = new Thread(_inputStreamGobbler);
		Thread tError = new Thread(_errorStreamGobbler);
		
		tError.start();
		tInput.start();
		tInput.join();
		
		sbError = _errorStreamGobbler.getOutputBuffer();
		sbResult =_inputStreamGobbler.getOutputBuffer();

		String result = sbResult.toString();
		String[] resultArr = result.split("\n");
		String Sumresult = "";
		for(int i = 0; i< resultArr.length; i++) {
			if(resultArr[i].trim().equals("evaluation value HLEPOR of every single sentence:")) {
				Sumresult = resultArr[i+1];
				break;
			}
		}
		
		
		
		System.out.println("Sum Result: "+Sumresult);
	}

}
