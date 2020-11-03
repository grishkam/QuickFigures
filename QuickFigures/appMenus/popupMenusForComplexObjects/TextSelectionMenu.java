package popupMenusForComplexObjects;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import graphicalObjects_BasicShapes.TextGraphic;
import logging.IssueLog;
import menuUtil.PopupMenuSupplier;
import menuUtil.SmartPopupJMenu;

import java.awt.datatransfer.Clipboard;

public class TextSelectionMenu extends SmartPopupJMenu implements ActionListener,
PopupMenuSupplier, Transferable{

	static final String COPY="copy", PASTE="paste", CUT="cut";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TextGraphic text;
	ArrayList<JMenuItem> items=new ArrayList<JMenuItem>();
	private  static String theCippedText;

	public TextSelectionMenu(TextGraphic textGraphic) {
		this.text=textGraphic;
		
		addBasicItems();
	}

	protected void addBasicItems() {
		addItemToMenu(COPY);
		addItemToMenu(PASTE);
		addItemToMenu(CUT);
	}

	protected void addItemToMenu(String item) {
		JMenuItem jitem = new JMenuItem(item);
		jitem.setActionCommand(item);
		jitem.addActionListener(this);
		this.add(jitem);
	}

	@Override
	public JPopupMenu getJPopup() {
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		
		String a = e.getActionCommand();
		Clipboard c=Toolkit.getDefaultToolkit().getSystemClipboard();
		
		if(COPY.equals(a)||CUT.equals(a)) {
			theCippedText = text.getSelectedText();
			c.setContents(this, null);
		}
		if(CUT.equals(a)) {
			if (text.hasHighlightRegion())text.onBackspace();
		}
		if(PASTE.equals(a)) try {
			String st=""+c.getData(DataFlavor.stringFlavor);
			text.handlePaste(st);
		} catch (Throwable t) {}
		
	}

	@Override
	public Object getTransferData(DataFlavor arg0) throws UnsupportedFlavorException, IOException {
		if (arg0.equals(DataFlavor.stringFlavor))
		return this.theCippedText;
		return null;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		// TODO Auto-generated method stub
		return new DataFlavor[] {DataFlavor.stringFlavor};
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor arg0) {
		if (arg0.equals(DataFlavor.stringFlavor)) return true;
		return false;
	}

}
