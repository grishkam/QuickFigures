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
 * Date Modified: Jan 5, 2021
 * Version: 2022.1
 */
package undo;

import java.util.ArrayList;

import channelMerging.ImageDisplayLayer;
import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.PanelList;
import figureOrganizer.PanelListElement;
import figureOrganizer.PanelManager;
import figureOrganizer.insetPanels.PanelGraphicInsetDefiner;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import graphicalObjects_SpecialObjects.BarGraphic;

/**An undo for changes made to many parts of a split channel figure. since those edits are ofter done by methods in the
 *  panel manager class, it is called the panel manager undo.
  */
public class PanelManagerUndo extends CombinedEdit {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PanelList list;
	private double innitialPixelDensityRatio;
	private double finalPixelDensityRatio;
	private ArrayList<PanelListElement> iPanels=new ArrayList<PanelListElement>();
	private ArrayList<PanelListElement> fPanels=new ArrayList<PanelListElement>();
	
	public PanelManagerUndo(PanelList list) {
		this.list=list;
		innitialPixelDensityRatio=list.getPixelDensityRatio();
		iPanels=new ArrayList<PanelListElement>();
		iPanels.addAll(list.getPanels());
		for(PanelListElement i:iPanels) {
			ListElementUndo edit = new ListElementUndo(i);
			addEditToList(edit);
			if(edit.iBar!=null) {addEditToList(edit.iBar.provideUndoForDialog());}
		}
		addEditToList(new ChannelUseChangeUndo(list.getChannelUseInstructions()));
		
	}
	
	/**stores the current state of all the objects as the final state for the undo*/
	public void establishFinalState() {
		super.establishFinalState();
		finalPixelDensityRatio=list.getPixelDensityRatio();
		fPanels=new ArrayList<PanelListElement>();
		fPanels.addAll(list.getPanels());
		
	}
	
	
	public void redo() {
		super.redo();
		list.setPixelDensityRatio(finalPixelDensityRatio);
		list.eliminateAllPanels();
		list.getPanels().addAll(fPanels);
	}
	
	public void undo() {
		super.undo();
		list.setPixelDensityRatio(innitialPixelDensityRatio);
		list.eliminateAllPanels();
		list.getPanels().addAll(iPanels);
	}

	/**creates an undo for the given panel manager*/
	public static CombinedEdit createFor(PanelManager pm) {
		CombinedEdit output = new CombinedEdit();
		output.addEditToList(new PanelManagerUndo(pm.getPanelList()));
		output.addEditToList(new UndoLayerContentChange(pm.getImageDisplayLayer()));
		output.addEditToList(new UndoLayerContentChange(pm.getLayer()));
		DefaultLayoutGraphic layout = pm.getGridLayout();
		if (layout!=null) {
			layout.generateCurrentImageWrapper();
			output.addEditToList(new UndoLayoutEdit(layout));
		}
		output.addEditListener(pm);
		return output;
	}
	
	/**creates an undoable edit for one imaged isplay layer*/
	public static CombinedEdit createFor(ImageDisplayLayer pm) {
		CombinedEdit output = createFor(pm.getPanelManager());
		output.addEditToList(new PreprocessChangeUndo(pm));
		return output;
	}
	
	/**creates an undoable edit for one imaged isplay layer*/
	public static CombinedEdit createFor(FigureOrganizingLayerPane pm) {
		CombinedEdit output = new CombinedEdit();
		for(ImageDisplayLayer a:pm.getMultiChannelDisplaysInLayoutOrder()) {
			output.addEditToList(createFor(a));
		}
		for(ZoomableGraphic a:pm.getAllGraphics()) {
			if(a instanceof PanelGraphicInsetDefiner) {
				output.addEditToList(
						createFor(((PanelGraphicInsetDefiner) a).getPanelManager())
				);
			}
			
		}
		return output;
	}
	
	/**creates an undoable edit for every image display layer*/
	public static CombinedEdit createForMany(ArrayList<? extends ImageDisplayLayer> all) {
		CombinedEdit output = new CombinedEdit();
		for(ImageDisplayLayer a:all) {
			output.addEditToList(createFor(a));
		}
		return output;
	}
	
	/**creates an undoable edit for every image display layer*/
	public static CombinedEdit createForManyInset(ArrayList<?> all) {
		CombinedEdit output = new CombinedEdit();
		for(Object a:all) {
			if(a instanceof PanelGraphicInsetDefiner) {
				PanelGraphicInsetDefiner panelGraphicInsetDefiner = (PanelGraphicInsetDefiner)a;
				output.addEditToList(createFor(panelGraphicInsetDefiner.getPanelManager()));
			}
		}
		return output;
	}
	
	/**an undo for a specific element in thte panel list*/
	public class ListElementUndo extends AbstractUndoableEdit2  {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private PanelListElement iElement;
		private PanelListElement fElement;
		private PanelListElement panel;
		private BarGraphic iBar;
		private BarGraphic fBar;
		
		
		
		
		public ListElementUndo(PanelListElement p) {
			this.panel=p;
			iElement=p.copy();
			iBar=p.getScaleBar();
			
		}
		public void establishFinalState() {
			fElement=panel.copy();
			fBar=panel.getScaleBar();
			
		}
		public void redo() {
			fElement.giveObjectsAndSettingsTo(panel);
			if (iElement.getScaleBar()!=fBar)iElement.setScaleBar(fBar);
		}
		
		public void undo() {
			iElement.giveObjectsAndSettingsTo(panel);
			if (iElement.getScaleBar()!=iBar)iElement.setScaleBar(iBar);
				}
	}
	
}
