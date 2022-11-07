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
 * Version: 2022.2
 */
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

import graphicalObjects_SpecialObjects.TextGraphic;
import menuUtil.BasicSmartMenuItem;
import menuUtil.PopupMenuSupplier;
import menuUtil.SmartPopupJMenu;

/**A simple cut copy and paste menu for textGraphic objects.
   */
public class TextSelectionMenu extends SmartPopupJMenu implements ActionListener,
PopupMenuSupplier, Transferable{

	static final String COPY="copy", PASTE="paste", CUT="cut";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TextGraphic text;
	ArrayList<JMenuItem> items=new ArrayList<JMenuItem>();
	private  static String theCoppedText;

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
		JMenuItem jitem = new BasicSmartMenuItem(item);
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
			theCoppedText = text.getSelectedText();
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
		return theCoppedText;
		return null;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] {DataFlavor.stringFlavor};
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor arg0) {
		if (arg0.equals(DataFlavor.stringFlavor)) return true;
		return false;
	}

}
