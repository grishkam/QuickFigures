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
package multiChannelFigureUI;

import java.util.ArrayList;

import channelMerging.MultiChannelImage;
import logging.IssueLog;
import standardDialog.DialogItemChangeEvent;
import standardDialog.StandardDialog;
import standardDialog.StringInputPanel;
import standardDialog.StandardDialogListener;

/**shows a dialog that lets the user name the stack slices channels of a multichannel images*/
public class StackSliceNamingDialog {
	ArrayList<Integer> indeces;
	MultiChannelImage imp2;
	
	public void showNamingDialog(ArrayList<Integer> indeces, MultiChannelImage imp2) {
		StandardDialog gd = new StandardDialog("Name Stack Slice");	
		gd.setWindowCentered(true);
	//	gd.setModal(true);
		for (int in2: indeces) {addSliceNameFieldtoDialog(gd, in2, imp2); }
		this.imp2=imp2;
		this.indeces=indeces;
		gd.addDialogListener(new NamerListenr());
		gd.showDialog();

	}
	
	public void showNamingDialog(int indeces, MultiChannelImage imp2) {
		this.imp2=imp2;
		ArrayList<Integer> i = new ArrayList<Integer> () ; 
		i.add(indeces);
		showNamingDialog(i, imp2);
	}
	
	
	public class NamerListenr implements /**DialogListener, */StandardDialogListener{
/**
		@Override
		public boolean dialogItemChanged(GenericDialog gd, AWTEvent arg1) {
			if (imp2==null||indeces==null)return false;
			
			ImageStack st= imp2.getStack();
			 /**this part of the method tended to have imageJ crash once in a while during it. under a hypothesis that it had something to 
			  * do with another thread accessing the stack at the same time, I added the synchronized statement. as the crash only happens once in 
			  * a while, I cannot be sure if my edit helped yet.*/
			/** 
			 for (int in2: indeces) try{st.setSliceLabel(gd.getNextString(), in2);} catch (Throwable t){IssueLog.log(t); return false;}
		
			try{ imp2.updateAndDraw();} catch (Throwable e) {IssueLog.log(e);}
			 return true;
		}
*/
		@Override
		public void itemChange(DialogItemChangeEvent event) {
			if (imp2==null||indeces==null)return ;
			
			 /**this part of the method tended to have imageJ crash once in a while during it. under a hypothesis that it had something to 
			  * do with another thread accessing the stack at the same time, I added the synchronized statement. This did not permanently fix the issue
			  * so I redid the tool to use my own dialog instead of imageJ's generic dialog. */
			
			
			 for (int in2: indeces) try{imp2.setSliceName( event.getSource().getString(""+in2), in2);} catch (Throwable t){IssueLog.logT(t); return ;}
		
			try{ imp2.updateDisplay();} catch (Throwable e) {IssueLog.logT(e);}
			 return;
		}
		
	}
	
	
	
	/**This method adds a Field containing the name of a stack slice to the generic dialog gd.*/
	public void addSliceNameFieldtoDialog(StandardDialog gd, int i, MultiChannelImage theImage) {
		imp2=theImage;
		int[] position=theImage.convertIndexToPosition(i);
		addSliceNameFieldtoDialog(gd,  position[0],position[1], position[2], theImage, i);
	}
	
	/**This method adds a Field containing the name of a stack slice to the generic dialog gd.*/
	public void addSliceNameFieldtoDialog(StandardDialog gd, int channel, int slice, int frame, MultiChannelImage theImage, int i) {		
		imp2=theImage;
		String text="";
		boolean neeof=false;
		if (theImage.nChannels()>1) {text+=" Channel "+channel; neeof=true;}
		if (theImage.nSlices()>1) {text+=neeof? " of":"";text+=" Slice "+slice; neeof=true;}
		if (theImage.nFrames()>1) {text+=neeof? " of":"";text+=" Frame "+frame; neeof=true;}
		StringInputPanel sip = new StringInputPanel(text+ " Name", imp2.getSliceName(theImage.getStackIndex(channel, slice, frame)), 25 );
		gd.add(""+i, sip) ;				
	}
	

}
