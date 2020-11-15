package objectDialogs;

import graphicalObjects_BasicShapes.ArrowGraphic;
import standardDialog.ComboBoxPanel;
import standardDialog.NumberInputPanel;

/**a dialog for altering the appearance of the arrow heads*/
public class ArrowSwingDialog extends ShapeGraphicOptionsSwingDialog{

	
	private static final double DEGREE_RADIANS = 180/Math.PI;
	private static final int FULL_DIALOG = 0, LIMITED_DIALOG=1;;
	ArrowGraphic arrow;
	private int dialogType=FULL_DIALOG;
	public ArrowSwingDialog(ArrowGraphic s, int limited) {
		super(s, limited==LIMITED_DIALOG);
		this.dialogType=limited;
		addOptionsToDialog2();
		
	}
	
	
	protected void addOptionsToDialogPart1() {
		this.addNameField(s);
	}
	
	
	protected void addOptionsToDialog2() {
		
		if((dialogType!=LIMITED_DIALOG)) addOptionsToDialogPart1();
		addOptionsToDialogPart2();
	}
	
	protected void addOptionsToDialog() {
		
		
	}
	
	
	protected void addOptionsToDialogPart2() {
		
		if ((dialogType!=LIMITED_DIALOG))this.addStrokePanelToDialog(s);
		if (s instanceof ArrowGraphic) {
			arrow=(ArrowGraphic) s;
		}
		NumberInputPanel nip = new NumberInputPanel("Head Size", arrow.getArrowHeadSize(),true, true, 0, 100);
		this.add("headsize", nip) ;
		
		NumberInputPanel aip = new NumberInputPanel("Tip Angle", arrow.getArrowTipAngle()*DEGREE_RADIANS, true, true, 0, 180);
		this.add("HeadAngle", aip);
		aip = new NumberInputPanel("Notch Angle", arrow.getNotchAngle()*DEGREE_RADIANS, true, true, 0, 180);
		this.add("NotchAngle", aip);
		ComboBoxPanel cip = new ComboBoxPanel("Number of heads", new String[] {"0", "1", "2"},arrow.getHeadnumber());
		this.add("HeadNum", cip);
		ComboBoxPanel cp = new ComboBoxPanel("Head Style", ArrowGraphic.arrowStyleChoices, arrow.getArrowStyle());
		this.add("style", cp);
	
	}
	
	protected void setItemsToDiaog() {
		this.setNameFieldToDialog(s);
		if ((dialogType!=LIMITED_DIALOG))this.setStrokedItemtoPanel(s);
		
		
		arrow.setArrowHeadSize((int)this.getNumber("headsize"));
		arrow.setArrowTipAngle(this.getNumber("HeadAngle")/DEGREE_RADIANS);
		arrow.setNotchAngle(this.getNumber("NotchAngle")/DEGREE_RADIANS);
		arrow.setHeadnumber(this.getChoiceIndex("HeadNum"));
		arrow.setArrowStyle(this.getChoiceIndex("style"));
		
}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
}
