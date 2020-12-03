package layersGUI;

import javax.swing.tree.TreeCellRenderer;

/**Any objects that have unique tree cells can implement this interface to ensure they are drawn correctly*/
public interface HasOwnCellRenderer {
	
	public TreeCellRenderer getCellRenderer();
}
