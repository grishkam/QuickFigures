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

import genericMontageKit.SelectionManager;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import imageDisplayApp.BasicGraphicalObjectCreator;

/**This class has a collection of pixels and objects. most classes just use it as a stand in*/
public class GenericImage implements ImageWrapper {

	
	transient public UndoManagerPlus undoManager;
	
	int id=(int)Math.random()*100000;
	ObjectContainer objects;
	transient PixelContainer pixels;
	private GraphicLayer layer;
	SelectionManager selectionManager=new SelectionManager();
	ObjectCreator objectCreator=new BasicGraphicalObjectCreator();
	private String title;
	
	public GenericImage(ObjectContainer c, PixelContainer pix) {
		objects =c;
		pixels=pix;
	}
	
	/**should not be called*/
	public GenericImage() {}
	
	public GenericImage(ObjectContainer c) {
		setObjectContainer(c);
	}
	public void setObjectContainer(ObjectContainer c) {
		objects =c;
	}
	
	
	@Override
	public void takeRoiFromImage(LocatedObject2D roi) {
		if (objects!=null)objects.takeRoiFromImage(roi);
		
	}

	@Override
	public void addRoiToImage(LocatedObject2D roi) {
		if (objects!=null)objects.addRoiToImage(roi);
		
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PixelWrapper getPixelWrapper() {
	if (pixels!=null) return pixels.getPixelWrapper();
		return null;
	}

	@Override
	public void CanvasResizePixelsOnly(int width, int height, int xOff, int yOff) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int width() {
		if (pixels!=null) return pixels.width();
		return 0;
	}

	@Override
	public int height() {
		if (pixels!=null) return pixels.height();
		return 0;
	}



	@Override
	public String getTitle() {
		return title;
		//return null;
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
		if (pixels!=null) return pixels.getPixelWrapper().dim();
		return new Dimension();
	}


	@Override
	public ImageWrapper getAsWrapper() {
		return this;
	}


	@Override
	public MetaInfoWrapper getMetadataWrapper() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SelectionManager getSelectionManagger() {
		// TODO Auto-generated method stub
		return selectionManager;
	}

	@Override
	public void updateDisplay() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ObjectCreator getDefaultObjectCreator() {
		// TODO Auto-generated method stub
		return objectCreator;
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
	public DisplayedImageWrapper getImageDisplay() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UndoManagerPlus getUndoManager() {
		if ( undoManager==null)  undoManager=new UndoManagerPlus();
		return  undoManager;
	}



	



	
	

}
