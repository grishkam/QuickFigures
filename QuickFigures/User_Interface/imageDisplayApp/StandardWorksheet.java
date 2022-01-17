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
 * Date Modified: Jan 12, 2021
 * Version: 2022.0
 */
package imageDisplayApp;


import java.awt.Dimension;

import applicationAdapters.DisplayedImage;
import applicationAdapters.GenericImage;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects.KnowsSetContainer;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.GraphicLayerPane;
/**Simple subclass of the generic image
 * that contains an object linking it to the 
 * the window being used to display the */
public class StandardWorksheet extends GenericImage {

	public static final String SAVE_EXTENSION = ".ser";

	
	private String savePath=null;//where was this saved
	
	/**The window that displays the item*/
	private transient ImageWindowAndDisplaySet displayItems=null;

	
	/**minimal constructor*/
	public StandardWorksheet() {
		this(new GraphicLayerPane("Name"), new BasicImageInfo());
	
	}
	
	/**creates an image with the given file info and parent layer*/
	public StandardWorksheet(GraphicLayerPane layer2, BasicImageInfo basics) {
		super.setLayer(layer2);
		super.setObjectContainer(layer2);
		onItemLoad(layer2);
		this.basics=basics;
	}


	/**returns the object storing this image, window and canvas*/
	@Override
	public DisplayedImage getImageDisplay() {
		return displayItems;
	}
	
	/**the title always matches the name of the top level layer*/
	@Override
	public String getTitle() {
		if (getTopLevelLayer()==null) return null;
		return this.getTopLevelLayer().getName();
	}
	
	/**returns the name used when this will be saved*/
	public String getSaveName() {
		if (getTopLevelLayer()==null) return null;
		return this.getTopLevelLayer().getName()+SAVE_EXTENSION;
	}
	
	/**Sets the title*/
	public void setTitle(String t) {
		this.getTopLevelLayer().setName(t);
	}


	
	public void setDisplayGroup(ImageWindowAndDisplaySet displayGroup) {
		this.displayItems=displayGroup;
		
	}
	
	


	@Override
	public void updateDisplay() {
		displayItems.updateDisplay();
		
	}


	@Override
	public Dimension getCanvasDims() {
		return new Dimension(getWidth(),getHeight());
	}
	


	


	
	/**this method is run to initialize an added item
	  it is also called after objects have been deserialized*/
	public void onItemLoad(ZoomableGraphic z) {
		
		if (z instanceof KnowsSetContainer) {
			KnowsSetContainer kn=(KnowsSetContainer) z;
			kn.setGraphicSetContainer(this);
		}
		if (z instanceof GraphicLayer) {
			for(ZoomableGraphic g: ((GraphicLayer) z).getAllGraphics()) {
				onItemLoad(g);
			}
			for(ZoomableGraphic g: ((GraphicLayer) z).getSubLayers()) {
				onItemLoad(g);
				
			}
		}
	}
	
	
	/**Getter method for width*/
	public int getWidth() {
		return getBasics().getWidth();
	}

	
	/**Setter method for width*/
	public void setWidth(int width) {
		getBasics().setWidth(width);
	}

	
	/**Getter method for height*/
	public int getHeight() {
		return getBasics().getHeight();
	}

	/**Setter method for height*/
	public void setHeight(int height) {
		getBasics().setHeight(height);
	}
	
	/**called when the worksheet is resized*/
	@Override
	public void worksheetResize(int width, int height, int xOff, int yOff) {
		getBasics().setWidth(width);
		getBasics().setHeight(height);
		
	}

	public BasicImageInfo getBasics() {
		return basics;
	}

	public void setBasics(BasicImageInfo basics) {
		this.basics = basics;
	}

	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}
	
	@Override
	public boolean allowAutoResize() {
		if(CanvasOptions.current!=null &&!CanvasOptions.current.resizeCanvasAfterEdit)
			return false;
		return !getBasics().isAutoResizeBlocked();
	}
	
}
