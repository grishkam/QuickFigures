package graphicalObjects_LayoutObjects;

import java.util.ArrayList;

import genericMontageKit.PanelContentExtract;
import plasticPanels.BasicSpacedPanelLayout;

public class SpacedPanelLayoutGraphic extends PanelLayoutGraphic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public BasicSpacedPanelLayout getPanelLayout() {
		if (this.layout instanceof BasicSpacedPanelLayout) return (BasicSpacedPanelLayout) this.layout;
	return null;	
	}
	
	public void repack() {
		this.generateCurrentImageWrapper();
		ArrayList<PanelContentExtract> stack = this.getEditor().cutStack(getPanelLayout());
		getPanelLayout().autoLocatePanels();
		getEditor().pasteStack(getPanelLayout(), stack);
		this.snapLockedItems();
		
	}

}
