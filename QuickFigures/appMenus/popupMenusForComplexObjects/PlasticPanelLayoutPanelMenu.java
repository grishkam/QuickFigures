package popupMenusForComplexObjects;

import java.awt.event.ActionEvent;

import javax.swing.JMenu;

import fLexibleUIKit.ObjectAction;
import graphicalObjects_LayoutObjects.PanelLayoutGraphic;
import graphicalObjects_LayoutObjects.PlasticPanelLayoutGraphic;
import menuUtil.IndexChoiceMenu;
import objectDialogs.SpacedPanelLayoutBorder;

public class PlasticPanelLayoutPanelMenu extends LockedItemMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PlasticPanelLayoutPanelMenu(PlasticPanelLayoutGraphic c) {

		add(addCreatePanelMenu(c));
		
		super.setLockedItem(c);
		super.addLockedItemMenus();
		
		//add(createPanelSizeDefSubMenu(c));
		
		
	}
	
	JMenu createPanelSizeDefSubMenu(PanelLayoutGraphic c) {
		JMenu psize = new JMenu("Panel Size Definers");
		AddPanelSizeDefiningItemMenu def = new AddPanelSizeDefiningItemMenu(c, c.getPanelSizeDefiningItems());
		def.setRemove(true);
		psize.add(new AddPanelSizeDefiningItemMenu(c, c.getLockedItems()));
		psize.add(def);
		return psize;
		
		
	}
	
	JMenu addCreatePanelMenu(PlasticPanelLayoutGraphic c) {
		JMenu panelsmen = new JMenu("Panels");
		panelsmen.	add(new ObjectAction<PlasticPanelLayoutGraphic>(c) {
					@Override
					public void actionPerformed(ActionEvent arg0) {item.repack();item.repack();item.repack(); item.updateDisplay();}	
			}.createJMenuItem("Repack Panels"));
		
		panelsmen.add(new ObjectAction<PlasticPanelLayoutGraphic>(c) {
			@Override
			public void actionPerformed(ActionEvent arg0) {SpacedPanelLayoutBorder dialog = new SpacedPanelLayoutBorder(item);  dialog.showDialog(); }	
	}.createJMenuItem("Set Borders"));
		panelsmen.add(new ObjectAction<PlasticPanelLayoutGraphic>(c) {
			@Override
			public void actionPerformed(ActionEvent arg0) {item.getPanelLayout().addNewPanel(); item.updateDisplay();}	
	}.createJMenuItem("Add Panel"));
		try {
			panelsmen.add(new removalMenu(c));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		panelsmen.	add(new ObjectAction<PlasticPanelLayoutGraphic>(c) {
			@Override
			public void actionPerformed(ActionEvent arg0) {item.getPanelLayout().sortLeftToRightAboveToBottom(); item.updateDisplay();}	
	}.createJMenuItem("Resort Panels"));
		
		return panelsmen ;
	}
	
	public class removalMenu extends IndexChoiceMenu<PlasticPanelLayoutGraphic> {

		public removalMenu(PlasticPanelLayoutGraphic o) {
			super(o, "Remove Panel", 1, o.getPanelLayout().nPanels());

		}
		
		public void performAction(PlasticPanelLayoutGraphic t, int i) {
			t.getPanelLayout().removePanel(i);
			t.updateDisplay();
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		
	}
	
}
