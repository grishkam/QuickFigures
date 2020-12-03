package undo;


import channelLabels.ChannelLabelProperties;

/**An undo for changes in the channel label properties*/
public class ChannelLabelPropertiesUndo extends AbstractUndoableEdit2 {

	private ChannelLabelProperties properties;
	private ChannelLabelProperties iProp;
	private ChannelLabelProperties fProp;

	public ChannelLabelPropertiesUndo(ChannelLabelProperties prop) {
		this.properties=prop;
		iProp=properties.copy();
	}
	
	public void establishFinalState() {
		fProp=properties.copy();
	}
	public void redo() {
		properties.copyOptionsFrom(fProp);
	}
	
	public void undo() {
		properties.copyOptionsFrom(iProp);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
}
