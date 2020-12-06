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
package figureEditDialogs;

import java.util.ArrayList;

import channelLabels.ChannelLabel;
import channelLabels.ChannelLabelProperties;
import graphicActionToolbar.CurrentFigureSet;
import objectDialogs.GraphicItemOptionsDialog;
import standardDialog.ComboBoxPanel;
import standardDialog.StringInputPanel;

/**A dialog that allows the user to change how the channel label's are shown
 */
public class ChannelLabelPropertiesDialog extends GraphicItemOptionsDialog {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<ChannelLabel> labels;
	ChannelLabelProperties properties;

	/**Constructs a dialog for the given channel label properties*/
	public  ChannelLabelPropertiesDialog(ChannelLabelProperties p) {
		properties = p;
		this.addOptionsToDialog();
	}
	
	/**sets the channel labels that this dialog affects*/
	public void setLabelItems(Iterable<?> items) {
	labels=new ArrayList<ChannelLabel>();
		for(Object  i:items) {
			if (i instanceof ChannelLabel)  labels.add((ChannelLabel) i);
		}
	}
	
	protected void addOptionsToDialog() {
		ComboBoxPanel mergeTypeCombo = new ComboBoxPanel("How To label Merge",ChannelLabelProperties.mergeLabelOptions, properties.getMergeLabelStyle() );
		this.add("mergeType", mergeTypeCombo );
		ComboBoxPanel mergeTextCombo = new ComboBoxPanel("Merge Label",ChannelLabelProperties.mergeTexts, properties.getMergeTextOption() );
		this.add("mergeText", mergeTextCombo  );
		ComboBoxPanel sepTextCombo = new ComboBoxPanel("Separator Used For Single Line Label",ChannelLabelProperties.separatorOptions, properties.getSeparatorOption() );
		this.add("sepText", sepTextCombo  );
		this.add("Custom Merge Text ", new StringInputPanel("Custom Merge Text ",properties.getCustomMergeText() ));
		this.add("Custom Separator ", new StringInputPanel("Custom Separator ",properties.getCustomSeparator() ));
	}
	protected void setItemsToDiaog() {
		properties.setMergeLabelStyle(this.getChoiceIndex("mergeType"));
		properties.setMergeTextOption(this.getChoiceIndex("mergeText"));
		properties.setSaparatorOption(this.getChoiceIndex("sepText"));
		properties.setCustomMergeText(this.getString("Custom Merge Text "));
		properties.setCustomSeparator(this.getString("Custom Separator "));
		if (labels!=null) {
			for(ChannelLabel l:labels) {l.setParaGraphToChannels();}
		}
		CurrentFigureSet.canvasResize();
	}
}
