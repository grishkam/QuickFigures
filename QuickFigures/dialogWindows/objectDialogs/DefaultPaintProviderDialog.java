package objectDialogs;

import standardDialog.ColorInputPanel;
import standardDialog.ComboBoxPanel;
import standardDialog.FixedEdgeSelectable;
import standardDialog.ItemSelectblePanel;
import utilityClassesForObjects.DefaultPaintProvider;

public class DefaultPaintProviderDialog extends GraphicItemOptionsDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DefaultPaintProvider paintprovider;
	
	public DefaultPaintProviderDialog(DefaultPaintProvider d) {
		this.paintprovider=d;
		this.add("Color", new ColorInputPanel("Color", paintprovider.getColor() ));
		this.add("Color2", new ColorInputPanel("Color", paintprovider.getFillColor2() ));
		FixedEdgeSelectable f = new FixedEdgeSelectable(paintprovider.getFe1());
		ItemSelectblePanel is = new ItemSelectblePanel("Select Fixed Edge 1", f);
		add("edge fix1", is);
		
		FixedEdgeSelectable f2 = new FixedEdgeSelectable(paintprovider.getFe2());
		ItemSelectblePanel is2 = new ItemSelectblePanel("Select Fixed Edge 2", f2);
		add("edge fix2", is2);
		
		this.add("type", new ComboBoxPanel("Paint Type",DefaultPaintProvider.types, paintprovider.getType()));
	}
	
	protected void setItemsToDiaog() {
		paintprovider.setColor(this.getColor("Color"));
		paintprovider.setFillColor2(this.getColor("Color2"));
		paintprovider.setType(this.getChoiceIndex("type"));
		paintprovider.setFe1(this.getChoiceIndex("edge fix1"));
		paintprovider.setFe2(this.getChoiceIndex("edge fix2"));
	}
	

}