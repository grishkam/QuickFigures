package graphicalObjectHandles;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import graphicalObjects.ImagePanelGraphic;
import utilityClassesForObjects.RectangleEdges;

/**A set of handles for editing of a user selected image*/
public class ImagePanelHandleList extends SmartHandleList {

	public static final int FRAME_HANDLE_ID = 10;
	/**
	 * 
	 */
	
	ImagePanelHandle frameHandle=null;
	private ImagePanelGraphic panel;
	private static final long serialVersionUID = 1L;
	public ImagePanelHandleList(ImagePanelGraphic panel) {
		this.panel=panel;
		Rectangle bounds = panel.getBounds();
		int[] spots = RectangleEdges.internalLocations;//.getLocationsForHandles(bounds);
		for(int i=0; i<9; i++) {
			this.add(new ImagePanelHandle(panel, i)); 
			
			this.get(i).setCordinateLocation(RectangleEdges.getLocation(spots[i], bounds));
		
		
		}
		frameHandle=new ImagePanelHandle(panel, FRAME_HANDLE_ID); 
		this.add(frameHandle);
		frameHandle.setCordinateLocation(getFrameHandlePoint());
		
	}
	
	public void updateHandleLocs() {
		ArrayList<Point2D> spots = RectangleEdges.getLocationsForHandles(panel.getBounds());
		for(int i=0; i<9; i++) {
			this.get(i).setCordinateLocation(spots.get(i));
		}
		frameHandle.setCordinateLocation(getFrameHandlePoint());
		for(int i=10; i<this.size(); i++) {
			SmartHandle item = this.get(i);
			if (item instanceof LockedItemHandle) {
				LockedItemHandle lh=(LockedItemHandle) item;
			lh.updateLocation();
			}
		}
	}
	
	protected Point getFrameHandlePoint() {
		Rectangle rect2 = panel.getExtendedBounds();
		return new Point((int)(rect2.getCenterX()-rect2.width/4), rect2.y+rect2.height);
	}

}