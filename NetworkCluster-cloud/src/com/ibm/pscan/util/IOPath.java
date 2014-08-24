package com.ibm.pscan.util;

public class IOPath {

	private static final String BASE_PATH=Config.BASE_PATH;
	
	/**
	 * For YammerIO.java & Unzip.java & YammerToAzure.java
	 */
	public static final String ZIP_BASE=Config.BASE_PATH+"/yammer";
	public static final String ZIP_FILE=ZIP_BASE+"/message.zip";
	public static final String UNZIP_FILE=ZIP_BASE+"/message/";;
	public static final String INPUT_FILE_PATH=UNZIP_FILE+"/Messages.csv";
	
	
	/**
	 * Input and Output Files for finding participant
	 */
	//For GetParticipant.java
	public static final String GETPAR_INPUT=BASE_PATH+"ori/Messages.csv";
	
	public static final String[] COMMON_WORDS=
		{"a","an","am","about","above","after", "and","could","can","cool","him","itself",
		"any","couldnt","her","let", "are","did","here","me",
		"as","do","hers","more","at","does","herself","my","be","doing","him","no",
		"because","down","himself","nor","been","during","his","not","before","each","how","off",
		"being","for","i","of","below","from","im","on","between","had","if","or","both","have","in","other",
		"but","has","into","our","by","he","is","ours","out","should","that","themselves",
		"over","so","the","then","own","some","their","there","same","such","theirs","theres",
		"she","than","them","these","they","this","those","to","wasnt","was","very","too",
		"way","were","what","when","who","while","which","where","whos","whom","why","with",
		"would","you","your","yours","wont","wouldnt","yourselves","yourself","done","we","u","up",
		"will","really","well","like","cannot","it","its","ourselves","please","ll","*","-","just","us","ur"};

	
}
