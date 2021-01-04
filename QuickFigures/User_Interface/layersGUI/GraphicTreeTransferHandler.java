/*******************************************************************************
 * Copyright (c) 2021 Gregory Mazo
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
package layersGUI;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.TransferHandler;

import graphicalObjects.ZoomableGraphic;

/**Work in progress. a transfer handler for drag and drops on trees*/
public class GraphicTreeTransferHandler extends WrappingTransferHandler {

	public static DataFlavor selectedItemListFlavor=new DataFlavor(ArrayList.class, "ArrayList"); {
		try {
			selectedItemListFlavor=new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType);
			
		} catch (Throwable t) {}
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GraphicSetDisplayTree theTree;

	private GraphicTreeTransferHandler(TransferHandler t) {
		super(t);
		
	}

	private GraphicTreeTransferHandler(String property) {
		super(property);
	
	}

	public GraphicTreeTransferHandler(TransferHandler transferHandler, GraphicSetDisplayTree graphicSetDisplayTree) {
		this(transferHandler);
		this.setTheTree(graphicSetDisplayTree);
	}
	
	protected Transferable 	createTransferable(JComponent c) {
		return new TreeTransferableItem(c);
	}
	
	public	Icon 	getVisualRepresentation(Transferable t) {
		ArrayList<ZoomableGraphic> items = getTheTree().getSelecteditems();
	
		return getIconForGraphic(items.get(0));
		
		}
	
	public Icon getIconForGraphic(ZoomableGraphic item) {
		if (item instanceof HasTreeLeafIcon) {
			return ((HasTreeLeafIcon)item).getTreeIcon();
		}
		
		if (item instanceof HasTreeBranchIcon) {
			return ((HasTreeBranchIcon)item).getTreeIcon(false);
		}
		
		return super.getVisualRepresentation(null);
	}
	
	
	public 	Image 	getDragImage() {
		
		ArrayList<ZoomableGraphic> items = getTheTree().getSelecteditems();
		
		int size=80+items.size()*4;
		
		BufferedImage img=new BufferedImage(size, size,  BufferedImage.TYPE_INT_RGB);
			Graphics2D g = (Graphics2D) img.getGraphics();
			g.setColor(Color.white);
			g.fillRect(0, 0, 150, 150);
			g.setTransform(AffineTransform.getScaleInstance(4, 4));
		
			for(int index=0; index<items.size(); index++) {
				Icon  icon=getIconForGraphic(items.get(index));
			//icon = this.getVisualRepresentation(null);
				icon.paintIcon(new JPanel(), g, 2+index*4, 2+index*4);
			
			}
		
		return img;
	
	}
	

	public GraphicSetDisplayTree getTheTree() {
		return theTree;
	}

	public void setTheTree(GraphicSetDisplayTree theTree) {
		this.theTree = theTree;
	}


	public class TreeTransferableItem implements Transferable {

		
		

		public TreeTransferableItem() {}
		public TreeTransferableItem(JComponent c) {
			
		}
		@Override
		public Object getTransferData(DataFlavor theflavor) throws UnsupportedFlavorException, IOException {
			
			if (selectedItemListFlavor.equals(theflavor)) return getTheTree();
			return null;
		}
		
	

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { selectedItemListFlavor};
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor theflavor) {
			if (selectedItemListFlavor==theflavor) return true;
			return false;
		}

		public GraphicSetDisplayTree getTheTree() {
			return theTree;
		}
	
	
	}
	

}
