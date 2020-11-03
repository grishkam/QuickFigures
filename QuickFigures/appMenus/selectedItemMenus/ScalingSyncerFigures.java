package selectedItemMenus;

import java.util.ArrayList;

import genericMontageKit.BasicOverlayHandler;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_FigureSpecific.FigureScaler;
import graphicalObjects_FigureSpecific.PanelGraphicInsetDef;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import sUnsortedDialogs.ScaleAboutDialog;
import undo.CompoundEdit2;
import utilityClasses1.ArraySorter;

public class ScalingSyncerFigures extends BasicMultiSelectionOperator {

	@Override
	public String getMenuCommand() {
		return  "Scale Figures";
	}
	
	public String getMenuPath() {
		
		return "Scale";
	}

	@Override
	public void run() {
		ScaleAboutDialog aa = new ScaleAboutDialog();
		aa.setModal(true);
		aa.setWindowCentered(true);
		aa.showDialog();
		
		if (!aa.wasOKed()) return;
		
			ArrayList<ZoomableGraphic> items = super.getAllArray();//super.selector.getSelecteditems();
			
			ArrayList<ZoomableGraphic> panelLayouts = new ArraySorter<ZoomableGraphic> ().getThoseOfClass(items, PanelLayoutGraphic.class);
			removeThoseForInsets(panelLayouts);
			
			ArrayList<PanelLayoutGraphic> layouts = new ArrayList<PanelLayoutGraphic>();
			for(ZoomableGraphic p: panelLayouts) {layouts.add((PanelLayoutGraphic) p);}
			
			CompoundEdit2 undo = new FigureScaler(true).scaleMultipleFigures(layouts,aa.getAbout(), aa.getScaleLevel());
			
			
			BasicOverlayHandler boh = new BasicOverlayHandler();
			
			boh.resizeCanvasToFitAllObjects(selector.getImageWrapper());
			
			selector.getGraphicDisplayContainer().getUndoManager().addEdit(undo);
	}
	
	void removeThoseForInsets(ArrayList<ZoomableGraphic> panelLayouts) {
		ArrayList<PanelGraphicInsetDef> insets = PanelGraphicInsetDef.getInsetDefinersFromLayer(getTopLayer(selector.getSelectedLayer()));
		
		for(PanelGraphicInsetDef ins: insets) {
			panelLayouts.remove(ins.personalGraphic);
		}
	
	}
	
	GraphicLayer getTopLayer(GraphicLayer layer) {
		while(layer.getParentLayer()!=null) layer=layer.getParentLayer();
		return layer;
	}
	
	

}
