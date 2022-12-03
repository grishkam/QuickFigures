/**
 * Author: Greg Mazo
 * Date Modified: Nov 20, 2022
 * Copyright (C) 2022 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package pdfImporter;

import logging.IssueLog;

/**
 
 * 
 */
public class PDF_importLog {
	static boolean logPDF=false;
	
	public static void log(String... args) {
		if(logPDF)
			IssueLog.log(args);
	}
}
