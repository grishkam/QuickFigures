package imageDisplayApp;


import undo.UndoManagerPlus;
import utilityClassesForObjects.Selectable;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.geom.Point2D;

import javax.swing.JComponent;

import applicationAdapters.CanvasMouseEventWrapper;
import applicationAdapters.DisplayedImage;
import applicationAdapters.ImageWrapper;
import graphicalObjectHandles.SmartHandle;
import graphicalObjectHandles.SmartHandleList;
import graphicalObjects.BasicCoordinateConverter;
import layersGUI.GraphicTreeUI;

/**see description for interface*/
public class ImageWindowAndDisplaySet implements DisplayedImage {
	public static GraphicTreeUI exampletree;
	private GraphicSetDisplayWindow theWindow=null;
	private GraphicDisplayCanvas theCanvas=null;
	private GraphicContainingImage theFigure=null;
	private int currentFrame=0;
	private int endFrame=200;
	private Selectable selectedItem=null;
	
	transient UndoManagerPlus undoMan=null;
	
	/**Generates a Display of the given graphic set*/
	public ImageWindowAndDisplaySet(GraphicContainingImage graphicSet) {
		this.setTheSet( graphicSet);
		GraphicDisplayCanvas canvas = new GraphicDisplayCanvas();
		this.setTheCanvas(canvas);
		//updateCanvasDims();//
		
		this.setTheWindow(new GraphicSetDisplayWindow(this,canvas));
		this.getTheWindow().reSetCanvasAndWindowSizes() ;
		centreWindow(this.getWindow());
		ensureAllLinked();
	}
	
	
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
		return theFigure;
	}
	public void setTheSet(GraphicContainingImage theSet) {
		this.theFigure = theSet;
		theSet.undoManager=this.getUndoManager();
		if (theSet!=null)	theSet.setDisplayGroup(this);
	}
	
	
	int count =0;
	private MiniToolBarPanel sidePanel;
	public void updateDisplay() {
		
		if (this.getTheCanvas()==null) return;
		
		theCanvas.repaint();
		if (this.sidePanel!=null) sidePanel.repaint();
		 count++;
	}
	
	public BasicCoordinateConverter getConverter() {
		if (theWindow==null) {
			//IssueLog.log("Problem: Cordinate conversion factor requested despite no window being set");
		return new BasicCoordinateConverter();}
		return theWindow.getZoomer().getConverter();
	}
	
	@Override
	public ImageWrapper getImageAsWrapper() {
		return theFigure;
	}
	@Override
	public Window getWindow() {
		return this.theWindow;
	}
	
	
	
	public static ImageWindowAndDisplaySet createAndShowNew(String title, int width, int height) {
		GraphicContainingImage gs = new GraphicContainingImage();
		gs.setTitle(title);
		gs.getBasics().setWidth(width);
		gs.getBasics().setHeight(height);
		return  show(gs);
	}
	
	/**creates the window an user interface elements needed to display the image*/
	public static ImageWindowAndDisplaySet  show(GraphicContainingImage gs) {
		ImageWindowAndDisplaySet set = new ImageWindowAndDisplaySet(gs);
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
			if (theFigure!=null)theFigure.undoManager=undoMan;
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

	/**returns the zoom level (100% is no zoom)*/
	@Override
	public double getZoomLevel() {
		return 100*getTheWindow().getZoomer().getZoomMagnification();
	}


	public void setSidePanel(MiniToolBarPanel miniToolBarPanel) {
		sidePanel=miniToolBarPanel;
		
	}

	/**a handle used for resizing the canvas*/
	class CanvasResizeHandle extends SmartHandle {

		public CanvasResizeHandle(ImageWindowAndDisplaySet s) {
			super(0, 0);
			this.setHandleNumber(999910044);
			this.setHandleColor(Color.DARK_GRAY);
			
		}
		
		public Point2D getCordinateLocation() {
			Dimension d = theFigure.getCanvasDims();
			return new Point2D.Double(d.getWidth(), d.getHeight());
			}
		public void handleDrag(CanvasMouseEventWrapper lastDragOrRelMouseEvent) {
			Point p = lastDragOrRelMouseEvent.getCoordinatePoint();
			theFigure.getBasics().setWidth(p.x);
			theFigure.getBasics().setHeight(p.y);
			//updateDisplay();
			theWindow.resetCanvasSize();
		}
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
	}


	transient SmartHandleList canvasHandleList;
	@Override
	public SmartHandleList getCanvasHandles() {
		if (canvasHandleList==null) canvasHandleList = SmartHandleList.createList(new CanvasResizeHandle(this));
		return canvasHandleList;
	}
	
}
