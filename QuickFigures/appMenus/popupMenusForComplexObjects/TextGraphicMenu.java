package popupMenusForComplexObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import fLexibleUIKit.ObjectAction;
import graphicalObjects_BasicShapes.TextGraphic;
import graphicalObjects_FigureSpecific.FigureOrganizingLayerPane;
import menuUtil.SmartPopupJMenu;
import menuUtil.PopupMenuSupplier;
import objectDialogs.TextInsetsDialog;

public class TextGraphicMenu extends SmartPopupJMenu implements ActionListener,
PopupMenuSupplier  {

	/**
	 * 
	 */
	
	static final String inset="Inset Options", options="Text Options", backGroundShap="Background Shape", duplicate="Duplicate";
	
	TextGraphic textG;
	public TextGraphicMenu(TextGraphic textG) {
		super();
		this.textG = textG;
		for(JMenuItem i: getItems() ) {add(i);}
	}
	
	public ArrayList<JMenuItem> getItems() {
		ArrayList<JMenuItem> jm=new ArrayList<JMenuItem>();
		jm.add(createItem(options));
		jm.add(createItem(inset));
		jm.add(createItem(backGroundShap));
		jm.add(new ObjectAction<TextGraphic>(textG) {

			public void actionPerformed(ActionEvent e) {
				TextGraphic c = textG.copy();
				c.moveLocation(5, 2);
				if(textG.getParentLayer()==null) return;
				textG.getParentLayer().add(c);
			}}.createJMenuItem("Duplicate"));
		
		FigureOrganizingLayerPane f = FigureOrganizingLayerPane.findFigureOrganizer(textG);
		if(f!=null) {
			JMenuItem menuItem = f.getMenuSupplier().getLabelEditorMenuItemFor(textG);
			if (menuItem==null ) menuItem=new EditLabels(textG);
			if (menuItem!=null)jm.add(menuItem);
		}
		
		return jm;
	}
	
	public JMenu getJMenu(String st) {
		JMenu out=new JMenu(st);
		for(JMenuItem i: getItems() ) {out.add(i);}
		return out;
	}
	
	public JMenuItem createItem(String st) {
		JMenuItem o=new JMenuItem(st);
		o.addActionListener(this);
		o.setActionCommand(st);
		
		return o;
	}

	private static final long serialVersionUID = 1L;

	@Override
	public JPopupMenu getJPopup() {
		return this;
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		String com=arg0.getActionCommand();
		if (com.equals(backGroundShap)) {
			textG.getBackGroundShape().showOptionsDialog();
		}
		if (com.equals(options)) {
			textG.showOptionsDialog();
		}
		if (com.equals(inset)) {
			TextInsetsDialog id = new TextInsetsDialog(textG);
			id.showDialog();
		}
	}
	
	
	
}
