package imageDisplayApp;

import java.io.File;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import figureFormat.DirectoryHandler;
import graphicalObjects.ZoomableGraphic;
import ij.IJ;
import locatedObject.LocatedObject2D;
import logging.IssueLog;
import testing.FigureTester;
import testing.TestProvider;
import testing.TestShapes;
import ultilInputOutput.FileChoiceUtil;

/**needs additional examples*/
class ImageDisplayIOTest {

	@Test
	/**creates a series of example images, containing every type of object, shape, Text and so on
	  in a variety of forms. Generally, only 3 kinds of problems might be predicted if there is something wrong. 
	  1)a NonSerializable object can result in an exception or
	  2) the loss of a transient object might lead prevent the objects appearance or function from being normal
	  
	   The variety of test cases including all possible objects should trigger these if they can occur
	   */
	void test() {
		
		/***/
		for(int i: TestShapes.each) {
			testExampleImage(TestShapes.createExample( i));
		}
		TestProvider[] ex = FigureTester.getTests();
		for(TestProvider createExample: ex)testExampleImage(	(ImageWindowAndDisplaySet) createExample.createExample());
		;
	
	}

	private void testExampleImage(ImageWindowAndDisplaySet i) {
		String path = new DirectoryHandler().getTempFolderPath()+"testSave1.ser";
		 File f = new File(path);
		 
		 /**selects and deselects each to ensure that selection history does not affect serialization
		   during manual testing one class with not serializable handles can interfere with serialization
		   if it had been selected*/
		 for(LocatedObject2D l:i.getTheSet().getLocatedObjects()) {
			 l.select();
			 l.makePrimarySelectedItem(true);
			 i.updateDisplay();
			 IssueLog.waitMiliseconds(5);
			 l.deselect();
			
		 }
		 
		ImageDisplayIO.writeToFile(f, i.getTheSet());
	;
		
		
		IJ.wait(200);
		
		ImageWindowAndDisplaySet i2 = ImageDisplayIO.showFile(f);
		
		i.getWindow().setLocation(200, 20);
		i2.getWindow().setLocation(800, 20);
		i.setZoomLevel(1);
		i2.setZoomLevel(1);
		IJ.wait(400);
		assert(FileChoiceUtil.yesOrNo("One window contains the original example image, "+
				"the other a saved copy that was re opened. Do they appear to be the same?"));
		f.delete();
		
		compare(i, i2);
	}

	/**Checks to make sure the same number and kind of object is present*/
	private void compare(ImageWindowAndDisplaySet i, ImageWindowAndDisplaySet i2) {
		assert(i.getImageAsWrapper().getTitle().equals(i.getImageAsWrapper().getTitle()));
	
		ArrayList<ZoomableGraphic> c1 = i.getImageAsWrapper().getTopLevelLayer().getAllGraphics();
		ArrayList<ZoomableGraphic> c2 = i.getImageAsWrapper().getTopLevelLayer().getAllGraphics();
		assert(c2.size()==c1.size());
		for(int j=0; j<c2.size();j++) {
			ZoomableGraphic z1 = c1.get(j);
			ZoomableGraphic z2 = c2.get(j);
			compare2(z1, z2);
		}
	
	}

	/**
	a simple comparison of two objects. only meant to ensure that they are similar sorts of objects
	and does not check every field
	 */
	public void compare2(ZoomableGraphic z1, ZoomableGraphic z2) {
		assert(z1.getClass()==z2.getClass());
		assert(z1.toString().contentEquals(z2.toString()));
		
	}

}
