package addObjectMenus;

import java.awt.geom.Rectangle2D;

import dividedPanels.DividedPanelLayout;
import dividedPanels.DividedPanelLayoutGraphic;
import dividedPanels.DividedPanelLayout.layoutDividedArea;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;

public class DividedLayoutAdder extends BasicGraphicAdder {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public ZoomableGraphic add(GraphicLayer gc) {
		GraphicLayerPane l22 = new GraphicLayerPane("Divided Layout Layer");
		gc.add(l22);
		PanelLayoutGraphic p = getNewLayout() ;
		l22.add(p);
		p.select();
		return l22;
	}
	
	public PanelLayoutGraphic getNewLayout() {
		
		DividedPanelLayout layout=new DividedPanelLayout(new Rectangle2D.Double(30, 30, 504,648));
		layout.mainArea.divide(216);
		layoutDividedArea sub = layout.mainArea.getSubareas().get(1);
		sub.setHorizontal(false);
		sub.divide(200);
		 sub = layout.mainArea.getSubareas().get(0);
		 sub.setHorizontal(false);
				sub.divide(180);
				sub.divide(360);
		 
		layout.mainArea.divide(432);
		
		DividedPanelLayoutGraphic p=new DividedPanelLayoutGraphic(layout);
		
		return p;
	}

	@Override
	public String getCommand() {
		return "Add Divided Layout";
	}

	@Override
	public String getMenuCommand() {
		return "Add Divided Layout";
	}

}
