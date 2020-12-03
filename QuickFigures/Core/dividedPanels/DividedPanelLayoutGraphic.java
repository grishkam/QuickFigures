package dividedPanels;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import applicationAdapters.CanvasMouseEvent;
import dividedPanels.DividedPanelLayout.layoutDividedArea;
import dividedPanels.DividedPanelLayout.LayoutDivider;
import genericMontageKit.PanelContentExtract;
import graphicalObjectHandles.SmartHandle;
import graphicalObjectHandles.SmartHandleList;
import graphicalObjectHandles.HasSmartHandles;
import graphicalObjects.CordinateConverter;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import menuUtil.SmartPopupJMenu;
import standardDialog.NumberInputPanel;
import standardDialog.StandardDialog;
import utilityClassesForObjects.RectangleEdges;

/**Displays a divided panel layout. Available for use as a ''super* layout that contains 
  other layouts. Offers few advantages*/
public class DividedPanelLayoutGraphic extends PanelLayoutGraphic implements HasSmartHandles{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private transient ArrayList<DividerHandle> vHandle;
	private transient ArrayList<DividerHandle> hHandle;
	transient SmartHandleList smartl;
	private transient ArrayList<AreaHandleForDividedLayout> areaHandles;
	private transient ArrayList<EdgeHandleForDivitedLayout> edgeHandles;
	
	public DividedPanelLayoutGraphic(DividedPanelLayout layout) {
		super(layout);
	}

	
	public void drawHandles(Graphics2D graphics, CordinateConverter<?> cords) {
		vHandle=new ArrayList<DividerHandle>();
		hHandle=new ArrayList<DividerHandle>();
		areaHandles=new ArrayList<AreaHandleForDividedLayout>();

		edgeHandles=new ArrayList<EdgeHandleForDivitedLayout>();
		smartl = new SmartHandleList();;
		
		ArrayList<LayoutDivider> hDiv = getPanelLayout().mainArea.getHorizontalDividerArray(true);
		ArrayList<LayoutDivider> vDiv = getPanelLayout().mainArea.getHorizontalDividerArray(false);
		ArrayList<layoutDividedArea> subArea = getPanelLayout().mainArea.getAllBottomLevelSubareas();
		for(int i=0; i<9; i++) edgeHandles.add(new EdgeHandleForDivitedLayout(i));
		
		for(LayoutDivider div:hDiv) {
			hHandle.add(new DividerHandle(div,  Color.orange, cords, this));
			}
		for(LayoutDivider div:vDiv) {
			vHandle.add(new DividerHandle(div,  Color.pink, cords, this));
			}
		
		for(int i=0; i<subArea.size(); i++) {
			areaHandles.add(new AreaHandleForDividedLayout(subArea.get(i), Color.white, cords, i+1));
			}
		
		
		smartl.addAll(hHandle);
		smartl.addAll(vHandle);
		smartl.addAll(areaHandles);
		smartl.addAll(edgeHandles);
		
	
		for(int i=0; i<subArea.size(); i++)	smartl.add(new LowerRightHandleForDividedPanelLayout(this.getPanelLayout(), subArea.get(i), i+1));
			

		smartl.draw(graphics, cords);	
	}

		/**Overrides superclass for drawing a layout*/
	protected void drawLayoutTypeSpecific(Graphics2D graphics,
			CordinateConverter<?> cords) {
		if (this.isSelected())drawHandles(graphics, cords);
		
	}
	

	@Override
	public void handleMove(int handlenum, Point p1, Point p2) {
		this.generateCurrentImageWrapper();
		ArrayList<PanelContentExtract> stack = this.getEditor().cutStack(getPanelLayout());
		
		
		SmartHandle item = this.getSmartHandleList().getHandleNumber(handlenum);
		
		if(this.edgeHandles.contains(item) && item instanceof EdgeHandleForDivitedLayout) {
			EdgeHandleForDivitedLayout edge=(EdgeHandleForDivitedLayout) item;
			if(edge.edge==RectangleEdges.RIGHT||edge.edge==RectangleEdges.LOWER_RIGHT||edge.edge==RectangleEdges.UPPER_RIGHT) {
				double dx = p2.getX()-this.getPanelLayout().getBoundry().getBounds2D().getMaxX();
				this.getPanelLayout().width+=dx;
				getPanelLayout().resetPtsPanels();
			}
			
			if(edge.edge==RectangleEdges.BOTTOM||edge.edge==RectangleEdges.LOWER_RIGHT||edge.edge==RectangleEdges.LOWER_LEFT) {
				double dx = p2.getY()-this.getPanelLayout().getBoundry().getBounds2D().getMaxY();
				this.getPanelLayout().height+=dx;
				getPanelLayout().resetPtsPanels();
			}
			
			if(edge.edge==RectangleEdges.TOP||edge.edge==RectangleEdges.UPPER_LEFT) {
				double dx = p2.getY()-this.getPanelLayout().getBoundry().getBounds2D().getMinY();
				this.getPanelLayout().height-=dx;
				getPanelLayout().mainArea.nudgeDividersIfIndependant(true, -(int)dx);//so that resize will not move any dividers in strange ways
				
				getPanelLayout().move(0, dx);
				getPanelLayout().resetPtsPanels();
			}
			
			if(edge.edge==RectangleEdges.LEFT||edge.edge==RectangleEdges.UPPER_LEFT) {
				double dx = p2.getX()-this.getPanelLayout().getBoundry().getBounds2D().getMinX();
				this.getPanelLayout().width-=dx;
				getPanelLayout().move(dx, 0);
				getPanelLayout().mainArea.nudgeDividersIfIndependant(false, -(int)dx);//so that resize will not move any dividers in strange ways
				
				getPanelLayout().resetPtsPanels();
			}
			
			if(edge.edge==RectangleEdges.CENTER) {
				double dx = p2.getX()-this.getPanelLayout().getBoundry().getBounds2D().getCenterX();
				double dy = p2.getY()-this.getPanelLayout().getBoundry().getBounds2D().getCenterY();
			
				getPanelLayout().move(dx, dy);
				
				getPanelLayout().resetPtsPanels();
			}
			
		} else
		
		
			if(this.hHandle.contains(item))
			 {
				DividerHandle  d=(DividerHandle) item;;
				LayoutDivider divider = d.divider;
				double dy = p2.getY()-divider.rect.getY();
				divider.nudgePosition((int)dy);
			} else
		
				if(this.vHandle.contains(item)) {
					DividerHandle  d=(DividerHandle) item;;
					LayoutDivider divider = d.divider;
			double dy = p2.getX()-divider.rect.getX();
			divider.nudgePosition((int)dy);
		} else  {
			
				if(item!=null) {
					double dy=p2.getY()-item.getCordinateLocation().getY();
					double dx=p2.getX()-item.getCordinateLocation().getX();
					
					item.nudgeHandle(dx, dy);
				}
			
		}
		
		this.getPanelLayout().resetPtsPanels();
		
		
		getEditor().pasteStack(getPanelLayout(), stack);
		
		this.mapPanelLocationsOfLockedItems();
	}
	
	@Override
	public DividedPanelLayout getPanelLayout() {
		// TODO Auto-generated method stub
		return (DividedPanelLayout)layout;
	}
	
	class EdgeHandleForDivitedLayout extends SmartHandle  {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		ArrayList<Point2D> places;
		private int edge;
		
		public EdgeHandleForDivitedLayout(int edge) {
			super(0, 0);
			 places = RectangleEdges.getLocationsForHandles(getPanelLayout().getBoundry().getBounds2D());
			 this.edge=edge;
			 setHandleNumber(edge+10000000);
			 this.setCordinateLocation(new Point((int)places.get(edge).getX(), (int)places.get(edge).getY()));
			 
			 if(edge==RectangleEdges.CENTER) this.setHandleColor(Color.green.darker());
		}
		
		
		
	}
	

	
	
	class AreaHandleForDividedLayout  extends SmartHandle  implements ActionListener {

		
		protected layoutDividedArea dividedArea;
		private int panelnumber;

		public AreaHandleForDividedLayout(layoutDividedArea div, Color orange, CordinateConverter<?> cords, int panelnumber) {
			super((int)div.getCenterX(), (int)div.getCenterY());
			super.setCordinateLocation(new Point((int)div.getCenterX(), (int)div.getCenterY()));
			this.panelnumber=panelnumber;
			this.dividedArea=div;
		}
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		
		@Override
		public void actionPerformed(ActionEvent arg0) {
		
			
			layoutDividedArea useA = dividedArea;
			if (arg0.getActionCommand().equals("+h")) {
				 if (!useA.isSubdivided()) {useA.setHorizontal(true);}
				useA.divide(dividedArea.getCenterY()-dividedArea.getY());
			}
			if (arg0.getActionCommand().equals("+v")) {

					if (!useA.isSubdivided()) {useA.setHorizontal(false);}
				dividedArea.divide(dividedArea.getCenterX()-dividedArea.getX());
			}
			
			boolean multiVertical = arg0.getActionCommand().equals("+mv");
			boolean multiH = arg0.getActionCommand().equals("+mh");
			
			if(multiVertical||multiH) {
				double num= NumberInputPanel.getNumber("How many", 2, 0, false, null);
				
				if(multiVertical) {
					double separation = dividedArea.width/(num+1);
					dividedArea.setHorizontal(false);
					for(int i=1; i<=num; i++) {dividedArea.divide(separation*i);}
				}
				if(multiH) {
					double separation = dividedArea.height/(num+1);
					dividedArea.setHorizontal(true);
					for(int i=1; i<=num; i++) {dividedArea.divide(separation*i);}
				}
				
				
			}
			
			 if (arg0.getActionCommand().equals("pan")){
				 showPanelDimDialog(this.panelnumber);
			 }
			
		}
		
		
		public JPopupMenu getJPopup() {
			SmartPopupJMenu out = new SmartPopupJMenu();
			JMenuItem mi = new JMenuItem("Add Horizontal Divider");
			mi.setActionCommand("+h");
			mi.addActionListener(this);
			out.add(mi);
			
			mi= new JMenuItem("Add Vertical Divider");
			mi.setActionCommand("+v");
			mi.addActionListener(this);
			out.add(mi);
			
			mi= new JMenuItem("Add Multiple Vertical Dividers");
			mi.setActionCommand("+mv");
			mi.addActionListener(this);
			out.add(mi);
			
			mi= new JMenuItem("Add Multiple Horizontal Dividers");
			mi.setActionCommand("+mh");
			mi.addActionListener(this);
			out.add(mi);
			
			mi= new JMenuItem("Set Panel Dimensions");
			mi.setActionCommand("pan");
			mi.addActionListener(this);
			out.add(mi);
			
			return out;
		}
		
		public int getHandleNumber() {
			return smartl.indexOf(this);
		}
		
		public void nudgeHandle(double dx, double dy) {
			
			getPanelLayout().nudgePanel(panelnumber, dx, dy);
		}
		
	}
	
	

	@Override
	public SmartHandleList getSmartHandleList() {
		
		return smartl;
	}
	
	@Override
	public int handleNumber(int x, int y) {
		SmartHandleList hl = smartl;
		if (hl!=null) {
			int output=hl.handleNumberForClickPoint(x, y);
			return output;
		}
		
		return -1;
		}
	
	void showPanelDimDialog( int panelNum) {
		StandardDialog sd = new StandardDialog();
		sd.setWindowCentered(true);
		sd.setModal(true);
		Rectangle panel = this.getPanelLayout().getPanel(panelNum);
		
		sd.add("w", new NumberInputPanel("Width", panel.getWidth(),1));
		sd.add("h", new NumberInputPanel("Height", panel.getHeight(),1));
		
		sd.showDialog();
		
		double w = sd.getNumber("w");
		double h = sd.getNumber("h");
		double dw = w-panel.width;
		double dh = h-panel.height;
		this.generateCurrentImageWrapper();
		ArrayList<PanelContentExtract> stack = this.getEditor().cutStack(getPanelLayout());
		this.getPanelLayout().nudgePanelDimensions(panelNum, dw, dh);
		getPanelLayout().resetPtsPanels();
		this.getEditor().pasteStack(getPanelLayout(), stack);
		this.updateDisplay();
	}
	
	
	@Override
	public void handleMouseEvent(CanvasMouseEvent me, int handlenum, int button, int clickcount, int type,
			int... other) {
		if (clickcount==2 && type==MouseEvent.MOUSE_CLICKED &&handlenum<0)
					{ {this.showOptionsDialog();}
					
						
					}
		
		if (type==MouseEvent.MOUSE_PRESSED) {onhandlePress();}
		if (type==MouseEvent.MOUSE_RELEASED) {onhandleRelease();}
	
	}

}
