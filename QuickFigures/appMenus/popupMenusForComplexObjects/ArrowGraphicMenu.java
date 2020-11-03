package popupMenusForComplexObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import applicationAdapters.CanvasMouseEventWrapper;
import graphicalObjects_BasicShapes.ArrowGraphic;
import graphicalObjects_BasicShapes.PathGraphic;
import menuUtil.SmartPopupJMenu;
import undo.Edit;
import menuUtil.PopupMenuSupplier;

public class ArrowGraphicMenu extends SmartPopupJMenu implements ActionListener,
PopupMenuSupplier  {

	/**
	 * 
	 */
	
	static final String  backGroundShap="Outline Shape", flipHead="Swap Ends", mv= "Make Vertical",mh= "Make Horizontal";
	
	ArrowGraphic textG;
	ShapeGraphicMenu shapeGraphicMenu;

	private String copyArrow="Arrow Copy";
	
	public ArrowGraphicMenu(ArrowGraphic textG) {
		super();
		this.textG = textG;
		shapeGraphicMenu = new ShapeGraphicMenu(textG);
		this.addAllMenuItems(shapeGraphicMenu.createMenuItems());
		
		add(createItem(backGroundShap));
		add(createItem(flipHead));
		//add(createItem(copyArrow));
		add(createItem(mh));
		add(createItem(mv));
	}
	
	public void setLastMouseEvent(CanvasMouseEventWrapper e) {
		super.setLastMouseEvent(e);
		if (shapeGraphicMenu!=null)shapeGraphicMenu.setLastMouseEvent(e);
	}
	
	public JMenuItem createItem(String st) {
		JMenuItem o=new JMenuItem(st);
		o.addActionListener(this);
		o.setActionCommand(st);
		
		return o;
	}

	private static final long serialVersionUID = 1L;

	@Override
	public JPopupMenu getJPopup() {
		// TODO Auto-generated method stub
		return this;
	}

	

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String com=arg0.getActionCommand();
		if (com.equals(backGroundShap)) {
			textG.getBackGroundShape().showOptionsDialog();
		}
	
		if (com.equals(flipHead)) {
			textG.swapDirections();
			textG.updateDisplay();
		}
		
		if (com.equals(copyArrow)) {
			PathGraphic cop = textG.createPathCopy();
			
			performUndoable(
					Edit.addItem(textG.getParentLayer(),cop)
					);
		}
		
		if (com.equals(mv)) {
			ArrayList<Point2D> pp = textG.getEndPoints();
			double x = pp.get(0).getX();
			double y = pp.get(1).getY();
			textG.setPoints(pp.get(0), new Point2D.Double(x, y));
			
			
		}
		if (com.equals(mh)) {
			ArrayList<Point2D> pp = textG.getEndPoints();
			double x2 = pp.get(1).getX();
			double y = pp.get(0).getY();
			textG.setPoints(pp.get(0), new Point2D.Double(x2, y));
		}
		textG.updateDisplay();
		
	}
	
	
	
}
