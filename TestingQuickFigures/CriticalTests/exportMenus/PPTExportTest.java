/**
 * Author: Greg Mazo
 * Date Modified: Feb 4, 2021
 * Copyright (C) 2021 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package exportMenus;

import logging.IssueLog;

/**
 
 * 
 */
public class PPTExportTest extends QuickExportTest {

	/**
	 * @return
	 */
	QuickExport createExporter() {
		IssueLog.sytemprint=true;
		return new PPTQuickExport(false);
	}

}
