package objectDialogs;

import java.awt.GridBagConstraints;

import graphicalObjects_BasicShapes.ComplexTextGraphic;
import graphicalObjects_BasicShapes.TextGraphic;
import standardDialog.BooleanInputPanel;
import standardDialog.ColorDimmingBox;
import standardDialog.ComboBoxPanel;
import standardDialog.ObjectEditEvent;

public class ComplexTextGraphicSwingDialog extends TextGraphicSwingDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ComplexTextGraphic ct=null;
	
	public ComplexTextGraphicSwingDialog(ComplexTextGraphic t) {
		textItem=t;
		ct=t;
		addOptionsToDialog();
	}

	
	protected void addOptionsToDialog() {
		this.addNameField(textItem);
		
		addJustificationToDialog(textItem);
		
		//this.add("Text", new StringInputPanel("Text", textItem.getText()));
		
		this.addFixedEdgeToDialog(textItem);
		super.addFontAngleToDialog();
		addBackgroundOptionsToDialog();
		ComboBoxPanel cp=new ComboBoxPanel("Color Dims ",  new ColorDimmingBox(textItem.getDimming()));
		this.add("dim", cp);
		this.getMainPanel().moveGrid(2, -1);
		this.add("dim?", new BooleanInputPanel("Dim Color?", textItem.isDimColor()));
		this.getMainPanel().moveGrid(-2, 0);
		
		addLineTabs();
		
		//textItem.setSnappingBehaviour(SnappingBehaviour.defaultInternal());
		addSnappingBehviourToDialog(textItem);
		
		
	}


	protected void addLineTabs() {
		ParaGraphPane tabsfull = new ParaGraphPane(ct.getParagraph());
		
		tabsfull.addObjectEditListener(this);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx=gx;
		c.gridy=gy;
		c.gridheight=4;
		c.gridwidth=6;
		//this.add(tabsfull, c);
		gy+=4;
		getOptionDisplayTabs().addTab("Edit Text Lines",tabsfull);
		
		
	}
	
	protected void setItemsToDiaog(TextGraphic textItem) {
		super.setNameFieldToDialog(textItem);
		this.setBackgroundOptionsToDialog(textItem);
		//textItem.setText(this.getString("Text"));
		setAtrributesToDialog(textItem);
	}
	
	protected void setItemsToDiaog() {
		setItemsToDiaog(textItem);
		textItem.setDimColor(this.getBoolean("dim?"));
		setComplexProperteisToDialog(ct);
}
	
	public static void main(String[] args) {
		ComplexTextGraphic g = new ComplexTextGraphic();
		new ComplexTextGraphicSwingDialog(g).showDialog();;
	}
	
	@Override
	public void objectEdited(ObjectEditEvent oee) {
		
		notifyAllListeners(null, oee.getKey());
		pack();
		
	}
	
	

}
