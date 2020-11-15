package popupMenusForComplexObjects;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.undo.AbstractUndoableEdit;

import graphicalObjects.ImagePanelGraphic;
import graphicalObjects.ZoomableGraphic;
import graphicalObjects_BasicShapes.BarGraphic;
import menuUtil.SmartPopupJMenu;
import menuUtil.PopupMenuSupplier;
import undo.CombinedEdit;
import undo.UndoTakeLockedItem;
import utilityClasses1.ArraySorter;
import utilityClassesForObjects.LocatedObject2D;
import utilityClassesForObjects.LockedItemList;

public class BarGraphicMenu extends SmartPopupJMenu implements ActionListener,
PopupMenuSupplier  {

	/**
	 * 
	 */
	
	static final String options="Options";//, backGroundShap="Outline Shape";
	
	BarGraphic barG;
	public BarGraphicMenu(BarGraphic textG) {
		super();
		this.barG = textG;
		add(createItem(options));
		add(new TextGraphicMenu(textG.getBarText()).getJMenu("Bar Text"));
		add(new SwitchBarToOtherPanelMenu());
		
		//add(createItem(backGroundShap));
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
		// TODO Auto-generated method stub
		return this;
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		String com=arg0.getActionCommand();
		
		if (com.equals(options)) {
			barG.showOptionsDialog();
		}
		
	}
	
	class SwitchBarToOtherPanelMenu extends SelectItemJMenu {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		SwitchBarToOtherPanelMenu() {
			super("Switch Panels");
			ArrayList<ZoomableGraphic> localItems = barG.getParentLayer().getAllGraphics();
			ArraySorter.removeThoseNotOfClass(localItems, ImagePanelGraphic.class);//a bar graphic can only be attached to an image panel
			createMenuItemsForList2(localItems);
		}
		
		/**Adds bar to selected ImagePanel
		 * @return */
		@Override
		public AbstractUndoableEdit performAction(LocatedObject2D target) {
			ImagePanelGraphic image=(ImagePanelGraphic) target;
			CombinedEdit output = new CombinedEdit();
			
			LockedItemList.removeFromAlltakers( barG, barG.getParentLayer().getTopLevelParentLayer().getAllGraphics(), output);
			
			UndoTakeLockedItem undo = new UndoTakeLockedItem(image, barG, false);
	
			image.addLockedItem(barG);//does the deed
			
			undo.establishFinalState();
			output.addEditToList(undo);
			image.updateDisplay();
			return output;
		}
		}
	
}
