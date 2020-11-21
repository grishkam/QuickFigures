package exportMenus;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import appContext.ImageDPIHandler;
import applicationAdapters.DisplayedImage;
import basicMenusForApp.BasicMenuItemForObj;
import graphicalObjects.BasicCoordinateConverter;
import graphicalObjects.FigureDisplayContainer;
import standardDialog.BooleanInputPanel;
import standardDialog.InfoDisplayPanel;
import standardDialog.NumberInputEvent;
import standardDialog.NumberInputListener;
import standardDialog.NumberInputPanel;
import standardDialog.StandardDialog;

/**Creates a buffered image for the display. that will be a snapshot of the figure display*/
public class FlatCreator extends BasicMenuItemForObj implements Transferable{

	
	private BufferedImage image;
	private boolean useTransparent=true;
	private FigureDisplayContainer cont;
	static	double ratio=1/ImageDPIHandler.ratioFor300DPI();//So the copied images can be 300ppi when in the equivalent dimensions
	
	public FlatCreator() {this(true);}
	public FlatCreator(boolean transparent) {setUseTransparent(transparent);}
	
	public BufferedImage createFlat(FigureDisplayContainer cont) {
		this.cont=cont;
		return createFlat();
	}
	
	public boolean writePNGFile(String newpath) throws IOException {
		ImageIO.write(createFlat(), "PNG", new File(newpath));
		return true;
	}
	
	/**creates a buffered image and draws all of the graphics on it*/
	public BufferedImage createFlat() {
		
		Dimension dim = cont.getCanvasDims();
		 BufferedImage img=new BufferedImage((int)(dim.width*ratio), (int)(dim.height*ratio),  BufferedImage.TYPE_INT_ARGB);
			Graphics g = img.getGraphics();
			g.setColor(Color.white);
			if (isUseTransparent()) g.setColor(new Color(255,255,255,00));
			
			g.fillRect(0, 0, img.getWidth(), img.getHeight());
		cont.getGraphicLayerSet().draw((Graphics2D) g, new BasicCoordinateConverter(0,0,ratio));
		
		return img;
	}
	
	/**creates a buffered image and draws all of the graphics on it. 
	 * copies the buffered image to the system clipboard*/
	public void toSystemClip(FigureDisplayContainer cont) {
		image = createFlat( cont) ;
		Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        clip.setContents(this, null);
	}

	
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { DataFlavor.imageFlavor };
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		// TODO Auto-generated method stub
		return image ;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		if (DataFlavor.imageFlavor ==flavor) return true;
		return false;
	}

	@Override
	public void performActionDisplayedImageWrapper(DisplayedImage diw) {
		toSystemClip(diw.getImageAsWrapper());
		
	}

	@Override
	public String getCommand() {
		return "Copy To System Clipboard";
	}

	@Override
	public String getNameText() {
		return "Copy To System Clipboard";
	}

	@Override
	public String getMenuPath() {
		return "Image";
	}
	
	public void showDialog() {new FlatDialog().showDialog();}
	
	public boolean isUseTransparent() {
		return useTransparent;
	}
	public void setUseTransparent(boolean useTransparent) {
		this.useTransparent = useTransparent;
	}

	class FlatDialog extends StandardDialog {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private InfoDisplayPanel sip;
		private NumberInputPanel nop;


		public FlatDialog() {
			super("Export Options");
			BooleanInputPanel bip = new BooleanInputPanel("Transpartent background?", isUseTransparent());
			this.add("transp" , bip);
			nop=new NumberInputPanel("Output PPI", ratio*72, 2 );
		//	sip= new InfoDisplayPanel("Output Size", "");
			
			nop.addNumberInputListener(new NumberInputListener() {
			
			
				@Override
				public void numberChanged(NumberInputEvent ne) {
					// TODO Auto-generated method stub
					if (ne.getSourcePanel()==nop)ratio=nop.getNumber()/72;
				//	if (cont!=null) sip.setContentText(" "+((int)cont.getCanvasDims().getWidth()*ratio)+" X "+(int)(cont.getCanvasDims().getHeight()*ratio));
				}});
			this.add("ratio" , nop);
			//this.add("size" , sip);
			super.setWindowCentered(true);
			this.setModal(true);
		}
		
		
		@Override
		public void afterEachItemChange() {
			setUseTransparent(this.getBoolean("transp"));
			
		}
	}
}
