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
 * Date Modified: Jan 11, 2021
 * Version: 2021.1
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
import java.awt.event.ActionListener;
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
	
	
	public ChannelListChoiceInputPanel(String labeln, ArrayList<ChannelEntry> availableChannels, ArrayList<Integer> start, String alltext) {
		
		label.setText(labeln);
		this.setValues(start);
		originalValues=this.currentValues;
		this.noneSelectedLabel=alltext;
		items = new ArrayList<ChannelEntryMenuItem>();
		items.add(new ChannelEntryMenuItem("dont exclude any"));
		for(ChannelEntry entry: availableChannels) {
			items.add(new ChannelEntryMenuItem(entry));
			this.availableValues.add(entry.getOriginalChannelIndex());
		}
		
		/**called so a separate array list stores the current and the originala*/
		this.setValues(start);
		box.resetLabels();
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
		return  currentValues;
	}

	/**returns the choice to its original values */
	public void revert() {
		setValues(originalValues);
		
	}


	
	/**
 * @param originalValues2
 */
private void setValues(ArrayList<Integer> originalValues2) {
	currentValues=new ArrayList<Integer>();
	for(Integer v: originalValues2) {
		currentValues.add(v);
	}
	
}

	public void notifyChoiceListeners(int i) {
		box.repaint();
		notifyListeners(new ChoiceInputEvent(this, box, i, currentValues));
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
	

	/**
	The component functions similarly to a combo box that works for multiple selections
	that shows which ones are currently selected
	 */
public class ChanListBox extends JPanel implements MouseListener {

	/**A downward triangle*/
		private static final char DOWN_CHAR = '\u25bc';
	ArrayList<JLabel> labels=new ArrayList<JLabel> ();
	int maxLabel=15;
	private int widthLimit=250;
	private int heightMin=28;
	
	
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
		if(currentValues!=null)
			this.resetLabels(currentValues.size()==0);
	}
	
	@Override
	public Dimension getPreferredSize() {
		if (widthLimit==0)
			return super.getPreferredSize();
		
		int height2 = super.getPreferredSize().height;
		if(height2<heightMin) height2=heightMin;
		return new Dimension(widthLimit, height2);
	}
	
	/**sets the labels to their innitial values*/
	void resetLabels(boolean noneLab) {
		for(int i=0; i<maxLabel; i++) {
			JLabel label1 = labels.get(i);
			String text="";
			if(noneLab&&i==0) text=noneSelectedLabel;
			else if (noneLab) text=" ";
			
			label1.setText(text);
			label1.setForeground(Color.black);
			
		}
	}
	/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	/**
	 * @return
	 */
	public int getSelectedIndex() {
		if (currentValues.size()>0)
			return currentValues.get(0);
		return 0;
	}
	
	
	
	@Override
	public void paintComponent(Graphics g) {
		
		resetLabels();
		if(currentValues.size()>0) {
			
			/**makes labels for this component match the channels selected*/
			for(int i=0; i< maxLabel&&i<items.size(); i++) {
				ChannelEntryMenuItem item = items.get(i);
				JLabel label1 = labels.get(i);
				if(item.isExcludedChannel())
						{	
								String text =" "+ item.getText()+" ";
								label1.setText(text);
							
								{label1.setForeground(item.getDisplayColor().darker());}
							
							
						}
				
				
			}
			
			/**in the event that some of the values exceed the number of channels in the list, this makes some of the labels
			 * equal to the numbers rather than the channel names*/
			for(int i=0; i<currentValues.size(); i++) {
				Integer i2 = currentValues.get(i);
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
class ChannelEntryMenuItem extends BasicChannelEntryMenuItem implements ActionListener {

	/**
	 * @param ce
	 */
	public ChannelEntryMenuItem(ChannelEntry ce) {
		super(ce);
		this.addActionListener(this);
	}
	
	public ChannelEntryMenuItem(String st) {
		super(null);
		this.setText(st);
		this.updateFont();
		this.addActionListener(this);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected boolean isExcludedChannel() {
		if(super.entry==null) return false;
		return currentValues.contains(entry.getOriginalChannelIndex());
	}
	
	/**The color of the text of the menu item*/
	public Color getTextColor() {
		return getDisplayColor().darker();
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if(entry==null) {
			currentValues.clear();
		} else 
		if (this.isExcludedChannel()) {
			ArrayList<Integer> newList = new ArrayList<Integer>();
			for(Integer c: currentValues) {
				if (c==entry.getOriginalChannelIndex()) continue;
				newList.add(c);
			}
			currentValues=newList;
		}
		else {
			if(maxChannelSelectable==1) currentValues.clear();
			currentValues.add(entry.getOriginalChannelIndex());
		}
		if (entry!=null)
		 notifyChoiceListeners(entry.getOriginalChannelIndex());
		else notifyChoiceListeners(ChannelUseInstructions.NONE_SELECTED);
	}
	
} 
	
}
