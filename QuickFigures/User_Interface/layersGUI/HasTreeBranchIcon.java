package layersGUI;

import javax.swing.Icon;
import javax.swing.tree.DefaultTreeCellRenderer;


/**Objects that have their own unique tree branch icons can implement this interface*/
public interface HasTreeBranchIcon {

	public Icon getTreeIcon(boolean open);
	
	static Icon defaultLeaf= new DefaultTreeCellRenderer().getDefaultOpenIcon();//new ImageIcon(new IconSet("iconsTree/TreeGraphicIcon.png").getIcon(0));
	static Icon defaultLeaf2= new DefaultTreeCellRenderer().getDefaultClosedIcon();
	
	
}
