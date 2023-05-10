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
 * Date Modified: October 25, 2021
 * Version: 2023.2
 */
package multiChannelFigureUI;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import channelLabels.ChannelLabelManager;
import channelLabels.ChannelLabelTextGraphic;
import channelMerging.ChannelUseInstructions;
import figureOrganizer.CollectivePanelManagement;
import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.PanelListElement;
import figureOrganizer.PanelManagementGroup;
import figureOrganizer.PanelManager;
import figureOrganizer.PanelOrderCorrector;
import figureOrganizer.insetPanels.PanelGraphicInsetDefiner;
import graphicalObjects_LayoutObjects.DefaultLayoutGraphic;
import layout.basicFigure.BasicLayout;
import layout.basicFigure.LayoutSpaces;
import undo.ChannelUseChangeUndo;
import undo.CombinedEdit;
import undo.PanelManagerUndo;
import undo.UndoLayerContentChange;
import undo.UndoLayoutEdit;

/**
 Class to implement the add/remove channel panel option. 
 Seems to work perfectly but still work in progress due to limited testing
 */
public class ChannelPanelRemover implements LayoutSpaces{

	private FigureOrganizingLayerPane figure;
	private PanelOrderCorrector orderer;
	private CollectivePanelManagement panelManagement;
	
	public static final int ADD_CUSTOM_PANEL=-1;

	/**Creates a channel panel remover for the figure
	 * @param pressedFigure
	 */
	 ChannelPanelRemover(FigureOrganizingLayerPane pressedFigure) {
		this.figure=pressedFigure;
		panelManagement=new  PanelManagementGroup(figure);
		orderer=new PanelOrderCorrector(panelManagement);
		
	}

	/**
	 * @param pressedInset
	 */
	public ChannelPanelRemover(PanelGraphicInsetDefiner pressedInset) {
		panelManagement=new  InsetPanelManagementGroup(pressedInset);
		orderer=new PanelOrderCorrector(panelManagement);
	}

	/**Adds channel panels for the given channel to the figure
	 * @param chan The channel added
	 * @return an undoable edit
	 */
	public CombinedEdit addChannelPanels(Integer chan) {
		panelManagement.updatePanelLevelScale();
		
		CombinedEdit output=new CombinedEdit();
				
				output.addEditToList(
						PanelManagerUndo.createForMany(orderer.getDisplaysInOrder() ));
				
				Integer changeLayout = orderer.determineChannelLayout();
			boolean insertIntoLayout = changeLayout!=null && (changeLayout==ROWS||changeLayout==COLS);
			if (insertIntoLayout) {
					
					DefaultLayoutGraphic layout = getTargetLayout();
					output.addEditToList(new UndoLayoutEdit(layout));	
					if (changeLayout==ROWS)layout.getEditor().addRow(layout.getPanelLayout(), 1, null);
					if (changeLayout==COLS)layout.getEditor().addColumn(layout.getPanelLayout(), 1, null);
				}
			
			ArrayList<PanelListElement> panels =new ArrayList<PanelListElement>();
			for(PanelManager d: panelManagement.getPanelManagersInLayoutOrder()) {
				output.addEditToList(new UndoLayerContentChange(d));
				panels.addAll(
						d.generateManyPanels(chan)
						);
				
				if(chan!=null)
					d.getPanelList().getChannelUseInstructions().setChannelPanelExcluded(chan, false);
				if(chan==null)
					d.getPanelList().getChannelUseInstructions().MergeHandleing=ChannelUseInstructions.MERGE_FIRST;
				
				/**check if this manager is the same one that has channel labels*/
				if (panelManagement.getChannelLabelManager().getPanelList()==d.getPanelList()) {
					addChannelLabel(chan, panelManagement.getChannelLabelManager());
				}
				
				
				d.updatePanels();
				
				
				
			}
			
			DefaultLayoutGraphic layout = getTargetLayout();
			for(int i=0; i<panels.size(); i++) {
				int index=0;
				BasicLayout panelLayout = layout.getPanelLayout();
				if (changeLayout!=null&&changeLayout==ROWS) index=panelLayout.getIndexAtPosition(1,i+1);
				if (changeLayout!=null&&changeLayout==COLS) index=panelLayout.getIndexAtPosition(i+1, 1);
				
				if (index>0) {
					Rectangle2D rect = panelLayout.getPanel(index);
					panels.get(i).getImageDisplayObject().setLocationUpperLeft(rect.getX(), rect.getY());
					
					}
				
			}
			
		if (figure!=null&&changeLayout!=null)
			figure.updateChannelOrder(changeLayout);
		
			
			output.establishFinalState();
			return output;
			
	}

	

	/**
	 returns the layout that contains the targetted panels
	 */
	protected DefaultLayoutGraphic getTargetLayout() {
		return panelManagement.getTargetLayout();
	}

	/**Adds a channel label for a newly created channel panel
	 * if the channel number given is null, creates label for merge panel
	 * @param chan the channel that is being added
	 * @param channelLabelManager2 the multidimensional images' display layer
	 */
	void addChannelLabel(Integer chan, ChannelLabelManager channelLabelManager) {
		/**Adds a channel label to the selected channel*/
		
		ArrayList<PanelListElement> panels2 = channelLabelManager.getPanelList().getPanels();
		
		
		for( PanelListElement panel: panels2) {
					if(channelLabelManager.isNonLabeledSlice(panel)
							|| (chan!=null && panel.targetChannelNumber!=chan)
							|| (panel.isTheMerge()&&chan!=null)
							|| (!panel.isTheMerge()&&chan==null)
							) 
						continue;
					ArrayList<ChannelLabelTextGraphic> oldLabels = channelLabelManager.getAllLabels();
					ChannelLabelTextGraphic newLabel = channelLabelManager.generateChannelLabelFor(panel);
					if(oldLabels.size()>0) newLabel.copyAttributesFrom(oldLabels.get(0));
			}
		
	}

	/**
	 removes the channel panels for the given channel
	 */
	public CombinedEdit removeChannelPanels(Integer chaneIndex) {
		if(chaneIndex==null)
			chaneIndex=0;
		CombinedEdit output=new CombinedEdit();
		
		output.addEditToList(
				ChannelUseChangeUndo.createForManyManagers(panelManagement.getPanelManagers())
				//new ChannelUseChangeUndo(figure.getPrincipalMultiChannel().getPanelList().getChannelUseInstructions())
				);
		
		Integer changeLayout = orderer.determineChannelLayout();
	if (changeLayout!=null && (changeLayout==ROWS||changeLayout==COLS)) {
			
			DefaultLayoutGraphic layout = getTargetLayout();
			output.addEditToList(new UndoLayoutEdit(layout));
			
			ArrayList<Integer> possibleIndex = orderer.indexOfChannel(chaneIndex, changeLayout);
		
			if (possibleIndex.size()>0) {
				for (int removing=possibleIndex.size()-1; removing>=0; removing--) {
					int rowIndex=possibleIndex.get(removing);
					if (changeLayout==ROWS)layout.getEditor().removeRow(layout.getPanelLayout(), rowIndex);
					if (changeLayout==COLS)layout.getEditor().removeColumn(layout.getPanelLayout(), rowIndex);
				}
			}
		
			
		}
		
		for(PanelManager d: panelManagement.getPanelManagers()) {
			for(PanelListElement l:d.getPanelList().getPanels()) {
				boolean remove = (!l.isTheMerge()&&l.targetChannelNumber==chaneIndex)
						|| (l.isTheMerge()&&chaneIndex==0);
				if (remove) {
					output.addEditToList(
							d.removeDisplayObjectsFor(l)
							);
					
				}
			}
			
			
			if(chaneIndex>PanelListElement.NONE)
				d.getPanelList().getChannelUseInstructions().setChannelPanelExcluded(chaneIndex, true);
			if(chaneIndex==PanelListElement.NONE)
				d.getPanelList().getChannelUseInstructions().MergeHandleing=ChannelUseInstructions.NO_MERGE_PANELS;
			d.updatePanels();
		}

		
		output.establishFinalState();
		return output;
	}

	

}
