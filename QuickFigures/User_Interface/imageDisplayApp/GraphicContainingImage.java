package imageDisplayApp;


import java.awt.Dimension;

import applicationAdapters.DisplayedImage;
import applicationAdapters.GenericImage;
import graphicalObjects.FigureDisplayContainer;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects.KnowsSetContainer;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.GraphicLayerPane;
/**Simple subclass of the generic image
 * that contains an object linking it to the 
 * the window being used to display the */
public class GraphicContainingImage extends GenericImage implements FigureDisplayContainer {

	public static final String SAVE_EXTENSION = ".ser";

	
	private String savePath=null;
	/**The window that displays the item*/
	private transient ImageWindowAndDisplaySet displayItems=null;

	private BasicImageInfo basics=new BasicImageInfo();
	
	/**creates an image with the given file info and parent layer*/
	public GraphicContainingImage(GraphicLayerPane layer2, BasicImageInfo basics) {
		super.setLayer(layer2);
		super.setObjectContainer(layer2);
		onItemLoad(layer2);
		this.basics=basics;
	}

	public GraphicContainingImage() {
		this(new GraphicLayerPane("Name"), new BasicImageInfo());
	
	}
	
	/***/
	public static void onItemLoad(FigureDisplayContainer gsd, ZoomableGraphic z) {
	
		if (z instanceof KnowsSetContainer) {
			KnowsSetContainer kn=(KnowsSetContainer) z;
			kn.setGraphicSetContainer(gsd);
		}
		if (z instanceof GraphicLayer) {
			for(ZoomableGraphic g: ((GraphicLayer) z).getAllGraphics()) {
				onItemLoad(gsd, g);
			}
			for(ZoomableGraphic g: ((GraphicLayer) z).getSubLayers()) {
				onItemLoad(gsd, g);
			}
		}
	}
	
	
	
	/**returns the object storing this image, window and canvas*/
	@Override
	public DisplayedImage getImageDisplay() {
		return displayItems;
	}
	

	
	
	@Override
	public String getTitle() {
		if (getGraphicLayerSet()==null) return null;
		return this.getGraphicLayerSet().getName();
	}
	
	public String getSaveName() {
		if (getGraphicLayerSet()==null) return null;
		return this.getGraphicLayerSet().getName()+SAVE_EXTENSION;
	}
	
	
	public void setTitle(String t) {
		this.getGraphicLayerSet().setName(t);
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
	
	/***/
	@Override
	public void CanvasResizePixelsOnly(int width, int height, int xOff, int yOff) {
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
	
}
