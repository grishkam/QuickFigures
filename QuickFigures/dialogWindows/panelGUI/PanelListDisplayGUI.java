package panelGUI;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import channelLabels.ChannelLabelManager;
import channelMerging.ChannelEntry;
import genericMontageKit.PanelListElement;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import graphicalObjects_FigureSpecific.PanelManager;
import standardDialog.ChannelEntryBox;
import standardDialog.ComboBoxPanel;
import standardDialog.StandardDialog;


/**A gui that show a list of panels*/
public class PanelListDisplayGUI extends JFrame implements ListSelectionListener, ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PanelListDisplay listPanels;
	private PanelManager pm;
	private ChannelLabelManager cm;
	private ChannelListDisplay listChannels;
	
	JButton addPanelButton=new JButton("+"); 
	JButton addChannelButton=new JButton("+");
	JButton removePanelButton=new JButton("-"); 
	JButton removeChannelButton=new JButton("-");
	
	
	JLabel panelListLabel=new JLabel("Panel List");
	JLabel chanListLabel=new JLabel("Channel List");
	
	
		public PanelListDisplayGUI(PanelManager pm, ChannelLabelManager cm) {
			this.cm=cm;
			pm.getStack().setChannelUpdateMode(true);
			GridBagLayout gb = new GridBagLayout();
			GridBagConstraints gc=new GridBagConstraints();
			setLayout(gb);
			gc.gridx=1;
			gc.gridy=1;
			
			this.pm=pm;
			listPanels=new PanelListDisplay(pm);
			listChannels=new ChannelListDisplay(pm, pm.getStack().getMergePanel());
			listChannels.setPanelListPartner(listPanels);
			this.setTitle("Panels for: "+pm.getMultiChannelWrapper().getTitle());
			listPanels.addListSelectionListener(this);
			
			
			gc.gridwidth=3;
			this.add(listPanels, gc);
			gc.gridx=6;
			gc.gridy=1;
			this.add(listChannels, gc);
			gc.gridwidth=1;
			
			
			gc.gridx=6;
			gc.gridy=2;
			gc.anchor=GridBagConstraints.WEST;
			this.add(addChannelButton, gc);
			gc.gridx=7;
			gc.gridy=2;
			gc.anchor=GridBagConstraints.WEST;
			this.add(removeChannelButton, gc);
			
			
			
			gc.gridwidth=1;
			
			gc.gridx=6;
			gc.gridy=0;
			add(chanListLabel, gc);
			
			gc.gridx=1;
			gc.gridy=2;
			gc.anchor=GridBagConstraints.WEST;
			this.add(addPanelButton, gc);
			gc.gridx=2;
			gc.gridy=2;
			gc.anchor=GridBagConstraints.WEST;
			this.add(removePanelButton, gc);
			
			
			
			
			addPanelButton.addActionListener(this);
			addChannelButton.addActionListener(this);
			removePanelButton.addActionListener(this);
			removeChannelButton.addActionListener(this);
			
			
			this.setLocation(400, 300);
			
			gc.gridx=1;
			gc.gridy=0;
			add(panelListLabel, gc);
			
			
			
			
			this.pack();
		}
		
		public static void main(String[] arg) {
			
			;
			//ImagePlusWrapper wrap = new ImagePlusWrapper(IJ.openImage());
			//MultichannelImageDisplay multi = new IJ1MultiChannelCreator().creatMultiChannelDisplayFromUserSelectedImage(true, "/Users/mazog/Desktop/Mon Wiz stuff/Control;Model Source Stack.tif");
		//	showMultiChannel(multi);
			
		}
		
		public static void showMultiChannel(MultichannelDisplayLayer multi) {
		
			PanelListDisplayGUI distpla = new PanelListDisplayGUI(multi.getPanelManager(), multi.getChannelLabelManager());
			
			distpla.setVisible(true);
			
		}

		@Override
		public void valueChanged(ListSelectionEvent arg0) {
			int indexsel = listPanels.getSelectedIndex();
			if (indexsel>-1&&indexsel<listPanels.elements.size()) {
				PanelListElement panel= listPanels.elements.get(listPanels.getSelectedIndex());
				listChannels.setPanel(panel);
				}
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (arg0.getSource()==this.addPanelButton) {
				
				  PanelListElement panel = pm.addSingleChannelPanel(pm.getStack());
				  cm.generateChanelLabel(panel);
				
				  listPanels.setList(pm.getStack());
				  pm.putSingleElementOntoGrid(panel, true);
				  panel.getChannelLabelDisplay().updateDisplay();
				
				pack();
			}
			
			if (arg0.getSource()==this.addChannelButton) {
				
				ArrayList<ChannelEntry> chans = pm.getMultiChannelWrapper().getChannelEntriesInOrder();
			
				StandardDialog sd = new StandardDialog();
				sd.add("Channel ", new ComboBoxPanel("Chan: ", new ChannelEntryBox(pm.getMultiChannelWrapper().getChannelEntriesInOrder())));
				sd.setModal(true);
				sd.showDialog();
				
				int chan = sd.getChoiceIndex("Channel ");
				
				ChannelEntry newChan = chans .get(chan-1);
				
				listChannels.elements.add(newChan);
				listChannels.getPanel().addChannelEntry(newChan);
				listChannels.updateDisplay();
				listChannels.repaint();
				
				pack();
			}
			
			if (arg0.getSource()==this.removeChannelButton) {
				listChannels.removeSelectedChannels();
			}
			if (arg0.getSource()==this.removePanelButton) {
				listPanels.removeSelectedPanels() ;
			}
			
		}
		
		
		@Override
		public
		Dimension  	getPreferredSize() {
			return 	new Dimension(600,200) ;
		}
}
