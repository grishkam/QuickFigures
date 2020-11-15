package channelMerging;

import javax.swing.JComboBox;

import standardDialog.ComboBoxPanel;
import standardDialog.StandardDialog;

/**"Dialog appears when the user chooses Saving Options for an image.
  Gives the user an option to load the image from a save location 
  after a figure is de-Serialized. Arguably, the user has no reason to change
  this from the default"*/
public class MultiChannelSlotDialog extends StandardDialog  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MultiChannelSlot theslot;

	public MultiChannelSlotDialog(MultiChannelSlot slot) {
		super("Retrieval", true);
		theslot = slot;
		JComboBox<?> retOps = new JComboBox<String>(MultiChannelSlot.retrivalOptions);
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
