package graphicTools;

import applicationAdapters.ImageWrapper;
import genericMontageUIKit.Object_Mover;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import logging.IssueLog;
import undo.UndoAddItem;
import utilityClassesForObjects.LocatedObject2D;

/**A tool for adding and manipulating shapes*/
public class GraphicTool extends Object_Mover {

	{createSelector=false;}
	protected boolean temporaryTool=false;//not yet implemented

	
	@Override 
	public void mouseEntered() {
		this.getImageWrapperClick().getGraphicLayerSet();
	
	}
	

	
	public void mousePressed() {
		
		super.mousePressed();
	
		try{
		
			{
			
			onPress(getImageWrapperClick(), super.getSelectedObject()) ;

			
		}
		


		} catch (Throwable t) {
			IssueLog.logT(t);
		}
	}

	@Override
	public void mouseReleased() {
		super.mouseReleased();
		onRelease(this.getImageWrapperClick(),getSelectedObject());
		if (temporaryTool) {
			
			super.todefaultTool();}
	}
	
	public void onPress(ImageWrapper gmp, LocatedObject2D roi2) {
		
	}
	
	public void onRelease(ImageWrapper imageWrapper, LocatedObject2D roi2) {
		
	}
	
	public void addUndoerForAddItem(ImageWrapper gmp, GraphicLayer layer, ZoomableGraphic bg) {
		gmp.getImageDisplay().getUndoManager().addEdit(new UndoAddItem(layer, bg));
	}
	

	public String getToolName() {
		return "Graphic Tool";
	}
	

}
