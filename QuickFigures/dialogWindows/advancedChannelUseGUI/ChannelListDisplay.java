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
 * Version: 2022.0
 */
package advancedChannelUseGUI;

import java.awt.Dimension;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import channelMerging.ChannelEntry;
import figureOrganizer.MultichannelDisplayLayer;
import figureOrganizer.PanelListElement;
import figureOrganizer.PanelManager;
import logging.IssueLog;
import menuUtil.SmartJMenu;
import undo.CombinedEdit;
import undo.EditListener;
import undo.UndoReorderArray;


/**A JList that displays a list of possible channels in their respective channel names and colors*/
public class ChannelListDisplay extends JList<Object> implements ActionListener, DropTargetListener, KeyListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Vector<ChannelEntry> elements=new Vector<ChannelEntry>();
	
	private ChannelColorCellRenerer2 render= new ChannelColorCellRenerer2(this);
	private PanelManager panelManager;
	private PanelListElement panel;
	private PanelListDisplay panelDisp;
	JMenu j=new SmartJMenu("Channels");
	
	/**constructor with starting panel and panel manager*/
	public ChannelListDisplay(PanelManager man, PanelListElement panel) {
		this.panelManager=man;
		
		
		setPanel(panel, man);
		this.setListData(elements);
		
		this.setDragEnabled(true);
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION );
		new DropTarget(this, this);
		this.addKeyListener(this);
		
	}
	
	public void setPanelListPartner(PanelListDisplay panelDisp) {this.panelDisp=panelDisp;}

	/**changes the panel whose channel's are displayed
	  replaces the channel list with a new one*/
	public void setPanel(PanelListElement panel, PanelManager man) {
		if(panel==null) return;
		this.panel=panel;
		this.panelManager=man;
		elements.clear();
		elements.addAll(panel.getChannelEntries());
		this.setListData(elements);
		
		getJMenuForChannels().removeAll();
		ArrayList<AvailableChannelsItem> makeentries = makeentries(panel);
		for(AvailableChannelsItem entiti: makeentries) {getJMenuForChannels().add(entiti);};
		this.repaint();
		
	}

	
	PanelListElement getPanel() {return panel;}
	


	
	
	
	public  ListCellRenderer<Object>	getCellRenderer() {return render;}
	



	@Override
	public void actionPerformed(ActionEvent arg0) {
	
		swapItems() ;
		if (panelDisp!=null)panelDisp.repaint();
	}
	
	/**switches the locations of two channels
	 * @return */
	CombinedEdit swapItems() {
		int[] ind = this.getSelectedIndices();
		if (ind.length>1) {
			ChannelEntry panel1 = elements.get(ind[0]);
			ChannelEntry panel2 = elements.get(ind[1]);
			return swapItems(panel1, panel2);
		}
		return null;
	}
	
	/**swaps the channel entry locations in the panel.
	  If the  multiple channels are shown in the merge label
	  a swap will be visible to the user 
	  (the swap is undone when the channel entries for that panel are updated)
	 * @return */
	CombinedEdit swapItems(ChannelEntry cl1, ChannelEntry cl2) {
		CombinedEdit undo = new CombinedEdit();
		int ind1 = elements.indexOf(cl1);
				int ind2 = elements.indexOf(cl2);
			undo.addEditToList(new UndoReorderArray<ChannelEntry>(panel.getChannelEntries()));
				elements.set(ind1, cl2);
				elements.set(ind2, cl1);
				panel.getChannelEntries().set(ind1, cl2);
				panel.getChannelEntries().set(ind2, cl1);
				updateDisplay();
				 
			undo.addEditListener(new EditListener() {
				@Override
				public void afterEdit() {
					updateDisplay();
					IssueLog.log("performing undo "+panel.getChannelEntries());
				}});
			
			if (this.panelDisp!=null) panelDisp.repaint();
			this.repaint();
			return undo;
	}
	
	
	void updateDisplay() {
		getCurrentPanelManager().updatePanels();
		
		if (panel!=null&&panel.getChannelLabelDisplay()!=null)panel.getChannelLabelDisplay().updateDisplay(); 
		
	}
	
	/**removes the channel entry from the panel and the list*/
	void removeItem(ChannelEntry o) {
			if(elements.size()<2) return;
			elements.remove(o);
			panel.removeChannelEntry(o);
			getCurrentPanelManager().updatePanels();
			getCurrentPanelManager().getImageDisplayLayer().onImageUpdated();
			this.repaint();
		
		
	}

	/**
	 * @return
	 */
	protected PanelManager getCurrentPanelManager() {
		return panelManager;
	}



	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
	}




	@Override
	public void dragExit(DropTargetEvent dte) {
	}




	@Override
	public void dragOver(DropTargetDragEvent dtde) {
	}



	/**In response to drag and drop, swaps two channels*/
	@Override
	public void drop(DropTargetDropEvent dtde) {
		if (dtde.getDropTargetContext().getComponent() ==this) {
			int index = getIndexForPoint(dtde.getLocation());
			
			if (index>-1) {
				ChannelEntry e1 = elements.get(index);
				ChannelEntry e2 = elements.get(this.getSelectedIndex());
				CombinedEdit undo = swapItems(e1, e2);
				PanelListDisplay.addToUndoManager(undo);
			}
			;
		}
		
	}




	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
		
	}
	
	/**based on a lick location within this component, returns the element clicked*/
	int getIndexForPoint(Point2D pt) {
		for(int i=0; i<elements.size(); i++) {
			if (getCellBounds(i,i).contains(pt)) return i;
		}
		
		return -1;
	}




	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode()==KeyEvent.VK_DELETE) {
			
		}
		if (arg0.getKeyCode()==KeyEvent.VK_BACK_SPACE) {
			
			removeSelectedChannels();
 		}
		
	}
	
	public void removeSelectedChannels() {
		
		List<Object> index1 = super.getSelectedValuesList();
		for(Object o: index1) {
			if (o instanceof ChannelEntry) {
				this.removeItem((ChannelEntry) o);
			}
		}
	}




	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}
	
	@Override
	public
	Dimension  	getPreferredSize() {
	return 	new Dimension(250,200) ;
	}
	
	/**returns the add/remove channels menu elements*/
	public ArrayList<AvailableChannelsItem> makeentries(PanelListElement e) {
		ArrayList<AvailableChannelsItem> output=new ArrayList<AvailableChannelsItem>();
		ArrayList<ChannelEntry> all = getCurrentPanelManager().getMultiChannelWrapper().getChannelEntriesInOrder();
		for(ChannelEntry entry:all) {
			output.add(new AvailableChannelsItem2(entry, e));
		}
		
		return output;
	}

	public JMenu getJMenuForChannels() {
		return j;
	}
	
	/**a version of the available channel menu item that can target many elements at once*/
	public class AvailableChannelsItem2 extends AvailableChannelsItem {

		/**
		 * @param ce
		 * @param e
		 */
		public AvailableChannelsItem2(ChannelEntry ce, PanelListElement e) {
			super(ce, e);
		}

		/**Adds/removes the channel from all selected panels*/
		public void onAction() { 
			
			super.onAction();
			setPanel(panel, panelManager);
			ArrayList<PanelListElement> panelsChanged =new ArrayList<PanelListElement>();
			List<PanelListElement> list = panelDisp.getSelectedValuesList();
			int cycle=1;
			for(PanelListElement panelp: list) {
				
				
				if (panelp==panel) continue;
				updateEntry(panelDisp.findPanelManager(panelp), entry);
				boolean include = !isExcludedChannel();
				
				this.setChannelIsIncluded(include, panelp);
				
				
				panelsChanged.add(panelp);
				cycle++;
			}
			
			for(PanelListElement panelp: panelsChanged) {
				panelDisp.findPanelManager(panelp).updatePanels();
			}
			panelManager.updatePanels();
			panelDisp.repaint();
			panelManager.updateDisplay();
		}
		/**
		 * @param findPanelManager
		 * @param entry
		 */
		private void updateEntry(PanelManager findPanelManager, ChannelEntry entry) {
			ArrayList<ChannelEntry> newEntries = findPanelManager.getImageDisplayLayer().getMultiChannelImage().getChannelEntriesInOrder();
			for(ChannelEntry en2: newEntries) {
				if(en2.getOriginalChannelIndex()==entry.getOriginalChannelIndex())
					this.entry=en2;
			}
		}
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;}

	
	
}
