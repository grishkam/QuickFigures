package channelMerging;

import javax.swing.JComboBox;

import standardDialog.ComboBoxPanel;
import standardDialog.StandardDialog;

public class MultiChannelSlotDialog extends StandardDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MultiChannelSlot theslot;

	public MultiChannelSlotDialog(MultiChannelSlot slot) {
		super("Retrieval", true);
		theslot = slot;
		JComboBox retOps = new JComboBox(MultiChannelSlot.retrivalOptions);
		retOps.setSelectedIndex(slot.getRetrieval());
		this.add("Retrival", new ComboBoxPanel("How to store multichannel image", retOps));
		
	}
	
	public void setItemsToDialog() {
		theslot.setRetrival(super.getChoiceIndex("Retrival"));
	}
	
	/**what action to take when the ok button is pressed*/
	protected void onOK() {
		setItemsToDialog() ;
	}

}
