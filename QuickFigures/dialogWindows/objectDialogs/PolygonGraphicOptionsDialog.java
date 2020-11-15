package objectDialogs;

import graphicalObjects_BasicShapes.RegularPolygonGraphic;
import standardDialog.NumberInputPanel;

/**An options dialog for regular polygons, includes options for the n-vertices in addition to all other options*/
public class PolygonGraphicOptionsDialog extends ShapeGraphicOptionsSwingDialog {

	RegularPolygonGraphic currentPolygon=null;
	
	/***/
	public PolygonGraphicOptionsDialog(RegularPolygonGraphic s, boolean simple) {
		super(s, true);
		currentPolygon=s;
	}
	
	protected void addOptionsToDialogPart1() {
		super.addOptionsToDialogPart1();
		if (s instanceof RegularPolygonGraphic) currentPolygon=(RegularPolygonGraphic) s;
		NumberInputPanel win = new NumberInputPanel("Width", currentPolygon.getBounds().width);
		NumberInputPanel hin = new NumberInputPanel("Height", currentPolygon.getBounds().height);
		NumberInputPanel vin = new NumberInputPanel("N-Vertex", currentPolygon.getNvertex());
		this.add("width", win);
		this.add("height", hin);
		this.add("Vertices", vin);
	}
	
	protected void setItemsToDiaog() {
		super.setItemsToDiaog();
		currentPolygon.setWidth(this.getNumber("width"));
		currentPolygon.setHeight(this.getNumber("height"));
		currentPolygon.setNvertex((int)this.getNumber("Vertices"));
	}
	

	private static final long serialVersionUID = 1L;
	
	

}
