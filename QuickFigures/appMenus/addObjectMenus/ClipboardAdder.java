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
import logging.IssueLog;

public class ClipboardAdder extends FileImageAdder {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ClipboardAdder(boolean RGBint) {
		super(RGBint);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws UnsupportedFlavorException, IOException {
		 Transferable transferable =  Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
	        boolean imageSupported = transferable.isDataFlavorSupported(DataFlavor.imageFlavor);
	        if (imageSupported) {
	        	Object ob = transferable.getTransferData(DataFlavor.imageFlavor);
	        	if (ob!=null)IssueLog.log(" image flavor ");
	        }
	       /** for(DataFlavor flavor:transferable.getTransferDataFlavors())
	        		System.out.println(flavor+"");*/
	        try {
				DataFlavor rtflavor = new DataFlavor("text/rtf");
				boolean richTextSupported = transferable.isDataFlavorSupported(rtflavor);
				if (richTextSupported)IssueLog.log(" rich text  ");
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
		return "ClipImage"+bufferedImageGraphic;
	}

	@Override
	public String getMenuCommand() {
		// TODO Auto-generated method stub
		if (bufferedImageGraphic)return "Add RGB Image From System Clipboard";
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
	        	this.isImageMade=false;
	        }
	        
	      if (buf==null){
	        	this.isImageMade=false;
	        	return null;
	        			
	        } else {isImageMade=true;}
		
		if (bufferedImageGraphic) ag=new BufferedImageGraphic(buf);
		else  ag = new ImagePanelGraphic(buf);
		
		return ag;
	}
	
	
}
