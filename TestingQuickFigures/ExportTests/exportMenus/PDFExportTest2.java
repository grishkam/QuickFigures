/**
 * Author: Greg Mazo
 * Date Modified: Feb 4, 2021
 * Copyright (C) 2021 Gregory Mazo
 * 
 */
package exportMenus;

/**
 
 * 
 */
public class PDFExportTest2 extends QuickExportTest {

	/**
	 * @return
	 */
	QuickExport createExporter() {
		return new PDFQuickExport(false);
	}

}
