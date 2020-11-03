package graphicalObjects;

import java.awt.Graphics2D;
import java.io.File;

import javax.swing.Icon;
import javax.swing.tree.DefaultTreeCellRenderer;

import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import layersGUI.HasTreeLeafIcon;
import standardDialog.InfoDisplayPanel;
import standardDialog.StandardDialog;
import utilityClassesForObjects.PointsToFile;
import utilityClassesForObjects.ShowsOptionsDialog;

/**A reference to a specific file. Allows */
public class FileStandIn implements ZoomableGraphic, HasTreeLeafIcon, ShowsOptionsDialog, PointsToFile {

	
	private File file=null;
	
	
	public FileStandIn(File file) {
		this.setFile(file);
	}
	
	public String toString() {
		return getFile().getName();
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void draw(Graphics2D graphics, CordinateConverter<?> cords) {
		// TODO Auto-generated method stub

	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public Icon getTreeIcon() {
		return new DefaultTreeCellRenderer().getLeafIcon();
	}
	
	public static void addFileStandIn(File f, GraphicLayer cont) {
		if (f.isDirectory()) {
			GraphicLayerPane layer = new GraphicLayerPane(f.getName());
			cont.add(layer);
			for (File f2: f.listFiles()) {
				addFileStandIn(f2, layer);
			}
		} else
			cont.add(new FileStandIn(f));
	}

	@Override
	public void showOptionsDialog() {
		StandardDialog sd = new StandardDialog();
		sd.add("path", new InfoDisplayPanel("path", file.getAbsolutePath()));
		sd.add("exists", new InfoDisplayPanel("Does File Exists?", file.exists()? "Yes": "No"));
		
		sd.showDialog();
	}

	
	private transient GraphicLayer layer;
	@Override
	public GraphicLayer getParentLayer() {
		// TODO Auto-generated method stub
		return layer;
	}

	@Override
	public void setParentLayer(GraphicLayer parent) {
		layer=parent;
		
	}

}
