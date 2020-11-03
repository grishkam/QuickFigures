package objectDialogs;

import java.util.ArrayList;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_BasicShapes.BarGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import logging.IssueLog;
import undo.Edit;

public class MultiBarDialog extends BarSwingGraphicDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<BarGraphic> array=new ArrayList<BarGraphic>();
	
	public MultiBarDialog(ArrayList<ZoomableGraphic> zs) {
		super();
		this.setGraphics(zs);
		if (array.size()==0) return;
		addOptionsToDialog();
	
	}
	
	public void setGraphics(ArrayList<ZoomableGraphic> zs) {
		array=new ArrayList<BarGraphic>();
		addGraphicsToArray(array, zs);
		if (array.size()==0) {
			IssueLog.log("there are no items in new array");
			return;
		}
		
			super.rect	=array.get(0);
			super.undoableEdit=Edit.createGenericEdit(zs);
	}
	
	
	public void addGraphicsToArray(ArrayList<BarGraphic> array, ArrayList<ZoomableGraphic> zs) {
		for(ZoomableGraphic z:zs) {
			
			if (z instanceof BarGraphic) {array.add(((BarGraphic) z));}
			if (z instanceof GraphicLayer) {
				addGraphicsToArray(array,	((GraphicLayer) z).getAllGraphics());
			}
					}
	}
	
	@Override
	public void setItemsToDiaog() {
		for(BarGraphic rect: array) setItemsToDialog(rect);
		return ;
	}

}
