package objectDialogs;

import graphicalObjects_BasicShapes.RoundedRectangleGraphic;
import standardDialog.NumberInputPanel;

public class RoundRectGraphicOptionsDialog extends ShapeGraphicOptionsSwingDialog {

	RoundedRectangleGraphic rect=null;
	
	public RoundRectGraphicOptionsDialog(RoundedRectangleGraphic roundedRectangleGraphic, boolean simple) {
		super(roundedRectangleGraphic, simple);
		rect=roundedRectangleGraphic;
	}
	
	protected void addOptionsToDialogPart1() {
		super.addOptionsToDialogPart1();
		if (s instanceof RoundedRectangleGraphic ) rect=(RoundedRectangleGraphic ) s;
		NumberInputPanel win = new NumberInputPanel("Width", rect.getBounds().width);
		NumberInputPanel hin = new NumberInputPanel("Height", rect.getBounds().height);
		NumberInputPanel arcw = new NumberInputPanel("Arc Width", rect.getArcw());
		NumberInputPanel arch = new NumberInputPanel("Arc Heigth", rect.getArch());
		this.add("width", win);
		this.add("height", hin);
		this.add("arcw", arcw );
		this.add("arch", arch );
	}
	
	protected void setItemsToDiaog() {
		super.setItemsToDiaog();
		rect.setWidth(this.getNumber("width"));
		rect.setHeight(this.getNumber("height"));
		rect.setArcw(this.getNumber("arcw"));
		rect.setArch(this.getNumber("arch"));
	}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	

}
