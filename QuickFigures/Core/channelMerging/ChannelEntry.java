package channelMerging;

import java.awt.Color;
import java.io.Serializable;

import logging.IssueLog;

public class ChannelEntry implements Serializable{
	
	/**
	 * 
	 */
	
	
	private String label="";
	private Color originalLutCol=Color.BLACK;
	private int originalChannelIndex;
	private int originalStackIndex;
	private String additional="";
	private String realChannelName;
	private int exposureMS=0;
	
	private static final long serialVersionUID = 1L;
	public ChannelEntry(String label, Color c, int number) {
		this.setLabel(label);
		this.setColor(c);
		this.setOriginalChannelIndex(number);
	}
	
	public String getLabel() {
		return label;
	}
	
	public String getShortLabel() {
		if (label==null) return getSimpleLabel();
		String short1 = label.split(";")[0];
		if(short1.length()>20) return getSimpleLabel();
		return short1;
	}

	private String getSimpleLabel() {
		return "Channel "+this.getOriginalChannelIndex();
	}
	
	public void setLabel(String label) {
		this.label = label;
	}

	public Color getColor() {
		return originalLutCol;
	}

	public void setColor(Color originalLutCol) {
		this.originalLutCol = originalLutCol;
	}

	public int getOriginalStackIndex() {
		return originalStackIndex;
	}

	
	public void setOriginalStackIndex(int originalStackIndex) {
		this.originalStackIndex = originalStackIndex;
	}

	public int getOriginalChannelIndex() {
		return originalChannelIndex;
	}

	public void setOriginalChannelIndex(int originalChannelIndex) {
		this.originalChannelIndex = originalChannelIndex;
	}

	public String getAdditionalString() {
		return additional;
	}

	public void setAdditionalString(String additional) {
		this.additional = additional;
	}
	
	/**Getter and setter methods for channel names. not updated properly when channels swap*/
	public void setRealChannelName(String realChannelName) {
		this.realChannelName=realChannelName;
		
	}
	public String getRealChannelName() {
		return realChannelName;
	}
	
	public int getStoredExposureTime() {
		return exposureMS;
	}


	
	/**updates a channel entry in the event of a color change, to match*/
	public void updateFrom(ChannelEntry ce) {
		
		
		this.label=ce.label;
		this.originalLutCol=ce.originalLutCol;//lut color change
		this.originalChannelIndex=ce.originalChannelIndex;//in the even the channel order was change
		this.realChannelName=ce.getRealChannelName();
		
		/**
		IssueLog.log("Updating channel entry ");
		IssueLog.log(" index "+ce.originalChannelIndex);
		IssueLog.log(" name "+ce.getRealChannelName());
		*/
	}
	
	
	

}
