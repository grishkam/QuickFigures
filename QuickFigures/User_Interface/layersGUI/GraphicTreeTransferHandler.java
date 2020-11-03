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

	public GraphicTreeTransferHandler(TransferHandler t) {
		super(t);
		
	}

	public GraphicTreeTransferHandler(String property) {
		super(property);
		// TODO Auto-generated constructor stub
	}

	public GraphicTreeTransferHandler(TransferHandler transferHandler, GraphicSetDisplayTree graphicSetDisplayTree) {
		this(transferHandler);
		this.setTheTree(graphicSetDisplayTree);
	}
	
	protected Transferable 	createTransferable(JComponent c) {
		return new treeTransferable(c);
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
		//return super.getDragImage();
	}
	

	public GraphicSetDisplayTree getTheTree() {
		return theTree;
	}

	public void setTheTree(GraphicSetDisplayTree theTree) {
		this.theTree = theTree;
	}


	public class treeTransferable implements Transferable {

		
		private JComponent component;

		public treeTransferable() {}
		public treeTransferable(JComponent c) {
			this.component=c;
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
			//if (DataFlavor.stringFlavor==theflavor) return true;
			if (selectedItemListFlavor==theflavor) return true;
			return false;
		}

		public GraphicSetDisplayTree getTheTree() {
			return theTree;
		}
	
	
	}
	

}
