/**
 * Author: Greg Mazo
 * Date Created: Nov 4, 2022
 * Date Modified: Nov 4, 2022
 * Copyright (C) 2022 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package graphicalObjects_SpecialObjects;

import java.io.Serializable;
import java.util.ArrayList;

import graphicalObjects.ZoomableGraphic;

/**
 A class for storing a list of objects that are drawn in front of an image panel
 */
public class OverlayObjectList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private transient ArrayList<Object> overlayObjects=new ArrayList<Object>();


	public ArrayList<Object> getOverlayObjects() {
		return overlayObjects;
	}


	public void setOverlayObjects(ArrayList<Object> overlayObjects) {
		this.overlayObjects = overlayObjects;
	}


	/**
	 * @param overlayObjectList
	 */
	public void addAll(ArrayList<Object> overlayObjectList) {
		 getOverlayObjects().addAll(overlayObjectList);
		
	}


	/**
	 * @param overlayObjects2
	 */
	public void addAll(OverlayObjectList overlayObjects2) {
		addAll(overlayObjects2.getOverlayObjects());
		
	}


	/**
	 * @param b
	 */
	public void add(ZoomableGraphic b) {
		this.overlayObjects.add(b);
		
	}
	

}
