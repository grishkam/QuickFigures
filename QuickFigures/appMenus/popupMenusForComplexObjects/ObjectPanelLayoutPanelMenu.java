package popupMenusForComplexObjects;

import java.awt.event.ActionEvent;

import fLexibleUIKit.ObjectAction;
import graphicalObjects_LayoutObjects.SpacedPanelLayoutGraphic;
import objectDialogs.SpacedPanelLayoutBorder;

public class ObjectPanelLayoutPanelMenu extends LockedItemMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ObjectPanelLayoutPanelMenu(SpacedPanelLayoutGraphic c) {
		add(new ObjectAction<SpacedPanelLayoutGraphic>(c) {
					@Override
					public void actionPerformed(ActionEvent arg0) {item.repack();item.repack();item.repack(); item.updateDisplay();}	
			}.createJMenuItem("Repack Panels"));
		
		add(new ObjectAction<SpacedPanelLayoutGraphic>(c) {
			@Override
			public void actionPerformed(ActionEvent arg0) {SpacedPanelLayoutBorder dialog = new SpacedPanelLayoutBorder(item);  dialog.showDialog(); }	
	}.createJMenuItem("Set Borders"));
		
		
		super.setLockedItem(c);
		super.addLockedItemMenus();
		
		
		
	}
	
}
