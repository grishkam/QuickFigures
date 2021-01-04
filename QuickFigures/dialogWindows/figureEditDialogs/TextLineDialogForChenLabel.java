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
package figureEditDialogs;

import java.awt.GridBagConstraints;
import java.util.ArrayList;

import channelMerging.ChannelEntry;
import objectDialogs.GraphicItemOptionsDialog;
import objectDialogs.LinePane;
import standardDialog.StandardDialog;
import standardDialog.StandardDialogListener;
import textObjectProperties.TextLine;
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
		c.gridy=gridPositionY;
		c.gridheight=4;
		c.gridwidth=6;
		this.getMainPanel().add(tabsfull, c);
		gridPositionY=5;
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
	
	public static StandardDialog showMultiTabDialogDialogss(ArrayList<ChannelEntry> chans, ChannelLabelProperties prop, StandardDialogListener sdl) {
		StandardDialog jf = createMultiLineDialog(chans, prop, sdl);
		return jf;
		
	}

	protected static StandardDialog createMultiLineDialog(ArrayList<ChannelEntry> chans, ChannelLabelProperties prop,
			StandardDialogListener sdl) {
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
