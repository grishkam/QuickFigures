package figureFormat;

import graphicalObjects_LayoutObjects.MontageLayoutGraphic;
import gridLayout.BasicMontageLayout;

public class GridLayoutPicker extends GraphicalItemPicker<MontageLayoutGraphic> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	{super.optionname="Chose Grid Layout";}
	
	public GridLayoutPicker(MontageLayoutGraphic model) {
		super(model);
		// TODO Auto-generated constructor stub
	}

	@Override
	boolean isDesirableItem(Object o) {
		if (o instanceof MontageLayoutGraphic) return true;
		return false;
	}
	
	@Override
	public void applyProperties(Object item) {
		
		if (this.getModelItem()==null) return;
		if (!(item instanceof MontageLayoutGraphic)) return;
		MontageLayoutGraphic item2=(MontageLayoutGraphic) item;
		item2.generateCurrentImageWrapper();//required or the items within the layout wont get moved
		
		
		BasicMontageLayout layout = item2.getPanelLayout();
		BasicMontageLayout modelLayout = super.getModelItem().getPanelLayout();
		item2.getEditor().setBordersToModelLayout(layout, modelLayout);
		item2.getEditor().setLabelSpacesToModelLayout(layout, modelLayout);
		
		
	}
	
	boolean displayGraphicChooser() {
		return false;
	}

}
