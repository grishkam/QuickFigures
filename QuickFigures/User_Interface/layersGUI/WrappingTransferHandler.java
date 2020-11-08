package layersGUI;

import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

public class WrappingTransferHandler extends TransferHandler {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TransferHandler transfer;

	public WrappingTransferHandler( TransferHandler t) {
		this.transfer=t;
	}

	public WrappingTransferHandler(String property) {
		super(property);
		// TODO Auto-generated constructor stub
	}
	
	public 	Image 	getDragImage() {return transfer.getDragImage();}
	public void 	setDragImage(Image img){transfer.setDragImage(img);}
	
	public Point 	getDragImageOffset() {return transfer.getDragImageOffset();}

	
	public 	void 	setDragImageOffset(Point p){transfer.setDragImageOffset(p);}
	
	
 	public int getSourceActions(JComponent c) { return transfer.getSourceActions(c);}
	public	Icon 	getVisualRepresentation(Transferable t) {return transfer.getVisualRepresentation(t);}
	
 	public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {return transfer.canImport(comp, transferFlavors);}

 	public boolean 	canImport(TransferHandler.TransferSupport support) {return transfer.canImport(support);}
	
 	
 	public void exportAsDrag(JComponent source, InputEvent data, int action) {transfer.exportAsDrag(source, data, action);}
	public boolean  	importData(TransferHandler.TransferSupport support) {return transfer.importData(support);}
 	public boolean importData(JComponent comp, Transferable t) {return transfer.importData(comp, t);}
 	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	
	protected Transferable 	createTransferable(JComponent c) {
		return new basicTransferable();
	}
	
	class basicTransferable implements Transferable {

		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			// TODO Auto-generated method stub
			return "Hello";
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] {DataFlavor.stringFlavor};
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			if (DataFlavor.stringFlavor==flavor) return true;
			return false;
		}}

}