package externalToolBar;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

public class IconWrappingToolIcon  extends GraphicToolIcon{

	private Icon treeIcon;

	public IconWrappingToolIcon(Icon treeIcon, int type) {
		super(type);
		// TODO Auto-generated constructor stub
		this.treeIcon = treeIcon;
	}
	
	protected void paintObjectOntoIcon(Component arg0, Graphics g, int arg2,
			int arg3) {
		treeIcon.paintIcon(arg0, g, arg2+2, arg3+2 );
		
	}
	
	public static IconSet createIconSet(Icon treeIcon) {
			return	new IconSet(
						new IconWrappingToolIcon(treeIcon, 0),
						new IconWrappingToolIcon(treeIcon, 1),
						new IconWrappingToolIcon(treeIcon, 2)
						
						);}
			
	public static IconSet createIconSet(Icon treeIcon0, Icon treeIcon1) {
			return	new IconSet(
						new IconWrappingToolIcon(treeIcon0, 0),
						new IconWrappingToolIcon(treeIcon1, 1),
						new IconWrappingToolIcon(treeIcon1, 2)
						);}
	

}
