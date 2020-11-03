package graphicalObjects_LayoutObjects;

import menuUtil.PopupMenuSupplier;
import plasticPanels.PlasticPanelLayout;
import popupMenusForComplexObjects.PlasticPanelLayoutPanelMenu;
import utilityClassesForObjects.LocatedObject2D;

public class PlasticPanelLayoutGraphic extends SpacedPanelLayoutGraphic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	{layout=new PlasticPanelLayout(3);}

	
	public PlasticPanelLayout getPanelLayout() {
		if (this.layout instanceof PlasticPanelLayout) return (PlasticPanelLayout) this.layout;
	return null;	
	}
	
	public PopupMenuSupplier getMenuSupplier(){
		
		return new  PlasticPanelLayoutPanelMenu(this);
	}
	
	 void resizePanelsToFit(LocatedObject2D l) {
		 	Integer loc = this.getPanelLocations().get(l);
		this.getPanelLayout().setPanelWidth(loc, l.getBounds().width);
		this.getPanelLayout().setPanelHeight(loc, l.getBounds().height);
		this.repack();
	}
	

}
