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
 * Date Modified: Jan 12, 2021
 * Version: 2022.0
 */
package figureEditDialogs;

import java.util.ArrayList;

import channelLabels.ChannelLabel;
import channelLabels.ChannelLabelProperties;
import channelLabels.MergeLabelStyle;
import graphicActionToolbar.CurrentFigureSet;
import imageDisplayApp.CanvasOptions;
import objectDialogs.GraphicItemOptionsDialog;
import standardDialog.choices.ChoiceInputPanel;
import standardDialog.strings.StringInputPanel;
import undo.ChannelLabelPropertiesUndo;
import undo.CombinedEdit;
import undo.ProvidesDialogUndoableEdit;

/**A dialog that allows the user to change how the channel label's are shown
 */
public class ChannelLabelPropertiesDialog extends GraphicItemOptionsDialog {

	private static final long serialVersionUID = 1L;
	private ArrayList<ChannelLabel> labels;
	ChannelLabelProperties properties;

	/**Constructs a dialog for the given channel label properties*/
	public  ChannelLabelPropertiesDialog(ChannelLabelProperties p) {
		this.setTitle("Merge Label");
		properties = p;
		this.addOptionsToDialog();
		undo=new ChannelLabelPropertiesUndo(p);
	}
	
	/**sets the channel labels that this dialog affects*/
	public void setLabelItems(Iterable<?> items) {
	labels=new ArrayList<ChannelLabel>();
	CombinedEdit labelUndo = new CombinedEdit(undo);
		for(Object  i:items) {
			if (i instanceof ChannelLabel) {
				ChannelLabel i2 = (ChannelLabel) i;
				labels.add(i2);
				
			}
			if (i instanceof ProvidesDialogUndoableEdit) {
				labelUndo.addEditToList(((ProvidesDialogUndoableEdit) i).provideUndoForDialog());
			}
		}
		undo=labelUndo;
	}
	
	/**Adds the options to the dialog*/
	protected void addOptionsToDialog() {
		ChoiceInputPanel mergeTypeCombo = new ChoiceInputPanel("How To label Merge",ChannelLabelProperties.mergeLabelOptions, properties.getMergeLabelStyle().ordinal() );
		this.add("mergeType", mergeTypeCombo );
		ChoiceInputPanel mergeTextCombo = new ChoiceInputPanel("Merge Label",ChannelLabelProperties.mergeTexts, properties.getMergeTextOption() );
		this.add("mergeText", mergeTextCombo  );
		ChoiceInputPanel sepTextCombo = new ChoiceInputPanel("Separator Used For Single Line Label",ChannelLabelProperties.separatorOptions, properties.getSeparatorOption() );
		this.add("sepText", sepTextCombo  );
		this.add("Custom Merge Text ", new StringInputPanel("Custom Merge Text ",properties.getCustomMergeText() ));
		this.add("Custom Separator ", new StringInputPanel("Custom Separator ",properties.getCustomSeparator() ));
	}
	protected void setItemsToDiaog() {
		int choiceIndex = this.getChoiceIndex("mergeType");
		
		properties.setMergeLabelStyle(MergeLabelStyle.values()[choiceIndex]);
		properties.setMergeTextOption(this.getChoiceIndex("mergeText"));
		properties.setSaparatorOption(this.getChoiceIndex("sepText"));
		properties.setCustomMergeText(this.getString("Custom Merge Text "));
		properties.setCustomSeparator(this.getString("Custom Separator "));
		if (labels!=null) {
			for(ChannelLabel l:labels) {l.setParaGraphToChannels();}
		}
		if (CanvasOptions.current.resizeCanvasAfterEdit)
			CurrentFigureSet.canvasResize();
	}
}
