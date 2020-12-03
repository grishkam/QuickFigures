/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package selectedItemMenus;

import java.util.ArrayList;

import genericMontageKit.BasicObjectListHandler;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_FigureSpecific.FigureScaler;
import graphicalObjects_FigureSpecific.PanelGraphicInsetDefiner;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import sUnsortedDialogs.ScaleAboutDialog;
import undo.CombinedEdit;
import utilityClasses1.ArraySorter;

public class ScalingSyncerFigures extends BasicMultiSelectionOperator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
			
			CombinedEdit undo = new FigureScaler(true).scaleMultipleFigures(layouts,aa.getAbout(), aa.getScaleLevel());
			
			
			BasicObjectListHandler boh = new BasicObjectListHandler();
			
			boh.resizeCanvasToFitAllObjects(selector.getImageWrapper());
			
			selector.getGraphicDisplayContainer().getUndoManager().addEdit(undo);
	}
	
	void removeThoseForInsets(ArrayList<ZoomableGraphic> panelLayouts) {
		ArrayList<PanelGraphicInsetDefiner> insets = PanelGraphicInsetDefiner.getInsetDefinersFromLayer(getTopLayer(selector.getSelectedLayer()));
		
		for(PanelGraphicInsetDefiner ins: insets) {
			panelLayouts.remove(ins.personalGraphic);
		}
	
	}
	
	GraphicLayer getTopLayer(GraphicLayer layer) {
		while(layer.getParentLayer()!=null) layer=layer.getParentLayer();
		return layer;
	}
	
	

}
