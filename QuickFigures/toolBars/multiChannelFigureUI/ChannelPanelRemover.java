/**
 * Author: Greg Mazo
 * Date Modified: Dec 8, 2020
 * Copyright (C) 2020 Gregory Mazo
 * 
 */
/**
 
 * 
 */
package multiChannelFigureUI;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import channelLabels.ChannelLabelManager;
import channelLabels.ChannelLabelTextGraphic;
import channelMerging.ImageDisplayLayer;
import genericMontageKit.PanelListElement;
import graphicalObjects_FigureSpecific.FigureOrganizingLayerPane;
import graphicalObjects_FigureSpecific.PanelOrderCorrector;
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

	/**Creates a channel panel remover for the figure
	 * @param pressedFigure
	 */
	public ChannelPanelRemover(FigureOrganizingLayerPane pressedFigure) {
		this.figure=pressedFigure;
		orderer=new PanelOrderCorrector(figure);
	}

	/**Adds channel panels for the given channel to the figure
	 * @param chan The channel added
	 * @return an undoable edit
	 */
	public CombinedEdit addChannelPanels(int chan) {
		figure.updatePanelLevelScale();
		
		CombinedEdit output=new CombinedEdit();
				
				output.addEditToList(
						PanelManagerUndo.createForMany(figure.getMultiChannelDisplaysInOrder()));
				
				Integer changeLayout = orderer.determineChannelLayout();
			boolean insertIntoLayout = changeLayout!=null && (changeLayout==ROWS||changeLayout==COLS);
			if (insertIntoLayout) {
					
					DefaultLayoutGraphic layout = figure.getMontageLayoutGraphic();
					output.addEditToList(new UndoLayoutEdit(layout));	
					if (changeLayout==ROWS)layout.getEditor().addRow(layout.getPanelLayout(), 1, null);
					if (changeLayout==COLS)layout.getEditor().addColumn(layout.getPanelLayout(), 1, null);
				}
			
			ArrayList<PanelListElement> panels =new ArrayList<PanelListElement>();
			for(ImageDisplayLayer d: figure.getMultiChannelDisplaysInLayoutOrder()) {
				output.addEditToList(new UndoLayerContentChange(d));
				panels.addAll(
						d.getPanelManager().generateManyChannelPanels(chan)
						);
				
				d.getPanelManager().getPanelList().getChannelUseInstructions().setChannelPanelExcluded(chan, false);
				
				
				if (figure.getPrincipalMultiChannel()==d) {
					addChannelLabel(chan, d);
				}
				
				
				d.getPanelManager().updatePanels();
				
				
				
			}
			
			DefaultLayoutGraphic layout = figure.getMontageLayoutGraphic();
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
			
		if (changeLayout!=null)
			figure.updateChannelOrder(changeLayout);
		
			
			output.establishFinalState();
			return output;
			
	}

	/**Adds a channel label for a newly created channel panel
	 * @param chan the channel that is being added
	 * @param d the multidimensional images' display layer
	 */
	void addChannelLabel(int chan, ImageDisplayLayer d) {
		/**Adds a channel label to the selected channel*/
		ChannelLabelManager channelLabelManager = d.getChannelLabelManager();
		for( PanelListElement panel: channelLabelManager.getPanelList().getPanels()) {
					if(channelLabelManager.isNonLabeledSlice(panel)
							|| panel.targetChannelNumber!=chan
							|| panel.isTheMerge()) 
						continue;
					ArrayList<ChannelLabelTextGraphic> oldLabels = channelLabelManager.getAllLabels();
					ChannelLabelTextGraphic newLabel = channelLabelManager.generateChannelLabelFor(panel);
					if(oldLabels.size()>0) newLabel.copyAttributesFrom(oldLabels.get(0));
			}
		
	}

	/**
	 removes the channel panels for the given channel
	 */
	public CombinedEdit removeChannelPanels(int chaneIndex) {
		CombinedEdit output=new CombinedEdit();
		
		output.addEditToList(
				new ChannelUseChangeUndo(figure.getPrincipalMultiChannel().getPanelList().getChannelUseInstructions()));
		
		Integer changeLayout = orderer.determineChannelLayout();
	if (changeLayout!=null && (changeLayout==ROWS||changeLayout==COLS)) {
			
			DefaultLayoutGraphic layout = figure.getMontageLayoutGraphic();
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
		
		for(ImageDisplayLayer d: figure.getMultiChannelDisplays()) {
			for(PanelListElement l:d.getPanelList().getPanels()) {
				if (!l.isTheMerge()&&l.targetChannelNumber==chaneIndex) {
					output.addEditToList(
							d.getPanelManager().removeDisplayObjectsFor(l)
							);
					
				}
			}
			d.getPanelManager().getPanelList().getChannelUseInstructions().setChannelPanelExcluded(chaneIndex, true);
			d.getPanelManager().updatePanels();
		}

		
		output.establishFinalState();
		return output;
	}

}
