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
 * Date Modified: April 23, 2022
 * Version: 2023.2
 */
package advancedChannelUseGUI;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import channelLabels.ChannelLabelManager;
import channelMerging.ImageDisplayLayer;
import channelMerging.MultiChannelImage;
import figureEditDialogs.ChannelSliceAndFrameSelectionDialog;
import figureOrganizer.FigureOrganizingLayerPane;
import figureOrganizer.MultichannelDisplayLayer;
import figureOrganizer.PanelListElement;
import figureOrganizer.PanelManager;
import graphicActionToolbar.CurrentFigureSet;
import graphicalObjects_LayerTypes.GraphicLayer;
import iconGraphicalObjects.ChannelUseIcon;
import logging.IssueLog;
import menuUtil.SmartPopupJMenu;
import undo.CombinedEdit;
import undo.PanelManagerUndo;


/**A gui that show a list of panels and their components channels
  Panels and channel can be edited with the buttons in the GUI*/
public class AdvancedChannelUseGUI extends JFrame implements ListSelectionListener, ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private PanelManager pm;
	private ChannelLabelManager cm;
	
	
	
	private PanelListDisplay listPanels;
	private ChannelListDisplay listChannels;
	
	JLabel panelListLabel=new JLabel("Panel List");
	JLabel chanListLabel=new JLabel("Channel List");
	
	/**buttons to add a panel*/
	JButton addPanelButton=new JButton("+"); 
	JButton removePanelButton=new JButton("-"); 

	/***/
	JButton chooseChannelButton=new JButton("Add/Remove Channels");{chooseChannelButton.setIcon(new ChannelUseIcon());}
	JButton alterZButton=new JButton("Z"); 
	JButton alterTButton=new JButton("T"); 
	JCheckBox invertChannelCheckBox=createInvertCheckBox();
	
	ArrayList<GraphicLayer> searchLayers=new ArrayList<GraphicLayer>();

	public AdvancedChannelUseGUI(FigureOrganizingLayerPane layer0) {
		this(layer0.getPrincipalMultiChannel().getPanelManager(), layer0.getPrincipalMultiChannel().getChannelLabelManager());
		searchLayers.add(layer0);
		listPanels.setSearchLayer(layer0);
		listPanels.setListToSearchLayer();
	}
	
	
	public AdvancedChannelUseGUI(MultichannelDisplayLayer layer) {
		this(layer.getPanelManager(), layer.getChannelLabelManager());
		searchLayers.add(layer);
		listPanels.setSearchLayer(layer);
		
	}

	/**Creates a gui and switches the panel manager to advanced channel use mode*/
	public AdvancedChannelUseGUI(PanelManager pm, ChannelLabelManager cm) {
			this.cm=cm;
			pm.setChannelUseMode(PanelManager.ADVANCED_CHANNEL_USE);
			pm.getPanelList().setChannelUpdateMode(true);
			GridBagLayout gb = new GridBagLayout();
			GridBagConstraints gc=new GridBagConstraints();
			setLayout(gb);
			gc.gridx=1;
			gc.gridy=1;
			
			boolean multipleChannel=pm.getMultiChannelWrapper().nChannels()>1;
			
			this.pm=pm;
			listPanels=new PanelListDisplay(pm);
			listChannels=new ChannelListDisplay(pm, pm.getPanelList().getMergePanel());
			getJListForChannels().setPanelListPartner(getPanelJList());
			this.setTitle("Panels for: "+pm.getMultiChannelWrapper().getTitle());
			getPanelJList().addListSelectionListener(this);
			
			
			gc.gridwidth=4;
			this.add(createScrollPane(getPanelJList(), 300), gc);
			getPanelJList().addMouseListener(createPanelEditListener());
			this.addWindowListener(getPanelJList());
			this.addMouseListener(getPanelJList());
			gc.gridx=6;
			gc.gridy=1;
			if ( multipleChannel)this.add(createScrollPane(getJListForChannels(), 250), gc);
			gc.gridwidth=1;
			
		
			gc.gridx=8;
			gc.gridy=2;
			gc.anchor=GridBagConstraints.WEST;
			if ( multipleChannel)this.add(chooseChannelButton, gc);
			
			gc.gridx=8;
			gc.gridy=7;
			gc.anchor=GridBagConstraints.WEST;
			this.add(this.invertChannelCheckBox, gc);
			
			gc.gridwidth=1;
			
			gc.gridx=8;
			gc.gridy=0;
			if ( multipleChannel)add(chanListLabel, gc);
			
			gc.gridx=1;
			gc.gridy=2;
			gc.anchor=GridBagConstraints.WEST;
			this.add(addPanelButton, gc);
			gc.gridx=2;
			gc.gridy=2;
			gc.anchor=GridBagConstraints.WEST;
			this.add(removePanelButton, gc);
			gc.gridx=3;
			gc.gridy=2;
			gc.anchor=GridBagConstraints.WEST;
			this.add( alterZButton, gc);
			gc.gridx=4;
			gc.gridy=2;
			gc.anchor=GridBagConstraints.WEST;
			this.add( alterTButton, gc);
			
			gc.gridx=5;
			gc.gridy=2;
			gc.anchor=GridBagConstraints.WEST;
			
			
			
			addPanelButton.addActionListener(this);
			chooseChannelButton.addActionListener(this);
			alterZButton.addActionListener(this);
			alterTButton.addActionListener(this);
			removePanelButton.addActionListener(this);
			
			
			this.setLocation(400, 300);
			
			gc.gridx=1;
			gc.gridy=0;
			add(panelListLabel, gc);
			
			
			
			
			this.pack();
		}

		/**Generates a scroll pane for the list*/
		public JScrollPane createScrollPane(JList<?> list, int width) {
			JScrollPane jScrollPane = new JScrollPane(list);
			
			jScrollPane.setPreferredSize(new Dimension(width, 150));
			return jScrollPane;
		}
		
		/**A mouse listener that will respond to double clicks on panels*/
		private MouseListener createPanelEditListener() {
			return new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
					if(e.getClickCount()==2) {
						editPanel();
					}
				}

				@Override
				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub
					
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
					
				}};
		}

		
		/**Creates an advanced channel use gui and shows it*/
		public static void showMultiChannel(MultichannelDisplayLayer multi) {
		
			AdvancedChannelUseGUI distpla = new AdvancedChannelUseGUI(multi.getPanelManager(), multi.getChannelLabelManager());
			
			distpla.setVisible(true);
			
		}

		/**Called to update the channel list to match the panel that has focus*/
		@Override
		public void valueChanged(ListSelectionEvent arg0) {
			int indexsel = getPanelJList().getSelectedIndex();
			if (indexsel>-1&&indexsel<getPanelJList().elements.size()) {
				PanelListElement panel= getPanelJList().elements.get(getPanelJList().getSelectedIndex());
				
				PanelManager currentPanelManager = getCurrentPanelManager(panel);
				getJListForChannels().setPanel(panel, currentPanelManager);
				this.invertChannelCheckBox.setSelected(panel.invertChannelColor);
				currentPanelManager.updatePanels();
				}
		}

		/**
		 * @param panel 
		 * @return
		 */
		public PanelManager getCurrentPanelManager() {
			return pm;
		}
		
		/**gets the panel manager for the given panels
		 * work in progress. this dislog will eventually work for multiple panel managers but currently works with on
		 * @param panel 
		 * @return
		 */
		public PanelManager getCurrentPanelManager(PanelListElement panel) {
			if(pm.getPanelList().getPanels().contains(panel))
				return pm;
			else {
				pm=this.getPanelJList().getCurrentPanelManager(panel);
			}
			return pm;
		}
		

		@Override
		public void actionPerformed(ActionEvent arg0) {
			CombinedEdit e2=PanelManagerUndo.createFor(getCurrentPanelManager());
			if (arg0.getSource()==this.addPanelButton) {
				
				  addPanel();
			}
			
			if (arg0.getSource()==chooseChannelButton) {
				this.displayMenu(arg0);
			}
			
			
			if (arg0.getSource()==this.removePanelButton) {
				getPanelJList().removeSelectedPanels() ;
			}
			
			if(arg0.getSource()==this.alterTButton) {
				this.editPanelFrame();
			}
			if(arg0.getSource()==this.alterZButton) {
				this.editPanelSlice();
			}
			e2.establishFinalState();
			new CurrentFigureSet().addUndo(e2);
			
		}

		/**
		updates the gui after the number of channels has changed
		 */
		public void afterChannelAddSubtract() {
			repaint();
			getJListForChannels().updateDisplay();
			getJListForChannels().repaint();
			
			pack();
		}

		/**
		Called to display the channel addition menu
		 */
		public void displayMenu(ActionEvent arg0) {
			SmartPopupJMenu ppopme = new SmartPopupJMenu();
			
			ArrayList<AvailableChannelsItem> items = getJListForChannels().makeentries(getJListForChannels().getPanel());
			for(AvailableChannelsItem item: items) {
				ppopme.add(item);
				item.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						
					afterChannelAddSubtract();
						
					}});
			}
			ppopme.show((Component) arg0.getSource(), 0,0);
		}

		/**shows a panel adding dialog*/
		public void addPanel() {
			PanelListElement panel = getCurrentPanelManager().addSingleChannelPanel(getCurrentPanelManager().getPanelList());
			  cm.generateChanelLabel(panel);
			  getPanelJList().addPanel(panel);
			//  getPanelJList().updateList();
			  
			  
			  getCurrentPanelManager().putSingleElementOntoGrid(panel, true);
			  panel.getChannelLabelDisplay().updateDisplay();
			
			pack();
			
		}
		
		
		

		public PanelListElement getPrimarySelectedPanel() {
			return getJListForChannels().getPanel();
		}
		
	
		/**called to show a dialog for the selected panel*/
		private void editPanel() {
			editPanelSliceAndFrame(getPrimarySelectedPanel());
		}
		/**if there are multiple options for the target slice and frame
		 * this shows a dialog */
		private void editPanelSliceAndFrame(PanelListElement panel) {
			ChannelSliceAndFrameSelectionDialog dia = new ChannelSliceAndFrameSelectionDialog(panel.targetChannelNumber,panel.targetSliceNumber, panel.targetFrameNumber,getCurrentPanelManager(panel).getMultiChannelWrapper());
			dia.show2DimensionDialog();
			for(PanelListElement panel1: getSelectedPanels()) {
				panel1.setFrameNumber(dia.getFrame());
				panel1.setSliceNumber(dia.getSlice());
			}
				
			updatePanelDisplay();
		}
		
		/**shows a dialog for the user to change the slice number of selected panels*/
		private void editPanelSlice() {
			editPanelSlice(getPrimarySelectedPanel());
		}
		/**shows a dialog for the user to change the frame number of selected panels*/
		private void editPanelFrame() {
			editPanelFrame(getPrimarySelectedPanel());
		}
		
		/**shows a dialog for the user to change the slice number of selected panels*/
		private void editPanelSlice(PanelListElement panel) {
			ChannelSliceAndFrameSelectionDialog dia = new ChannelSliceAndFrameSelectionDialog(panel.targetChannelNumber,panel.targetSliceNumber, panel.targetFrameNumber,getCurrentPanelManager(panel).getMultiChannelWrapper());
			dia.showSliceDialog();
			for(PanelListElement panel1: getSelectedPanels()) {
				panel1.setSliceNumber(dia.getSlice());
			}
				
			updatePanelDisplay();
		}
		/**shows a dialog for the user to change the frame number of selected panels*/
		private void editPanelFrame(PanelListElement panel) {
			ChannelSliceAndFrameSelectionDialog dia = new ChannelSliceAndFrameSelectionDialog(panel.targetChannelNumber,panel.targetSliceNumber, panel.targetFrameNumber,getCurrentPanelManager(panel).getMultiChannelWrapper());
			dia.showFrameDialog();
			for(PanelListElement panel1: getSelectedPanels()) {
				panel1.setFrameNumber(dia.getFrame());
			}
				
			updatePanelDisplay();
		}

		/**updates the panels in the figure*/
		private void updatePanelDisplay() {
			getCurrentPanelManager().updatePanels();
			getCurrentPanelManager().updateDisplay();
			pack();
		}
		
		/**returns panels that are selected in the gui*/
		Iterable<PanelListElement> getSelectedPanels() {
			return getPanelJList().getSelectedValuesList();
		}
		
		@Override
		public
		Dimension  	getPreferredSize() {
			return 	new Dimension(600,400) ;
		}

		public PanelListDisplay getPanelJList() {
			return listPanels;
		}

		public ChannelListDisplay getJListForChannels() {
			return listChannels;
		}
		
		/**Creates a checkbox that determines whether the channels are set to invert
		 * @return
		 */
		protected JCheckBox createInvertCheckBox() {
			JCheckBox jCheckBox = new JCheckBox("Invert");
			jCheckBox.addMouseListener(new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mousePressed(MouseEvent e) {
					
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					boolean selected = invertChannelCheckBox.isSelected();
					
					getPanelJList().getSelectedValue().invertChannelColor=selected;
					
					for(PanelListElement panel: getPanelJList().getSelectedValuesList()) {
						panel.invertChannelColor=selected;
						listPanels.findPanelManager(panel).updatePanels();
					}
					updatePanelDisplay();
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}});
			return jCheckBox;
		}

}
