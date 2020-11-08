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

import logging.IssueLog;
import standardDialog.ObjectEditEvent;
import standardDialog.ObjectEditListener;
import standardDialog.ObjectInputTab;
import utilityClassesForObjects.TextLine;
import utilityClassesForObjects.TextParagraph;

public class ParaGraphPane extends ObjectInputTab implements ActionListener, MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	{this.addMouseListener(this);}
	
	TextParagraph para;
	int line=1;
	private ArrayList<LinePane> LinePanels=new ArrayList<LinePane> ();
	
	MenuItem addLin=makeMenuItem("Add Line");
	MenuItem removeLin=makeMenuItem("Remove Line");
	MenuItem moveLineForward=makeMenuItem("Move Line Down");
	MenuItem moveLineBackward=makeMenuItem("Move Line Up");
	PopupMenu pmenu=createPopup() ;
	
	public PopupMenu createPopup() {
		PopupMenu pmen=new PopupMenu();  
		
	
		//addLin.addActionListener(this);
		//removeLin.addActionListener(this);
		
		pmen.add(addLin);
		pmen.add(removeLin);
		pmen.add(moveLineForward);
		pmen.add(moveLineBackward);
	//	IssueLog.log(""+pmen.getItemCount());
		this.add(pmen);
		//IssueLog.log(""+pmen.getItemCount());
		return pmen;
	}
	
	public MenuItem makeMenuItem(String st) {
		MenuItem addLin2=new MenuItem(st);
		addLin2.addActionListener(this);
		return addLin2;
	}
	
	
	public ParaGraphPane(TextParagraph tp) {
		para=tp;
		this.setTabPlacement(JTabbedPane.LEFT);
		setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		for(TextLine line:tp) {
			addLine(line);
		}
	}
	
	public void addLine(TextLine t) {
		LinePane lp = new LinePane(t);
		this.add("Line "+line, lp);
		getLinePanels().add(lp);
		lp.addObjectEditListeners(this.lis);
		line++;
	}
	
	public LinePane getSelectedPanel() {
		int i = this.getSelectedIndex();
		Component comp = this.getComponentAt(i);
		if (comp instanceof LinePane) {
			LinePane p=(LinePane) comp;
			return p;
			}
		return null;
	}
	
	public TextLine getSelectedLine() {
		LinePane p=getSelectedPanel();
		if (p!=null) return p.getLine();
		return null;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource()==addLin) {
			TextLine newline = para.addLine();
			this.addLine(newline);
			super.notifyListeners(new ObjectEditEvent(para));
		}
		
		if (arg0.getSource()==removeLin) {
			TextLine newline = this.getSelectedLine();
			para.remove(newline);
			LinePane pan = this.getSelectedPanel();
			this.remove(pan);
			super.notifyListeners(new ObjectEditEvent(para));
			
		}
		
		if (arg0.getSource()==moveLineForward) try {
			//moveTabForward(this.getSelectedIndex());
			this.para.moveLineForward(getSelectedLine());
			this.repaint();
			super.notifyListeners(new ObjectEditEvent(para));
			
		}
		catch (Throwable t) {
			IssueLog.log(t);
		}
		if (arg0.getSource()==moveLineBackward) {
			this.para.moveLineBackward(getSelectedLine());
			this.repaint();
			super.notifyListeners(new ObjectEditEvent(para));
		}
		
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
			pmenu.show(this, arg0.getX(), arg0.getY());
		//	IssueLog.log(""+pmenu.getItemCount());
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public ArrayList<LinePane> getLinePanels() {
		return LinePanels;
	}
	
	public void addObjectEditListener(ObjectEditListener o) {
		for(LinePane pan:this.LinePanels) {pan.addObjectEditListener(o);}
		super.addObjectEditListener(o);
	}

	
	
}