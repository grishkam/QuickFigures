/**
 * Author: Greg Mazo
 * Date Modified: Feb 4, 2021
 * Copyright (C) 2021 Gregory Mazo
 * 
 */
package exportMenus;

import appContext.CurrentAppContext;
import appContextforIJ1.IJ1MultichannelContext;

/**
 
 * 
 */
public class TiffExportTest6 extends QuickExportTest {

	/**
	 * @return
	 */
	QuickExport createExporter() {
		CurrentAppContext.setMultichannelContext(new IJ1MultichannelContext());
		return new TiffQuickExport(false);
	}

}
