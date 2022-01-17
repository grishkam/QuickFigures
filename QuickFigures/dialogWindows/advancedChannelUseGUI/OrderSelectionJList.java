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

import java.awt.Color;
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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import menuUtil.SmartPopupJMenu;


/**GUI part to select items in list. The use can delete items, add items
  or change their order*/
public class OrderSelectionJList<Type> extends JList<String> implements ActionListener, DropTargetListener, KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Vector<String> namesInOrder;
	private HashMap<String, Type> reverseMap;
	private HashMap<Type, String> forwardMap;
	private Vector<String> elements;
	private Vector<String> removedElements=new  Vector<String>();
	private boolean removeEnabled=true;
	private Vector<String> addable;
	
	private ArrayList<Type> addedDuringEdit=new ArrayList<Type>();//the list of objects added using the menu
	
	public OrderSelectionJList(ArrayList<Type> listOriginal, HashMap<Type, String> listNames, Iterable<Type> addAble){
		if (listNames==null)listNames=new HashMap<Type, String>();
		namesInOrder=new Vector<String> ();
		addable=new Vector<String> ();
		reverseMap=new HashMap<String, Type> ();
		forwardMap=listNames;
		elements=namesInOrder;
		this.setBackground(new Color(200, 200, 250));
		for(Type t: listOriginal) {
			setupObjectForChoice(t);
		}
		
		if (addAble!=null)for(Type t: addAble) {
			setupObjectForAddonChoice(t);
		}
		
		this.setListData(namesInOrder);
		
		this.setDragEnabled(true);
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION );
		this.setDragEnabled(true);
		
		new DropTarget(this, this);
		this.addKeyListener(this);
		this.addMouseListener(addMouseList(true) );
	
				
	}

MouseListener addMouseList(boolean b) {
	return new MouseListener() {
		
		@Override
		public void mouseClicked(MouseEvent arg0) {
			if (arg0.isPopupTrigger()||arg0.isControlDown()||arg0.isMetaDown()||arg0.getSource() instanceof JButton) {
				SmartPopupJMenu men = new SmartPopupJMenu();
				for(JMenuItem m: createJMenuForAddable()) {
					men.add(m);
				}
				men.show(arg0.getComponent(), arg0.getX(), arg0.getY());
			}
			
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}};
}
	

	private void setupObjectForChoice(Type t) {
		if (!forwardMap.containsKey(t)) {
			forwardMap.put(t, t.toString());
		}
		String name=forwardMap.get(t);
		reverseMap.put(name,t);
		namesInOrder.add(name);
	}
	
	private void setupObjectForAddonChoice(Type t) {
		if (!forwardMap.containsKey(t)) {
			forwardMap.put(t, t.toString());
		}
		String name=forwardMap.get(t);
		reverseMap.put(name,t);
		if (namesInOrder.contains(name)) return;
		addable.add(name);
	}
	
	public ArrayList<JMenuItem> createJMenuForAddable() {
		ArrayList<JMenuItem> menuItems=new ArrayList<JMenuItem>();
		
		for(String a: addable) {
			JMenuItem jmi = new JMenuItem();
			jmi.setText("Add "+a);jmi.setActionCommand(a);
			menuItems.add(jmi);
			jmi.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					String comm=arg0.getActionCommand();
				  Type willAdd = reverseMap.get(comm);
					elements.add(comm);
					addable.remove(comm);
					removedElements.remove(comm);
					addedDuringEdit.add(willAdd);
					setListData(elements);//needed to ensure everything stays visible
					repaint();
				}});
		}
		return  menuItems;
		
	}
	
	
	/**returns the new order*/
	public ArrayList<Type> getNewOrder() {
		ArrayList<Type> output = new ArrayList<Type>();
		for(String s: elements) {
			output.add(reverseMap.get(s));
		}
		
		return output;
	}
	
	/**returns the new order*/
	public ArrayList<Type> getRemovedItems() {
		ArrayList<Type> output = new ArrayList<Type>();
		for(String s: removedElements) {
			output.add(reverseMap.get(s));
		}
		
		return output;
	}
	
	
	class listener1 implements ListSelectionListener {

		public listener1(PanelListDisplay panelListDisplay) {
			
		}

		@Override
		public void valueChanged(ListSelectionEvent arg0) {
			//selectListSelectedDisplays() ;
			
		}}

	
	void swapItems() {
		int[] ind = this.getSelectedIndices();
		if (ind.length>1) {
			String panel1 = elements.get(ind[0]);
			String panel2 = elements.get(ind[1]);
			swapItems(panel1, panel2);
		}
		
	}
	
	void swapItems(String panel1,String panel2) {
		
		int ind1 = elements.indexOf(panel1);
				int ind2 = elements.indexOf(panel2);
		
			
			elements.set(ind1, panel2);
			elements.set(ind2, panel1);
			//this.setListData(elements);
			this.repaint();
		
		
	}
	
	void removeItem(String panel12) {
		
			elements.remove(panel12);
		    removedElements.addElement(panel12);
			addedDuringEdit.remove(reverseMap.get(panel12));
			
			//this.setListData(elements);
			this.repaint();
		
		
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
	
		swapItems() ;
		
	}
	
	@Override
	public void drop(DropTargetDropEvent dtde) {
		if (dtde.getDropTargetContext().getComponent() ==this) {
			int index = getIndexForPoint(dtde.getLocation());
			
			if (index>-1) {
				String e1 = elements.get(index);
				String e2 = elements.get(this.getSelectedIndex());
				swapItems(e1, e2);
			}
			
		}
		
	}
	int getIndexForPoint(Point2D pt) {
		for(int i=0; i<elements.size(); i++) {
			if (getCellBounds(i,i).contains(pt)) return i;
		}
		
		return -1;
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
	public void dragEnter(DropTargetDragEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragExit(DropTargetEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragOver(DropTargetDragEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args) {
		JFrame frame=new JFrame("test");
		ArrayList<String> listS=new ArrayList<String>();
		listS.add("Rabbit");listS.add("Horse");listS.add("Pigeon");listS.add("Lark");
			OrderSelectionJList<String> ilist = new OrderSelectionJList<String> (listS, new HashMap<String, String> (), new ArrayList<String>());
	frame.add(ilist);
	frame.pack();
	frame.setVisible(true);
	}
	
	JButton createRemoveButton() {
		JButton output=new JButton("Remove Item");
		output.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						removeSelectedPanels();
					}}
					);
		return output;
	}
	
	JButton createAddButton() {
		JButton output=new JButton("Add Item");
		output.addMouseListener(addMouseList(true));
		return output;
	}
	
	public JPanel createButtonPanel() {
		JPanel p = new JPanel();
		p.add(createAddButton());
		p.add(createRemoveButton());
		
		return p;
	}
	
	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode()==KeyEvent.VK_DELETE) {
			removeSelectedPanels();
		}
		if (arg0.getKeyCode()==KeyEvent.VK_BACK_SPACE) {
			removeSelectedPanels();
 		}
		
	}
	
	public void removeSelectedPanels() {
		if (this.removeEnabled) 
			for(String o: this.getSelectedValuesList()) {
				this.removeItem((String) o);
				addable.addElement((String) o);
				
		}
	}




	public boolean isRemoveEnabled() {
		return removeEnabled;
	}




	public void setRemoveEnabled(boolean removeEnabled) {
		this.removeEnabled = removeEnabled;
	}

	public ArrayList<Type> getNewlyAddedItems() {
		
		return addedDuringEdit;
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(200, 300);
	}
	
	
}
