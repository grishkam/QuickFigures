/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
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
/**
 * Author: Greg Mazo
 * Date Modified: Jan 6, 2021
 * Version: 2022.0
 */
package selectedItemMenus;

import java.util.ArrayList;

import figureOrganizer.FigureScaler;
import figureOrganizer.insetPanels.PanelGraphicInsetDefiner;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import layout.BasicObjectListHandler;
import sUnsortedDialogs.ScaleAboutDialog;
import undo.CombinedEdit;
import utilityClasses1.ArraySorter;

/**opens a figure scaling dialog*/
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
		
			ArrayList<ZoomableGraphic> items = super.getAllArray();
			
			ArrayList<ZoomableGraphic> panelLayouts = new ArraySorter<ZoomableGraphic> ().getThoseOfClass(items, PanelLayoutGraphic.class);
			removeThoseForInsets(panelLayouts);
			
			ArrayList<PanelLayoutGraphic> layouts = new ArrayList<PanelLayoutGraphic>();
			for(ZoomableGraphic p: panelLayouts) {layouts.add((PanelLayoutGraphic) p);}
			
			CombinedEdit undo = new FigureScaler(true).scaleMultipleFigures(layouts,aa.getAbout(), aa.getScaleLevel());
			
			
			BasicObjectListHandler boh = new BasicObjectListHandler();
			
			boh.resizeCanvasToFitAllObjects(selector.getImageWrapper());
			
			selector.getWorksheet().getUndoManager().addEdit(undo);
	}
	
	/**excludes the layouts that are used by inset definers*/
	void removeThoseForInsets(ArrayList<ZoomableGraphic> panelLayouts) {
		ArrayList<PanelGraphicInsetDefiner> insets = PanelGraphicInsetDefiner.getInsetDefinersFromLayer(getTopLayer(selector.getSelectedLayer()));
		
		for(PanelGraphicInsetDefiner ins: insets) {
			panelLayouts.remove(ins.personalLayout);
		}
	
	}
	
	GraphicLayer getTopLayer(GraphicLayer layer) {
		while(layer.getParentLayer()!=null) layer=layer.getParentLayer();
		return layer;
	}
	
	

}
