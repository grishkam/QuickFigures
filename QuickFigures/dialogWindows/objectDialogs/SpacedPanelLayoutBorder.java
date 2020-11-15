package objectDialogs;

import java.util.ArrayList;

import genericMontageKit.PanelContentExtract;
import graphicalObjects_LayoutObjects.SpacedPanelLayoutGraphic;
import plasticPanels.BasicSpacedPanelLayout;
import standardDialog.BooleanInputPanel;
import standardDialog.NumberInputPanel;

public class SpacedPanelLayoutBorder extends GraphicItemOptionsDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SpacedPanelLayoutGraphic layoutg;
	private BasicSpacedPanelLayout layoutl;

	
	public  SpacedPanelLayoutBorder(
			SpacedPanelLayoutGraphic item) {
		layoutg=item;
		layoutl=item.getPanelLayout();
		addOptionsToDialog() ;
	}
	
	
	protected void addOptionsToDialog() {
		add("bh", new NumberInputPanel("Spacing Horizontal ", layoutl.getHorizontalBorder(), 0,100));
		add("bv", new NumberInputPanel("Spacing Vertical ", layoutl.getVerticalBorder(), 0,100));
		add("bottomA", new BooleanInputPanel("Allign Bottoms", layoutl.getneighborFinder().isPerformBottomEdgeAllign()));
		add("rightA", new BooleanInputPanel("Allign Right edges", layoutl.getneighborFinder().isPerformRightEdgeAllign()));
	}
	protected void setItemsToDiaog() {
		layoutg.generateCurrentImageWrapper();
		ArrayList<PanelContentExtract> stack = layoutg.getEditor().cutStack(layoutl);
		 layoutl.setHorizontalBorder(this.getNumberInt("bh"));
		 layoutl.setVerticalBorder(this.getNumberInt("bv"));
		 layoutl.getneighborFinder().setPerformBottomEdgeAllign(this.getBoolean("bottomA"));
		 layoutl.getneighborFinder().setPerformRightEdgeAllign(this.getBoolean("rightA"));
		 layoutg.repack();
		 layoutg.repack();
		 layoutg.repack();
		 layoutg.getEditor().pasteStack(layoutl, stack);
		// layoutg.repack();
	}
}
