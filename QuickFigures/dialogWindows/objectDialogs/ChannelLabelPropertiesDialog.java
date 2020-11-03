package objectDialogs;

import java.util.ArrayList;

import channelLabels.ChannelLabel;
import channelLabels.ChannelLabelProperties;
import graphicActionToombar.CurrentSetInformerBasic;
import standardDialog.ComboBoxPanel;
import standardDialog.StringInputPanel;

public class ChannelLabelPropertiesDialog extends GraphicItemOptionsDialog {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<ChannelLabel> labels;
	ChannelLabelProperties properties;

	public  ChannelLabelPropertiesDialog(ChannelLabelProperties p) {
		properties = p;
		this.addOptionsToDialog();
	}
	
	public void setLabelItems(Iterable<?> items) {
	labels=new ArrayList<ChannelLabel>();
		for(Object  i:items) {
			if (i instanceof ChannelLabel)  labels.add((ChannelLabel) i);
		}
	}
	
	protected void addOptionsToDialog() {
		ComboBoxPanel mergeTypeCombo = new ComboBoxPanel("How To label Merge",ChannelLabelProperties.mergeLabelOptions, properties.getMergeLabelType() );
		this.add("mergeType", mergeTypeCombo );
		ComboBoxPanel mergeTextCombo = new ComboBoxPanel("Merge Label",ChannelLabelProperties.mergeTexts, properties.getMergeTextOption() );
		this.add("mergeText", mergeTextCombo  );
		ComboBoxPanel sepTextCombo = new ComboBoxPanel("Separator Used For Single Line Label",ChannelLabelProperties.separatorOptions, properties.getSaparatorOption() );
		this.add("sepText", sepTextCombo  );
		this.add("Custom Merge Text ", new StringInputPanel("Custom Merge Text ",properties.getCustomMergeText() ));
		this.add("Custom Separator ", new StringInputPanel("Custom Separator ",properties.getCustomSeparator() ));
	}
	protected void setItemsToDiaog() {
		properties.setMergeLabelType(this.getChoiceIndex("mergeType"));
		properties.setMergeTextOption(this.getChoiceIndex("mergeText"));
		properties.setSaparatorOption(this.getChoiceIndex("sepText"));
		properties.setCustomMergeText(this.getString("Custom Merge Text "));
		properties.setCustomSeparator(this.getString("Custom Separator "));
		if (labels!=null) {
			for(ChannelLabel l:labels) {l.setParaGraphToChannels();}
		}
		CurrentSetInformerBasic.canvasResize();
	}
}
