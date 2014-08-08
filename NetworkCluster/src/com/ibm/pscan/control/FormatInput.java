package com.ibm.pscan.control;

import java.io.IOException;

import com.ibm.pscan.dataHelper.GetParticipant;

public class FormatInput {
	
	public static void main(String[] args) throws IOException {
		GetParticipant getParti=GetParticipant.getInstance();
		getParti.relationship();
	}
}
