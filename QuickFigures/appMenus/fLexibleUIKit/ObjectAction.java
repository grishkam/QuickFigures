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
package fLexibleUIKit;

import java.awt.event.ActionListener;
import java.lang.reflect.Method;

import javax.swing.Icon;
import javax.swing.JMenuItem;

import graphicActionToolbar.CurrentFigureSet;
import menuUtil.BasicSmartMenuItem;
import undo.AbstractUndoableEdit2;

public abstract class ObjectAction<Type> implements ActionListener {
	
	public Type item;
	Method m;
	
	public ObjectAction(Type i) {
		item=i;
	}
	
	public ObjectAction(Type i, Method m) {
		item=i;
		this.m=m;
	}

	public JMenuItem createJMenuItem(String st) {
		JMenuItem out=new BasicSmartMenuItem(st);
		out.addActionListener(this);
		return out;
	}
	
	public JMenuItem createJMenuItem(String st, Icon i) {
		JMenuItem out=new JMenuItem(st);
		out.setIcon(i);
		out.addActionListener(this);
		return out;
	}
	
	public void addUndo(AbstractUndoableEdit2 e) {
		new CurrentFigureSet().addUndo(e);
	}
	
	

}
