package com.omniscien.lsmetric.process;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
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

public class WordErrorRate {
	
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
	
	public WordErrorRate(String s1, String s2) {
		this.s1 = s1;
		this.s2 = s2;
	}
	
	public WordErrorRate() {
		// Properties
		if (rp == null) {
			rp = new ReadProp("/omniscien/tools/wfs/metrics/metricConfig.properties");
		}
	}
	
	public Double getScore(String s1, String s2) {
		try {
			WordSequenceAligner werEval = new WordSequenceAligner();
//		    String [] ref = this.s1.split(" ");
//		    String [] hyp = this.s2.split(" ");
		    String [] ref = s1.split(" ");
		    String [] hyp = s2.split(" ");
		    WordSequenceAligner.Alignment aData = werEval.align(ref, hyp);
		    Float fWERScore = (aData.numSubstitutions + aData.numInsertions + aData.numDeletions) / (float) aData.getReferenceLength();
			return Double.valueOf(fWERScore);			
		} catch (Exception e) {
			return 0.0;
		}
	}
	
	public class WordSequenceAligner{
		/** Cost of a substitution string edit operation applied during alignment. 
		 * From edu.cmu.sphinx.util.NISTAlign, which should be referencing the NIST sclite utility settings. */
		public static final int DEFAULT_SUBSTITUTION_PENALTY = 100;
		
		/** Cost of an insertion string edit operation applied during alignment. 
		 * From edu.cmu.sphinx.util.NISTAlign, which should be referencing the NIST sclite utility settings. */
		public static final int DEFAULT_INSERTION_PENALTY = 75;
		
		/** Cost of a deletion string edit operation applied during alignment. 
		 * From edu.cmu.sphinx.util.NISTAlign, which should be referencing the NIST sclite utility settings. */
		public static final int DEFAULT_DELETION_PENALTY = 75;

		/** Substitution penalty for reference-hypothesis string alignment */
		private final int substitutionPenalty;
				 
		/** Insertion penalty for reference-hypothesis string alignment */
		private final int insertionPenalty;
		
	    /** Deletion penalty for reference-hypothesis string alignment */
		private final int deletionPenalty;
		
		
		/**
		 * Result of an alignment.
		 * Has a {@link #toString()} method that pretty-prints human-readable metrics.
		 *  
		 * @author romanows
		 */
		public class Alignment {
			/** Reference words, with null elements representing insertions in the hypothesis sentence and upper-cased words representing an alignment mismatch */
			public final String [] reference;
			
			/** Hypothesis words, with null elements representing deletions (missing words) in the hypothesis sentence and upper-cased words representing an alignment mismatch */
			public final String [] hypothesis;
			
			/** Number of word substitutions made in the hypothesis with respect to the reference */
			public final int numSubstitutions;
			
			/** Number of word insertions (unnecessary words present) in the hypothesis with respect to the reference */
			public final int numInsertions;
			
			/** Number of word deletions (necessary words missing) in the hypothesis with respect to the reference */
			public final int numDeletions;
		
			
			/**
			 * Constructor.
			 * @param reference reference words, with null elements representing insertions in the hypothesis sentence
			 * @param hypothesis hypothesis words, with null elements representing deletions (missing words) in the hypothesis sentence
			 * @param numSubstitutions Number of word substitutions made in the hypothesis with respect to the reference 
			 * @param numInsertions Number of word insertions (unnecessary words present) in the hypothesis with respect to the reference
			 * @param numDeletions Number of word deletions (necessary words missing) in the hypothesis with respect to the reference
			 */
			public Alignment(String [] reference, String [] hypothesis, int numSubstitutions, int numInsertions, int numDeletions) {
				if(reference == null || hypothesis == null || reference.length != hypothesis.length || numSubstitutions < 0 || numInsertions < 0 || numDeletions < 0) {
					throw new IllegalArgumentException();
				}
				this.reference = reference;
				this.hypothesis = hypothesis;
				this.numSubstitutions = numSubstitutions;
				this.numInsertions = numInsertions;
				this.numDeletions = numDeletions;
			}
			
			/**
			 * Number of word correct words in the aligned hypothesis with respect to the reference.
			 * @return number of word correct words 
			 */
			public int getNumCorrect() {
				return getHypothesisLength() - (numSubstitutions + numInsertions);  // Substitutions are mismatched and not correct, insertions are extra words that aren't correct
			}
			
			/** @return true when the hypothesis exactly matches the reference */
			public boolean isSentenceCorrect() {
				return numSubstitutions == 0 && numInsertions == 0 && numDeletions == 0;
			}
			
			/**
			 * Get the length of the original reference sequence.
			 * This is not the same as {@link #reference}.length(), because that member variable may have null elements 
			 * inserted to mark hypothesis insertions.
			 * 
			 * @return the length of the original reference sequence
			 */
			public int getReferenceLength() {
				return reference.length - numInsertions;
			}
			
			/**
			 * Get the length of the original hypothesis sequence.
			 * This is not the same as {@link #hypothesis}.length(), because that member variable may have null elements 
			 * inserted to mark hypothesis deletions.
			 * 
			 * @return the length of the original hypothesis sequence
			 */
			public int getHypothesisLength() {
				return hypothesis.length - numDeletions;
			}
			
			/*
			 * (non-Javadoc)
			 * @see java.lang.Object#toString()
			 */
			@Override
			public String toString() {
				StringBuilder ref = new StringBuilder();
				StringBuilder hyp = new StringBuilder();
				ref.append("REF:\t");
				hyp.append("HYP:\t");
				for(int i=0; i<reference.length; i++) {
					if(reference[i] == null) {
						for(int j=0; j<hypothesis[i].length(); j++) {
							ref.append("*");
						}
					} else {
						ref.append(reference[i]);
					}
					
					if(hypothesis[i] == null) {
						for(int j=0; j<reference[i].length(); j++) {
							hyp.append("*");
						}
					} else {
						hyp.append(hypothesis[i]);
					}

					if(i != reference.length - 1) {
						ref.append("\t");
						hyp.append("\t");
					}
				}
				
				StringBuilder sb = new StringBuilder();
				sb.append("\t");
				sb.append("# seq").append("\t");
				sb.append("# ref").append("\t");
				sb.append("# hyp").append("\t");
				sb.append("# cor").append("\t");
				sb.append("# sub").append("\t");
				sb.append("# ins").append("\t");
				sb.append("# del").append("\t");
				sb.append("acc").append("\t");
				sb.append("WER").append("\t");
				sb.append("# seq cor").append("\t");

				sb.append("\n");
				sb.append("STATS:\t");
				sb.append(1).append("\t");
				sb.append(getReferenceLength()).append("\t");
				sb.append(getHypothesisLength()).append("\t");
				sb.append(getNumCorrect()).append("\t");
				sb.append(numSubstitutions).append("\t");
				sb.append(numInsertions).append("\t");
				sb.append(numDeletions).append("\t");
				sb.append(getNumCorrect() / (float) getReferenceLength()).append("\t");
				sb.append((numSubstitutions + numInsertions + numDeletions) / (float) getReferenceLength()).append("\t");
				sb.append(isSentenceCorrect() ? 1 : 0);

				sb.append("\n");
				sb.append("-----\t");
				sb.append("-----\t");
				sb.append("-----\t");
				sb.append("-----\t");
				sb.append("-----\t");
				sb.append("-----\t");
				sb.append("-----\t");
				sb.append("-----\t");
				sb.append("-----\t");
				sb.append("-----\t");
				sb.append("-----\t");

				sb.append("\n");
				sb.append(ref).append("\n").append(hyp);
							
				return sb.toString();
			}
		}
		
		
		/**
		 * Collects several alignment results.
		 * Has a {@link #toString()} method that pretty-prints a human-readable summary metrics for the collection of results.
		 *  
		 * @author romanows
		 */
		public class SummaryStatistics {
			/** Number of correct words in the aligned hypothesis with respect to the reference */
			private int numCorrect;

			/** Number of word substitutions made in the hypothesis with respect to the reference */
			private int numSubstitutions;
			
			/** Number of word insertions (unnecessary words present) in the hypothesis with respect to the reference */
			private int numInsertions;
			
			/** Number of word deletions (necessary words missing) in the hypothesis with respect to the reference */
			private int numDeletions;
			
			/** Number of hypotheses that exactly match the associated reference */
			private int numSentenceCorrect;

			/** Total number of words in the reference sequences */
			private int numReferenceWords;
			
			/** Total number of words in the hypothesis sequences */
			private int numHypothesisWords;
			
			/** Number of sentences */
			private int numSentences;
			
			
			/**
			 * Constructor.
			 * @param alignments collection of alignments
			 */
			public SummaryStatistics(Collection<Alignment> alignments) {
				for(Alignment a : alignments) {
					add(a);
				}
			}
			
			/**
			 * Add a new alignment result
			 * @param alignment result to add
			 */
			public void add(Alignment alignment) {
				numCorrect += alignment.getNumCorrect();
				numSubstitutions += alignment.numSubstitutions;
				numInsertions += alignment.numInsertions;
				numDeletions += alignment.numDeletions;
				numSentenceCorrect += alignment.isSentenceCorrect() ? 1 : 0;
				numReferenceWords += alignment.getReferenceLength();
				numHypothesisWords += alignment.getHypothesisLength();
				numSentences++;
			}
			
			public int getNumSentences() {
				return numSentences;
			}

			public int getNumReferenceWords() {
				return numReferenceWords;
			}
			
			public int getNumHypothesisWords() {
				return numHypothesisWords;
			}
			
			public float getCorrectRate() {
				return numCorrect / (float) numReferenceWords;
			}
			
			public float getSubstitutionRate() {
				return numSubstitutions / (float) numReferenceWords;
			}

			public float getDeletionRate() {
				return numDeletions / (float) numReferenceWords;
			}

			public float getInsertionRate() {
				return numInsertions / (float) numReferenceWords;
			}
			
			/** @return the word error rate of this collection */
			public float getWordErrorRate() {
				return (numSubstitutions + numDeletions + numInsertions) / (float) numReferenceWords;
			}
			
			/** @return the sentence error rate of this collection */
			public float getSentenceErrorRate() {
				return (numSentences - numSentenceCorrect) / (float) numSentences;
			}
			
			/*
			 * (non-Javadoc)
			 * @see java.lang.Object#toString()
			 */
			@Override
			public String toString() {
				StringBuilder sb = new StringBuilder();
				sb.append("# seq").append("\t");
				sb.append("# ref").append("\t");
				sb.append("# hyp").append("\t");
				sb.append("cor").append("\t");
				sb.append("sub").append("\t");
				sb.append("ins").append("\t");
				sb.append("del").append("\t");
				sb.append("WER").append("\t");
				sb.append("SER").append("\t");
				sb.append("\n");

				sb.append(numSentences).append("\t");
				sb.append(numReferenceWords).append("\t");
				sb.append(numHypothesisWords).append("\t");
				sb.append(getCorrectRate()).append("\t");
				sb.append(getSubstitutionRate()).append("\t");
				sb.append(getInsertionRate()).append("\t");
				sb.append(getDeletionRate()).append("\t");
				sb.append(getWordErrorRate()).append("\t");
				sb.append(getSentenceErrorRate());
				return sb.toString();
			}
		}
		
		
		/**
		 * Constructor.
		 * Creates an object with default alignment penalties.  
		 */
		public WordSequenceAligner() {
			this(DEFAULT_SUBSTITUTION_PENALTY, DEFAULT_INSERTION_PENALTY, DEFAULT_DELETION_PENALTY);
		}
		
		
		/**
		 * Constructor. 
		 * @param substitutionPenalty substitution penalty for reference-hypothesis string alignment
		 * @param insertionPenalty insertion penalty for reference-hypothesis string alignment
		 * @param deletionPenalty deletion penalty for reference-hypothesis string alignment
		 */
		public WordSequenceAligner(int substitutionPenalty, int insertionPenalty, int deletionPenalty) {
			this.substitutionPenalty = substitutionPenalty;
			this.insertionPenalty = insertionPenalty;
			this.deletionPenalty = deletionPenalty;
		}
		
		
		/**
		 * Produce alignment results for several pairs of sentences.
		 * @see #align(String[], String[])
		 * @param references reference sentences to align with the given hypotheses 
		 * @param hypotheses hypothesis sentences to align with the given references
		 * @return collection of per-sentence alignment results
		 */
		public List<Alignment> align(List<String []> references, List<String []> hypotheses) {
			if(references.size() != hypotheses.size()) {
				throw new IllegalArgumentException();
			}
			if(references.size() == 0) {
				return new ArrayList<WordSequenceAligner.Alignment>();
			}
			
			List<Alignment> alignments = new ArrayList<WordSequenceAligner.Alignment>();
			Iterator<String[]> refIt = references.iterator();
			Iterator<String[]> hypIt = hypotheses.iterator();
			while(refIt.hasNext()) {
				alignments.add(align(refIt.next(), hypIt.next()));
			}
			return alignments;
		}

		
		/**
		 * Produces {@link Alignment} results from the alignment of the hypothesis words to the reference words.
		 * Alignment is done via weighted string edit distance according to {@link #substitutionPenalty}, {@link #insertionPenalty}, {@link #deletionPenalty}.
		 * 
		 * @param reference sequence of words representing the true sentence; will be evaluated as lowercase.
		 * @param hypothesis sequence of words representing the hypothesized sentence; will be evaluated as lowercase.
		 * @return results of aligning the hypothesis to the reference 
		 */
		public Alignment align(String [] reference, String [] hypothesis) {
			// Values representing string edit operations in the backtrace matrix
			final int OK = 0;  
			final int SUB = 1;
			final int INS = 2;
			final int DEL = 3;

			/* 
			 * Next up is our dynamic programming tables that track the string edit distance calculation.
			 * The row address corresponds to an index within the sequence of reference words.
			 * The column address corresponds to an index within the sequence of hypothesis words.
			 * cost[0][0] addresses the beginning of two word sequences, and thus always has a cost of zero.  
			 */
			
			/** cost[3][2] is the minimum alignment cost when aligning the first two words of the reference to the first word of the hypothesis */
			int [][] cost = new int[reference.length + 1][hypothesis.length + 1];
			
			/** 
			 * backtrace[3][2] gives information about the string edit operation that produced the minimum cost alignment between the first two words of the reference to the first word of the hypothesis.
			 * If a deletion operation is the minimum cost operation, then we say that the best way to get to hyp[1] is by deleting ref[2].
			 */
			int [][] backtrace = new int[reference.length + 1][hypothesis.length + 1];
			
			// Initialization
			cost[0][0] = 0;
			backtrace[0][0] = OK;
			
			// First column represents the case where we achieve zero hypothesis words by deleting all reference words.
			for(int i=1; i<cost.length; i++) {
				cost[i][0] = deletionPenalty * i;
				backtrace[i][0] = DEL; 
			}
			
			// First row represents the case where we achieve the hypothesis by inserting all hypothesis words into a zero-length reference.
			for(int j=1; j<cost[0].length; j++) {
				cost[0][j] = insertionPenalty * j;
				backtrace[0][j] = INS; 
			}

			// For each next column, go down the rows, recording the min cost edit operation (and the cumulative cost). 
			for(int i=1; i<cost.length; i++) {
				for(int j=1; j<cost[0].length; j++) {
					int subOp, cs;  // it is a substitution if the words aren't equal, but if they are, no penalty is assigned.
					if(reference[i-1].toLowerCase().equals(hypothesis[j-1].toLowerCase())) {
						subOp = OK;
						cs = cost[i-1][j-1];
					} else {
						subOp = SUB;
						cs = cost[i-1][j-1] + substitutionPenalty;
					}
					int ci = cost[i][j-1] + insertionPenalty;
					int cd = cost[i-1][j] + deletionPenalty;
					
					int mincost = Math.min(cs, Math.min(ci, cd));
					if(cs == mincost) {
						cost[i][j] = cs;
						backtrace[i][j] = subOp;
					} else if(ci == mincost) {
						cost[i][j] = ci;
						backtrace[i][j] = INS;					
					} else {
						cost[i][j] = cd;
						backtrace[i][j] = DEL;					
					}
				}
			}
			
			// Now that we have the minimal costs, find the lowest cost edit to create the hypothesis sequence
			LinkedList<String> alignedReference = new LinkedList<String>();
			LinkedList<String> alignedHypothesis = new LinkedList<String>();
			int numSub = 0;
			int numDel = 0;
			int numIns = 0;
			int i = cost.length - 1;
			int j = cost[0].length - 1;
			while(i > 0 || j > 0) {
				switch(backtrace[i][j]) {
					case OK: alignedReference.add(0, reference[i-1].toLowerCase()); alignedHypothesis.add(0,hypothesis[j-1].toLowerCase()); i--; j--; break;
					case SUB: alignedReference.add(0, reference[i-1].toUpperCase()); alignedHypothesis.add(0,hypothesis[j-1].toUpperCase()); i--; j--; numSub++; break;
					case INS: alignedReference.add(0, null); alignedHypothesis.add(0,hypothesis[j-1].toUpperCase()); j--; numIns++; break;
					case DEL: alignedReference.add(0, reference[i-1].toUpperCase()); alignedHypothesis.add(0,null); i--; numDel++; break;
				}
			}
			
			return new Alignment(alignedReference.toArray(new String[] {}), alignedHypothesis.toArray(new String[] {}), numSub, numIns, numDel);
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
					resultSummaryModel.setMetric(Constant.METRIC_WER);
				}else if(CaseSensitive == 2) {
					scoreSummaryOnlyCSModel = new ScoreSummaryOnlyCSModel();
					resultSummaryCSModel = new ResultSummaryCSModel();
					scoreSummaryOnlyCSModel.setStartdate(startTimeStr);
					resultSummaryCSModel.setMetric(Constant.METRIC_WER);
				}else if(CaseSensitive == 3) {
					scoreSummaryOnlyCIModel = new ScoreSummaryOnlyCIModel();
					resultSummaryCIModel = new ResultSummaryCIModel();
					scoreSummaryOnlyCIModel.setStartdate(startTimeStr);
					resultSummaryCIModel.setMetric(Constant.METRIC_WER);
				}

			} else if (OutputFormat == 3) {
				if (CaseSensitive == 1) {
					scorePerLine = new ScorePerLineModel();
					resultPerLineOnlyModel = new ResultPerLineOnlyModel();
					detailNotIncludeTextModel = new DetailNotIncludeTextModel();
					resultPerLineOnlyModel.setMetric(Constant.METRIC_WER);
					scorePerLine.setStartdate(startTimeStr);
				}else if(CaseSensitive == 2) {
					scorePerLineCSModel = new ScorePerLineCSModel();
					resultPerLineOnlyCSModel = new ResultPerLineOnlyCSModel();
					detailNotIncludeTextCSModel = new DetailNotIncludeTextCSModel();
					resultPerLineOnlyCSModel.setMetric(Constant.METRIC_WER);
					scorePerLineCSModel.setStartdate(startTimeStr);
				}else if(CaseSensitive == 3) {
					scorePerLineCIModel = new ScorePerLineCIModel();
					resultPerLineOnlyCIModel = new ResultPerLineOnlyCIModel();
					detailNotIncludeTextCIModel = new DetailNotIncludeTextCIModel();
					resultPerLineOnlyCIModel.setMetric(Constant.METRIC_WER);
					scorePerLineCIModel.setStartdate(startTimeStr);
				}
			} else if (OutputFormat == 4) {
				if (CaseSensitive == 1) {
					scoreTotal = new ScoreTotalModel();
					resultTotalModel = new ResultModel();
					scoreTotal.setStartdate(startTimeStr);
					resultTotalModel.setMetric(Constant.METRIC_WER);
					
				}else if(CaseSensitive == 2) {
					scoreTotalCSModel = new ScoreTotalCSModel();
					resultCSModel = new ResultCSModel();
					scoreTotalCSModel.setStartdate(startTimeStr);
					resultCSModel.setMetric(Constant.METRIC_WER);
				}
				else if(CaseSensitive == 3) {
					scoreTotalCIModel = new ScoreTotalCIModel();
					resultCIModel = new ResultCIModel();
					scoreTotalCIModel.setStartdate(startTimeStr);
					resultCIModel.setMetric(Constant.METRIC_WER);
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
				resultRespondOnly.metric = Constant.METRIC_WER;
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
				resultRespondOnly.metric = Constant.METRIC_WER;
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

			if(OutputFormat == 2) {
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
						double scoreCS = getScore(InputReferenceList.get(i), InputCandidateList.get(i));
						double scoreCI = getScore(InputReferenceList.get(i).toLowerCase(), InputCandidateList.get(i).toLowerCase());
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
						double scoreCS = getScore(InputReferenceList.get(i), InputCandidateList.get(i));
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
						double scoreCI = getScore(InputReferenceList.get(i).toLowerCase(), InputCandidateList.get(i).toLowerCase());
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
			}else if(OutputFormat == 4) {
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
						double scoreCS = getScore(InputReferenceList.get(i), InputCandidateList.get(i));
						double scoreCI = getScore(InputReferenceList.get(i).toLowerCase(), InputCandidateList.get(i).toLowerCase());
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
						double scoreCS = getScore(InputReferenceList.get(i), InputCandidateList.get(i));
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
						double scoreCI = getScore(InputReferenceList.get(i).toLowerCase(), InputCandidateList.get(i).toLowerCase());
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
					double scoreCS = getScore(InputReferenceList.get(i), InputCandidateList.get(i));
					double scoreCI = getScore(InputReferenceList.get(i).toLowerCase(), InputCandidateList.get(i).toLowerCase());
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
			}else if(OutputFormat == 6){
				List<Double> cs = new ArrayList<Double>();
				List<Double> ci = new ArrayList<Double>();
				for(int i = 0;i<InputReferenceList.size();i++) {
					double scoreCS = getScore(InputReferenceList.get(i), InputCandidateList.get(i));
					double scoreCI = getScore(InputReferenceList.get(i).toLowerCase(), InputCandidateList.get(i).toLowerCase());
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
				double score = getScore(ref, can);
				scoreSum = scoreSum+score;
			}			
		}else {
			for(int i = 0; i < inputReferenceList.size(); i++) {
				String ref = inputReferenceList.get(i).toLowerCase();
				String can = inputCandidateList.get(i).toLowerCase();
				double score = getScore(ref, can);
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
