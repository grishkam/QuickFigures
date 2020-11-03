package popupMenusForComplexObjects;

import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import objectDialogs.GraphicItemOptionsDialog;
import standardDialog.BooleanInputPanel;
import standardDialog.ComboBoxPanel;

public class MontageLayoutDisplayOptions extends GraphicItemOptionsDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PanelLayoutGraphic mg;
	
	public MontageLayoutDisplayOptions(PanelLayoutGraphic mg) {
		this.setWindowCentered(true);
		this.setModal(true);
		this.mg=mg;
		addOptionsToDialog() ;
	}
	
	public void addOptionsToDialog() {
		this.addNameField(mg);
		this.add("editmode", new ComboBoxPanel("How to handle edits", new String[] {"Contents of parent layer", "Layout only"}, mg.getEditMode()));
		this.add("always show", new BooleanInputPanel("Always Show", mg.isAlwaysShow()));
		this.add("locked in place", new BooleanInputPanel("Protected from mouse Drags ", mg.isUserLocked()==1));
	}
	
	protected void setItemsToDiaog() {
		mg.setName(this.getString("name"));
		mg.setEditMode(this.getChoiceIndex("editmode"));
		mg.setAlwaysShow(this.getBoolean("always show"));
		mg.setUserLocked(this.getBoolean("locked in place")?1:0);
	}

}
