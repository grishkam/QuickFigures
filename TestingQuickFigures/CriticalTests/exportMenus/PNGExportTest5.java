/**
 * Author: Greg Mazo
 * Date Modified: Mar 28, 2021
 * Version: 2021.1
 */
package exportMenus;

/**runs the export tests for png files*/
public class PNGExportTest5 extends QuickExportTest {

	/**
	 * @return
	 */
	QuickExport createExporter() {
		return new PNGQuickExport(false);
	}

}
