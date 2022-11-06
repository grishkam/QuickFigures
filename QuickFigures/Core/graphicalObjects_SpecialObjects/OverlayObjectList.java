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

import graphicalObjects_LayerTypes.GraphicLayerPane;

/**
 A class for storing a list of objects that are drawn in front of an image panel
 */
public class OverlayObjectList extends GraphicLayerPane implements Serializable {

	/**
	 * @param name
	 */
	public OverlayObjectList(String name) {
		super(name);
	}


	/**
	 * 
	 */
	public OverlayObjectList() {
		super("");
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private ArrayList<Object> overlayObjects=new ArrayList<Object>();


	public ArrayList<?> getOverlayObjects() {
		return super.getAllGraphics();
	}


	public void setOverlayObjects(ArrayList<Object> overlayObjects) {
		this.overlayObjects = overlayObjects;
	}


	/***/
	public  OverlayObjectList copy() {
		OverlayObjectList output = new  OverlayObjectList(description);
		for(Object o:this.overlayObjects) {
			output.overlayObjects.add(o);
		}
		return output;
	}
}
