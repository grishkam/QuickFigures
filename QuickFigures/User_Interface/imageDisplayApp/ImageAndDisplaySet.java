package imageDisplayApp;


import undo.UndoManagerPlus;
import utilityClassesForObjects.Selectable;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.geom.Point2D;

import javax.swing.JComponent;

import applicationAdapters.DisplayedImage;
import applicationAdapters.ImageWrapper;
import graphicalObjects.BasicCordinateConverter;
import layersGUI.GraphicTreeUI;

/**see description for interface*/
public class ImageAndDisplaySet implements DisplayedImage {
	public static GraphicTreeUI exampletree;
	private GraphicSetDisplayWindow theWindow=null;
	private GraphicDisplayCanvas theCanvas=null;
	private GraphicContainingImage theSet=null;
	private int currentFrame=0;
	private int endFrame=200;
	private Selectable selectedItem=null;
	
	transient UndoManagerPlus undoMan=null;
	
	/**Generates a Display of the given graphic set*/
	public ImageAndDisplaySet(GraphicContainingImage graphicSet) {
		this.setTheSet( graphicSet);
		GraphicDisplayCanvas canvas = new GraphicDisplayCanvas();
		this.setTheCanvas(canvas);
		//updateCanvasDims();//
		
		this.setTheWindow(new GraphicSetDisplayWindow(this,canvas));
		this.getTheWindow().reSetCanvasAndWindowSizes() ;
		centreWindow(this.getWindow());
		ensureAllLinked();
	}
	
	/**
	public ImageAndDisplaySet(GraphicDisplayCanvas graphicDisplayCanvas,
			GraphicSet graphicSet, GraphicSetDisplayWindow canvasDisplayWindow) {
		this.setTheCanvas(graphicDisplayCanvas);
		this.setTheSet( graphicSet);
		setTheWindow(canvasDisplayWindow);
		
		ensureAllLinked();
	}*/
	
	
	void ensureAllLinked() {
		this.getTheWindow().setDisplaySet(this);
	}
	
	public JComponent getTheCanvas() {
		return theCanvas;
	}
	
	
	public void setTheCanvas(GraphicDisplayCanvas theCanvas) {
		
		this.theCanvas = theCanvas;
		
	}
	public GraphicSetDisplayWindow getTheWindow() {
		return theWindow;
	}
	public void setTheWindow(GraphicSetDisplayWindow theWindow) {
		this.theWindow = theWindow;
		if (theWindow!=null) {
			theWindow.setDisplaySet(this);
			
		}
	}
	public GraphicContainingImage getTheSet() {
		return theSet;
	}
	public void setTheSet(GraphicContainingImage theSet) {
		this.theSet = theSet;
		theSet.undoManager=this.getUndoManager();
		if (theSet!=null)	theSet.setDisplayGroup(this);
	}
	
	
	int count =0;
	public void updateDisplay() {
		
		if (this.getTheCanvas()==null) return;
		//updateCanvasDims();
		theCanvas.repaint();
		//theCanvas.paint(theCanvas.getGraphics());
		//IssueLog.log("updating display");
		//if (count>7) throw new NullPointerException();
		 count++;
	}
	
	public BasicCordinateConverter getConverter() {
		if (theWindow==null) {
			//IssueLog.log("Problem: Cordinate conversion factor requested despite no window being set");
		return new BasicCordinateConverter();}
		return theWindow.getZoomer().getConverter();
	}
	
	@Override
	public ImageWrapper getImageAsWrapper() {
		return theSet;
	}
	@Override
	public Window getWindow() {
		// TODO Auto-generated method stub
		return this.theWindow;
	}
	
	
	
	public static ImageAndDisplaySet createAndShowNew(String title, int width, int height) {
		GraphicContainingImage gs = new GraphicContainingImage();
		gs.setTitle(title);
		gs.getBasics().setWidth(width);
		gs.getBasics().setHeight(height);
		return  show(gs);
	}
	
	public static ImageAndDisplaySet  show(GraphicContainingImage gs) {
		ImageAndDisplaySet set = new ImageAndDisplaySet(gs);
		Window win = set.getWindow();
		win.pack();
		return set;
		
	}
	
	@Override
	public void updateWindowSize() {
		this.getTheWindow().reSetCanvasAndWindowSizes();
		
	}
	
	public static void centreWindow(Window frame) {
	    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
	    frame.setLocation(x, y);
	}

	@Override
	public void setCursor(Cursor c) {
		if (c==null)return;
		if (theCanvas.getCursor().equals(c)) return;
	
		theCanvas.setCursor(c);
	
	}

	public void autoZoom() {
		this.getTheWindow().comfortZoom();
		
	}

	@Override
	public void zoomOutToFitScreen() {
		getTheWindow().shrinktoFit();
		
	}

	public UndoManagerPlus getUndoManager() {
		if ( undoMan==null) {
			undoMan=new UndoManagerPlus();
			if (theSet!=null)theSet.undoManager=undoMan;
		}
		
		return undoMan;
	}

	@Override
	public void zoom(String st) {
		getTheWindow().zoom(st);
		
	}
	
	@Override
	public void setZoomLevel(double st) {
		getTheWindow().getZoomer().setZoom(st);
	}


	public int getCurrentFrame() {
		return currentFrame;
	}

	public void setCurrentFrame(int currentFrame) {
		this.currentFrame = currentFrame;
	}

	public int getEndFrame() {
		return endFrame;
	}

	public void setEndFrame(int endFrame) {
		this.endFrame = endFrame;
	}

	@Override
	public void scrollPane(double dx, double dy) {
		theWindow.scrollPane(dx, dy);
		
	}
	
	@Override
	public void setScrollCenter(double dx, double dy) {
		theWindow.centerZoom(new Point2D.Double(dx, dy));
		
	}

	@Override
	public void closeWindowButKeepObjects() {
		this.getTheWindow().closeGroupWithoutObjectDeath();
		
	}
	
	public String toString() {
		return this.getTheSet().getTitle();
	}

	public Selectable getSelectedItem() {
		if (selectedItem!=null&&!selectedItem.isSelected()) return null;
		return selectedItem;
	}

	public void setSelectedItem(Selectable selectedItem) {
		this.selectedItem = selectedItem;
	}

	@Override
	public double getZoomLevel() {
		return 100*getTheWindow().getZoomer().getZoom();
	}

	
	
}
