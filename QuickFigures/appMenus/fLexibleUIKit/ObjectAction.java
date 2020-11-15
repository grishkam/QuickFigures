package fLexibleUIKit;

import java.awt.event.ActionListener;
import java.lang.reflect.Method;

import javax.swing.Icon;
import javax.swing.JMenuItem;

import graphicActionToolbar.CurrentFigureSet;
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
		JMenuItem out=new JMenuItem(st);
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
