/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
/**
 * Author: Greg Mazo
 * Date Created: May 1, 2021
 * Date Modified: Dec 3, 2022
 * Version: 2022.2
 */
package graphicalObjects_LayerTypes;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JMenu;

import fLexibleUIKit.MenuItemExecuter;
import fLexibleUIKit.MenuItemMethod;
import figureOrganizer.insetPanels.PanelGraphicInsetDefiner;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import graphicalObjects_Shapes.ArrowGraphic;
import graphicalObjects_Shapes.FrameGraphic;
import graphicalObjects_Shapes.RectangularGraphic;
import graphicalObjects_Shapes.ShapeGraphic;
import graphicalObjects_Shapes.SimpleGraphicalObject;
import graphicalObjects_SpecialObjects.ImagePanelGraphic;
import handles.HasSmartHandles;
import handles.RectangleEdgeHandle;
import handles.SmartHandle;
import handles.SmartHandleList;
import layout.basicFigure.BasicLayout;
import layout.basicFigure.LayoutSpaces;
import layout.basicFigure.LayoutSpaces.SpaceType;
import locatedObject.LocatedObject2D;
import locatedObject.LocationChangeListener;
import locatedObject.RectangleEdges;
import logging.IssueLog;
import menuUtil.HasUniquePopupMenu;
import menuUtil.PopupMenuSupplier;
import popupMenusForComplexObjects.DonatesMenu;
import popupMenusForComplexObjects.InsetMenu;

/**
 Soemtimes a user wants an shape displayed over a few different parent panels
 but at the equivalent location in each panel.
 A special layer that contains items whose location is determined by a parent item
 Whenever a user changes the location of the parent item, the reflections will be updated.
 */
public class PanelMirror extends GraphicLayerPane implements LocationChangeListener , HasUniquePopupMenu, DonatesMenu {

	

	private PanelAddress primaryPanelAddress;
	private ShapeGraphic primaryShape;
	private final ArrayList<Reflection> reflections=new ArrayList<Reflection>();
	
	/**mirror can be paused*/
	private boolean mirrorActive=true;
	private boolean locationMirrorActive=true;
	private boolean colorMirrowActive=true;
	

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
		this.primaryPanelAddress=new ImagePanelAddress(startPanel);
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
			
			
			PanelAddress panelAddress = new ImagePanelAddress(panel);
			addReflection(copy, panelAddress);
			//copy.addLocationChangeListener(this);//experimental
		}
		
		s.addLocationChangeListener(this);
		
	}
	
	/**Creates a new panel mirrow
	 * @param s
	 * @param startPanel
	 * @param otherPanels
	 */
	public PanelMirror(ShapeGraphic s, PanelAddress panel) {
		this("mirror");
		this.primaryPanelAddress=panel;
		this.primaryShape=s;
		
		s.addLocationChangeListener(this);
		
	}

	/**Adds a new reflection to the list
	 * @param copy
	 * @param panelAddress
	 */
	public void addReflection(SimpleGraphicalObject copy, PanelAddress panelAddress) {
		reflections.add(new Reflection(copy, panelAddress));
		copy.addLocationChangeListener(this);
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
		private PanelAddress targetPanel;
		private SimpleGraphicalObject reflectedObject;

		/**
		 * @param copy
		 * @param panel
		 */
		public Reflection(SimpleGraphicalObject copy, PanelAddress panel) {
			
			this.reflectedObject=copy;
			this.targetPanel=panel;
			moveReflectionToDestinationPanel(targetPanel, primaryPanelAddress, reflectedObject);
			
		}

		

		/**updates the location of the reflected object*/
		public void updateLocation() {
			PanelMirror.updateLocation(targetPanel, primaryPanelAddress,  reflectedObject, primaryShape) ;
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
				mirrorProperties(r);
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
		copyColorAndLineTraits();
		this.updateAllReflectionLocations();
	}
}

@MenuItemMethod(menuActionCommand = "Update location all mirrors", menuText = "location, size and angle",subMenuName="copy to reflections", orderRank=9, iconMethod="isLocationMirrorActive")
public void turnLocationMirrorOnOff() {
	if(this.isLocationMirrorActive()) {
		this.setLocationMirrorActive(false);
	}
	else {
		this.setLocationMirrorActive(true);
		this.updateAllReflectionLocations();
	}
}


/**copies the colors from the original to all reflections*/
public void copyColorAndLineTraits() {
	
	for(Reflection r: reflections) try {
		r.mirrorColors();
	} catch (Throwable t) {
		IssueLog.logT(t);
	}
}

boolean updateOngoing=false;

/**whenever the primary object is moved, this updates every reflection*/
	@Override
	public void objectMoved(LocatedObject2D object) {
		
	}

/**
 * @param object
 * @return 
 */
private PanelAddress findAddress(LocatedObject2D object) {
	if(object==primaryShape)
		return this.primaryPanelAddress;
	for(Reflection r: reflections) try {
		if(r.reflectedObject==object)
			return r.targetPanel;
	} catch (Throwable t) {
		IssueLog.logT(t);
	}
	return null;
	
}

/**
updates the reflections if the mirror is active
 */
public void mirrorAfterObjectMove() {
	if(!isMirrorActive())
		return;
	if (isLocationMirrorActive())
		updateAllReflectionLocations();
	if(isColorMirrorActive()) {
		copyColorAndLineTraits();
		
		}
}


/**updates the location of all reflections. does not affect the original*/
public void updateAllReflectionLocations() {
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
		updateAllReflectionLocations();
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
		mirrorAfterObjectMove();
		
	}

	@Override
	public void objectEliminated(LocatedObject2D object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void userMoved(LocatedObject2D object) {
		
		mirrorReflectionBackToOriginal(object);
			
				mirrorAfterObjectMove();
			
		
		
	}

	/**if the object given is one of the reflections, will 
	 * @param object
	 */
	public void mirrorReflectionBackToOriginal(LocatedObject2D object) {
		
		if(updateOngoing||!isMirrorActive())
			return;//return here precents infinite loops
		updateOngoing=true;
		//IssueLog.log("Object move detected "+System.currentTimeMillis());
		PanelAddress panel = findAddress(object);
		if(panel!=null&&object!=this.primaryShape/**&& !(object instanceof PanelGraphicInsetDefiner)*/) {
		
			PanelMirror.updateLocation(primaryPanelAddress, panel,  primaryShape, (ShapeGraphic) object);
			if(primaryShape instanceof RectangularGraphic)
				((RectangularGraphic) primaryShape).afterHandleMove(RectangleEdges.LOWER_LEFT, new Point(), new Point());
		}
		
		updateOngoing=false;
	}

	@Override
	public void userSizeChanged(LocatedObject2D object) {
		mirrorReflectionBackToOriginal(object);
		mirrorAfterObjectMove();
		
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
	 returns true if the object given is one fo the reflections
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

	/**An interface that keeps tack of which panel the reflection or the original is located*/
	static interface PanelAddress extends Serializable {

		/**
		 * @return
		 */
		Point2D getLocationUpperLeft();
		}
	
	/**Implementation of panel address for image panels*/
	public static class ImagePanelAddress implements PanelAddress {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private ImagePanelGraphic imagePanel;

		/**
		 * @param startPanel
		 */
		public ImagePanelAddress(ImagePanelGraphic startPanel) {
			this.imagePanel=startPanel;
		}

		/**
		 * @return
		 */
		public Point2D getLocationUpperLeft() {
			return imagePanel.getLocationUpperLeft();
		}
		}
	
	
	/**Implementation of panel address for layout panels*/
	public static class LayoutAddress implements PanelAddress {

		

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private DefaultLayoutGraphic layout;
		private int index;
		private SpaceType type;

		/**
		 * @param thePanels
		 */
		public  LayoutAddress(DefaultLayoutGraphic thePanels, int index, LayoutSpaces.SpaceType type) {
			this.layout=thePanels;
			this.index=index;
			this.type=type;
		}

		/**
		 * @return
		 */
		public Point2D getLocationUpperLeft() {
			BasicLayout panelLayout = layout.getPanelLayout().makeAltered(type.getFullSpaceCode());
			Rectangle2D rect = panelLayout.getPanel(index);
			return new Point2D.Double(rect.getX(), rect.getY());
		}
		}
	
	/**Copies certain kinds of parameters using the handles. does not yet function 
	 * @param primaryShape
	 * @param reflectedObject2
	 */
	public static void copyParamterHandles(ShapeGraphic primaryShape, SimpleGraphicalObject reflectedObject2) {
	
		if(primaryShape instanceof HasSmartHandles && reflectedObject2 instanceof HasSmartHandles) {
			HasSmartHandles model=(HasSmartHandles) primaryShape;
			HasSmartHandles target=(HasSmartHandles) reflectedObject2;
			SmartHandleList list1 = model.getSmartHandleList();
			SmartHandleList list2 = target.getSmartHandleList();
			for(SmartHandle mHandle: list1)
				for(SmartHandle tHandle: list2) {
				
					if(mHandle.getHandleNumber()==tHandle.getHandleNumber()&&mHandle instanceof RectangleEdgeHandle && tHandle instanceof RectangleEdgeHandle) {
						
						RectangleEdgeHandle mHandle2=(RectangleEdgeHandle) mHandle;
						RectangleEdgeHandle tHandle2=(RectangleEdgeHandle) tHandle;
						tHandle2.copyValuesFrom(mHandle2);
					}
				}
		}
		
	}
	
	/**
	Assuming that a reflected objects starts in one panel (primary panel), moves the object to the equivalent location in the target panel
	This only needs to be done once to put each copy with the right panel
	 */
	protected static void moveReflectionToDestinationPanel(PanelAddress targetPanel2, PanelAddress primaryPanel, SimpleGraphicalObject reflectedObject) {
		Point2D pf = targetPanel2.getLocationUpperLeft();
		Point2D pi = primaryPanel.getLocationUpperLeft();
		
		double dx = pf.getX()-pi.getX();
		double dy = pf.getY()-pi.getY();
		reflectedObject.moveLocation(dx, dy);
	}
	/**
	 * 
	 */
	public static void updateLocation(PanelAddress targetPanel2, PanelAddress primaryPanel, SimpleGraphicalObject reflectedObject, ShapeGraphic primaryShape) {
		
		/**what to do if the mirrored object is a rectangle*/
		if(reflectedObject instanceof RectangularGraphic && primaryShape instanceof RectangularGraphic ) {
			 RectangularGraphic r= (RectangularGraphic) reflectedObject ;
			 r.copyAttributesFrom(primaryShape);
			 r.setRectangle(((RectangularGraphic) primaryShape).getRectangle());
			 r.setAngle(primaryShape.getAngle());
			 moveReflectionToDestinationPanel(targetPanel2, primaryPanel, reflectedObject) ;
		}
		
		if(reflectedObject instanceof ArrowGraphic && primaryShape instanceof ArrowGraphic ) {
			ArrowGraphic r= (ArrowGraphic) reflectedObject ;
			 r.copyAttributesFrom(primaryShape);
			 ArrowGraphic primary=(ArrowGraphic) primaryShape;
			 r.setPoints(primary.getLineStartLocation(), primary.getLineEndLocation());
			r.copyArrowAtributesFrom(primary);
			 moveReflectionToDestinationPanel(targetPanel2, primaryPanel, reflectedObject) ;
		}
		
		/**what to do if the mirrored object is a rectangle*/
		if(reflectedObject instanceof FrameGraphic && primaryShape instanceof PanelGraphicInsetDefiner ) {
			FrameGraphic r= (FrameGraphic) reflectedObject ;
			// 
			 PanelGraphicInsetDefiner primaryShape2 = (PanelGraphicInsetDefiner ) primaryShape;
			r.setRectangle(primaryShape2.getRectangle());
			 r.setAngle(primaryShape.getAngle());
			 moveReflectionToDestinationPanel(targetPanel2, primaryPanel, reflectedObject) ;
		}
		
		/**what to do if the mirrored object is a rectangle*/
		if(reflectedObject instanceof PanelGraphicInsetDefiner && primaryShape instanceof PanelGraphicInsetDefiner ) {
			PanelGraphicInsetDefiner r= (PanelGraphicInsetDefiner) reflectedObject ;
			// 
			 PanelGraphicInsetDefiner primaryShape2 = (PanelGraphicInsetDefiner ) primaryShape;
			 new InsetMenu.ChangeInsetScale(r, primaryShape2.getInsetScale()).rescale();
		}
		
		copyParamterHandles(primaryShape, reflectedObject);
		
	}

}
