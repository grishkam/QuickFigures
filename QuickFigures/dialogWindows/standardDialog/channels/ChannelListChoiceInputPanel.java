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
 * Date Modified: Dec 4, 2022
 * Date Created: Jan 10, 2021
 * Version: 2023.1
 */
package standardDialog.channels;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import channelMerging.ChannelEntry;
import channelMerging.ChannelUseInstructions;
import menuUtil.SmartPopupJMenu;
import multiChannelFigureUI.BasicChannelEntryMenuItem;
import standardDialog.InputPanel;
import standardDialog.OnGridLayout;
import standardDialog.choices.ChoiceInputEvent;
import standardDialog.choices.ChoiceInputListener;

/**An input panel for the user to select one or more channels*/
public class ChannelListChoiceInputPanel extends InputPanel implements OnGridLayout{

	

	protected JLabel label=new JLabel();
	protected ChanListBox box=new ChanListBox();
	String noneSelectedLabel="none";
	String use_none_option = "dont exclude any";
	
	boolean omitLabel=false;
	
	public ChanListBox getBox() {return box;}
	ArrayList<ChoiceInputListener> listeners=new ArrayList<ChoiceInputListener>();
	
	/**Indicates the maziumum number that can be selected*/
	int maxChannelSelectable=100;
	
	private ArrayList<Integer> originalValues=new ArrayList<Integer>();
	
	/**stores the indices that are currently selected*/
	private ArrayList<Integer> currentValues=new ArrayList<Integer>();
	
	/**A list of all available values*/
	public ArrayList<Integer> availableValues=new ArrayList<Integer>();
	
	private ArrayList<ChannelEntryMenuItem> items;
	private boolean invert;
	private boolean useStrike;
	
	public int boxWidthLimit=200;
	private int boxHeightMin=28;
	
	
	public ChannelListChoiceInputPanel(String labeln, ArrayList<ChannelEntry> availableChannels, ArrayList<Integer> start) {
		this(labeln, availableChannels, start, "none selected");
	}
	
	public ChannelListChoiceInputPanel(String labeln, ArrayList<ChannelEntry> availableChannels, ArrayList<Integer> start, String alltext) {
		label.setText(labeln);
		this.noneSelectedLabel=alltext;
		
		setupChannelOptions(availableChannels, start);
	}

	/**
	 * @param availableChannels
	 * @param start
	 */
	public void setupChannelOptions(ArrayList<ChannelEntry> availableChannels, ArrayList<Integer> start) {
		currentValues=new ArrayList<Integer>();
		
		this.setValues(start);
		originalValues=this.getCurrentValues();
		
		items = new ArrayList<ChannelEntryMenuItem>();
		
		items.add(new ChannelEntryMenuItem(use_none_option));
		for(ChannelEntry entry: availableChannels) {
			items.add(createMenuItemFor(entry));
			this.availableValues.add(entry.getOriginalChannelIndex());
		}
		
		/**called so a separate array list stores the current and the originala*/
		this.setValues(start);
		box.resetLabels();
	}
	
	/**constructor for a single value version of the combo box*/
	public ChannelListChoiceInputPanel(String labeln, ArrayList<ChannelEntry> availableChannels, Integer start, String noneText, boolean invert) {
		this.invert=invert;
		this.useStrike=!invert;
		label.setText(labeln);
		
		this.noneSelectedLabel=noneText;
		use_none_option=noneText;
		this.maxChannelSelectable=1;//so that only one value is ever selected
		
		setupForChannelList(availableChannels, start);
	}

	/**
	 * @param availableChannels
	 * @param start
	 */
	public void setupForChannelList(ArrayList<ChannelEntry> availableChannels, Integer start) {
		
		if(start!=null)
			this.setValues(start); 
		originalValues=this.getCurrentValues();
		
		items = new ArrayList<ChannelEntryMenuItem>();
		items.add(new ChannelEntryMenuItem("none"));
		for(ChannelEntry entry: availableChannels) {
			items.add(createMenuItemFor(entry));
			this.availableValues.add(entry.getOriginalChannelIndex());
		}
		
		/**called so a separate array list stores the current and the originala*/
		if(start!=null)
			this.setValues(start);
		box.resetLabels();
		
	}

	/**
	 * @param entry
	 * @return
	 */
	public ChannelEntryMenuItem createMenuItemFor(ChannelEntry entry) {
		ChannelEntryMenuItem channelEntryMenuItem = new ChannelEntryMenuItem(entry);
		
			channelEntryMenuItem.invert=invert;
			channelEntryMenuItem.useStrike=this.useStrike;
		return channelEntryMenuItem;
	}
	

	/**sets the font*/
	public void setItemFont(Font f) {
		box.setFont(f);
		label.setFont(f);
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void placeItems(Container jp, int x0, int y0) {
		GridBagConstraints gc = new GridBagConstraints();
		
		if (!omitLabel)	{
			gc.anchor = GridBagConstraints.EAST;
			gc.insets=firstInsets;
			gc.gridx=x0;
			gc.gridy=y0;
			jp.add(label, gc);
			gc.gridx++;
			}
		
		gc.anchor = GridBagConstraints.WEST;
		
		gc.insets=lastInsets;
		jp.add(box, gc);
		
		
	}

	

	@Override
	public int gridHeight() {
		return 1;
	}

	@Override
	public int gridWidth() {
	
		return 2;
	}

	
	/**returns the selected index*/
	public ArrayList<Integer> getSelectedIndices() {
		return  getCurrentValues();
	}

	/**returns the choice to its original values */
	public void revert() {
		setValues(originalValues);
		
	}


	
	/**
 changes the currently set values. 
 */
public void setValues(ArrayList<Integer> values2) {
	currentValues=new ArrayList<Integer>();
	for(Integer v: values2) {
		getCurrentValues().add(v);
		
	}
	
}

/**
* @param values2
*/
public void setValues(int v) {
	currentValues=new ArrayList<Integer>();

	getCurrentValues().add(v);
	

}

	public void notifyChoiceListeners(int i) {
		box.repaint();
		notifyListeners(new ChoiceInputEvent(this, box, i, getCurrentValues()));
		
	}


	public void notifyListeners(ChoiceInputEvent ni) {
		
		for(ChoiceInputListener l :listeners) {
			if(l==null) continue;
			l.valueChanged(ni);
		}
	}
	
	public void addChoiceInputListener(ChoiceInputListener ni) {
		listeners.add(ni);
	}
	public void removeChoiceInputListener(ChoiceInputListener ni) {
		listeners.remove(ni);
	}
	public ArrayList<ChoiceInputListener> getChoiceInputListeners() {
		return listeners;
	}

	
	public int howManyMayBeSelected() {return 10;}
	

	public ArrayList<Integer> getCurrentValues() {
		return currentValues;
	}


	/**
	The component functions similarly to a combo box that works for multiple selections
	that shows which ones are currently selected
	 */
public class ChanListBox extends JPanel implements MouseListener {

	/**A downward triangle*/
		private static final char DOWN_CHAR = '\u25bc';
	ArrayList<JLabel> labels=new ArrayList<JLabel> ();
	int maxLabel=15;

	
	
	public ChanListBox() {
		this.addMouseListener(this);
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		for(int i=0; i<maxLabel; i++) {
			JLabel l = new JLabel("");
			labels.add(l);
			this.add(l);
		}
		resetLabels();
		
	}

	/**
	 *
	 */
	private void resetLabels() {
		if(getCurrentValues()!=null)
			this.resetLabels(getCurrentValues().size()==0);
	}
	
	@Override
	public Dimension getPreferredSize() {
		if (boxWidthLimit==0)
			return super.getPreferredSize();
		
		int height2 = super.getPreferredSize().height;
		if(height2<boxHeightMin) height2=boxHeightMin;
		return new Dimension(boxWidthLimit, height2);
	}
	
	/**sets the labels to their innitial values*/
	void resetLabels(boolean noneLab) {
		for(int i=0; i<maxLabel; i++) {
			JLabel label1 = labels.get(i);
			String text="";
			if(noneLab&&i==0)
				text=noneSelectedLabel;
			else if (noneLab) 
				text=" ";
			
			label1.setText(text);
			label1.setForeground(Color.black);
			
		}
	}
	/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	/**
	 * returns the selected channel's channel index (if there is at least one).
	 * If there is no selected channel, returns 0.
	 * @return
	 */
	public int getSelectedIndex() {
		if (getCurrentValues().size()>0)
			return getCurrentValues().get(0);
		return 0;
	}
	
	
	
	@Override
	public void paintComponent(Graphics g) {
		
		resetLabels();
		if(getCurrentValues().size()>0) {
			
			ArrayList<ChannelEntryMenuItem> eachExcluded=new ArrayList<ChannelEntryMenuItem>();
			
			/**makes labels for this component match the channels selected*/
			for(int i=0; i<items.size(); i++) {
				ChannelEntryMenuItem item = items.get(i);
				
				if(item.isExcludedChannel())
						{	
					eachExcluded.add(item);
								
							
							
						}
				
				
			}
			
			/**makes labels for this component match the channels selected*/
			for(int i=0; i< maxLabel; i++) {
				ChannelEntryMenuItem item =null;
				if(i<eachExcluded.size()) 
					item= eachExcluded.get(i);
				JLabel label1 = labels.get(i);
				if(item!=null&&item.isExcludedChannel())
						{	
					
								String text =" "+ item.getText()+" ";
								label1.setText(text);
							
								{label1.setForeground(item.getDisplayColor().darker());}
							
							
						}
				
				
			}
			
			/**in the event that some of the values exceed the number of channels in the list, this makes some of the labels
			 * equal to the numbers rather than the channel names*/
			for(int i=0; i<getCurrentValues().size(); i++) {
				Integer i2 = getCurrentValues().get(i);
				JLabel label1 = labels.get(i+10);
				if(i2>=items.size())  {
					label1.setText("# "+i2);
					}
			}
			
			
			
		} else this.setForeground(Color.black);
		
		super.paintComponent(g);
		if (g instanceof Graphics2D) {
			((Graphics2D) g).setStroke(new BasicStroke());
		}
		g.setColor(Color.black);
		g.drawRect(0, 0, this.getWidth()-1, this.getHeight()-1);
		
		g.drawString(DOWN_CHAR+" ", this.getWidth()-16, this.getHeight()/2+5);
		
	}



	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void mousePressed(MouseEvent e) {
		
			SmartPopupJMenu pp = new SmartPopupJMenu();
			
			for(ChannelEntryMenuItem i: items) {
				i.updateFont();
				pp.add(i);
			}
			
			pp.show(this, 0, this.getHeight());
		
		
	}



	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	

}

/**Menu items for each channel entry*/
class ChannelEntryMenuItem extends BasicChannelEntryMenuItem  {

	/**
	 * @param ce
	 */
	public ChannelEntryMenuItem(ChannelEntry ce) {
		super(ce);
		//this.addActionListener(this);
	}
	
	public ChannelEntryMenuItem(String st) {
		super(null);
		this.setText(st);
		this.updateFont();
		//this.addActionListener(this);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected boolean isExcludedChannel() {
		if(super.entry==null) return false;
		return getCurrentValues().contains(entry.getOriginalChannelIndex());
	}
	
	/**The color of the text of the menu item*/
	public Color getTextColor() {
		return getDisplayColor().darker();
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(entry==null) {
			getCurrentValues().clear();
		} else 
		if (this.isExcludedChannel()) {
			ArrayList<Integer> newList = new ArrayList<Integer>();
			for(Integer c: getCurrentValues()) {
				if (c==entry.getOriginalChannelIndex()) continue;
				newList.add(c);
			}
			currentValues=newList;
			
		}
		else {
			if(maxChannelSelectable==1) getCurrentValues().clear();
			getCurrentValues().add(entry.getOriginalChannelIndex());
			
		}
		if (entry!=null)
		 notifyChoiceListeners(entry.getOriginalChannelIndex());
		else notifyChoiceListeners(ChannelUseInstructions.NONE_SELECTED);
		
		
	}
	
} 
	
}
