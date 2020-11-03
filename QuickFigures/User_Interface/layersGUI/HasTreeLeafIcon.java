package layersGUI;

import javax.swing.Icon;

import externalToolBar.IconSet;

public interface HasTreeLeafIcon {

	public Icon getTreeIcon();
	
	static Icon defaultLeaf=new IconSet("iconsTree/TreeGraphicIcon.png").getIcon(0);
	static Icon defaultHiddenLeaf= new IconSet("iconsTree/RedX.png").getIcon(0);
}
