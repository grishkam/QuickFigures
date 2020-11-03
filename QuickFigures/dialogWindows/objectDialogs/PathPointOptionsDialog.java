package objectDialogs;

import graphicalObjects_BasicShapes.PathGraphic;
import standardDialog.BooleanInputPanel;
import utilityClassesForObjects.PathPoint;

public class PathPointOptionsDialog extends GraphicItemOptionsDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected PathGraphic s;
	PathPoint p;
	
	

	public PathPointOptionsDialog(PathGraphic s, PathPoint p) {
	this.s=s;	
	this.p=p;
	 addOptionsToDialog();
	}
	
	
	
	
	
	protected void addOptionsToDialog() {
		
		this.add("Closed", new BooleanInputPanel("Is closed ", p.isClosePoint()));
		
	}
	
	
	protected void setItemsToDiaog() {
		p.setClosePoint(this.getBoolean("Closed"));
		s.updatePathFromPoints();
}
	
}
