package objectDialogs;

import graphicalObjects_BasicShapes.ShapeGraphic;

public class StrokeOnlySwingDialog extends ShapeGraphicOptionsSwingDialog{

	public StrokeOnlySwingDialog(ShapeGraphic s) {
		super(s);
		addOptionsToDialog2();
		
	}
	
	

	
	protected void addOptionsToDialog2() {
		
		addOptionsToDialogPart2();
	}
	
	protected void addOptionsToDialog() {
		
		
	}
	
	
	protected void addOptionsToDialogPart2() {
		// TODO Auto-generated method stub
		
		addStrokePanelToDialog(s);
		
		
	}
	
	protected void setItemsToDiaog() {
		this.setStrokedItemtoPanel(s);
		
		
		
}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
}
