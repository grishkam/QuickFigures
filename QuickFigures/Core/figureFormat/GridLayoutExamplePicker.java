package figureFormat;

import javax.swing.undo.AbstractUndoableEdit;

import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import gridLayout.BasicMontageLayout;
import undo.UndoLayoutEdit;

/**A graphical item picker for layouts*/
public class GridLayoutExamplePicker extends GraphicalItemPicker<MontageLayoutGraphic> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	{super.optionname="Chose Grid Layout";}
	
	public GridLayoutExamplePicker(MontageLayoutGraphic model) {
		super(model);
	}

	@Override
	boolean isDesirableItem(Object o) {
		if (o instanceof MontageLayoutGraphic) return true;
		return false;
	}
	
	/**if the object is a layout, changes the borders and spaces to fit the format of the modelLayout
	 * @return */
	@Override
	public AbstractUndoableEdit applyProperties(Object item) {
		if (this.getModelItem()==null) return null;
		if (!(item instanceof MontageLayoutGraphic)) return null;
		MontageLayoutGraphic item2=(MontageLayoutGraphic) item;
		UndoLayoutEdit undo = new UndoLayoutEdit(item2);
		item2.generateCurrentImageWrapper();//required or the items within the layout wont get moved
		
		
		BasicMontageLayout layout = item2.getPanelLayout();
		BasicMontageLayout modelLayout = super.getModelItem().getPanelLayout();
		item2.getEditor().setBordersToModelLayout(layout, modelLayout);
		item2.getEditor().setLabelSpacesToModelLayout(layout, modelLayout);
		
		return undo;
	}
	
	/**returns true if the dialog should show this object itself in a combo box or popup menu.
	  if false, will just show the objects name*/
	boolean displayGraphicChooser() {
		return false;
	}

}
