package ultilInputOutput;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import logging.IssueLog;

public class ForDragAndDrop {
	
	
	/**returns a list of files that were dropped on a GUI.*/
	public static ArrayList<File> dropedFiles(DropTargetDropEvent dtde) {
		return dropedFiles(dtde, false);
	}
	
	public static void listDataFlavors(DropTargetDropEvent dtde) {
		DataFlavor[] flavors = dtde.getTransferable().getTransferDataFlavors();
		for( DataFlavor flavor: flavors) {
			
			IssueLog.log(flavor.getMimeType());
			
			if (flavor.isFlavorTextType()) try {
				IssueLog.log(""+dtde.getTransferable().getTransferData(DataFlavor.stringFlavor));
			} catch (Throwable t) {}
			
		}
	}
	
	/**returns a list of files that were dropped on a GUI.*/
	public static ArrayList<File> dropedFiles(DropTargetDropEvent dtde, boolean includeFolders) {
		if(!dtde.getTransferable().isDataFlavorSupported(DataFlavor.javaFileListFlavor))  { return null;}
		ArrayList<File> output = new ArrayList<File>();
		// TODO Auto-generated method stub
	    dtde.acceptDrop(DnDConstants.ACTION_COPY);
	    
		    Transferable t = dtde.getTransferable();
		    if(dtde.getTransferable().isDataFlavorSupported(DataFlavor.javaFileListFlavor))
		    {
		    	List<?> data;
				try {
					data = (List<?>)t.getTransferData(DataFlavor.javaFileListFlavor);
					for (Object data2: data) {
		    		 File file= (File) data2;
					   if( !file.isDirectory() ||includeFolders)  // don't handle directories unless asked to 
					       output.add(new File(file.getAbsolutePath()));
		    	}
				} catch (UnsupportedFlavorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
		    	
		    }
	    

	    dtde.dropComplete(true);
	    return output;
	}
	
	/**returns a list of files that were dropped on a GUI.*/
	public static ArrayList<File> dropedFolders(DropTargetDropEvent dtde) {
		if(!dtde.getTransferable().isDataFlavorSupported(DataFlavor.javaFileListFlavor))  { return null;}
		ArrayList<File> output = new ArrayList<File>();
	    dtde.acceptDrop(DnDConstants.ACTION_COPY);
	    
		    Transferable t = dtde.getTransferable();
		    if(dtde.getTransferable().isDataFlavorSupported(DataFlavor.javaFileListFlavor))
		    {
		    	List<?> data;
				try {
					data = (List<?>)t.getTransferData(DataFlavor.javaFileListFlavor);
					for (Object data2: data) {
		    		 File file= (File) data2;
					   if( file.isDirectory() )  // don't handle directories for now
					       output.add(new File(file.getAbsolutePath()));
		    	}
				} catch (UnsupportedFlavorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
		    	
		    }
	    

	    dtde.dropComplete(true);
	    return output;
	}


	public static String getExtension(File f) {
		String output="null";
		String apath=f.getAbsolutePath();
		String[] parts = apath.split("\\.");
		if (parts.length>1) output= parts[parts.length-1].toLowerCase();
		return output;
	}
	
	
}
