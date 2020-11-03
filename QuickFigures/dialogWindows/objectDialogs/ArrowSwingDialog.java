package objectDialogs;

import graphicalObjects_BasicShapes.ArrowGraphic;
import standardDialog.ComboBoxPanel;
import standardDialog.NumberInputPanel;

public class ArrowSwingDialog extends ShapeGraphicOptionsSwingDialog{

	
	ArrowGraphic arrow;
	private int limited;
	public ArrowSwingDialog(ArrowGraphic s, int limited) {
		super(s);
		this.limited=limited;
		addOptionsToDialog2();
		
	}
	
	
	protected void addOptionsToDialogPart1() {
		this.addNameField(s);
		//this.addFixedEdgeToDialog(s);
	}
	
	
	protected void addOptionsToDialog2() {
		
		if(!(limited==1)) addOptionsToDialogPart1();
		addOptionsToDialogPart2();
	}
	
	protected void addOptionsToDialog() {
		
		
	}
	
	
	protected void addOptionsToDialogPart2() {
		// TODO Auto-generated method stub
		
		if (!(limited==1))this.addStrokePanelToDialog(s);
		if (s instanceof ArrowGraphic) {
			arrow=(ArrowGraphic) s;
		}
		NumberInputPanel nip = new NumberInputPanel("Head Size", arrow.getArrowHeadSize(),true, true, 0, 100);
		this.add("headsize", nip) ;
		//this.addSlider("Arrow Head Length", 0, 100, rect.getArrowHeadSize());
		
		NumberInputPanel aip = new NumberInputPanel("Tip Angle", arrow.getArrowTipAngle()*(180/Math.PI), true, true, 0, 180);
		this.add("HeadAngle", aip);
		aip = new NumberInputPanel("Notch Angle", arrow.getNotchAngle()*(180/Math.PI), true, true, 0, 180);
		this.add("NotchAngle", aip);
		ComboBoxPanel cip = new ComboBoxPanel("Number of heads", new String[] {"0", "1", "2"},arrow.getHeadnumber());
		this.add("HeadNum", cip);
		ComboBoxPanel cp = new ComboBoxPanel("Head Style", ArrowGraphic.arrowStyleChoices, arrow.getArrowStyle());
		this.add("style", cp);
		
		//if (s.getSnappingBehaviour()!=null) this.addSnappingBehviourToDialog(s);
	}
	
	protected void setItemsToDiaog() {
		//this.setFixedEdgeToDialog(s);
		this.setNameFieldToDialog(s);
		if (!(limited==1))this.setStrokedItemtoPanel(s);
		
		
		arrow.setArrowHeadSize((int)this.getNumber("headsize"));
		arrow.setArrowTipAngle(this.getNumber("HeadAngle")/(180/Math.PI));
		arrow.setNotchAngle(this.getNumber("NotchAngle")/(180/Math.PI));
		arrow.setHeadnumber(this.getChoiceIndex("HeadNum"));
		arrow.setArrowStyle(this.getChoiceIndex("style"));
		
}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
}
