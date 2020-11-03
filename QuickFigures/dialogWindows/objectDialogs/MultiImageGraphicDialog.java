package objectDialogs;

import java.util.ArrayList;

import graphicalObjects.ImagePanelGraphic;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import logging.IssueLog;
import undo.Edit;

public class MultiImageGraphicDialog extends ImageGraphicOptionsDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ArrayList<ImagePanelGraphic > array=new ArrayList<ImagePanelGraphic > ();

	public MultiImageGraphicDialog(ArrayList<ZoomableGraphic> zs) {
		super();
		this.setGraphics(zs);
		if (getArray().size()==0) return;
		super.addCommonOptionsToDialog();
	}
	
	public void setGraphics(ArrayList<ZoomableGraphic> zs) {
		setArray(new ArrayList<ImagePanelGraphic >());
		addGraphicsToArray(getArray(), zs);
		if (getArray().size()==0) {
			IssueLog.log("there are no items in new array");
			return;
		}
		
			super.image=getArray().get(0);
		super.undoableEdit=Edit.createGenericEdit(zs);
	}
	
	@Override
	public void setItemsToDiaog() {
		for(ImagePanelGraphic  rect: getArray()) this.setCommonOptionsToDialog(rect);
		return ;
	}
	
	
	public void addGraphicsToArray(ArrayList<ImagePanelGraphic > array, ArrayList<ZoomableGraphic> zs) {
		for(ZoomableGraphic z:zs) {
			if (z instanceof ImagePanelGraphic) {array.add((ImagePanelGraphic) z);}
			if (z instanceof GraphicLayer) {
				addGraphicsToArray(array,	((GraphicLayer) z).getAllGraphics());
			}
					}
	}

	public ArrayList<ImagePanelGraphic > getArray() {
		return array;
	}

	public void setArray(ArrayList<ImagePanelGraphic > array) {
		this.array = array;
	}
	
}
