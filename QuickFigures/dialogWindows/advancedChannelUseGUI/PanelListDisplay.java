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
package advancedChannelUseGUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import channelMerging.ChannelEntry;
import genericMontageKit.PanelList;
import genericMontageKit.PanelListElement;
import graphicalObjects_FigureSpecific.MultichannelDisplayLayer;
import graphicalObjects_FigureSpecific.PanelManager;
import standardDialog.colors.ColorDimmingBox;


/**experimenting with a way for the user to look through the parts of a panel list*/
public class PanelListDisplay extends JList<PanelListElement> implements ActionListener, DropTargetListener, KeyListener, WindowListener, MouseListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PanelList list=null;
	Vector<PanelListElement> elements=new Vector<PanelListElement>();
	private SpecialCellRenderer render= new SpecialCellRenderer();
	private PanelManager panelManager;
	
	
	public PanelListDisplay(PanelManager man) {
		this.panelManager=man;
		this.setList(man.getPanelList());
		this.setDragEnabled(true);
		addListSelectionListener(new listener1(this));
		new DropTarget(this, this);
		this.addKeyListener(this);
	}
	
	void setList(PanelList list) {
		this.list=list;
		elements.clear();
		elements.addAll(list.getPanels());
		this.setListData(elements);
		selectedPanels();
	}
	

	
	/**sets this to the user selected panels*/
	public void selectedPanels() {
		ArrayList<Integer> ints=new ArrayList<Integer>();
		for(int i=0; i<this.elements.size(); i++) {
			if(elements.get(i).isSelected()) ints.add(i);
		}
		int[] ar = new int[ints.size()];
		for(int i=0; i<ints.size(); i++) {
			ar[i]=ints.get(i);
		}
		super.setSelectedIndices(ar);
	}

	void selectListSelectedDisplays() {
		for(int i=0; i<elements.size(); i++) {
			 elements.get(i).selectLabelAndPanel(false);;
			
			
		}
		
		
		int[] index1 = this.getSelectedIndices();
		
		for(int i=0; i<index1.length; i++) {
			PanelListElement ele = elements.get(index1[i]);
			ele.selectLabelAndPanel(true);
			
			ele.getPanelGraphic().updateDisplay();
		}
	
	}
	
	public static void main(String[] arg) {
		
		;
		//ImagePlusWrapper wrap = new ImagePlusWrapper(IJ.openImage());
	//	MultichannelImageDisplay multi = new IJ1MultiChannelCreator().creatMultiChannelDisplayFromUserSelectedImage(true, null);
	//	showMultiChannel(multi);
		
	}
	
	public static void showMultiChannel(MultichannelDisplayLayer multi) {
		JFrame jf = new JFrame();
		jf.setLayout(new FlowLayout());
		PanelListDisplay distpla = new PanelListDisplay(multi.getPanelManager());
		jf.add(distpla);
		JButton swapButton=new JButton("Swap Items");
		jf.add(swapButton);
		swapButton.addActionListener(distpla);
		jf.pack();jf.setVisible(true);
		
	}
	
	
	public  ListCellRenderer	getCellRenderer() {return render;}
	
	
	class SpecialCellRenderer extends  DefaultListCellRenderer {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private int theindex;
		//private int panelNumber;
		private boolean focus;
		private boolean isSelected;
		public Component 	getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			theindex=index;
			focus=cellHasFocus;
			Component out = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (out instanceof SpecialCellRenderer) {
				SpecialCellRenderer c=(SpecialCellRenderer) out;
				c.focus=cellHasFocus;
				//c.panelNumber=theindex-1;
				c.isSelected=isSelected;
					{this.setFont(this.getFont().deriveFont(Font.BOLD).deriveFont((float)20.0));}
				if (isSelected) {
				//	c.panelNumber=theindex-1;
		
					}
			}
		
			return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		}	
		
		
		public void paint(Graphics g) {
		
			int dim=theindex;
			if (dim==-1) {dim=theindex;}
			
			PanelListElement panel = list.getPanels().get(theindex);
			ArrayList<ChannelEntry> theChannelentries = panel.getChannelEntries();
			
			boolean merge=panel.designation+0==0+PanelListElement.MERGE_IMAGE_PANEL;
			int size=theChannelentries.size()+1 ;
			
			int[] lengths=new int[size];
			Color[] colors=new Color[size];
			ArrayList<String> names=new ArrayList<String>();
			String all="";
			
			int i=0;
			int start = 0;
			
				start=1;
				;
				String panelType="(c="+panel.targetChannelNumber+ ", f="+panel.targetFrameNumber+", z="+panel.targetSliceNumber+")";
				if (merge) 	panelType="(Merge "+", f="+panel.targetFrameNumber+", z="+panel.targetSliceNumber+")";;
				names.add(panelType);
			
				lengths[0]=panelType.length();
				colors[0]=Color.black;
				all+=panelType;
			
			
			for(; i<theChannelentries.size(); i++ ) {
				ChannelEntry chan=theChannelentries.get(i);
				String real = chan.getRealChannelName();
				if(real==null) real=chan.getLabel();
				if(real==null) real="Channel #"+chan.getOriginalChannelIndex();
				names.add(real); lengths[i+start]=real.length();
				colors[i+start]=chan.getColor();
				all+=real;
				
			}
			
			
			//super.paint(g);
			double w = g.getFontMetrics().getStringBounds(all, g).getWidth();
			g.setColor(Color.blue);
			if (focus||isSelected) g.fillRect(1,1, (int)w, this.getFont().getSize()+2);
			ColorDimmingBox.drawRainBowString(g, 1,this.getFont().getSize()+1, names, lengths, colors);
			//if (theChannelentries.size()>0) ChannelEntryBox.drawRainbowString(g, theChannelentries.get(0), 1,this.getFont().getSize()+1);
			//else {ChannelEntryBox.drawRainbowString(g, null, 1,this.getFont().getSize()+1);}
			
		}
	
	}
	

	class listener1 implements ListSelectionListener {

		public listener1(PanelListDisplay panelListDisplay) {
			
		}

		@Override
		public void valueChanged(ListSelectionEvent arg0) {
			selectListSelectedDisplays() ;
			
		}}


	@Override
	public void actionPerformed(ActionEvent arg0) {
	
		swapItems() ;
		
	}
	
	void swapItems() {
		int[] ind = this.getSelectedIndices();
		if (ind.length>1) {
			PanelListElement panel1 = elements.get(ind[0]);
			PanelListElement panel2 = elements.get(ind[1]);
			swapItems(panel1, panel2);
		}
		
	}
	
	void swapItems(PanelListElement panel1, PanelListElement panel2) {
		
		int ind1 = elements.indexOf(panel1);
				int ind2 = elements.indexOf(panel2);
			
			panelManager.getPanelList().swapPanelLocations(panel1, panel2);
			
			
			panelManager.updatePanels();
			if (panel1.getChannelLabelDisplay()!=null)panel1.getChannelLabelDisplay().updateDisplay(); 
			
			
			elements.set(ind1, panel2);
			elements.set(ind2, panel1);
			//this.setListData(elements);
			this.repaint();
		
		
	}
	
	void removeItem(PanelListElement panel12) {
		
			elements.remove(panel12);
		
			
			panelManager.getPanelList().remove(panel12);
			
			panelManager.removeDisplayObjectsFor(panel12);
			panelManager.updatePanels();
			panelManager.getDisplay().onImageUpdated();
			
			//this.setListData(elements);
			this.repaint();
		
		
	}




	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void dragExit(DropTargetEvent dte) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void drop(DropTargetDropEvent dtde) {
		if (dtde.getDropTargetContext().getComponent() ==this) {
			int index = getIndexForPoint(dtde.getLocation());
			
			if (index>-1) {
				PanelListElement e1 = elements.get(index);
				PanelListElement e2 = elements.get(this.getSelectedIndex());
				swapItems(e1, e2);
			}
			;
		}
		
	}




	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
		// TODO Auto-generated method stub
		
	}
	
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
			removeSelectedPanels();
			
 		}
		
	}
	
	public void removeSelectedPanels() {
		for(PanelListElement o: getSelectedValuesList()) {
			
				this.removeItem( o);
			
		}
	}




	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	/**
	@Override
	public
	Dimension  	getPreferredSize() {
	return 	new Dimension(300,200) ;
	}
	*/

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		selectedPanels();
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
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
		this.selectedPanels();
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
}
