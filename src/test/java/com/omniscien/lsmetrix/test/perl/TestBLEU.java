package com.omniscien.lsmetrix.test.perl;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.omniscien.lsmetric.util.StreamGobblerWithOutput;

import org.apache.commons.lang.StringUtils;

public class TestBLEU {
	
	private static Process _proc = null;
	private static StreamGobblerWithOutput _errorStreamGobbler;
	private static StreamGobblerWithOutput _inputStreamGobbler;
	private static WorkerStatus _workerStatus;
	private static ExecutorService _executor;

	public TestBLEU() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Worker Status
	 */
	public enum WorkerStatus {
		STOPPED, LOADING, RUNNING, ERROR
	}

	/**
	 * Get Worker Status
	 */
	public WorkerStatus status() {
		return _workerStatus;
	}

	public static void main(String[] args) throws Exception {
		String python3 = "python3";
		String RIBES = "/home/chanwit/Documents/LSMetrix/INTEGRATION/RIBES/RIBES.py";
		String referArg = "-r";
		String RefFile = "/home/chanwit/Documents/LSMetrix/D.Test/1_BLEU/Ref.txt";
		String CanFile = "/home/chanwit/Documents/LSMetrix/D.Test/1_BLEU/Can.txt";
		String s = "-s";
		String c = "-c";

		try {
			_executor = Executors.newSingleThreadExecutor();
			
			StringBuilder sbError = new StringBuilder();
			StringBuilder sbResult = new StringBuilder();
			String[] commands = {python3, RIBES,  referArg, RefFile, CanFile, s, c};
//								, "/home/chanwit/Documents/LSMetrix/A.Prestudy/multi-bleu.perl"
////								, "-lc", CanFile, "<", RefFile};
//			Runtime oRuntime = Runtime.getRuntime();
//			Process oProcess = null;
//
//			oProcess = oRuntime.exec(aCmdArgs);
//			 oProcess.waitFor();
//			 
//			 /* dump output stream */
//		        BufferedReader is = new BufferedReader
//		            ( new InputStreamReader(oProcess.getInputStream()));
//		        String sLine;
//		        while ((sLine = is.readLine()) != null) {
//		            System.out.println(sLine);
//		        }
//		        System.out.flush();
//
//		        /* print final result of process */
//		        System.err.println("Exit status=" + oProcess.exitValue());
			
			ProcessBuilder proc = new ProcessBuilder(commands);
			proc.redirectErrorStream(false); // setting  true
			_proc = proc.start();
	
			// Fix issue #36 Deadlock if SentenceJoin writes to stderr
			_inputStreamGobbler = new StreamGobblerWithOutput("InputStreamST", _proc.getInputStream(), _proc.getOutputStream(), "test\teating");
			_errorStreamGobbler = new StreamGobblerWithOutput("ErrorStreamST", _proc.getErrorStream());
			Thread tInput = new Thread(_inputStreamGobbler);
			Thread tError = new Thread(_errorStreamGobbler);
			
			tError.start();
			tInput.start();
			tInput.join();
			
			sbError = _errorStreamGobbler.getOutputBuffer();
			sbResult =_inputStreamGobbler.getOutputBuffer();

			String result = sbResult.toString();
			String[] resultArr = result.split("\n");
			System.out.println(result);
			System.out.println("[0]: "+resultArr[0]);
			
			if (sbError.length() > 0) {
//			    // #55 to skip the "This binary file contains trie with quantization and array-compressed pointers." from KenLM.
//				// and check error with system exit code.
				String sTemp = sbError.toString();
////				String result = sbResult.toString();
				sTemp = sTemp.replaceAll("(This binary file contains trie with quantization and array\\-compressed pointers\\.)(\\n*)", "");
				if (!_proc.isAlive() && !StringUtils.isEmpty(sTemp) && _proc.exitValue() != 0)
					throw new Exception(sTemp.toString());
				else {
					_workerStatus = WorkerStatus.RUNNING;
				}
			}else {
				// success
				_workerStatus = WorkerStatus.RUNNING;
			}
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		_executor.shutdown();
	}

}
