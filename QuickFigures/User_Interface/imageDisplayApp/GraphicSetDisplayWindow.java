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
 * Version: 2021.1
 */
package imageDisplayApp;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoundedRangeModel;
import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import addObjectMenus.PasteItem;
import applicationAdapters.DisplayedImage;
import basicAppAdapters.GenericCanvasMouseAction;
import basicMenusForApp.SelectedSetLayerSelector;
import basicMenusForApp.MenuBarForApp;
import exportMenus.FlatCreator;
import externalToolBar.DragAndDropHandler;
import externalToolBar.InterfaceExternalTool;
import externalToolBar.InterfaceKeyStrokeReader;
import externalToolBar.ToolBarManager;
import graphicActionToolbar.CurrentFigureSet;
import graphicalObjects_LayerTypes.GraphicLayer;
import includedToolbars.StatusPanel;
import locatedObject.Mortal;
import logging.IssueLog;
import selectedItemMenus.CopyItem;
import selectedItemMenus.ItemRemover;
import ultilInputOutput.FileChoiceUtil;

/**The JFrame containing a canvas that figures are drawn inside.
 mouse actions on that canvas call methods form the toolbar so that the user may edit anything. */
public class GraphicSetDisplayWindow extends JFrame implements KeyListener, MouseListener, MouseMotionListener,MouseWheelListener, DropTargetListener{
	
	/**
	 * 
	 */
	private static final String ZOOM_OUT = "Out", ZOOM_IN = "In";
	
	/**keeps a count of how many of these windows have been created*/
	static int windowCount=0;
	int windowNumber=windowCount+1; {
		windowCount+=1;
	}
	
	private static final long serialVersionUID = 1L;
	
	/**object that stores this window, the canvas, the layers palette in a single location*/
	private ImageWindowAndDisplaySet display;
	
	private ImageZoom zoomer=new ImageZoom();//object to keep track of how zoomed in or out a user is
	private JScrollPane pane=new JScrollPane();//the scroll pane for the canvas
	
	/**The canvas that actually shows all of the drawn object*/
	private GraphicDisplayCanvas theCanvas=null;//the canvas
	
	/**stores the on screen location of the window before certain actions are taken. */
	private double lx=0;
	private double ly=0;

	/**although there is no user option for this. a programmer can create a version of the 
	  window that does not use a scroll pane but instead indicates the position 
	  in the same way as imageJ does. in that case a scroll indicator need be drawn*/
	ScrollIndicator indicator=new ScrollIndicator(this);
	/**does this windows use a scroll pane. user cannot change this but programmer can. If set to false scrolling will work more like imageJ scrolling*/
	private boolean useScrollPane=true;
	
	/**the side panel is a work in progress*/
	public static boolean startsWithSidePanel=false;
	private boolean usesBuiltInSidePanel=startsWithSidePanel;
	/**A small panel that displays a mini toolbar inside*/
	private MiniToolBarPanel sidePanel;
	
	/**the position of the most recent click is stored here*/
	private MouseEvent lastPress;
	private Point2D lastPointCordinate;
	

	/**constructor for the window*/
	public GraphicSetDisplayWindow(ImageWindowAndDisplaySet set, GraphicDisplayCanvas canvas) {
		display=set;
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setUpCanvas(canvas);
		setJMenuBar(new  MenuBarForApp());
	}
	
	
	/**returns the current tool*/
	public InterfaceExternalTool<DisplayedImage> getCuttentTool() {return ToolBarManager.getCurrentTool();}
	
	/**returns true if a scroll pane is being used within the windows*/
	public boolean usesScrollPane() {
		
		return useScrollPane;
	}
	
	/**returns the dimensions the canvas needs to be in order to display the image
	  at the given zoom and canvas display size.*/
	public Dimension getMaxNeededCanvasSizeforGraphicSet() {
		
		int mw=(int)Math.ceil(getTheSet().getWidth()*getZoomer().getZoomMagnification());
		int mh= (int)Math.ceil(getTheSet().getHeight()*getZoomer().getZoomMagnification());
		return new Dimension(mw,mh);
	}
	
	/**information about the zoom level will appear in the window title after this is called*/
	private void setTitleBasedOnSet() {
		this.setTitle(this.getDisplaySet().getTheSet().getTitle()+"          Zoom = "+100*getZoomer().getZoomAsFewDigits()+"%");
	}
	

	/**Automatically sets the windows size to something that better fits the objects in the 
	  figure*/
	void reSetCanvasAndWindowSizes() {
		/**stores the current on screen locations of the window*/
		if(!isVisible()) return;
		lx=this.getLocationOnScreen().getX();
		ly=this.getLocationOnScreen().getY();
		
		
		resetCanvasDisplayObjectSize();
		if (ZoomOptions.current.resizeWindowsAfterZoom)
			pack();
	}
	
	

	/**sets the sizes for the canvas, size limit will depend on the screen size
		  and window position. If using a scroll pane, see the getPrefferedSize method in the nested scroll pane
	 */
	void resetCanvasDisplayObjectSize() {
		setTitleBasedOnSet() ;//this updates the title to include the current zoom level. I put it here since this method is called after every zoom change
		Dimension b1 = determineMaxBoundsForWindow();
		Dimension b2 = getMaxNeededCanvasSizeforGraphicSet();
		
		b2.width=Math.min(b1.width, b2.width);
		b2.height=Math.min(b1.height, b2.height);
		
		Dimension b3 = new Dimension(b2.width+42, b2.height+30);
	
		getTheCanvas().setPreferredSize( b3 );
		getTheCanvas().setSize(b2);
	}
	

	
	/**returns the max allowable bounds for the window that still allow for comfortable viewing.
	 *  this is set to be comfortably smaller than the screen*/
	Dimension determineMaxBoundsForWindow() {
		Dimension bounds = Toolkit.getDefaultToolkit().getScreenSize().getSize();
		
        bounds.width= (bounds.width-super.getLocationOnScreen().x-50)*8/10;
        bounds.height=(bounds.height-super.getLocationOnScreen().y-80)*8/10;
        
       return bounds;
		
	}
	
	
	
	/**this reduces the zoom until the image is small enough to 
	  fit in the 'max' window size while the window is small enough to fit in the screen*/
	public void shrinktoFit() {
		while(!willFitInMaxWindowSize()) {
			this.getZoomer().zoomOut();
		}
	}
	
	/**returns true if the canvas is smaller than the max reccomendeds size for the window*/
	boolean willFitInMaxWindowSize() {
		Dimension size1 = getMaxNeededCanvasSizeforGraphicSet();
		Dimension b2 = determineMaxBoundsForWindow();
		if (size1.width> b2.width) return false;
		if (size1.height> b2.height) return false;
		return true;
	}
	
	/**returns the recommended size for the window*/
	Dimension getReccomendedSize() {
		Dimension size1 = getMaxNeededCanvasSizeforGraphicSet();
		Dimension b2 = determineMaxBoundsForWindow();
		size1.width=Math.min(size1.width, b2.width);
		size1.height=Math.min(size1.height, b2.height);
		return size1;
	}

	
	/**sets up the canvas and shows the window*/
	public void setUpCanvas(GraphicDisplayCanvas canvas) {
		if (canvas==null) {IssueLog.log("no canvas");}
		this.theCanvas=canvas;
	
		addComponentsToWindow();

		
		this.addKeyListener(this);
		this.getRootPane().addKeyListener(this);//bugfix for loss of key functions. not sure what other effects this would have
		getTheCanvas().addKeyListener(this);
		getTheCanvas().addMouseListener(this);
		getTheCanvas().addMouseMotionListener(this);
		getTheCanvas().addMouseWheelListener(this);
		getTheCanvas().setDispWindow(this);
		new DropTarget(getTheCanvas(), this);
		getDisplaySet().updateDisplay();
		this.setVisible(true);
		
		reSetCanvasAndWindowSizes() ;
		this.pack();
	
	}


	/**
	adds the canvas, scroll pane, side panel to the window
	 */
	public void addComponentsToWindow() {
		if (useScrollPane) {
				pane=new SpecialPaneForCanvas(getTheCanvas());
				pane.setPreferredSize(getTheCanvas().getPreferredSize());
				
				/**if window should include a side panel*/
			if (usesBuiltInSidePanel()) {
				
				this.setLayout(new GridBagLayout());
				GridBagConstraints c = new GridBagConstraints();
				c.anchor=GridBagConstraints.NORTHWEST;
				addSidePanel(c);
				c.gridx=1;
				this.add(pane, c);
				this.setResizable(false);
			}
			else {
				this.setLayout(new BorderLayout());
				this.add(pane);
				this.setResizable(true);
			}
			
		;
			
		} else
		this.add(getTheCanvas());
	}
	


	/**
	This method adds a side panel with tools
	 */
	public void addSidePanel(GridBagConstraints c) {
		c.gridx=0;
		sidePanel = new MiniToolBarPanel(this.display);
		this.add(sidePanel, c);
	}



	
	/**returns the key stroke reader object that will be used when keys are pressed*/
	public InterfaceKeyStrokeReader<DisplayedImage> getStrokeReader() {
		if (ToolBarManager.getCurrentTool()==null) return null;
		return ToolBarManager.getCurrentTool().getCurrentKeyStrokeReader();
	}
	
	/**scrolls in or out in response to wheel movements with control down*/
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		boolean WindowsOrMacMeta = isModifierKeyDown(e);
		if(e.isConsumed())
			return;
		else
		if(WindowsOrMacMeta) {
			if(e.getUnitsToScroll()>0)
				ZoomOut();
			else
				ZoomIn();
		}
		e.consume();
	}
	

	/**Called after a mouse event. */
	@Override
	public void keyPressed(KeyEvent arg0) {
		StatusPanel.updateStatus("Key press is heard "+arg0.getKeyChar());
		try {
		if(arg0.isConsumed()) 
			StatusPanel.updateStatus("Key is consumed  "+arg0.getKeyChar());
		
		
		if (getStrokeReader() !=null)  {
			getStrokeReader().keyPressed(getDisplaySet(), arg0);
			
		}
		
		if (arg0.getSource()==this.getRootPane()) {
			//IssueLog.log("issue: "+"key events should not be comming from root pane");
			
		}
		
	
		
		if (arg0.getKeyCode()==KeyEvent.VK_PLUS||arg0.getKeyChar()=='='||arg0.getKeyChar()=='+') {
			if(!arg0.isConsumed())
			ZoomIn();
			
		}
		if (arg0.getKeyCode()==KeyEvent.VK_MINUS||arg0.getKeyChar()=='-') {
			if(!arg0.isConsumed())
			ZoomOut();
			
		}
		
		if (arg0.getKeyCode()==KeyEvent.VK_PLUS||arg0.getKeyChar()=='+') {
			
			if(!arg0.isConsumed() &&arg0.isShiftDown())
			this.comfortZoomIn();
		}
		
		if (arg0.getKeyChar()=='_') {
			
			scrollToComfort();
		}
		
 		if (arg0.getKeyChar()=='p'&&arg0.isAltDown()) {
			pack();
		}
 		
 		
 		boolean WindowsOrMacMeta = isModifierKeyDown(arg0);
 		
 		
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
		
		/**if the canvas is not in a scroll pane, user arrow keys will scroll*/
		if (arg0.isShiftDown()&&!this.useScrollPane)switch (arg0.getKeyCode()) {
			case KeyEvent.VK_LEFT: {getZoomer().scroll(-numberscroll, 0); ;break;}
			case KeyEvent.VK_RIGHT: {getZoomer().scroll(numberscroll, 0); ;break;}
			case KeyEvent.VK_UP: {getZoomer().scroll(0, -numberscroll); ;break;}
			case KeyEvent.VK_DOWN: {getZoomer().scroll(0, numberscroll); ;break;}
		}
		
		
		
		getDisplaySet().updateDisplay();
		
		} catch (Throwable t) {
			IssueLog.log(t);
		}

	}


	/**determines if either the apple key (for Mac) or the control key (for windows)
	 * is down
	 * @param arg0
	 * @return
	 */
	public static boolean isModifierKeyDown(InputEvent arg0) {
		boolean WindowsOrMacMeta=false;
 		if (IssueLog.isWindows() &&arg0.isControlDown()) {
 			
 			WindowsOrMacMeta=true;
 		}
 		if (!IssueLog.isWindows() &&arg0.isMetaDown()) WindowsOrMacMeta=true;
		return WindowsOrMacMeta;
	}
	
	/**Performs scrolling action. Exactly how this is implemented depends on whether a JScroll pane object is used
	  for scrolling or if the a scrolling is done with only a canvas visible */
	void scrollPane(double dx, double dy) {
		if (!usesScrollPane()) {
			double mag = getZoomer().getZoomMagnification();
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
		double factor = getZoomer().getZoomMagnification();
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
	
	
	
	/**zooms the window so that the canvas can fit inside easily and still be seen
	  Comfortably. in other words, zooms in again and again*/
	public void comfortZoomIn() {
		Dimension dim = determineMaxBoundsForWindow() ;
		dim.width/=1.2;
		dim.height/=1.2;
		
		while (this.getWidth()<dim.width&& this.getHeight()<dim.height) {
			ZoomIn();
		}
		
	}
	
	/**changes the magnification to a lower level, zooming out*/
	private void ZoomOut() {
		
		getZoomer().zoomOut();
		centerZoom() ;
		
		reSetCanvasAndWindowSizes() ;
		scrollToComfort();
		
	}
	
	/**changes the magnification to a higher level, zooming in*/
	private void ZoomIn() {
		getZoomer().zoomIn();
		
		centerZoom() ;
		
		reSetCanvasAndWindowSizes() ;
		scrollToComfort() ;
		
	}
	
	/** scrolls such that as much of the canvas is visible as possible
	If the scroll pane is being used, this means setting the preferred size of the pane to the canvas size*/
	private void scrollToComfort() {
		
		moveVirtualScrollingToVisible();
		
		if (pane!=null)
			this.pane.setPreferredSize(this.getTheCanvas().getPreferredSize());
	}


	/**
	 scrolls such that as much of the canvas is visible as possible
	 */
	private void moveVirtualScrollingToVisible() {
		scrollOptimalX();
		scrollOptimalY();
		if (canEntireCanvasHeightFit()) this.getZoomer().setScrollY(0);
		if (canEntireCanvasWidthFit()) this.getZoomer().setScrollX(0);
	}
	
	/**not implemented*/
	void deleteSelection() {
		
		
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

	public StandardWorksheet getTheSet() {
		return getDisplaySet().getTheSet();
	}

	
	/**called after a mouse event, creates a canvas mouse event object for the figure and passes it on to the current tool
	  in the toolbar*/
	@Override
	public void mouseDragged(MouseEvent arg0) {
		if (ToolBarManager.getCurrentTool()==null) return;
		ToolBarManager.getCurrentTool().mouseDragged(getDisplaySet(), new GenericCanvasMouseAction(getDisplaySet(), arg0));
		
	}

	/**called after a mouse event, creates a canvas mouse event object for the figure and passes it on to the current tool
	  in the toolbar*/
	@Override
	public void mouseMoved(MouseEvent arg0) {
		if (ToolBarManager.getCurrentTool()==null) return;
		ToolBarManager.getCurrentTool().mouseMoved(getDisplaySet(), new GenericCanvasMouseAction(getDisplaySet(), arg0));
		
	}

	/**called after a mouse event, creates a canvas mouse event object for the figure and passes it on to the current tool
	  in the toolbar*/
	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (ToolBarManager.getCurrentTool()==null) return;
		ToolBarManager.getCurrentTool().mouseClicked(getDisplaySet(),new GenericCanvasMouseAction(getDisplaySet(), arg0));
		
	}

	/**called after a mouse event, creates a canvas mouse event object for the figure and passes it on to the current tool
	  in the toolbar*/
	@Override
	public void mouseEntered(MouseEvent arg0) {
		if (ToolBarManager.getCurrentTool()==null) return;
		ToolBarManager.getCurrentTool().mouseEntered(getDisplaySet(), new GenericCanvasMouseAction(getDisplaySet(), arg0));
		
	}

	/**called after a mouse event, creates a canvas mouse event object for the figure and passes it on to the current tool
	  in the toolbar*/
	@Override
	public void mouseExited(MouseEvent arg0) {
		if (ToolBarManager.getCurrentTool()==null) return;
		ToolBarManager.getCurrentTool().mouseExited(getDisplaySet(), new GenericCanvasMouseAction(getDisplaySet(), arg0));
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		this.lastPress=arg0;
		lastPointCordinate = this.getZoomer().getConverter().unTransformP(arg0.getPoint());
		
		if (ToolBarManager.getCurrentTool()==null) return;
		ToolBarManager.getCurrentTool().mousePressed(getDisplaySet(), new GenericCanvasMouseAction(getDisplaySet(), arg0));
	
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {

		if (ToolBarManager.getCurrentTool()==null) return;
		ToolBarManager.getCurrentTool().mouseReleased(getDisplaySet(), new GenericCanvasMouseAction(getDisplaySet(), arg0));
		
	}

	public ImageZoom getZoomer() {
		return zoomer;
	}

	public void setZoomer(ImageZoom zoomer) {
		this.zoomer = zoomer;
	}
	
	/**Scrolls such that the last clicked on co-ordinate location is at the center of the window*/
	private void centerZoom() {
		centerZoom(this.lastPointCordinate);

	}
	
	/**Scrolls such that the co-ordinate location is at the center of the window*/
	 void centerZoom(Point2D lastPointCordinate) {
		if (this.usesScrollPane() && lastPress!=null) {
			/**needs fixing. does not work perfectly on edges*/
			BoundedRangeModel mx = pane.getHorizontalScrollBar().getModel();
			BoundedRangeModel my = pane.getVerticalScrollBar().getModel();
			double zoom = getZoomer().getZoomMagnification();
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
	public ImageWindowAndDisplaySet getDisplaySet() {
		return display;
	}
	public void setDisplaySet(ImageWindowAndDisplaySet display) {
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
	
	/**returns true if the objects drawn on the canvas can fit inside the height of the canvas*/
	private boolean canEntireCanvasHeightFit() {
		Dimension d = this.getMaxNeededCanvasSizeforGraphicSet();
		double canHeight=this.getTheCanvas().getCanvasHeightInUnits();
		if (d.height>canHeight) return false;
		return true;
	}
	
	/**returns true if the objects drawn on the canvas can fit inside the width of the canvas*/
	private boolean canEntireCanvasWidthFit() {
		Dimension d = this.getMaxNeededCanvasSizeforGraphicSet();
		double canWidth=getTheCanvas().getCanvasWidthInUnits() ;
		
		if (d.width>canWidth) return false;
		return true;
	}
	
	/**if scrolling method does not use the scroll pane, this method is used
	  to keep the scroll position in a place where the canvas can be seen*/
	private boolean scrollOptimalX() {
		double xs = -this.getTheCanvas().getSlackSpaceW()/getZoomer().getZoomMagnification();
		if (xs<0) {
		this.getZoomer().scroll(xs, 0);
		
		}
		
		return true;
	}
	/**if scrolling method does not use the scroll pane, this method is used
	  to keep the scroll position in a place where the canvas can be seen*/
	private boolean scrollOptimalY() {
		double xs = -this.getTheCanvas().getSlackSpaceH()/getZoomer().getZoomMagnification();
		if (xs<0)
		this.getZoomer().scroll(0, xs);
		
		return true;
	}
	

	/**closes the window and calls the kill() method on any mortal objects. 
	  also checks to see if any of the objects are associated with supporting windows (also closed)*/
	
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
			if (g instanceof  HasSupportingWindow) {
				HasSupportingWindow m=(HasSupportingWindow) g;
				
				m.closeWindow(save);
			}
			
		}
		
		
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		this.setVisible(false);
		
		
		
	}
	
	/**called when windows is opened or closed
	 * closing the window also calls the .kill method for
	 * the objects in the layer*/
	@Override
	public void setVisible(boolean b) {
		
		
		super.setVisible(b);
		
		if (b) CurrentFigureSet.onApperance(display);
		else CurrentFigureSet.onDisapperance(display);
		
		GraphicLayer layer = this.getDisplaySet().getTheSet().getLayer();
		if (!b)
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
	
	
	

	@Override
	public void dragEnter(DropTargetDragEvent arg0) {
		if(getCuttentTool()==null) return;
		try{
				DragAndDropHandler dragger = getCuttentTool().getDraghandler();
				if(dragger!=null) dragger.dragEnter(getDisplaySet(), arg0);
		}catch (Throwable t) {
			IssueLog.logT(t);
		}
	}

	@Override
	public void dragExit(DropTargetEvent arg0) {
		if(getCuttentTool()==null) return;
		try{	
				DragAndDropHandler dragger = ToolBarManager.getCurrentTool().getDraghandler();
				if(dragger!=null) dragger.dragExit(getDisplaySet(), arg0);
		
	}catch (Throwable t) {
		IssueLog.logT(t);
	}
		
	}

	@Override
	public void dragOver(DropTargetDragEvent arg0) {
		if(getCuttentTool()==null) return;
				try{
				DragAndDropHandler dragger = ToolBarManager.getCurrentTool().getDraghandler();
				if(dragger!=null) dragger.dragOver(getDisplaySet(), arg0);
			}catch (Throwable t) {
				IssueLog.logT(t);
			}
	}

	@Override
	public void drop(DropTargetDropEvent arg0) {
		if(getCuttentTool()==null) return;
		try{
			DragAndDropHandler dragger = ToolBarManager.getCurrentTool().getDraghandler();
			if(dragger!=null) dragger.drop(getDisplaySet(), arg0);
		}catch (Throwable t) {
			IssueLog.logT(t);
		}
	}

	
	@Override
	public void dropActionChanged(DropTargetDragEvent arg0) {
		if(getCuttentTool()==null) return;
				try{
				DragAndDropHandler dragger = ToolBarManager.getCurrentTool().getDraghandler();
				if(dragger!=null) dragger.dropActChange(getDisplaySet(), arg0);
				
			}catch (Throwable t) {
				IssueLog.logT(t);
			}
	}

	/**Zooms the canvas in or out depending on the string*/
	public void zoom(String st) {
		if (st==null) return;
			if (st.contains(ZOOM_IN)) this.ZoomIn();
			if (st.contains(ZOOM_OUT)) this.ZoomOut();
	}
	
	/**A scroll pane that will contain the canvas*/
	public class SpecialPaneForCanvas extends JScrollPane {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		
		/**creates a scroll pane and adds black scroll bars*/
		public SpecialPaneForCanvas(GraphicDisplayCanvas theCanvas) {
			super(theCanvas);
			this.setVerticalScrollBar(new BlackBarScrollBar(JScrollBar.VERTICAL));
			this.setHorizontalScrollBar(new BlackBarScrollBar(JScrollBar.HORIZONTAL));

		}
		

		/**determines the scroll pane size based on the size of the canvas and screen 
		  The size returned will depends on the position of the window and the screen size
		  This method is crucial for the normal appearance and behavior of windows. */
		public Dimension getPreferredSize() {
			int w=getTheCanvas().getPreferredSize().width+this.getVerticalScrollBar().getWidth();
			int h=getTheCanvas().getPreferredSize().height+this.getHorizontalScrollBar().getHeight();
			
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			
			/**subtracts the window location from the size to get the width and height available on the screen */
			double width = screenSize.getWidth()-lx;
			double height = screenSize.getHeight()-ly;
			
			/**if the size would be larger than 80% of the available width or height, this reduces it*/
			if (w>width*0.8) w=(int) (width*0.8);
			if (h>height*0.8)h=(int) (height*0.8);
			return new Dimension(w,h);
		}
		
	}
	
	
	/**A version of J scroll bar with dark bars over the normal grey,
	  easier for user to see*/
	class BlackBarScrollBar extends JScrollBar{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public BlackBarScrollBar(int vertical) {
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

	
	
	/**returns true if the window is setup with a side panel*/
	/**side panels are a work in progress and do not appear in current version*/
	public boolean usesBuiltInSidePanel() {
		return usesBuiltInSidePanel;
	}
	
	/**Alters the window to show built in side panels*/
	/**side panels are a work in progress and do not appear in current version*/
	public void setUsesBuiltInSidePanel(boolean b) {
		if (usesBuiltInSidePanel==b) return;
		
		
		usesBuiltInSidePanel=b;
		if (this.pane!=null)this.remove(pane);
		if (this.sidePanel!=null)this.remove(sidePanel);
		this.remove(theCanvas);
		startsWithSidePanel=b;
		this.addComponentsToWindow();
		this.pack();
	}


	


	
}
