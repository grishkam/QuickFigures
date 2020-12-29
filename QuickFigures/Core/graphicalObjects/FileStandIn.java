/*******************************************************************************
 * Copyright (c) 2020 Gregory Mazo
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package graphicalObjects;

import java.awt.Graphics2D;
import java.io.File;

import javax.swing.Icon;
import javax.swing.tree.DefaultTreeCellRenderer;

import graphicalObjects_LayerTypes.GraphicLayer;
import graphicalObjects_LayerTypes.GraphicLayerPane;
import layersGUI.HasTreeLeafIcon;
import standardDialog.StandardDialog;
import standardDialog.strings.InfoDisplayPanel;
import utilityClassesForObjects.PointsToFile;
import utilityClassesForObjects.ShowsOptionsDialog;

/**A reference to a specific file. Allow the user to input a list of files into the JTree*/
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
	public void draw(Graphics2D graphics, CordinateConverter cords) {
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
