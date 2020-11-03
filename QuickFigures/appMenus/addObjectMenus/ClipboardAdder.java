package addObjectMenus;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.rtf.RTFEditorKit;

import graphicalObjects.BufferedImageGraphic;
import graphicalObjects.ImagePanelGraphic;

public class ClipboardAdder extends FileImageAdder {
	public ClipboardAdder(boolean RGBint) {
		super(RGBint);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws UnsupportedFlavorException, IOException {
		 Transferable transferable =  Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
	        boolean imageSupported = transferable.isDataFlavorSupported(DataFlavor.imageFlavor);
	        if (imageSupported) {
	        	Object ob = transferable.getTransferData(DataFlavor.imageFlavor);
	        	
	        }
	       /** for(DataFlavor flavor:transferable.getTransferDataFlavors())
	        		System.out.println(flavor+"");*/
	        try {
				DataFlavor rtflavor = new DataFlavor("text/rtf");
				boolean richTextSupported = transferable.isDataFlavorSupported(rtflavor);
				ByteArrayInputStream ob = (ByteArrayInputStream) transferable.getTransferData(rtflavor);
				//String s = new String(ob.readAllBytes());
				//String[] lines = s.split(""+'\t');
				
				DefaultStyledDocument doc = new DefaultStyledDocument();
				RTFEditorKit kit = new RTFEditorKit();
				kit.read(ob, doc, 0);
				
				
				
	        } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	}
	
	@Override
	public String getCommand() {
		// TODO Auto-generated method stub
		return "ClipImage"+rg;
	}

	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		if (rg)return "Add RGB Image From System Clipboard";
		else return "Paste From System Clipboard";
	}
	
	public ImagePanelGraphic getImage() {
		ImagePanelGraphic ag;
		 Transferable transferable =  Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
	     BufferedImage buf=null;  
		 boolean imageSupported = transferable.isDataFlavorSupported(DataFlavor.imageFlavor);
	        if (imageSupported) try {
	        	Object ob = transferable.getTransferData(DataFlavor.imageFlavor);
	        	
	        	if (ob instanceof BufferedImage) buf=(BufferedImage) ob;
	        } catch (Throwable t) {
	        	this.isImageMale=false;
	        }
	        
	      if (buf==null){
	        	this.isImageMale=false;
	        	return null;
	        			
	        } else {isImageMale=true;}
		
		if (rg) ag=new BufferedImageGraphic(buf);
		else  ag = new ImagePanelGraphic(buf);
		
		return ag;
	}
	
	
}
