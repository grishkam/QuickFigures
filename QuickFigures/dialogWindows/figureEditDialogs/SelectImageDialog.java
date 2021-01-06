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
/**
 * Author: Greg Mazo
 * Date Modified: Jan 6, 2021
 * Version: 2021.1
 */
package figureEditDialogs;

import java.util.ArrayList;

import appContext.CurrentAppContext;
import channelMerging.MultiChannelImage;
import logging.IssueLog;
import standardDialog.StandardDialog;
import standardDialog.choices.ChoiceInputPanel;
import standardDialog.strings.InfoDisplayPanel;

/**A dialog for selecting one or more open multidimensional images*/
public class SelectImageDialog extends StandardDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	/**The choices*/
	private ArrayList<MultiChannelImage> optioins;



	private boolean includeChans=true;

	private int num=0;
	{this.setModal(true);}
	
	/**builds a dialog*/
	public SelectImageDialog( ArrayList<MultiChannelImage> ops, boolean includeChans, int numb) {
		this.setWindowCentered(true);
		this.num=numb;
		this.includeChans=includeChans;
		if (ops.size()==0) {
			IssueLog.log("not options");
			this.add("Info", new InfoDisplayPanel("Problem:","no image is open"));
			return;
		}
		String[] s=new 	String[ops.size()+1];
		this.optioins=ops;
		for(int i=0; i<ops.size(); i++) {
			//IssueLog.log("got title "+optioins.get(i).getTitle());
			s[i+1]=(this.optioins.get(i).getTitle());
			
		}
		s[0]="none";
		for(int i=1; i<=num; i++) {
			int innitial=i;
			if(i>2) innitial=0;
			this.add(""+i, new ChoiceInputPanel("Image "+i, s, innitial));
			}
		
		if (this.includeChans)
			addChannelCheckBoxes(optioins.get(0));
	}
	
	
	
	
	public static void main(String[] arg) {
		new SelectImageDialog(new ArrayList<MultiChannelImage> (), true,4).showDialog();;
	}
	
	
	/**returns a list of the multichannel images that were selected*/
	public ArrayList<MultiChannelImage> getList() {
		ArrayList<MultiChannelImage> listoutput=new ArrayList<MultiChannelImage> ();
		
		for(int  i=1; i<=num; i++) try {
			 int index = this.getChoiceIndex(""+i);
			 
			 if (index>0) {
				 MultiChannelImage item = this.optioins.get(index-1);
				 if (!listoutput.contains(item)) 
					 listoutput.add(item);
				 }
			
			
		} catch (Throwable t) {
			t.printStackTrace();
			IssueLog.logT(t);
		}
		IssueLog.log("got list of "+listoutput.size()+" out of a possible " +num);
		return listoutput;
		
	}
	
	/**returns a list of the multichannel images that are open in the application*/
	protected static	ArrayList<MultiChannelImage>  getAvailableMultis() {
		return  CurrentAppContext.getMultichannelContext().getallVisibleMultichanal();
	}
	
	/**shows a dialog for selecting images and returns it after the user has selected one
	  or returns immediately*/
	public static SelectImageDialog getSelectedMultis(boolean includeChans, int num) {
		ArrayList<MultiChannelImage> multis =getAvailableMultis();
		
		SelectImageDialog sd = new SelectImageDialog(multis, includeChans,num);
		if (multis.size()==0) return sd;//does not waste time showing the dialog if there are not options
		sd.showDialog();;
		return sd;
	}
}
