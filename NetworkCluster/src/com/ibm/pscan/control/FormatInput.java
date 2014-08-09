package com.ibm.pscan.control;

import java.io.IOException;

import com.ibm.pscan.dataHelper.GetParticipant;

/**
 * Get the participant information from the input file
 * @author Nancy
 *
 */
public class FormatInput {
	
	public static void main(String[] args) throws IOException {
		GetParticipant getParti=GetParticipant.getInstance();
		getParti.relationship();
	}
}
