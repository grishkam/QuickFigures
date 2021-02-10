/**
 * Author: Greg Mazo
 * Date Modified: Jan 6, 2021
 * Copyright (C) 2021 Gregory Mazo
 * 
 */
package figureEditDialogs;

import org.junit.Test;

import channelLabels.ChannelLabelProperties;

/**
 
 * 
 */
public class TextLineDialogForChannelLabelTest {

	@Test
	public void test() {
		
		String chanLabel="eGFP";
		
		showCustomDialogForChannel(chanLabel, new ChannelLabelProperties());
		
		
	}
	
	public static void showCustomDialogForChannel(String chanLabel, ChannelLabelProperties prop) {
		
		
		
				//TextLineDialogForChannelLabel dialog = new TextLineDialogForChannelLabel(prop.getTextLineForChannel(new ChannelEntry()) );
				//dialog.setModal(true);
				//dialog.showDialog();
		
	}

}
