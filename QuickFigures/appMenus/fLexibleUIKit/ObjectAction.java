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
 * Date Modified: Dec 4, 2021
 * Version: 2023.2
 */
package fLexibleUIKit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;

import javax.swing.Icon;
import javax.swing.JMenuItem;

import graphicActionToolbar.CurrentFigureSet;
import menuUtil.BasicSmartMenuItem;
import undo.AbstractUndoableEdit2;

/**an abstract class for action listeners that perform one task targettig one
 * specific object*/
public abstract class ObjectAction<Type> implements ActionListener {
	
	public Type item;
	Method m;
	private BasicSmartMenuItem menuitem;
	
	public ObjectAction(Type i) {
		item=i;
	}
	
	public ObjectAction(Type i, Method m) {
		item=i;
		this.m=m;
	}

	public JMenuItem createJMenuItem(String st) {
		BasicSmartMenuItem out=new BasicSmartMenuItem(st);
		out.addActionListener(this);
		menuitem=out;
		return out;
	}
	
	public JMenuItem createJMenuItem(String st, Icon i) {
		BasicSmartMenuItem out=new BasicSmartMenuItem(st);
		out.setIcon(i);
		out.addActionListener(this);
		menuitem=out;
		return out;
	}
	
	public void addUndo(AbstractUndoableEdit2 e) {
		if(e==null)
			return;
		if(menuitem.getUndoManager()!=null) {
			menuitem.getUndoManager().addEdit(e);
			
		} else 
		new CurrentFigureSet().addUndo(e);
	}
	
	/**May be overwritten by subclasses. Does some task and returns an undo*/
	public AbstractUndoableEdit2 performAction() {
		return null;
	}
	
	/**Called when the menu item is pressed, does some task and adds an undo to the undo manager*/
	public void actionPerformed(ActionEvent e) {
		addUndo(performAction());
	}
	
	

}
