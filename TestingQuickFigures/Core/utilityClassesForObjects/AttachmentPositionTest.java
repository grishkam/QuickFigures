package utilityClassesForObjects;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

import appContext.CurrentAppContext;
import appContextforIJ1.IJ1MultichannelContext;
import appContextforIJ1.ImageDisplayTester;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_SpecialObjects.ComplexTextGraphic;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import graphicalObjects_SpecialObjects.TextGraphic;
import imageDisplayApp.ImageWindowAndDisplaySet;
import locatedObject.AttachmentPosition;
import locatedObject.RectangleEdgePositions;
import logging.IssueLog;
import testing.TestExample;
import testing.TestShapes;
import testing.TestingUtils;

/**tests that the attachment position class to make sure that the locations it calculates 
  are consistent with expected locations. 
  although any aberrations in attachment position should have been obvious to a user
  while using it, this class tests all sorts of locations while a user might not try every single one*/
class AttachmentPositionTest {

	@Test
	void test() {
		performAutomatedTests();
		
		CurrentAppContext.setMultichannelContext(new IJ1MultichannelContext());
		ImageDisplayTester.startToolbars(true);
		IssueLog.sytemprint=true;
		IssueLog.windowPrint=false;
		
		manuallyTestInternalAttachment(true);
		
		manuallyTestExternalAttachment(true);
		
	manuallyTestInternalAttachment(false);
		
		manuallyTestExternalAttachment(false);
		
	}

	/**
	  creates objects with every internal attachment position labeled with their intended locations
	 asks user to visually confirm that every location appears appropriate.
	 */
	void manuallyTestInternalAttachment(boolean text) {
		ImageWindowAndDisplaySet image = TestShapes.createExample(TestExample.EMPTY);
		ImagePanelGraphic panel = createMockPanel("parent","panel", 400, 300);
		image.getImageAsWrapper().addItemToImage(panel);
		image.getWindow().setLocation(0, 200);
		panel.setLocationUpperLeft(20,  20);
		
		
		
		for (int location: AttachmentPosition.internalAttachmentLocations) {
			AttachmentPosition position=new AttachmentPosition();;
			position.setLocationCategory(AttachmentPosition.INTERNAL);
			position.setLocationTypeInternal(location);
			if (text) {
				createTextForPosition(image, panel, position);
			} else {
			 ImagePanelGraphic panel2 = createMockPanel(position.getShortDescription(), position.getSecondDescription(),100, 40);
			panel2.setFrameWidthH(12);
			panel2.setFrameWidthV(12);
			position.setUseExtendedBounds(AttachmentPosition.EXTENDED_BOUNDS);
			position.snapObjectToRectangle(panel2, panel.getBounds());
			image.getImageAsWrapper().addItemToImage(panel2);
			}
		}
		
		TestingUtils.askUser("look at the locations of the attached items. Does each location match the text?");
		image.closeWindowButKeepObjects();
	}
	
	/**
	 creates objects with every external attachment position labeled with their intended locations
	 asks user to visually confirm that every location appears appropriate.
	 */
	void manuallyTestExternalAttachment(boolean text) {
		ImageWindowAndDisplaySet image = TestShapes.createExample(TestExample.EMPTY);
		ImagePanelGraphic panel = createMockPanel("parent", "panel", 300, 250);
		image.getImageAsWrapper().addItemToImage(panel);
		image.getWindow().setLocation(0, 200);
		panel.setLocationUpperLeft(150,  80);
		
		
		
		for (int location: AttachmentPosition.PRIMARY_EXTERNAL_LOCATIONS) {
			AttachmentPosition position=new AttachmentPosition();;
			
			position.setLocationCategory(AttachmentPosition.EXTERNAL);
			position.setLocationTypeExternal(location);
			if (text) {
				createTextForPosition(image, panel, position);
			} else {
					 ImagePanelGraphic panel2 = createMockPanel(position.getShortDescription(), position.getSecondDescription(),80, 60);
					panel2.setFrameWidthH(12);
					panel2.setFrameWidthV(12);
					position.setUseExtendedBounds(AttachmentPosition.EXTENDED_BOUNDS);
					
					position.snapObjectToRectangle(panel2, panel.getBounds());
					image.getImageAsWrapper().addItemToImage(panel2);
			}
		}
		
		TestingUtils.askUser("look at the locations of the attached items. Does each location match the text?");
	}

	/**creates a text item with a description of the attachment position
	 * @param image
	 * @param panel
	 * @param position
	 */
	private void createTextForPosition(ImageWindowAndDisplaySet image, ImagePanelGraphic panel,
			AttachmentPosition position) {
		TextGraphic t=new ComplexTextGraphic(position.getShortDescription(), position.getSecondDescription());
		t.setAttachmentPosition(position);
		t.setTextColor(Color.green);
		position.snapObjectToRectangle(t, panel.getBounds());
		image.getImageAsWrapper().addItemToImage(t);
	}

	/**
	creates a mock panel, containing nothing other than the text given
	 */
	ImagePanelGraphic createMockPanel(String text, String text2, int width, int height) {
		return new ImagePanelGraphic(createImageWithText(text, text2, width, height, 12));
	}

	/**
	performs multiple automated tests
	 */
	void performAutomatedTests() {
		AttachmentPosition p=new AttachmentPosition();;
	
		
		
		/**even number width/heights were used so that casts from double to integer values */
		Rectangle[] testedRectangles=new Rectangle[] {
				new Rectangle(0,0,100,100),  new Rectangle(900,900,10,10), new Rectangle(30, 30, 50,50), new Rectangle(30, 30, 2,2)
		,new Rectangle(30, 300, 2,2200)		,new Rectangle(30, 300, 200,20),new Rectangle(-30, -300, 200,20000)
		};
		
		int[] testedOffSets = new int[] {-10, -1, 0, 1, 10};

		/**series of loops iterates through each parameter testing many combinations
		  to make sure object has been moved correctly.
		  the internal locations to make sure the object has been moved*/
		for(Rectangle rParent:testedRectangles)
		for(Rectangle rChild: testedRectangles) {
			for(int hOff: testedOffSets)
			for(int vOff:  testedOffSets)
			{
			for(int i: RectangleEdgePositions.internalAttachmentLocations) {
				
					
					{
						RectangularGraphic gChild=new RectangularGraphic(rChild);
						testInternalAttachment(p, hOff, vOff, rParent, rChild, gChild, i);
					}
			}
			
			for(int i: RectangleEdgePositions.ALL_EXTERNAL_LOCATIONS) {
				
				
				{
					RectangularGraphic gChild=new RectangularGraphic(rChild);
					testExternalAttachment(p, hOff, vOff, rParent, rChild, gChild, i);
				}
		}
			}
		}
	}

	/**
	 Tests the location setting for an internal attached object with given parameters
	 */
	public void testInternalAttachment(AttachmentPosition position, int hOff, int vOff, Rectangle rParent, Rectangle rChild,
			RectangularGraphic gChild, int internalLocation) {
		position.setLocationCategory(AttachmentPosition.INTERNAL);
		position.setHorizontalOffset(hOff);
		position.setVerticalOffset(vOff);
		position.setLocationTypeInternal(internalLocation);
		position.snapObjectToRectangle(gChild, rParent);
		
		/**tests left location for horizontal left to make sure they are aligned with the left edges of the parent panel*/
		if(internalLocation==RectangleEdgePositions.UPPER_LEFT
				||internalLocation==RectangleEdgePositions.LOWER_LEFT
				||internalLocation==RectangleEdgePositions.LEFT
				) {
			assert(gChild.getBounds().x==rParent.getBounds().x+position.getHorizontalOffset());
			}
		
		/**tests locations that should be aligned with the right side of the parent panel*/
		if(internalLocation==RectangleEdgePositions.UPPER_RIGHT
				||internalLocation==RectangleEdgePositions.LOWER_RIGHT
				||internalLocation==RectangleEdgePositions.RIGHT
				) {
			assert(gChild.getBounds().x==rParent.getBounds().getMaxX()-rChild.width-position.getHorizontalOffset());
			}
		
		/**tests x location for positions that should be aligned with the center of the parent panel*/
		if(internalLocation==RectangleEdgePositions.TOP
				||internalLocation==RectangleEdgePositions.BOTTOM
				
				) {
			assert(gChild.getBounds().x==rParent.getBounds().getCenterX()-rChild.width/2+position.getHorizontalOffset());
			}
		
		
		/**tests y location for positions that should be aligned with the top of the parent panel*/
		if(internalLocation==RectangleEdgePositions.UPPER_LEFT
				||internalLocation==RectangleEdgePositions.UPPER_RIGHT
				||internalLocation==RectangleEdgePositions.TOP
				) {
			assert(gChild.getBounds().y==rParent.getBounds().y+position.getVerticalOffset());
			}
		
		/**tests y locations that should be aligned with the botteom edge of the parent panel*/
		if(internalLocation==RectangleEdgePositions.LOWER_LEFT
				||internalLocation==RectangleEdgePositions.LOWER_RIGHT
				||internalLocation==RectangleEdgePositions.BOTTOM
				) {
			assert(gChild.getBounds().y==rParent.getMaxY()-rChild.getHeight()-position.getVerticalOffset());
			}
		
		
		/**tests y location for central positions*/
		if(internalLocation==RectangleEdgePositions.LEFT
				||internalLocation==RectangleEdgePositions.RIGHT
				/**note center does not actually work nor is it ever needed by the user*/
				//||i==RectangleEdgePosisions.CENTER
				) {
			
			assert(gChild.getBounds().y==rParent.getBounds().getCenterY()-rChild.height/2+position.getVerticalOffset());
			}
			
		
		rChild.setRect(rChild);//reverts the location to the original
	}
	
	/**tests that the external attachment locations move an object to the correct spot*/
	public void testExternalAttachment(AttachmentPosition position, int hOff, int vOff, Rectangle rParent, Rectangle rChild,
			RectangularGraphic gChild, int internalLocation) {
		position.setLocationCategory(AttachmentPosition.EXTERNAL);
		position.setHorizontalOffset(hOff);
		position.setVerticalOffset(vOff);
		position.setLocationTypeExternal(internalLocation);
		position.snapObjectToRectangle(gChild, rParent);
		
		/**tests left locations*/
		if(internalLocation==RectangleEdgePositions.LEFT_SIDE_TOP
				||internalLocation==RectangleEdgePositions.LEFT_SIDE_MIDDLE
				||internalLocation==RectangleEdgePositions.LEFT_SIDE_BOTTOM
				) {
			assert(gChild.getBounds().x==rParent.getBounds().x-gChild.getBounds().width-position.getHorizontalOffset());
			}
		
		/**tests RIGHT locations*/
		if(internalLocation==RectangleEdgePositions.RIGHT_SIDE_TOP
				||internalLocation==RectangleEdgePositions.RIGHT_SIDE_MIDDLE
				||internalLocation==RectangleEdgePositions.RIGHT_SIDE_BOTTOM
				) {
			assert(gChild.getBounds().x==rParent.getBounds().getMaxX()+position.getHorizontalOffset());
			}
		
		/**tests TOP side locations*/
		if(internalLocation==RectangleEdgePositions.RIGHT_SIDE_TOP
				||internalLocation==RectangleEdgePositions.LEFT_SIDE_TOP
				
				) {
			assert(gChild.getBounds().y==rParent.getBounds().y+position.getVerticalOffset());
			}
		
		
		/**tests BOTTOM side locations*/
		if(internalLocation==RectangleEdgePositions.RIGHT_SIDE_BOTTOM
				||internalLocation==RectangleEdgePositions.LEFT_SIDE_BOTTOM
				
				) {
			assert(gChild.getBounds().y==rParent.getBounds().getMaxY()-gChild.getBounds().height-position.getVerticalOffset());
			}
		
		
		/**tests below positions y*/
		if(internalLocation==RectangleEdgePositions.BELOW_AT_LEFT
				||internalLocation==RectangleEdgePositions.BELOW_AT_MIDDLE
				||internalLocation==RectangleEdgePositions.BELOW_AT_RIGHT
				) {
			assert(gChild.getBounds().y==rParent.getBounds().getMaxY()+position.getVerticalOffset());
			}
			
		/**tests Above positions y*/
		if(internalLocation==RectangleEdgePositions.ABOVE_AT_LEFT
				||internalLocation==RectangleEdgePositions.ABOVE_AT_MIDDLE
				||internalLocation==RectangleEdgePositions.ABOVE_AT_RIGHT
				) {
			assert(gChild.getBounds().y==rParent.getBounds().y-gChild.getBounds().height-position.getVerticalOffset());
			}
		
		/**tests left side above/below locations*/
		if(internalLocation==RectangleEdgePositions.BELOW_AT_LEFT
				||internalLocation==RectangleEdgePositions.ABOVE_AT_LEFT
			
				) {
			assert(gChild.getBounds().x==rParent.getBounds().x+position.getHorizontalOffset());
			}
		
		/**tests left side above/below locations*/
		if(internalLocation==RectangleEdgePositions.BELOW_AT_RIGHT
				||internalLocation==RectangleEdgePositions.ABOVE_AT_RIGHT
			
				) {
			assert(gChild.getBounds().x==rParent.getBounds().getMaxX()-gChild.getBounds().width-position.getHorizontalOffset());
			}
		
		rChild.setRect(rChild);//reverts the location to the original
	}

	
	
	/**
	An image with a message. messages are meant to indicate the intended location of the panel
	 */
	public static BufferedImage createImageWithText(String text, String t2, int w, int h, int fontsize) {
		BufferedImage img=new BufferedImage(w, h,  BufferedImage.TYPE_INT_RGB);
		Graphics g = img.getGraphics();
		g.setFont(new Font("Arial", Font.BOLD, fontsize));
		g.setColor(Color.RED);
		
		g.drawString(text, 0, (int) (1.5*fontsize));
		if (t2!=null) {
			g.drawString(t2, 0, (int) (3*fontsize));
		}
		return img;
	}
}
