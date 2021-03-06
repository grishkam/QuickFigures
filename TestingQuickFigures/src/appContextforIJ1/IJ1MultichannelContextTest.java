/**
 * Author: Greg Mazo
 * Date Modified: Dec 20, 2020
 * Version: 2021.1
 */
package appContextforIJ1;


import org.junit.jupiter.api.Test;
import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;

/**test to determine if the methods in the IJ1MultichannelContext class 
 * really reflect what imagej has open*/
class IJ1MultichannelContextTest {

	@Test
	void test() {
		IJ1MultichannelContext c = new IJ1MultichannelContext();
		ImageDisplayTester.setupImageJ();
		
		WindowManager.closeAllWindows();
		//test getter method for all visible image 
		assert(c.getallVisibleMultichanal().size()==0);//if test is run in isolation, size will be 0
		ImagePlus i = IJ.createHyperStack("b", 400, 300, 3, 1, 1, 16);
		i.show();
		assert(c.getallVisibleMultichanal().size()==1);
		assert(c.getCurrentMultichanal().getTitle().equals("b"));
		i = IJ.createHyperStack("d", 400, 300, 3, 1, 1, 16);
		i.show();
		assert(c.getallVisibleMultichanal().size()==2);
		
		
		//test current image
		assert(c.getCurrentMultichanal().getTitle().equals("d"));
		i.close();
		assert(c.getCurrentMultichanal().getTitle().equals("b"));
		
		
		assert(c.createMultichannelDisplay()!=null);
		assert(c.getMultichannelOpener()!=null);
		
	}

}
