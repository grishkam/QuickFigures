package objectDialogs;

import graphicalObjects_BasicShapes.RegularPolygonGraphic;
import standardDialog.NumberInputPanel;

public class PolygonGraphicOptionsDialog extends ShapeGraphicOptionsSwingDialog {

	RegularPolygonGraphic rect=null;
	
	public PolygonGraphicOptionsDialog(RegularPolygonGraphic s) {
		super(s);
		rect=s;
		// TODO Auto-generated constructor stub
	}
	
	protected void addOptionsToDialogPart1() {
		super.addOptionsToDialogPart1();
		if (s instanceof RegularPolygonGraphic) rect=(RegularPolygonGraphic) s;
		NumberInputPanel win = new NumberInputPanel("Width", rect.getBounds().width);
		NumberInputPanel hin = new NumberInputPanel("Height", rect.getBounds().height);
		NumberInputPanel vin = new NumberInputPanel("N-Vertex", rect.getNvertex());
		this.add("width", win);
		this.add("height", hin);
		this.add("Vertices", vin);
	}
	
	protected void setItemsToDiaog() {
		super.setItemsToDiaog();
		rect.setWidth(this.getNumber("width"));
		rect.setHeight(this.getNumber("height"));
		rect.setNvertex((int)this.getNumber("Vertices"));
	}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	

}
