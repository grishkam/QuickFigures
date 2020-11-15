package imageDisplayApp;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import javax.imageio.ImageIO;
import javax.swing.BoundedRangeModel;
import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import addObjectMenus.PasteItem;
import applicationAdapters.DisplayedImage;
import basicAppAdapters.GMouseEvent;
import basicMenusForApp.SelectedSetLayerSelector;
import basicMenusForApp.MenuBarForApp;
import exportMenus.FlatCreator;
import externalToolBar.DragAndDropHandler;
import externalToolBar.InterfaceExternalTool;
import externalToolBar.InterfaceKeyStrokeReader;
import externalToolBar.ToolBarManager;
import graphicActionToolbar.CurrentFigureSet;
import graphicalObjects_LayerTypes.GraphicLayer;
import logging.IssueLog;
import selectedItemMenus.CopyItem;
import selectedItemMenus.ItemRemover;
import ultilInputOutput.FileChoiceUtil;
import utilityClassesForObjects.Mortal;

public class GraphicSetDisplayWindow extends JFrame implements KeyListener, MouseListener, MouseMotionListener, DropTargetListener{
	
	/**
	 * 
	 */
	/**keeps a count of how many of these windows have been created*/
	static int windowCount=0;
	int windowNumber=windowCount+1; {
		windowCount+=1;
	}
	
	private static final long serialVersionUID = 1L;
	boolean buf=false;
	
	private ImageZoom zoomer=new ImageZoom();//object to keep track of how zoomed in or out a user is
	private JScrollPane pane=new JScrollPane();//the scroll pane for the canvas
	
	private GraphicDisplayCanvas theCanvas=null;//the canvas
	
	private ImageAndDisplaySet display;
	/**although there is no user option for this. a programmer can create a version of the 
	  window that does not use a scroll pane but instead indicates the position 
	  in the same way as imageJ does. in that case a scroll indicator need be drawn*/
	ScrollIndicator indicator=new ScrollIndicator(this);
	
	/**the position of the most recent click is stored here*/
	private MouseEvent lastPress;
	private Point2D lastPointCordinate;
	
	/**does this windows use a scroll pane*/
	private boolean useScrollPane=true;
	
	public boolean usesScrollPane() {
		return useScrollPane;
	}
	
	/**constructor for the window*/
	public GraphicSetDisplayWindow(ImageAndDisplaySet set, GraphicDisplayCanvas canvas) {
		display=set;
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setUpCanvas(canvas);
		setJMenuBar(new  MenuBarForApp());
	}
	
	/**returns the dimensions the canvas needs to be in order to display the image
	  at the given zoom and canvas display size.*/
	public Dimension getMaxNeededCanvasSizeforGraphicSet() {
		setTitleBasedOnSet() ;
		int mw=(int)Math.ceil(getTheSet().getWidth()*getZoomer().getZoom());
		int mh= (int)Math.ceil(getTheSet().getHeight()*getZoomer().getZoom());
		return new Dimension(mw,mh);
	}
	
	/**information about the zoom level will appear in the window title after this is called*/
	private void setTitleBasedOnSet() {
		this.setTitle(this.getDisplaySet().getTheSet().getTitle()+"          Zoom = "+100*getZoomer().get2SigFigZoom()+"%");
	}
	

	/**Automatically sets the windows size to something that better fits the objects in the 
	  figure*/
	void reSetCanvasAndWindowSizes() {
		Dimension b1 = determineMaxBounds();
		Dimension b2 = getMaxNeededCanvasSizeforGraphicSet();
		
		b2.width=Math.min(b1.width, b2.width);
		b2.height=Math.min(b1.height, b2.height);
		
		Dimension b3 = new Dimension(b2.width+42, b2.height+30);
	
		getTheCanvas().setPreferredSize( b3 );
		getTheCanvas().setSize(b2);
		pack();
	}
	
	
	
	/**returns the max allowable bounds for a window. this is set to be comfortably smaller than the screen*/
	static Dimension determineMaxBounds() {
		Dimension bounds = Toolkit.getDefaultToolkit().getScreenSize();
        bounds.width= bounds.width*8/10;
        bounds.height=bounds.height*8/10;
       return bounds;
		
	}
	
	/**this reduces the zoom untill the image is small enough to 
	  fit comfortable on screen*/
	public void shrinktoFit() {
		while(!willFitInMaxWindowSize()) {
			this.getZoomer().zoomOut();
		}
	}
	
	/**returns true if the canvas is smaller than the max size for the windows*/
	boolean willFitInMaxWindowSize() {
		Dimension size1 = getMaxNeededCanvasSizeforGraphicSet();
		Dimension b2 = determineMaxBounds();
		if (size1.width> b2.width) return false;
		if (size1.height> b2.height) return false;
		return true;
	}
	
	/**returns the recommended size for the window*/
	Dimension getReccomendedSize() {
		Dimension size1 = getMaxNeededCanvasSizeforGraphicSet();
		Dimension b2 = determineMaxBounds();
		size1.width=Math.min(size1.width, b2.width);
		size1.height=Math.min(size1.height, b2.height);
		return size1;
	}
	
	
	
	
	/**sets up the canvas and shows the window*/
	public void setUpCanvas(GraphicDisplayCanvas canvas) {
		if (canvas==null) {IssueLog.log("no canvas");}
		this.theCanvas=canvas;
		
		if (useScrollPane) {
			
			pane=new SpecialPaneForCanvas(getTheCanvas());
			this.add(pane);
			pane.setPreferredSize(getTheCanvas().getPreferredSize());
			//pane.setPreferredSize(preferredSize);
		} else
		this.add(getTheCanvas());

		//this.add(p);
		
		this.addKeyListener(this);
		this.addKeyListener(new KeyDownTracker());
		getTheCanvas().addKeyListener(this);
		getTheCanvas().addMouseListener(this);
		getTheCanvas().addMouseMotionListener(this);
		getTheCanvas().setDispWindow(this);
		new DropTarget(getTheCanvas(), this);
		getDisplaySet().updateDisplay();
		this.setVisible(true);
		reSetCanvasAndWindowSizes() ;
		
		this.pack();
	
	}


/**
	    
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ImageDisplayTester.main(args);
		
		
			GraphicSetDisplayWindow win = new GraphicSetDisplayWindow(new GraphicDisplayCanvas());
			win.setJMenuBar(new  MenuBarForApp());
			RectangularGraphic rr = new RectangularGraphic(3,3, 40,40);
			rr.setFillColor(Color.black); 
			rr.setFilled(true);
			win.getTheSet().getGraphicLayerSet().add(rr);
			
			RhombusGraphic rr2=new RhombusGraphic();
			rr=rr2;
			rr.setRectangle(new Rectangle(30,5, 40,30));
			rr.setFillColor(Color.green); 
			rr.setFilled(true);
			win.getTheSet().getGraphicLayerSet().add(rr);
			rr2.setAngleBend(Math.PI/8);
			BasicMontageLayout bl = new BasicMontageLayout(2, 3, 100,100,10,10, true);
			MontageLayoutGraphic gl = new MontageLayoutGraphic(bl);
			//gl.setLocationUpperLeft(100, 200);
		    
			win.getTheSet().getGraphicLayerSet().add(gl);
			
			ObjectToolset1 ot = new ObjectToolset1();
			ot.run("hi");
			
			win.show();
	}
*/
	
	
	public InterfaceKeyStrokeReader<DisplayedImage> getStrokeReader() {
		if (ToolBarManager.getCurrentTool()==null) return null;
		return ToolBarManager.getCurrentTool().getCurrentKeyStrokeReader();
	}
	
	
	

	@Override
	public void keyPressed(KeyEvent arg0) {
		
		
		if (getStrokeReader() !=null) {
			getStrokeReader().keyPressed(getDisplaySet(), arg0);
			
		}
		
		
		
		if (arg0.getKeyChar()=='=') {
			if(!arg0.isConsumed())
			ZoomIn();
			
		}
		if (arg0.getKeyCode()==KeyEvent.VK_MINUS||arg0.getKeyChar()=='-') {
			if(!arg0.isConsumed())
			ZoomOut();
			
		}
		
		if (arg0.getKeyCode()==KeyEvent.VK_PLUS||arg0.getKeyChar()=='+') {
			if(!arg0.isConsumed())
			this.comfortZoom();
		}
		
		if (arg0.getKeyChar()=='_') {
			scrollToComfort();
		}
		
 		if (arg0.getKeyChar()=='p'&&arg0.isAltDown()) {
			pack();
		}
 		
 		
 		boolean WindowsOrMacMeta=false;
 		if (IssueLog.isWindows() &&arg0.isControlDown()) {
 			
 			WindowsOrMacMeta=true;
 		}
 		if (!IssueLog.isWindows() &&arg0.isMetaDown()) WindowsOrMacMeta=true;
 		
 		
 		/**implementation of undo and redo*/
 		if (arg0.getKeyCode()==KeyEvent.VK_Z&&WindowsOrMacMeta) {
			if (getDisplaySet().getUndoManager().canUndo()) {
							this.getDisplaySet().getUndoManager().undo();
							
				};
		}
 		

		if (arg0.getKeyCode()==KeyEvent.VK_Y&&WindowsOrMacMeta) {
					
					if (getDisplaySet().getUndoManager().canRedo())this.getDisplaySet().getUndoManager().redo();
				}
		
		SelectedSetLayerSelector selector = new SelectedSetLayerSelector(this.getTheSet());
		/**The copy and paste options*/
				if (arg0.getKeyCode()==KeyEvent.VK_C&&WindowsOrMacMeta) {
					new FlatCreator().toSystemClip(getTheSet());
					CopyItem cc = new CopyItem();
					cc.setSelector(selector);
					cc.run();
					}
				
				if (arg0.getKeyCode()==KeyEvent.VK_X&&WindowsOrMacMeta) {
					new FlatCreator().toSystemClip(getTheSet());
					CopyItem cc = new CopyItem();
					cc.setSelector(selector);
					cc.run();
					
					ItemRemover ir = new ItemRemover();
					ir.setSelector(selector);
					ir.setSelection(selector.getSelecteditems());
					ir.run();
					this.repaint();
					}
				
				if (arg0.getKeyCode()==KeyEvent.VK_V&&WindowsOrMacMeta) {
					
					PasteItem pp = new PasteItem();
					pp.setSelector(new SelectedSetLayerSelector(this.getTheSet()));
					
					pp.add(new SelectedSetLayerSelector(this.getTheSet()).getSelectedLayer());
					//IssueLog.log("pasting copy");
					}
		
 		
		if (KeyEvent.VK_ESCAPE==arg0.getKeyCode()) {
			boolean b= FileChoiceUtil.yesOrNo("Are you sure you want to close the window");
			if(b)
				this.setVisible(false);
			}
		
		int numberscroll=10;
		if (arg0.isShiftDown())switch (arg0.getKeyCode()) {
		
			
			
			case KeyEvent.VK_ESCAPE: {this.setVisible(false);break;}
			case KeyEvent.VK_DELETE: {
				deleteSelection() ;
				;break;}
			case KeyEvent.VK_BACK_SPACE: {
				deleteSelection() ;
				;break;}
		
			
		}
		if (arg0.isShiftDown()&&!this.useScrollPane)switch (arg0.getKeyCode()) {
			case KeyEvent.VK_LEFT: {getZoomer().scroll(-numberscroll, 0); ;break;}
			case KeyEvent.VK_RIGHT: {getZoomer().scroll(numberscroll, 0); ;break;}
			case KeyEvent.VK_UP: {getZoomer().scroll(0, -numberscroll); ;break;}
			case KeyEvent.VK_DOWN: {getZoomer().scroll(0, numberscroll); ;break;}
		}
		else if (arg0.isShiftDown()&&this.useScrollPane) {
			
		}
		
		
		getDisplaySet().updateDisplay();
		
		//this.repaint();
		
		//getTheCanvas().repaint();
		
	}
	
	/**Performs scrolling action, depends on */
	void scrollPane(double dx, double dy) {
		if (!usesScrollPane()) {
			double mag = getZoomer().getZoom();
			this.getZoomer().scroll(dx/mag, dy/mag);
			double maxScrollX=this.getTheSet().getWidth()*0.9;
			if (getZoomer().getX0()>maxScrollX) {
				getZoomer().scroll(-(getZoomer().getX0()-maxScrollX), 0);
			}
			
			double maxScrollY=getTheSet().getHeight()*0.9;
			if (getZoomer().getY0()>maxScrollY) {
				getZoomer().scroll(0, -(getZoomer().getY0()-maxScrollY));
			}
		}
		double factor = getZoomer().getZoom();
		dx*=factor; dy*=factor;
		JScrollBar hbar = this.pane.getHorizontalScrollBar();
		JScrollBar vbar = this.pane.getVerticalScrollBar();
		int valueH = hbar.getValue();
		int valueV = vbar.getValue();
		hbar.setValue((int) (valueH+dx));
		hbar.getModel().setValue((int) (valueH+dx));
		vbar.setValue((int) (valueV+dy));
		vbar.getModel().setValue((int) (valueV+dy));
		this.repaint();
	}
	
	
	
	
	public void comfortZoom() {
		Dimension dim = determineMaxBounds() ;
		dim.width/=1.2;
		dim.height/=1.2;
		
		while (this.getWidth()<dim.width&& this.getHeight()<dim.height) {
			ZoomIn();
		}
		
	}
	
	private void ZoomOut() {
		
		getZoomer().zoomOut();
		centerZoom() ;
		
		reSetCanvasAndWindowSizes() ;
		scrollToComfort();
		//this.pane.setPreferredSize(this.getTheCanvas().getPreferredSize());
		pack();
	}
	
	private void ZoomIn() {
		getZoomer().zoomIn();
		
		centerZoom() ;
		
		reSetCanvasAndWindowSizes() ;
		scrollToComfort() ;
		pack();
	}
	
	private void scrollToComfort() {
		
		scrollOptimalX();
		scrollOptimalY();
		
		if (canEntireCanvasHeightFit()) this.getZoomer().setScrollY(0);
		if (canEntireCanvasWidthFit()) this.getZoomer().setScrollX(0);
		this.pane.setPreferredSize(this.getTheCanvas().getPreferredSize());
	}
	
	void deleteSelection() {
		
		//SelectionManager sel = this.getTheSet().getSelectionManagger();
		//ArrayList<locatedObject> obs = new BasicOverlayHandler().getContainedRois(sel.getSelectionBounds1(), this.getTheSet());
		// new BasicOverlayHandler().clearRoi(this.getTheSet(), sel.getSelectionBounds1());
		
	}

	
	@Override
	public void keyReleased(KeyEvent arg0) {
		
		
		if (getStrokeReader() !=null) {
			getStrokeReader().keyReleased(getDisplaySet(), arg0);
			return;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		if (getStrokeReader() !=null) {
			getStrokeReader().keyTyped(getDisplaySet(), arg0);
			return;
		}
		
	}

	public GraphicDisplayCanvas getTheCanvas() {
		return theCanvas;
	}

	public void setTheCanvas(GraphicDisplayCanvas theCanvas) {
		this.theCanvas = theCanvas;
	}

	public GraphicContainingImage getTheSet() {
		return getDisplaySet().getTheSet();
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		if (ToolBarManager.getCurrentTool()==null) return;
		ToolBarManager.getCurrentTool().mouseDragged(getDisplaySet(), new GMouseEvent(getDisplaySet(), arg0));
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		if (ToolBarManager.getCurrentTool()==null) return;
		ToolBarManager.getCurrentTool().mouseMoved(getDisplaySet(), new GMouseEvent(getDisplaySet(), arg0));
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (ToolBarManager.getCurrentTool()==null) return;
		ToolBarManager.getCurrentTool().mouseClicked(getDisplaySet(),new GMouseEvent(getDisplaySet(), arg0));
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		
		if (ToolBarManager.getCurrentTool()==null) return;
		//this.currentActiveDisplayGroup=display;
		ToolBarManager.getCurrentTool().mouseEntered(getDisplaySet(), new GMouseEvent(getDisplaySet(), arg0));
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		if (ToolBarManager.getCurrentTool()==null) return;
		ToolBarManager.getCurrentTool().mouseExited(getDisplaySet(), new GMouseEvent(getDisplaySet(), arg0));
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		this.lastPress=arg0;
		lastPointCordinate = this.getZoomer().getConverter().unTransformP(arg0.getPoint());
		
		if (ToolBarManager.getCurrentTool()==null) return;
		ToolBarManager.getCurrentTool().mousePressed(getDisplaySet(), new GMouseEvent(getDisplaySet(), arg0));
	
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {

		if (ToolBarManager.getCurrentTool()==null) return;
		ToolBarManager.getCurrentTool().mouseReleased(getDisplaySet(), new GMouseEvent(getDisplaySet(), arg0));
		
	}

	public ImageZoom getZoomer() {
		return zoomer;
	}

	public void setZoomer(ImageZoom zoomer) {
		this.zoomer = zoomer;
	}
	
	
	private void centerZoom() {
		centerZoom(this.lastPointCordinate);

	}
	
	/**Scrolls such that the cordinate location is at the center*/
	 void centerZoom(Point2D lastPointCordinate) {
		if (this.usesScrollPane() && lastPress!=null) {
			/**needs fixing. does not work perfectly on edges*/
			BoundedRangeModel mx = pane.getHorizontalScrollBar().getModel();
			BoundedRangeModel my = pane.getVerticalScrollBar().getModel();
			double zoom = getZoomer().getZoom();
			mx.setValue((int) (lastPointCordinate.getX()*zoom-mx.getExtent()/2));
			my.setValue((int) (lastPointCordinate.getY()*zoom-my.getExtent()/2));
			return;
		}
		
		if (lastPointCordinate==null) return;
		this.getTheCanvas().centerZoomAtPoint(lastPointCordinate);
		
		if (canEntireCanvasFit()) {
			 getZoomer().setX0(0);
			 getZoomer().setY0(0);
		}
	}
	

	
	

	 /**returns a byte array for a buffered image. used to embed the image 
	  * problems sometimes occur with tiffs*/
	public static byte[] imageToByteArray(BufferedImage image) throws IOException {
	  if (image==null) return null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ImageIO.write(image, "tif", baos);
	    return baos.toByteArray();
	}
	public ImageAndDisplaySet getDisplaySet() {
		return display;
	}
	public void setDisplaySet(ImageAndDisplaySet display) {
		this.display = display;
	}
	
	@Override
	protected void processWindowEvent(WindowEvent e) {
		
		super.processWindowEvent(e);
		if (e.getID()==WindowEvent.WINDOW_ACTIVATED||e.getID()==WindowEvent.WINDOW_OPENED)
		CurrentFigureSet.setCurrentActiveDisplayGroup(getDisplaySet());
		
	}
	
	
	/**returns true if entire display set can fit into window*/
	public boolean canEntireCanvasFit() {
		return canEntireCanvasHeightFit()&&canEntireCanvasWidthFit() ;
	}
	
	private boolean canEntireCanvasHeightFit() {
		Dimension d = this.getMaxNeededCanvasSizeforGraphicSet();
		double canHeight=this.getTheCanvas().getCanvasHeightInUnits();
		if (d.height>canHeight) return false;
		return true;
	}
	
	private boolean canEntireCanvasWidthFit() {
		Dimension d = this.getMaxNeededCanvasSizeforGraphicSet();
		double canWidth=getTheCanvas().getCanvasWidthInUnits() ;
		
		if (d.width>canWidth) return false;
		return true;
	}
	
	private boolean scrollOptimalX() {
		double xs = -this.getTheCanvas().getSlackSpaceW()/getZoomer().getZoom();
		if (xs<0) {
		this.getZoomer().scroll(xs, 0);
		
		}
		
		return true;
	}
	
	private boolean scrollOptimalY() {
		double xs = -this.getTheCanvas().getSlackSpaceH()/getZoomer().getZoom();
		if (xs<0)
		this.getZoomer().scroll(0, xs);
		
		return true;
	}
	

	
	public void closeGroupAndSupportingWindows(boolean save) {
		
		GraphicLayer layer = this.getDisplaySet().getTheSet().getLayer();
		
		
		if (layer.getTree()!=null) {}
		
		
		for(Object g: layer.getItemArray()) {
			if (g instanceof  Mortal) {
				Mortal m=(Mortal) g;
				m.kill();
			}
		}
		
		for(Object g: layer.getObjectsAndSubLayers()) {
			if (g instanceof  hasSupportingWindow) {
				hasSupportingWindow m=(hasSupportingWindow) g;
				
				m.closeWindow(save);
			}
			
		}
		
		
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		this.setVisible(false);
		
		
		
	}
	
	@Override
	public void setVisible(boolean b) {
		
		
		super.setVisible(b);
		
		if (b) CurrentFigureSet.onApperance(display);
		else CurrentFigureSet.onDisapperance(display);
		
		GraphicLayer layer = this.getDisplaySet().getTheSet().getLayer();
		for(Object g: layer.getItemArray()) {
			if (g instanceof  Mortal) {
				Mortal m=(Mortal) g;
				m.kill();
			}
			
		}
	}
	
	public void closeGroupWithoutObjectDeath() {
		super.setVisible(false);
		 CurrentFigureSet.onDisapperance(display);
	}
	
	
	public InterfaceExternalTool<DisplayedImage> getCuttentTool() {return ToolBarManager.getCurrentTool();}

	@Override
	public void dragEnter(DropTargetDragEvent arg0) {
		if(getCuttentTool()==null) return;
		try{
				DragAndDropHandler dragger = getCuttentTool().getDraghandler();
				if(dragger!=null) dragger.dragEnter(getDisplaySet(), arg0);
		}catch (Throwable t) {
			IssueLog.log(t);
		}
	}

	@Override
	public void dragExit(DropTargetEvent arg0) {
		if(getCuttentTool()==null) return;
		try{	
				DragAndDropHandler dragger = ToolBarManager.getCurrentTool().getDraghandler();
				if(dragger!=null) dragger.dragExit(getDisplaySet(), arg0);
		
	}catch (Throwable t) {
		IssueLog.log(t);
	}
		
	}

	@Override
	public void dragOver(DropTargetDragEvent arg0) {
		if(getCuttentTool()==null) return;
				try{
				DragAndDropHandler dragger = ToolBarManager.getCurrentTool().getDraghandler();
				if(dragger!=null) dragger.dragOver(getDisplaySet(), arg0);
			}catch (Throwable t) {
				IssueLog.log(t);
			}
	}

	@Override
	public void drop(DropTargetDropEvent arg0) {
		if(getCuttentTool()==null) return;
		try{
			DragAndDropHandler dragger = ToolBarManager.getCurrentTool().getDraghandler();
			if(dragger!=null) dragger.drop(getDisplaySet(), arg0);
		}catch (Throwable t) {
			IssueLog.log(t);
		}
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent arg0) {
		if(getCuttentTool()==null) return;
				try{
				DragAndDropHandler dragger = ToolBarManager.getCurrentTool().getDraghandler();
				if(dragger!=null) dragger.dropActChange(getDisplaySet(), arg0);
				
			}catch (Throwable t) {
				IssueLog.log(t);
			}
	}

	public void zoom(String st) {
		if (st==null) return;
			if (st.contains("In")) this.ZoomIn();
			if (st.contains("Out")) this.ZoomOut();
	}
	
	public class SpecialPaneForCanvas extends JScrollPane {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Color c=Color.darkGray;
		
		
		public SpecialPaneForCanvas(GraphicDisplayCanvas theCanvas) {
			super(theCanvas);
			this.setVerticalScrollBar(new blackBarScrollBar(JScrollBar.VERTICAL));
			this.setHorizontalScrollBar(new blackBarScrollBar(JScrollBar.HORIZONTAL));

		}
		
	

		


		public Dimension getPreferredSize() {
			int w=getTheCanvas().getPreferredSize().width+this.getVerticalScrollBar().getWidth();
			int h=getTheCanvas().getPreferredSize().height+this.getHorizontalScrollBar().getHeight();
			
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			double width = screenSize.getWidth();
			double height = screenSize.getHeight();
			
			if (w>width*0.8) w=(int) (width*0.8);
			if (h>height*0.8)h=(int) (height*0.8);
			return new Dimension(w,h);
		}
		
	}
	
	
	
	class blackBarScrollBar extends JScrollBar{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public blackBarScrollBar(int vertical) {
			super(vertical);
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.black);
			Graphics2D g2d=(Graphics2D) g;

			Stroke oldstroke = g2d.getStroke();
			g2d.setStroke(new BasicStroke(4));
			double v = super.getValue();//+super.getVisibleAmount();
			double max=this.getMaximum();
			double size=this.getSize().getHeight()-this.getWidth()*2;
			if (this.getOrientation()==HORIZONTAL) {
				size=this.getSize().getWidth()-this.getHeight()*2;
			}
			double position = size*v/max;
			double extent=size*this.getVisibleAmount()/max;
			if (this.getOrientation()==HORIZONTAL) {
				g.fillRect(15+(int) position, 3,  (int) extent, 9);
				} else
			g.fillRect(3, 15+(int) position, 9, (int) extent);
				g2d.setStroke(oldstroke);
		}
	}
	
	static void methodReport(Object thi) {
		Method[] methods = thi.getClass().getMethods();
		for (Method m:methods) {
			int types = m.getParameterTypes().length;
			
			if (types==0)
				try {
				} catch (Exception e) {
					
				} 
		}
	}
	
}
