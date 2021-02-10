/**
 * Author: Greg Mazo
 * Date Modified: Dec 20, 2020
 * Copyright (C) 2020 Gregory Mazo
 * 
 */

package genericMontageLayoutToolKit;

import org.junit.Test;

import icons.IconSet;
import layout.basicFigure.LayoutSpaces;
import logging.IssueLog;

/**
written to determine whether icon objects generated by code are really generated faster than those
loaded from jpeg files. these icons from this test are no longer used.
jpeg files are used for relatively few icons
 */
public class RowSwapperToolIconTest {

	@Test
	public void test() {
		long time=System.currentTimeMillis();
		new RowSwapperToolIcon(0, LayoutSpaces.COLS).generateIconSet();
		
		IssueLog.sytemprint=true;
		IssueLog.log("time taken for new tool creates "+(System.currentTimeMillis()-time));
		
		time=System.currentTimeMillis();
		new IconSet("icons/ColumnSwapperIcon.jpg","icons/ColumnSwapperPressIcon.jpg","icons/ColumnSwapperRollIcon.jpg");
		IssueLog.log("time taken for old tool creates "+(System.currentTimeMillis()-time));
		
		
		time=System.currentTimeMillis();
		new RowSwapperToolIcon(0, LayoutSpaces.ROWS).generateIconSet();
		
		IssueLog.sytemprint=true;
		IssueLog.log("time taken for new tool creates second time "+(System.currentTimeMillis()-time));
		
		time=System.currentTimeMillis();
		new IconSet("icons/ColumnSwapperIcon.jpg","icons/ColumnSwapperPressIcon.jpg","icons/ColumnSwapperRollIcon.jpg");
		IssueLog.log("time taken for old tool creates second time "+(System.currentTimeMillis()-time));
		
	}

}
