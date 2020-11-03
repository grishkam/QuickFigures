package addObjectMenus;

import javax.swing.Icon;

import graphicalObjects.ImagePanelGraphic;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_BasicShapes.BasicGraphicalObject;
import logging.IssueLog;
import selectedItemMenus.LayerSelector;
import standardDialog.GraphicDisplayComponent;
import utilityClassesForObjects.LocatedObject2D;

public abstract class BasicGraphicAdder implements GraphicAdder {


	protected LayerSelector selector;
	
	protected int unique=(int)Math.random()*1000;
	
	
	public void setSelector(LayerSelector selector) {
		this.selector=selector;
	}
	
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
			IssueLog.log(e);
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

}
