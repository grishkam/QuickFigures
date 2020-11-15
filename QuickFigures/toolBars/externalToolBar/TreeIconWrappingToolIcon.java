package externalToolBar;

import java.awt.Component;
import java.awt.Graphics;

import layersGUI.HasTreeLeafIcon;

public class TreeIconWrappingToolIcon  extends GraphicToolIcon{

	private HasTreeLeafIcon treeIcon;

	public TreeIconWrappingToolIcon(HasTreeLeafIcon treeIcon, int type) {
		super(type);
		// TODO Auto-generated constructor stub
		this.treeIcon = treeIcon;
	}
	
	protected void paintObjectOntoIcon(Component arg0, Graphics g, int arg2,
			int arg3) {
		treeIcon.getTreeIcon().paintIcon(arg0, g, arg2+5, arg3+5 );
		
	}
	
	public static IconSet createIconSet(HasTreeLeafIcon treeIcon) {
	return	new IconSet(
				new TreeIconWrappingToolIcon(treeIcon, 0),
				new TreeIconWrappingToolIcon(treeIcon, 1),
				new TreeIconWrappingToolIcon(treeIcon, 2)
				
				);
	
		
	}

	@Override
	public
	GraphicToolIcon copy(int type) {
		return new TreeIconWrappingToolIcon(treeIcon, type);
	}

}
