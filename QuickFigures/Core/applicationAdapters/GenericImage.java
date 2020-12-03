package applicationAdapters;

import infoStorage.MetaInfoWrapper;
import logging.IssueLog;
import undo.UndoManagerPlus;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.ObjectContainer;
import utilityClassesForObjects.ScaleInfo;

import java.awt.Dimension;
import java.awt.Window;
import java.util.ArrayList;

import genericMontageKit.OverlayObjectManager;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import imageDisplayApp.BasicImageInfo;


/** A barebones implementation of the interface
 This class has a collection of objects. 
  This class is used as just a 'virtual' image  by certain classes
  during modification of objects. some methods are not implemented
  Only the subclass is a more complete implementation
   Also @see GraphicContainingImage
  */
public class GenericImage implements ImageWrapper {

	
	transient public UndoManagerPlus undoManager;
	protected BasicImageInfo basics=new BasicImageInfo();
	int id=(int)Math.random()*100000;// a random id number
	ObjectContainer objects;
	//transient PixelContainer pixels;
	private GraphicLayer layer;
	OverlayObjectManager selectionManager=new OverlayObjectManager();
	private String title;

	private transient Object selected;
	

	
	/**should not be called*/
	protected GenericImage() {}
	
	public GenericImage(ObjectContainer c) {
		setObjectContainer(c);
	}
	public void setObjectContainer(ObjectContainer c) {
		objects =c;
	}
	
	
	@Override
	public void takeFromImage(LocatedObject2D roi) {
		if (objects!=null)objects.takeFromImage(roi);
		
	}

	@Override
	public void addItemToImage(LocatedObject2D roi) {
		if (objects!=null)objects.addItemToImage(roi);
		
	}

	@Override
	public void addRoiToImageBack(LocatedObject2D roi) {
		if (objects!=null)objects.addRoiToImageBack(roi);
		
	}

	@Override
	public ArrayList<LocatedObject2D> getLocatedObjects() {
		if (objects!=null)return objects.getLocatedObjects();
		return null;
	}

	@Override
	public LocatedObject2D getSelectionObject() {
		if(selected==null) return null;
		if (selected instanceof LocatedObject2D) return (LocatedObject2D) selected;
		return null;
	}

	

	@Override
	public void CanvasResize(int width, int height, int xOff, int yOff) {
		
	}

	@Override
	public int width() {
		if (basics!=null) return basics.getWidth();
		return 0;
	}

	@Override
	public int height() {
		if (basics!=null) return basics.getHeight();
		return 0;
	}



	@Override
	public String getTitle() {
		return title;
	}
	public void setTitle(String t) {
		title=t;
	}

	@Override
	public String getPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Window window() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	
	

	@Override
	public ScaleInfo getScaleInfo() {
		// TODO Auto-generated method stub
		return new ScaleInfo();
	}


	@Override
	public GraphicLayer getGraphicLayerSet() {
		if (getLayer()==null) {IssueLog.log("error no layer in generic image");}
		return getLayer();
	}


	


	@Override
	public void onItemLoad(ZoomableGraphic z) {
		// TODO Auto-generated method stub
		
	}


	public GraphicLayer getLayer() {
		return layer;
	}


	public void setLayer(GraphicLayer layer) {
		this.layer = layer;
	}


	

	@Override
	public Dimension getCanvasDims() {
		if (basics!=null) return basics.getDimensions();
		return new Dimension();
	}


	@Override
	public ImageWrapper getAsWrapper() {
		return this;
	}


	@Override
	public MetaInfoWrapper getMetadataWrapper() {
		return null;
	}

	@Override
	public OverlayObjectManager getOverlaySelectionManagger() {
	
		return selectionManager;
	}

	@Override
	public void updateDisplay() {
	}



	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return id;
	}

	
	@Override
	public boolean containsImage() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isSameImage(Object o) {
		// TODO Auto-generated method stub
		return o==this;
	}

	@Override
	public void setScaleInfo(ScaleInfo scaleInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DisplayedImage getImageDisplay() {
		return null;
	}

	@Override
	public UndoManagerPlus getUndoManager() {
		if ( undoManager==null)  undoManager=new UndoManagerPlus();
		return  undoManager;
	}

	@Override
	public boolean setPrimarySelectionObject(Object d) {
		
		 selected=d;
		 return true;
	}



	



	
	

}
