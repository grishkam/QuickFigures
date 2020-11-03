package layersGUI;

import javax.swing.Icon;
import javax.swing.tree.DefaultTreeCellRenderer;

import externalToolBar.IconSet;

public interface HasTreeBranchIcon {

	public Icon getTreeIcon(boolean open);
	
	static Icon defaultLeaf= new DefaultTreeCellRenderer().getDefaultOpenIcon();//new ImageIcon(new IconSet("iconsTree/TreeGraphicIcon.png").getIcon(0));
	static Icon defaultLeaf2= new DefaultTreeCellRenderer().getDefaultClosedIcon();
	static Icon defaultHiddenLeaf= new IconSet("iconsTree/RedX.png").getIcon(0);
	
	
}
