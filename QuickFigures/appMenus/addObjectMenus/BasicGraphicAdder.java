package addObjectMenus;

import java.awt.Font;

import javax.swing.Icon;

import graphicalObjects.ImagePanelGraphic;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_BasicShapes.BasicGraphicalObject;
import graphicalObjects_LayerTypes.GraphicLayer;
import logging.IssueLog;
import selectedItemMenus.BasicMultiSelectionOperator;

import standardDialog.GraphicDisplayComponent;
import undo.UndoAddItem;
import utilityClassesForObjects.LocatedObject2D;

public abstract class BasicGraphicAdder extends BasicMultiSelectionOperator implements GraphicAdder {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	protected int unique=(int)Math.random()*1000;
	

	public void addLockedItemToSelectedImage(LocatedObject2D ag) {
		
		try {
			//IssueLog.log(this.selector.getSelecteditems().size()+" items selected");
			if (this.selector.getSelecteditems().size() > 0) {
				ZoomableGraphic item = selector.getSelecteditems().get(0);
				if (item instanceof ImagePanelGraphic) {
					ImagePanelGraphic it = (ImagePanelGraphic) item;
					it.addLockedItem(ag);
				}
			}
		} catch (Throwable e) {
			IssueLog.logT(e);
		}
	}
	
	public Character getKey() {
		return null;
	}
	
	
	protected double getScaleToIcon() {
		return 0.3;
	}
	public Icon getIcon() {
		BasicGraphicalObject m = getModelForIcon();
		if (m==null)return null;
		return new GraphicDisplayComponent(getModelForIcon(), getScaleToIcon());
	}



	protected BasicGraphicalObject getModelForIcon() {
		return null;
	}

	@Override
	public String getMenuPath() {
		return null;
	}

	@Override
	public Font getMenuItemFont() {
		return null;
	}
	
	/**performs the action.*/
	public void run() {
		GraphicLayer l = null;
		if(selector!=null &&selector.getSelectedLayer()!=null)l=selector.getSelectedLayer();
	
		ZoomableGraphic item = this.add(l);
		
		if(item!=null) {
			this.getUndoManager().addEdit(new UndoAddItem(l, item));
		}
	}

	

}
