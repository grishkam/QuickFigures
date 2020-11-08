package panelGUI;

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
import java.util.Vector;

import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import channelMerging.ChannelEntry;
import genericMontageKit.PanelListElement;
import graphicalObjects_FigureSpecific.PanelManager;


/**experimenting with a way for the user to look through the parts of a panel list*/
public class ChannelListDisplay extends JList implements ActionListener, DropTargetListener, KeyListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Vector<ChannelEntry> elements=new Vector<ChannelEntry>();
	private ChannelColorCellRenerer2 render= new ChannelColorCellRenerer2(this);
	private PanelManager panelManager;
	private PanelListElement panel;
	private PanelListDisplay panelDisp;
	
	
	public ChannelListDisplay(PanelManager man, PanelListElement panel) {
		this.panelManager=man;
		
		
		setPanel(panel);
		this.setListData(elements);
		
		this.setDragEnabled(true);
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION );
		addListSelectionListener(new listener1(this));
		new DropTarget(this, this);
		this.addKeyListener(this);
	}
	
	public void setPanelListPartner(PanelListDisplay panelDisp) {this.panelDisp=panelDisp;}

	public void setPanel(PanelListElement panel) {
		if(panel==null) return;
		this.panel=panel;
		elements.clear();
		elements.addAll(panel.getChannelEntries());
		
		
		this.repaint();
		
	}

	
	PanelListElement getPanel() {return panel;}
	
	
	void selectListSelectedDisplays() {
		for(int i=0; i<elements.size(); i++) {
			
			
		}
		
		
		int[] index1 = this.getSelectedIndices();
		
		for(int i=0; i<index1.length; i++) {
			ChannelEntry ele = elements.get(index1[i]);
			
		}
	
	}
	
	public static void main(String[] arg) {
		
		
		
	}
	
	
	
	public  ListCellRenderer	getCellRenderer() {return render;}
	

	class listener1 implements ListSelectionListener {

		public listener1(ChannelListDisplay panelListDisplay) {
			
		}

		@Override
		public void valueChanged(ListSelectionEvent arg0) {
			selectListSelectedDisplays() ;
			
		}}


	@Override
	public void actionPerformed(ActionEvent arg0) {
	
		swapItems() ;
		if (panelDisp!=null)panelDisp.repaint();
	}
	
	void swapItems() {
		int[] ind = this.getSelectedIndices();
		if (ind.length>1) {
			ChannelEntry panel1 = elements.get(ind[0]);
			ChannelEntry panel2 = elements.get(ind[1]);
			swapItems(panel1, panel2);
		}
		
	}
	
	void swapItems(ChannelEntry cl1, ChannelEntry cl2) {
		
		int ind1 = elements.indexOf(cl1);
				int ind2 = elements.indexOf(cl2);
			
				elements.set(ind1, cl2);
				elements.set(ind2, cl1);
				panel.getChannelEntries().set(ind1, cl2);
				panel.getChannelEntries().set(ind2, cl1);
				
			
				 updateDisplay();
			
			
			
			if (this.panelDisp!=null) panelDisp.repaint();
			//this.setListData(elements);
			this.repaint();
		
		
	}
	
	void updateDisplay() {
		panelManager.updatePanels();
		
		if (panel!=null&&panel.getChannelLabelDisplay()!=null)panel.getChannelLabelDisplay().updateDisplay(); 
		
	}
	
	void removeItem(ChannelEntry o) {
			if(elements.size()<2) return;
			elements.remove(o);
		
			
			panel.removeChannelEntry(o);
			
			panelManager.updatePanels();
			
			
			panelManager.getDisplay().onImageUpdated();
			
			
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
				ChannelEntry e1 = elements.get(index);
				ChannelEntry e2 = elements.get(this.getSelectedIndex());
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
			
			removeSelectedChannels();
 		}
		
	}
	
	public void removeSelectedChannels() {
		Object[] index1 = this.getSelectedValues();
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
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public
	Dimension  	getPreferredSize() {
	return 	new Dimension(250,200) ;
	}
	
	
	
}
