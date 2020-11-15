package addObjectMenus;

import graphicalObjects.ZoomableGraphic;
import graphicalObjects_BasicShapes.SimpleGraphicalObject;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import graphicalObjects_LayoutObjects.MontageLayoutGraphic;

class LayoutAdder extends BasicGraphicAdder {
	
	

	@Override
	public ZoomableGraphic add(GraphicLayer gc) {
		GraphicLayerPane l22 = new GraphicLayerPane("Layout Layer");
		gc.add(l22);
		MontageLayoutGraphic p = createStandard() ;
		p.showOptionsDialog();
		p.getPanelLayout().resetPtsPanels();
		p.moveLocation(10, 10);
		l22.add(p);
		
		return l22;
	}
	
	public MontageLayoutGraphic createStandard() {
MontageLayoutGraphic p = new MontageLayoutGraphic();
		
		p.getPanelLayout().setStandardPanelWidth(100);
		p.getPanelLayout().setStandardPanelHeight(100);
		p.getPanelLayout().setNColumns(3);
		p.select();
		return p;
	}

	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return "Add Grid Layout";
	}

	@Override
	public String getMenuCommand() {
		return "Add Normal Layout (a grid)";
	}

	
	public SimpleGraphicalObject getCurrentDisplayObject() {
		// TODO Auto-generated method stub
		return createStandard();
	}

	
	public void setCurrentDisplayObject(
			SimpleGraphicalObject currentDisplayObject) {
		// TODO Auto-generated method stub
		
	}}
