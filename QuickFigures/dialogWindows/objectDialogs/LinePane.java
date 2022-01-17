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
package objectDialogs;

import java.awt.Component;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JTabbedPane;

import standardDialog.ObjectEditEvent;
import standardDialog.ObjectEditListener;
import standardDialog.ObjectInputTabPane;
import textObjectProperties.TextLine;
import textObjectProperties.TextLineSegment;

/**A tab for editing TextLines via dialog*/
public class LinePane  extends ObjectInputTabPane implements MouseListener, ActionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TextLine line;
	MenuItem addSegmetn=makeMenuItem("add segment");// {addSegmetn.addActionListener(this);}
	MenuItem remSegmetn=makeMenuItem("remove segment");// {remSegmetn.addActionListener(this);}
	MenuItem forSeg=makeMenuItem("move segment forward");
			MenuItem backSeg=makeMenuItem("move segment backward");
	
	PopupMenu mem=new PopupMenu() ;{mem.add(addSegmetn); mem.add(remSegmetn);mem.add(forSeg); mem.add(backSeg);}
	{this.addMouseListener(this);}
	private ArrayList<TextLineSegmentPanel> allLineSegmentPanels=new ArrayList<TextLineSegmentPanel>();
	int seg=1;
	boolean includeColor=true;
	
	public MenuItem makeMenuItem(String st) {
		MenuItem addLin2=new MenuItem(st);
		addLin2.addActionListener(this);
		return addLin2;
	}
	
	public LinePane(TextLine lin) {
		
		setLine(lin);
		setUp();
		addAllSegmentPanels(lin);
		
		
		
	}
	
	public void setUp() {
		setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		this.add(mem);
	}
	
	
	public void addAllSegmentPanels(TextLine lin) {
		for(TextLineSegment t: lin) {
			if (t==null) continue;
			addSegmentPanel(t);
		}
	}
	
	
	
	
	
	public void addSegmentPanel(TextLineSegment t) {
		TextLineSegmentPanel pan = new TextLineSegmentPanel(t);
			getAllLineSegmentPanels().add(pan);
			pan.addObjectEditListeners(this.lis);
			this.addTab("Segment "+seg, pan);
			seg++;
	}
	
	public TextLineSegmentPanel getSelectedPanel() {
		int i = this.getSelectedIndex();
		Component comp = this.getComponentAt(i);
		if (comp instanceof TextLineSegmentPanel) {
			TextLineSegmentPanel p=(TextLineSegmentPanel) comp;
			return p;
			}
		return null;
	}
	
	public TextLineSegment getSelectedSeg() {
		
			TextLineSegmentPanel p=getSelectedPanel() ;
			if (p!=null) return p.getSegment();
		
		return null;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
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
		if (arg0.isPopupTrigger()||arg0.getButton()==3) {
			
			mem.show(this, arg0.getX(), arg0.getY());
		}
		
	}
	
	public void addObjectEditListener(ObjectEditListener o) {
		lis.add(o);
		for(TextLineSegmentPanel sp:allLineSegmentPanels) {
			if (sp!=null)
			sp.addObjectEditListener(o);
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
	if (arg0.getSource()==this.addSegmetn) {
		TextLineSegment segnew = new TextLineSegment("new ", 0);
		getLine().add(segnew);
		addSegmentPanel(segnew);
		super.notifyListeners(new ObjectEditEvent(getLine()));
	//	this.repaint();
	}
	
	if (arg0.getSource()==this.remSegmetn) {
		
		getLine().remove(this.getSelectedSeg());
		this.remove(this.getSelectedComponent());
		super.notifyListeners(new ObjectEditEvent(getLine()));
	}
	
	if (arg0.getSource()==this.forSeg) {
		
		this.getLine().moveSegForward(this.getSelectedSeg());
		super.notifyListeners(new ObjectEditEvent(getLine()));
	}
	
	if (arg0.getSource()==this.backSeg) {
		
		this.getLine().moveSegBackward(this.getSelectedSeg());
		super.notifyListeners(new ObjectEditEvent(getLine()));
	}
		
	}

	public TextLine getLine() {
		return line;
	}

	public void setLine(TextLine line) {
		this.line = line;
	}

	public ArrayList<TextLineSegmentPanel> getAllLineSegmentPanels() {
		return allLineSegmentPanels;
	}

}
