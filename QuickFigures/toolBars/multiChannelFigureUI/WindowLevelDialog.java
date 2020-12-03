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

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;

import channelMerging.ChannelEntry;
import channelMerging.MultiChannelImage;
import graphicActionToolbar.CurrentFigureSet;
import standardDialog.GriddedPanel;
import standardDialog.NumberInputPanel;
import standardDialog.StandardDialog;
import undo.AbstractUndoableEdit2;
import standardDialog.ShowDisplayRange;

/**A more simplistic version of the window/level adjuster
   */
public class WindowLevelDialog extends StandardDialog  {

	

	public static final String MIN_TYPE = "Min", MAX_TYPE = "Max";
	/**
	 * 
	 */

	
	private static final long serialVersionUID = 1L;
	double min=0;
	double max=0;
		int chan=1;
		
		public static final int MIN_MAX=0, WINDOW_LEVEL=1, ALL=2;
		int winLevDialogType=MIN_MAX;
		
		
		int slidermax=(int)Math.pow(2, 12);
		int slidermin=0;
		JButton resetRange=new JButton("Reset") ;{
			resetRange.addActionListener(new ActionListener(){
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
				resetRange();
					
				}});
			super.additionButtons.add(resetRange);
		}
		
		
		ShowDisplayRange sdr = new ShowDisplayRange();
		private DisplayRangeChangeListener displayChangeLis;
		private double minstart;
		private double maxstart;
		private NumberInputPanel levelPanel;
		private NumberInputPanel windowPanel;
		private NumberInputPanel minPanel;
		private NumberInputPanel maxPanel;
		{
			sdr.setPrefferedSize(250,100);
		}
		
	
		public static void showWLDialogs(ArrayList<ChannelEntry> chans, MultiChannelImage mrp, DisplayRangeChangeListener listen, int winLev, AbstractUndoableEdit2 undo) {
			
			StandardDialog jf = new StandardDialog();
			new CurrentFigureSet();
			jf.currentUndoManager= CurrentFigureSet.getCurrentActiveDisplayGroup().getUndoManager();
			jf.undo=undo;
			
			
			jf.getOptionDisplayTabs().remove(jf.getMainPanel());
			for(ChannelEntry chan:chans) {
				WindowLevelDialog dis = new WindowLevelDialog(chan.getOriginalChannelIndex(), mrp, listen, winLev);
				GriddedPanel p=dis.getMainPanel();
				dis.remove(dis);
				jf.getOptionDisplayTabs().addTab(chan.getShortLabel(), p);
				dis.undo=undo;
				//dis.showDialog();
			}
			GridBagConstraints gc=new GridBagConstraints();
			gc.anchor=GridBagConstraints.EAST;
			gc.gridy=3;
			gc.gridx=1;
			gc.gridwidth=2;
			jf.add(jf.alternateCloseButton(), gc);
			jf.makeVisible();
			
			
			
		}



	public WindowLevelDialog(int chan, MultiChannelImage mrp, DisplayRangeChangeListener listen,int winLev) {
		
		this.winLevDialogType=winLev;
		
		this.displayChangeLis=listen;
		this.chan=chan;
		setMinMaxDisplay(mrp.getChannelMin(chan), mrp.getChannelMax(chan));
		setStartingminMax(mrp.getChannelMin(chan), mrp.getChannelMax(chan));
		
		int[] basis=mrp.getPixelWrapperForSlice(chan, 1, 1).getDistribution();
		
		slidermin=(int)ShowDisplayRange.findMinOfDistributionHistogram(basis);
		
		/**attempts to find the point in the histogram that the top of the distribution starts*/
		boolean maxSet=false;
		slidermax=basis.length-1;
		for(int i=basis.length-1; i>0; i--) {
			if (basis[i]==0) {
				slidermax=i;
			} else { 
				maxSet=true;
				break;
			}
			
		}
		if (!maxSet) {
			slidermax=(int)Math.pow(2, mrp.bitDepth());
		}
		
		
		sdr.setRange(basis);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth=3;
		gbc.gridx=super.gx;
		gbc.gridy=super.gy;
		
		this.getMainPanel().add(sdr,gbc);
		super.gy++;
		sdr.setLineColor(mrp.getChannelColor(chan));
		//setModal(true);
		
		if (this.winLevDialogType==WINDOW_LEVEL) {
			createWindowLevelPanels();
		
			//this.add( aip2);
		}
		else if (this.winLevDialogType==MIN_MAX) {
		
			 createMinMaxPanels();
		}
		else if (this.winLevDialogType==ALL) {
			 createMinMaxPanels();
			 createWindowLevelPanels();
		}
		 gbc.gridy+=5;
		 
		this.getMainPanel().add(super.generateButtonPanel(), gbc);
		
		
	}






	protected void createMinMaxPanels() {
		minPanel = new NumberInputPanel("Min ", min, true, true, slidermin, slidermax);
		this.add(MIN_TYPE, minPanel);
		maxPanel = new NumberInputPanel("Max ", max, true, true, slidermin, slidermax);
		this.add(MAX_TYPE, maxPanel);
		this.add( maxPanel);
	}


	protected void createWindowLevelPanels() {
		levelPanel = new NumberInputPanel("Level ", (min+max)/2, true, true, slidermin, slidermax);
		this.add("Level", levelPanel);
		
		 windowPanel = new NumberInputPanel("Window ", max-min, true, true, 10, (slidermax-slidermin)*2);
		this.add("Window", windowPanel);
	}
	
	
	@Override
	public void onOK() {
		
	
	}
	
	/**restores the starting display range*/
	@Override
	public void onCancel() {
		setMinMaxDisplay(minstart, maxstart);
		displayChangeLis.minMaxSet(chan, min, max);
	//
	}
	
	void setMinMaxDisplay(double min, double max) {
		this.min=min;
		this.max=max;
		sdr.setMinMax(min, max);
	}
	
	void setStartingminMax(double min, double max) {
		minstart=min;
		maxstart=max;
	}
	
	
	

	/**Applies the Min/Max range*/
	void resetRange() {
		imposeRange(slidermin, slidermax);
		
	}
	
	/**sets the display range for this dialog.*/
	public void imposeRange(int slidermin, int slidermax) {
		/**sets the internal min and max*/
		setMinMaxDisplay(slidermin, slidermax);
		
		sdr.repaint();
		displayChangeLis.minMaxSet(chan, min, max);
		
		if (winLevDialogType==WINDOW_LEVEL) {
			updateWindowLevelPanels();
		} else if (winLevDialogType==MIN_MAX) {
			updateMinMaxPanels();
		}
		else if (winLevDialogType==ALL) {
			updateWindowLevelPanels();
			updateMinMaxPanels();
		}
	}





/**updates the number input panels according to current display range*/
	protected void updateMinMaxPanels() {
		minPanel.setNumber(min);
		maxPanel.setNumber(max);
	}
	protected void updateWindowLevelPanels() {
		levelPanel.setNumber((min+max)/2);
		windowPanel.setNumber(max-min);
	}





	
	/**sets the min and max after each change to the GUI items*/
	public void notifyAllListeners(JPanel key, String string) {
		super.notifyAllListeners(key, string);
		int type=winLevDialogType;
		if(winLevDialogType==ALL){
			if(MIN_TYPE.equals(string)||MAX_TYPE.equals(string)) type=MIN_MAX; else type=WINDOW_LEVEL;
				}
		
		afterUserSetDisplayRange(type);
	}



	protected void afterUserSetDisplayRange(int type) {
		double min=0;
		double max=0;
		if (type==WINDOW_LEVEL) {
			double window=getNumber("Window");
			double level=getNumber("Level");
			min=level-window/2;
			max=level+window/2;
			if(this.winLevDialogType==ALL) {this.updateMinMaxPanels();}
			
		}  else if (type==MIN_MAX) {
				min=super.getNumber(MIN_TYPE);
				max=super.getNumber(MAX_TYPE);
				if(this.winLevDialogType==ALL) {updateWindowLevelPanels();}
		}
		
		setMinMaxDisplay(min, max);
		
		sdr.repaint();
		displayChangeLis.minMaxSet(chan, min, max);
	}
	
	 public void showDialog() {
		 
		  makeVisible();
		  
	  }
	 

	
}