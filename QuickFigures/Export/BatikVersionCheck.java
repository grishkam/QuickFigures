/**
 * Author: Greg Mazo
 * Date Modified: Jun 29, 2025
 * Copyright (C) 2025 Gregory Mazo
 * 
 */
/**
 
 * 
 */
import org.apache.batik.*;

import logging.IssueLog;
/**
 
 * 
 */
public class BatikVersionCheck {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		IssueLog.log(Version.getVersion());
			System.out.println(Version.getVersion());
	}

}
