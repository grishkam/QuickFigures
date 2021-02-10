/**
 * Author: Greg Mazo
 * Date Modified: Dec 3, 2020
 * Copyright (C) 2020 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package appContext;


import org.junit.Test;

import appContextforIJ1.IJ1MultichannelContext;

/**

 */
public class CurrentAppContextTest {

	@Test
	public void test() {
		CurrentAppContext.setMultichannelContext(new IJ1MultichannelContext());
		
		assert(CurrentAppContext.getMultichannelContext()!=null);
		assert(CurrentAppContext.getDefaultDirectory()!=null);
		
	}

}
