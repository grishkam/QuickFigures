/**
 * Author: Greg Mazo
 * Date Modified: Mar 28, 2021
 * Version: 2021.1
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
