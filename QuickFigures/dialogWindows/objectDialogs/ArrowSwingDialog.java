package objectDialogs;

import graphicalObjects_BasicShapes.ArrowGraphic;
import graphicalObjects_BasicShapes.ArrowGraphic.ArrowHead;
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
	
	/**Adds the arrow specific items to the dialog*/
	protected void addOptionsToDialogPart2() {
		
		if ((dialogType!=LIMITED_DIALOG))this.addStrokePanelToDialog(s);
		if (s instanceof ArrowGraphic) {
			arrow=(ArrowGraphic) s;
		}
		
		ComboBoxPanel cip = new ComboBoxPanel("Number of heads", new String[] {"0", "1", "2"},arrow.getNHeads());
		this.add("HeadNum", cip);
		
		ArrowHead head = arrow.getHead();
		String headCode="";
		
		addArrowHeadOptions(head, headCode);
		
		
		cip = new ComboBoxPanel("Outline", new String[] {"Do Not Outline", "Draw Outline Arrow"/**, "Outline for heads"*/},arrow.drawnAsOutline());
		this.add("outline", cip);
		
	
	}


	/**
	 Adds options for the given arrow head to the dialog
	 */
	public void addArrowHeadOptions(ArrowHead head, String headCode) {
		NumberInputPanel nip = new NumberInputPanel("Head Size"+ headCode, head.getArrowHeadSize(),true, true, 0, 100);
		this.add("headsize", nip) ;
		
		NumberInputPanel aip = new NumberInputPanel("Tip Angle"+ headCode, head.getArrowTipAngle()*DEGREE_RADIANS, true, true, 0, 180);
		this.add("HeadAngle", aip);
		aip = new NumberInputPanel("Notch Angle"+ headCode, head.getNotchAngle()*DEGREE_RADIANS, true, true, 0, 180);
		this.add("NotchAngle", aip);
		
		ComboBoxPanel cp = new ComboBoxPanel("Head Style"+headCode, ArrowGraphic.arrowStyleChoices, head.getArrowStyle());
		this.add("style", cp);
	}
	
	protected void setItemsToDiaog() {
		this.setNameFieldToDialog(s);
		if ((dialogType!=LIMITED_DIALOG))this.setStrokedItemtoPanel(s);
		arrow.setNHeads(this.getChoiceIndex("HeadNum"));
		
		ArrowHead head = arrow.getHead();
		String headCode="";
		
		setArrowHeadToDialog(head, headCode);
		
		arrow.setDrawAsOutline(this.getChoiceIndex("outline"));
		
}


	/**
	 * @param head
	 * @param headCode
	 */
	public void setArrowHeadToDialog(ArrowHead head, String headCode) {
		head.setArrowHeadSize((int)this.getNumber("headsize"+headCode));
		
		head.setArrowTipAngle(this.getNumber("HeadAngle"+headCode)/DEGREE_RADIANS);
		head.setNotchAngle(this.getNumber("NotchAngle"+headCode)/DEGREE_RADIANS);
		
		head.setArrowStyle(this.getChoiceIndex("style"+headCode));
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
}
