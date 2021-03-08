package imageDisplayApp;

import java.awt.event.KeyEvent;


import org.junit.jupiter.api.Test;

import ij.IJ;
import logging.IssueLog;
import messages.ShowMessage;
import testing.TestExample;
import testing.TestShapes;
import ultilInputOutput.FileChoiceUtil;

class GraphicSetDisplayWindowTest {

	@Test
	void test() {
		autoTest();
		
		ShowMessage.showMessages("Automated test of window passed "
				,"user will be shown instructions for manual test");
		//manualTest();//manual test is not often done. commented out so it will not be autmatically run

	}

	/**
	performs a set of tests that require input from the user
	 */
	void manualTest() {
		ImageWindowAndDisplaySet i = TestShapes.createExample(TestExample.DIVERSE_SHAPES);
		GraphicSetDisplayWindow w =i.getTheWindow();
		
	
		
		ShowMessage.showMessages("Instructions","An open window  with a white canvas should have appeared. To test: "
				,"Press the -/= keys to test zooming in and out");
		IJ.wait(4000);
		assert(FileChoiceUtil.yesOrNo("Testing: did the window appear and did the zoom work fine?"));
		
		for(int f=0; f<14; f++)i.zoom("In");
		w.setLocation(40,40);
		
		IssueLog.showMessage("if you zoom in enough, the windows size will grow to about 80% of the screen size"
				+ " scroll bars should appear. Try moving the scollbars to navigate");
		IJ.wait(10000);
		
		
		assert(FileChoiceUtil.yesOrNo("Testing: scroll bars work fine?"));
		
		/**side panels are a work in progress and do not appear in current version*/
		/**
		w.setUsesBuiltInSidePanel(true);
		assert(FileChoiceUtil.yesOrNo("A side panel with some icons should have appeared on the far left of the window?"));
		
		IJ.wait(10000);
		w.setUsesBuiltInSidePanel(false);
		assert(FileChoiceUtil.yesOrNo("The side panel should have vanished?"));
		*/
	}
	
	/**
	tests the zoom in and zoom out keyboard shortcuts. other keyboard shortcuts exist
	but are tested elsewhere
	 */
	void autoTest() {
		ImageWindowAndDisplaySet i = TestShapes.createExample(TestExample.DIVERSE_SHAPES);
		GraphicSetDisplayWindow w =i.getTheWindow();
		
		double startZoom=w.getZoomer().getZoomMagnification();
		simulateKeyStroke(i, '=', false);
		
		double endZoom=w.getZoomer().getZoomMagnification();
		assert(endZoom>startZoom);
		
		
		startZoom=w.getZoomer().getZoomMagnification();
		simulateKeyStroke(i, '-', false);
		endZoom=w.getZoomer().getZoomMagnification();
		assert(endZoom<startZoom);
		
		w.closeGroupWithoutObjectDeath();
		
		
		
	}
	
	/**simulates a key stroke*/
	static void simulateKeyStroke(ImageWindowAndDisplaySet image, char keyChar, boolean meta) {
		GraphicSetDisplayWindow c = image.getWindow();
		KeyEvent k = new KeyEvent(c, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), meta? KeyEvent.META_DOWN_MASK: 0, KeyEvent.getExtendedKeyCodeForChar(keyChar), keyChar);
		c.dispatchEvent(k);
		IJ.wait(100);
	}


	/**
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ImageDisplayTester.main(args);
		
		
			GraphicSetDisplayWindow win = new GraphicSetDisplayWindow(new GraphicDisplayCanvas());
			win.setJMenuBar(new  MenuBarForApp());
			RectangularGraphic rr = new RectangularGraphic(3,3, 40,40);
			rr.setFillColor(Color.black); 
			rr.setFilled(true);
			win.getTheSet().getGraphicLayerSet().add(rr);
			
			RhombusGraphic rr2=new RhombusGraphic();
			rr=rr2;
			rr.setRectangle(new Rectangle(30,5, 40,30));
			rr.setFillColor(Color.green); 
			rr.setFilled(true);
			win.getTheSet().getGraphicLayerSet().add(rr);
			rr2.setAngleBend(Math.PI/8);
			BasicMontageLayout bl = new BasicMontageLayout(2, 3, 100,100,10,10, true);
			MontageLayoutGraphic gl = new MontageLayoutGraphic(bl);
			//gl.setLocationUpperLeft(100, 200);
		    
			win.getTheSet().getGraphicLayerSet().add(gl);
			
			ObjectToolset1 ot = new ObjectToolset1();
			ot.run("hi");
			
			win.show();
	}
*/
}
