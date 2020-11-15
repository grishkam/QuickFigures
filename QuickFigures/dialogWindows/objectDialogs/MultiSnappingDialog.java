package objectDialogs;

import java.util.ArrayList;

import graphicalObjects_LayerTypes.GraphicLayer;
import logging.IssueLog;
import standardDialog.BooleanInputPanel;
import standardDialog.SnappingPanel;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.SnappingPosition;

public class MultiSnappingDialog extends GraphicItemOptionsDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ArrayList<LocatedObject2D> array=new ArrayList<LocatedObject2D>();

	private LocatedObject2D object;

	private boolean copySnap=true;
	
	
	public MultiSnappingDialog(boolean b) {
		copySnap=b;
	}

	public void setGraphics(ArrayList<?> zs) {
		array=new ArrayList<LocatedObject2D>();
		addGraphicsToArray(array, zs);
		if (array.size()==0) {
			IssueLog.log("there are no items in new array");
			return;
		}
		for(LocatedObject2D ob1: array) {
			
			if (ob1.getSnapPosition()!=null) object=ob1; 
		}
		
		SnappingPanel panel = this.addSnappingBehviourToDialog(object);
			this.getOptionDisplayTabs().remove(mainPanel);
			BooleanInputPanel booleanPanel = new BooleanInputPanel("Keep Relative Positions Same", !copySnap);
			add("create unique", booleanPanel);
			booleanPanel.placeItems(panel,0, 6);
			
			
	}
	
	public void addGraphicsToArray(ArrayList<LocatedObject2D> array, ArrayList<?> zs) {
		if (zs!=null)
		for(Object z:zs) {
			if (z instanceof LocatedObject2D) {array.add((LocatedObject2D) z);}
			if (z instanceof GraphicLayer) {
				addGraphicsToArray(array,	((GraphicLayer) z).getAllGraphics());
			}
					}
	}
	
	public boolean isEmpty() {
		if(array.size()==0 &&object==null) return true;
		return false;
	}

	protected void setItemsToDiaog() {
		copySnap=!this.getBoolean("create unique");
		
		this.setObjectSnappingBehaviourToDialog(object);
		
		for(LocatedObject2D s: array) {
				if (s.getSnapPosition()!=null) 
					{
					SnappingPosition newSnap = object.getSnapPosition().copy();
					if(!copySnap) newSnap = object.getSnapPosition();
					s.setSnappingBehaviour(newSnap);
					};
		}
}

}
