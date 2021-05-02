/**
 * Author: Greg Mazo
 * Date Created: May 1, 2021
 * Date Modified: May 1, 2021
 * Version: 2021.1
 */
package selectedItemMenus;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;

import figureOrganizer.insetPanels.PanelGraphicInsetDefiner;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_Shapes.ArrowGraphic;
import graphicalObjects_Shapes.FrameGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import graphicalObjects_Shapes.SimpleGraphicalObject;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import locatedObject.LocatedObject2D;
import locatedObject.LocationChangeListener;
import logging.IssueLog;
import selectedItemMenus.PanelMirror.Reflection;

/**
 A special layer that contains items whose location is determined by a parent item
 */
public class PanelMirror extends GraphicLayerPane implements LocationChangeListener {

	

	private ImagePanelGraphic primaryPanel;
	private ShapeGraphic primaryShape;
	private final ArrayList<Reflection> reflections=new ArrayList<Reflection>();

	/**
	 * @param name
	 */
	public PanelMirror(String name) {
		super(name);
	}

	/**
	 * @param s
	 * @param startPanel
	 * @param otherPanels
	 */
	public PanelMirror(ShapeGraphic s, ImagePanelGraphic startPanel, ArrayList<ImagePanelGraphic> otherPanels) {
		this("mirror");
		this.primaryPanel=startPanel;
		this.primaryShape=s;
		
		for(ImagePanelGraphic panel:otherPanels) {
			if(panel==null)
				continue;
			SimpleGraphicalObject copy = s.copy();
			if(s instanceof PanelGraphicInsetDefiner) {
				PanelGraphicInsetDefiner s2=(PanelGraphicInsetDefiner) s;
				copy=new FrameGraphic(s2.getRectangle());
				this.mirrorProperties((RectangularGraphic) copy);
				
			}
			this.add(copy);
			
			
			reflections.add(new Reflection(copy, panel));
			
		}
		
		s.addLocationChangeListener(this);
		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 
	 * 
	 */
public class Reflection implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private ImagePanelGraphic targetPanel;
		private SimpleGraphicalObject reflectedObject;

		/**
		 * @param copy
		 * @param panel
		 */
		public Reflection(SimpleGraphicalObject copy, ImagePanelGraphic panel) {
			
			this.reflectedObject=copy;
			this.targetPanel=panel;
			moveReflectionToDestinationPanel();
			
		}

		/**
		 * 
		 */
		protected void moveReflectionToDestinationPanel() {
			Point2D pf = targetPanel.getLocationUpperLeft();
			Point2D pi = primaryPanel.getLocationUpperLeft();
			
			double dx = pf.getX()-pi.getX();
			double dy = pf.getY()-pi.getY();
			reflectedObject.moveLocation(dx, dy);
		}

		/**
		 * 
		 */
		public void updateLocation() {
			
			/**what to do if the mirrored object is a rectangle*/
			if(reflectedObject instanceof RectangularGraphic && primaryShape instanceof RectangularGraphic ) {
				 RectangularGraphic r= (RectangularGraphic) reflectedObject ;
				 r.copyAttributesFrom(primaryShape);
				 r.setRectangle(((RectangularGraphic) primaryShape).getRectangle());
				 r.setAngle(primaryShape.getAngle());
				 moveReflectionToDestinationPanel() ;
			}
			
			if(reflectedObject instanceof ArrowGraphic && primaryShape instanceof ArrowGraphic ) {
				ArrowGraphic r= (ArrowGraphic) reflectedObject ;
				 r.copyAttributesFrom(primaryShape);
				 ArrowGraphic primary=(ArrowGraphic) primaryShape;
				 r.setPoints(primary.getLineStartLocation(), primary.getLineEndLocation());
				
				 moveReflectionToDestinationPanel() ;
			}
			
			/**what to do if the mirrored object is a rectangle*/
			if(reflectedObject instanceof FrameGraphic && primaryShape instanceof PanelGraphicInsetDefiner ) {
				FrameGraphic r= (FrameGraphic) reflectedObject ;
				 mirrorProperties(r);
				 PanelGraphicInsetDefiner primaryShape2 = (PanelGraphicInsetDefiner ) primaryShape;
				r.setRectangle(primaryShape2.getRectangle());
				 r.setAngle(primaryShape.getAngle());
				 moveReflectionToDestinationPanel() ;
			}
			
			
			
		}

		

}


/**
 * @param r
 */
protected void mirrorProperties(RectangularGraphic r) {
	r.copyAttributesFrom(primaryShape);
	 r.copyColorsFrom(primaryShape);
	 r.copyStrokeFrom(primaryShape);
}

/**whenever the primary object is moved, this updates every reflection*/
	@Override
	public void objectMoved(LocatedObject2D object) {
		
		updateAllReflections();
		
	}

/**
 * 
 */
protected void updateAllReflections() {
	for(Reflection r: reflections) try {
		r.updateLocation();
	} catch (Throwable t) {
		IssueLog.logT(t);
	}
}

	@Override
	public void objectSizeChanged(LocatedObject2D object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void objectEliminated(LocatedObject2D object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void userMoved(LocatedObject2D object) {
		updateAllReflections();
		
	}

	@Override
	public void userSizeChanged(LocatedObject2D object) {
		// TODO Auto-generated method stub
		
	}

}
