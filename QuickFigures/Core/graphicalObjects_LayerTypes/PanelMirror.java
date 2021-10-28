/**
 * Author: Greg Mazo
 * Date Created: May 1, 2021
 * Date Modified: October 24, 2021
 * Version: 2021.1
 */
package graphicalObjects_LayerTypes;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JMenu;

import fLexibleUIKit.MenuItemExecuter;
import fLexibleUIKit.MenuItemMethod;
import figureOrganizer.insetPanels.PanelGraphicInsetDefiner;
import graphicalObjects_Shapes.ArrowGraphic;
import graphicalObjects_Shapes.FrameGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import graphicalObjects_Shapes.SimpleGraphicalObject;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import locatedObject.LocatedObject2D;
import locatedObject.LocationChangeListener;
import logging.IssueLog;
import menuUtil.HasUniquePopupMenu;
import menuUtil.PopupMenuSupplier;
import popupMenusForComplexObjects.DonatesMenu;

/**
 Soemtimes a user wants an shape displayed over a few different parent panels
 but at the equivalent location in each panel.
 A special layer that contains items whose location is determined by a parent item
 Whenever a user changes the location of the parent item, the reflections will be updated.
 */
public class PanelMirror extends GraphicLayerPane implements LocationChangeListener , HasUniquePopupMenu, DonatesMenu {

	

	private ImagePanelGraphic primaryPanel;
	private ShapeGraphic primaryShape;
	private final ArrayList<Reflection> reflections=new ArrayList<Reflection>();
	
	/**mirror can be paused*/
	private boolean mirrorActive=true;
	private boolean locationMirrorActive=true;
	private boolean colorMirrowActive=false;
	

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
	This class keeps track of all the reflections in the mirrow
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
			moveReflectionToDestinationPanel(targetPanel, primaryPanel, reflectedObject);
			
		}

		/**
		Assuming that a reflected objects starts in one panel (primary panel), moves the object to the equivalent location in the target panel
		 */
		protected void moveReflectionToDestinationPanel(ImagePanelGraphic targetPanel, ImagePanelGraphic primaryPanel, SimpleGraphicalObject reflectedObject) {
			Point2D pf = targetPanel.getLocationUpperLeft();
			Point2D pi = primaryPanel.getLocationUpperLeft();
			
			double dx = pf.getX()-pi.getX();
			double dy = pf.getY()-pi.getY();
			reflectedObject.moveLocation(dx, dy);
		}

		/**updates the location of the reflected object*/
		public void updateLocation() {
			updateLocation(targetPanel, primaryPanel,  reflectedObject, primaryShape) ;
		}
		
		/**
		 * 
		 */
		public void updateLocation(ImagePanelGraphic targetPanel, ImagePanelGraphic primaryPanel, SimpleGraphicalObject reflectedObject, ShapeGraphic primaryShape) {
			
			/**what to do if the mirrored object is a rectangle*/
			if(reflectedObject instanceof RectangularGraphic && primaryShape instanceof RectangularGraphic ) {
				 RectangularGraphic r= (RectangularGraphic) reflectedObject ;
				 r.copyAttributesFrom(primaryShape);
				 r.setRectangle(((RectangularGraphic) primaryShape).getRectangle());
				 r.setAngle(primaryShape.getAngle());
				 moveReflectionToDestinationPanel(targetPanel, primaryPanel, reflectedObject) ;
			}
			
			if(reflectedObject instanceof ArrowGraphic && primaryShape instanceof ArrowGraphic ) {
				ArrowGraphic r= (ArrowGraphic) reflectedObject ;
				 r.copyAttributesFrom(primaryShape);
				 ArrowGraphic primary=(ArrowGraphic) primaryShape;
				 r.setPoints(primary.getLineStartLocation(), primary.getLineEndLocation());
				
				 moveReflectionToDestinationPanel(targetPanel, primaryPanel, reflectedObject) ;
			}
			
			/**what to do if the mirrored object is a rectangle*/
			if(reflectedObject instanceof FrameGraphic && primaryShape instanceof PanelGraphicInsetDefiner ) {
				FrameGraphic r= (FrameGraphic) reflectedObject ;
				 mirrorProperties(r);
				 PanelGraphicInsetDefiner primaryShape2 = (PanelGraphicInsetDefiner ) primaryShape;
				r.setRectangle(primaryShape2.getRectangle());
				 r.setAngle(primaryShape.getAngle());
				 moveReflectionToDestinationPanel(targetPanel, primaryPanel, reflectedObject) ;
			}
			
			
			
		}

		/**
		 changess the colors of the reflection to match the primary shape
		 */
		public void mirrorColors() {
			
			/**what to do if the mirrored object is a rectangle*/
			if(reflectedObject instanceof RectangularGraphic && primaryShape instanceof RectangularGraphic ) {
				 RectangularGraphic r= (RectangularGraphic) reflectedObject ;
				 r.copyAttributesFrom(primaryShape);
				 r.copyColorsFrom(primaryShape);
			}
			
			if(reflectedObject instanceof ArrowGraphic && primaryShape instanceof ArrowGraphic ) {
				ArrowGraphic r= (ArrowGraphic) reflectedObject ;
				 r.copyColorsFrom(primaryShape);
			}
			
			/**what to do if the mirrored object is a rectangle*/
			if(reflectedObject instanceof FrameGraphic && primaryShape instanceof PanelGraphicInsetDefiner ) {
				FrameGraphic r= (FrameGraphic) reflectedObject ;
				r.copyColorsFrom(primaryShape);
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

@MenuItemMethod(menuActionCommand = "Update color all mirrors", menuText = "line widths and colors",subMenuName="copy to reflections", orderRank=8, iconMethod="isColorMirrorActive")
public void turnColorMirrorOnOff() {
	if(this.isColorMirrorActive()) {
		this.setColorMirrorActive(false);
	}
	else {
		this.setColorMirrorActive(true);
		copyColors();
	}
}

@MenuItemMethod(menuActionCommand = "Update location all mirrors", menuText = "location, size and angle",subMenuName="copy to reflections", orderRank=9, iconMethod="isLocationMirrorActive")
public void turnLocationMirrorOnOff() {
	if(this.isLocationMirrorActive()) {
		this.setLocationMirrorActive(false);
	}
	else {
		this.setLocationMirrorActive(true);
		this.updateAllReflections();
	}
}


/**copies the colors from the original to all reflections*/
public void copyColors() {
	for(Reflection r: reflections) try {
		r.mirrorColors();
	} catch (Throwable t) {
		IssueLog.logT(t);
	}
}


/**whenever the primary object is moved, this updates every reflection*/
	@Override
	public void objectMoved(LocatedObject2D object) {
		if(!isMirrorActive())
			return;
		if (isLocationMirrorActive())
			updateAllReflections();
		if(isColorMirrorActive())
			copyColors();
		
	}


/**updates the location of all reflections. does not affect the original*/
public void updateAllReflections() {
	for(Reflection r: reflections) try {
		r.updateLocation();
	} catch (Throwable t) {
		IssueLog.logT(t);
	}
}


@MenuItemMethod(menuActionCommand = "Stop mirroring", menuText = "Stop mirroring",permissionMethod="mirrorActive", orderRank=1)
public void stopMirror() {
	this.setMirrorActive(false);
}

@MenuItemMethod(menuActionCommand = "Start mirroring", menuText = "Start mirroring",permissionMethod="mirrorPaused", orderRank=1)
public void startMirror() {
	this.setMirrorActive(true);
	if(isLocationMirrorActive())
		updateAllReflections();
}

/**returns true if the mirror is active*/
public boolean mirrorActive() {
	return this.isMirrorActive();
}

/**returns true if the mirror is not active, mirrored objects will not be modified if this is the case*/
public boolean mirrorPaused() {
	return !isMirrorActive();
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
		if(isMirrorActive()) {
			
			if(object!=primaryShape) {
				
			}
			else
				if(isLocationMirrorActive())
					updateAllReflections();
			
		}
		
	}

	@Override
	public void userSizeChanged(LocatedObject2D object) {
		// TODO Auto-generated method stub
		
	}
	
	/**creates a menu for this item*/
	public PopupMenuSupplier getMenuSupplier() {
		
		return new MenuItemExecuter(this);
	}

	/**returns the mirror submenu if the argument is one of the reflected objects*/
	@Override
	public JMenu getDonatedMenuFor(Object requestor) {
		if (requestor==primaryShape||isReflection(requestor)) {
			JMenu jMenu = new MenuItemExecuter(this).getJMenu();
			jMenu.setText("mirror options");
			return jMenu;
		}
		
		return null;
	}

	/**
	 returns true if the object given is one fo the reflection
	 */
	private boolean isReflection(Object requestor) {
		for(Reflection rel: this.reflections) {
			if(rel.reflectedObject==requestor)
				return true;
		}
		return false;
	}

	public boolean isColorMirrorActive() {
		return colorMirrowActive;
	}

	public void setColorMirrorActive(boolean colorMirrowActive) {
		this.colorMirrowActive = colorMirrowActive;
	}

	public boolean isLocationMirrorActive() {
		return locationMirrorActive;
	}

	public void setLocationMirrorActive(boolean locationMirrorActive) {
		this.locationMirrorActive = locationMirrorActive;
	}

	public boolean isMirrorActive() {
		return mirrorActive;
	}

	public void setMirrorActive(boolean mirrorActive) {
		this.mirrorActive = mirrorActive;
	}

}