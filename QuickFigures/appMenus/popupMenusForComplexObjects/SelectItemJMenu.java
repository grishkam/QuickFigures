package popupMenusForComplexObjects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JMenuItem;
import javax.swing.undo.AbstractUndoableEdit;

import genericMontageKit.BasicObjectListHandler;
import layersGUI.HasTreeLeafIcon;
import menuUtil.SmartJMenu;
import utilityClassesForObjects.LocatedObject2D;

public abstract class SelectItemJMenu extends SmartJMenu  implements ActionListener {
	protected ArrayList<LocatedObject2D> o=new ArrayList<LocatedObject2D>();
	protected ArrayList<JMenuItem> oi=new ArrayList<JMenuItem>();;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SelectItemJMenu(String st) {
		super(st);
	}

	/**When given a list of objects, generates a set of menu items with the object names and 
	  tree icons*/
	public void createMenuItemsForList(ArrayList<LocatedObject2D> arr) {
		for(LocatedObject2D l: arr) {
			if (l==null) continue;
			JMenuItem menuitem = new JMenuItem(l.toString()) ;
			menuitem.addActionListener(this);
			this.add(menuitem);
			if (l instanceof HasTreeLeafIcon) {
				HasTreeLeafIcon i=(HasTreeLeafIcon) l;
				menuitem.setIcon(i.getTreeIcon());
			}
			o.add(l);
			oi.add(menuitem);
		}
	}
	
	public void createMenuItemsForList2(Collection<?> localItems) {
		createMenuItemsForList(BasicObjectListHandler.getAs2DObjects( localItems));
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		int index = oi.indexOf(arg0.getSource());
		LocatedObject2D itemtoremove = o.get(index);
		addUndo(performAction(itemtoremove));
	}
	
	protected class ComplexAdder extends JMenuItem implements ActionListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private ArrayList<LocatedObject2D> list;
		
		public ComplexAdder(String name, ArrayList<LocatedObject2D> list) {
			this.setText(name);
			this.setActionCommand(name);
			this.list=list;
			this.addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			for(LocatedObject2D l:list) {
				performAction(l);
			}
			
		}}
	

	
	public abstract AbstractUndoableEdit performAction(LocatedObject2D target) ;
}
