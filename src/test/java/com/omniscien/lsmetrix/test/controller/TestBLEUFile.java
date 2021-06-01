package com.omniscien.lsmetrix.test.controller;

public class TestBLEUFile {

	public TestBLEUFile() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		com.omniscien.lsmetric.controller.Metrics metric = new com.omniscien.lsmetric.controller.Metrics();
		
//		String inputFileRef = "This is a book .  Those are books . \n Those are books . This is a book . ";
//		String inputFileCan = "This is a books .  This is a Books . \n This is a books . This is a Book . ";
		
		String inputFileRef = "/home/chanwit/Documents/LSMetrix/D.Test/1_BLEU/Ref.txt";
		String inputFileCan = "/home/chanwit/Documents/LSMetrix/D.Test/1_BLEU/Can.txt";
		
//		String result = metric.Metric(metric.METRIC_HLEPOR, 1, inputFileRef, inputFileCan,1,4, null);
		String result = metric.Metric(
//				String MetricType, 
				metric.METRIC_BLEU,
//				int InputType, 
				1,
//				String InputReference, 
				inputFileRef,
//				String InputCandidate,
				inputFileCan,
//				int CaseSensitive, 
				1,
//				String MetricOptions,
				null,
//				int JSONOutputFormat,
				4,
//				int FileOutputFormat, 
				6,
//				String FileOutputPath
				"/home/chanwit/Documents/LSMetrix/D.Test/output/BLEU.txt"
				);

		System.out.println(result);
	}

}
