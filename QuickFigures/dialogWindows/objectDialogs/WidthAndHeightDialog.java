package objectDialogs;

import graphicalObjects_BasicShapes.RectangularGraphic;
import standardDialog.NumberInputPanel;

public class WidthAndHeightDialog extends ShapeGraphicOptionsSwingDialog {

	RectangularGraphic rect=null;
	{this.setWindowCentered(true);}
	
	public WidthAndHeightDialog(RectangularGraphic s) {
		super(s);
		rect=s;
		// TODO Auto-generated constructor stub
	}
	
	protected void addOptionsToDialog() {
		 addOptionsToDialogPart1();
	}
	
	protected void addOptionsToDialogPart1() {
		addWidthAndHeightToDialog();
	}

	private void addWidthAndHeightToDialog() {
		if (s instanceof RectangularGraphic) rect=(RectangularGraphic) s;
		NumberInputPanel win = new NumberInputPanel("Width", rect.getRectangle().width);
		NumberInputPanel hin = new NumberInputPanel("Height", rect.getRectangle().height);
		this.add("width", win);
		this.add("height", hin);
	}
	
	protected void setItemsToDiaog() {
		setWidthAndHeighttoDialog();
	}

	private void setWidthAndHeighttoDialog() {
		rect.setWidth(this.getNumber("width"));
		rect.setHeight(this.getNumber("height"));
	}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	

}
