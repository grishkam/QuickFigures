package objectDialogs;

import java.awt.GridBagConstraints;
import java.util.ArrayList;

import channelMerging.ChannelEntry;
import standardDialog.StandardDialog;
import standardDialog.SwingDialogListener;
import utilityClassesForObjects.TextLine;
import channelLabels.ChannelLabelProperties;

public class TextLineDialogForChenLabel extends GraphicItemOptionsDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TextLine theTextLine;
	
	
	public  TextLineDialogForChenLabel(TextLine p) {
		theTextLine = p;
		this.addOptionsToDialog();
	}
	
	public void setLabelItems(Iterable<?> items) {
	
	}
	
	protected void addOptionsToDialog() {
		
		LinePane tabsfull = new LinePane(theTextLine);
		tabsfull.addObjectEditListener(this);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx=gx;
		c.gridy=gy;
		c.gridheight=4;
		c.gridwidth=6;
		this.getMainPanel().add(tabsfull, c);
		gy=5;
		gymax=5;
		
	
	
	}
	protected void setItemsToDiaog() {
		
	}
	
	public static void main(String[] artgs) {
		String chanLabel="eGFP";
		
		showCustomDialogForChannel(chanLabel, new ChannelLabelProperties());
		
		
	}
	
	public static void showCustomDialogForChannel(String chanLabel, ChannelLabelProperties prop) {
		
		
		
				//TextLineDialogForChenLabel dialog = new TextLineDialogForChenLabel(lin);
			//	dialog.setModal(true);
				//dialog.showDialog();
		
	}
	
	public static StandardDialog showMultiTabDialogDialogss(ArrayList<ChannelEntry> chans, ChannelLabelProperties prop, SwingDialogListener sdl) {
		StandardDialog jf = createMultiLineDialog(chans, prop, sdl);
		return jf;
		
	}

	protected static StandardDialog createMultiLineDialog(ArrayList<ChannelEntry> chans, ChannelLabelProperties prop,
			SwingDialogListener sdl) {
		StandardDialog jf = new StandardDialog();
		jf.getOptionDisplayTabs().remove(jf.getMainPanel());
		for(ChannelEntry chan:chans) {
		
			TextLine lin = prop.getTextLineForChannel(chan);
			TextLineDialogForChenLabel dis = new TextLineDialogForChenLabel(lin);
			dis.addDialogListener(sdl);
			
			jf.addSubordinateDialog(chan.getShortLabel(), dis);
			
			//dis.showDialog();
		}
		return jf;
	}

}
