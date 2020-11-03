package graphicalObjects;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import graphicalObjects_LayerTypes.GraphicLayerPane;
import logging.IssueLog;
import ultilInputOutput.FileChoiceUtil;

public class GraphicEncoder {
	Object gc;

	
	public GraphicEncoder(Object g) {
		gc=g;
	}

	
	
	public Object getItemToBeEncoded() {
		if(gc!=null)
		return gc;
		
		return null;
	}
	
	public void writeToOS(OutputStream os) {
		/**try {
			ObjectOutputStream oos = new ObjectOutputStream(os);
				oos.writeObject(getItemToBeEncoded());
			
			oos.flush();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		writeToOS(os, this.getItemToBeEncoded());
	}
	
	public static void writeToOS(OutputStream os, Object o) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(os);
				oos.writeObject(o);
			
			oos.flush();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static Object readFromIS(InputStream os) {
		try {
			ObjectInputStream oos = new ObjectInputStream(os);
				Object object = oos.readObject();
			
			return object;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	String graphicpath=".gra";
	
	public void writeToFile(String file) {
		file=modifyString(file);
		
		//IssueLog.log("attempting to save graphics at ", file);
		try{
		FileOutputStream fo = new FileOutputStream(new File(file));
		writeToOS(fo);
		fo.flush();
		fo.close();
		} catch (Throwable T) {IssueLog.log(T);}
	}
	
	public  GraphicLayerPane readFromFile(String file) {
		GraphicLayerPane output=null;
		file=modifyString(file);
		Object object = readObjectFromFile(file);
		if (object  instanceof GraphicLayerPane) return (GraphicLayerPane) object;
		return output;
	}
	
	public static Object readObjectFromFile(String file) {
		return readObjectFromFile(new File(file));
	}
	
	public static Object readObjectFromFile(File f) {
		Object output=null;
		
		//IssueLog.log("attempting to read graphics at ", file);
		try{
			if (!f.exists()) {return output;}
			FileInputStream fo = new FileInputStream(f);
			Object ob = readFromIS(fo);
			output=ob;
			fo.close();
			} catch (Throwable T) {
				IssueLog.log(T);
				IssueLog.log("Problem reading ");
			}
		
		return output;
	}
	
	public  ZoomableGraphic readGraphicFromFile(String file) {
		ZoomableGraphic output=null;
		file=modifyString(file);
		//IssueLog.log("attempting to read graphics at ", file);
		try{
			File f = new File(file);
			if (!f.exists()) {return output;}
			FileInputStream fo = new FileInputStream(f);
			Object ob = readFromIS(fo);
			if (ob instanceof ZoomableGraphic) {output= (ZoomableGraphic) ob;}
			fo.close();
			} catch (Throwable T) {IssueLog.log(T);}
		
		return output;
	}
	
	public ZoomableGraphic readFromUserSelectedFile() {
		return readGraphicFromFile(FileChoiceUtil.getOpenFile().getAbsolutePath());
	}
	
	String modifyString(String file) {
		if (file.endsWith(graphicpath)) return file;
		if (file.endsWith(".tif")) file=file.replace(".tif", graphicpath);
		else file+=graphicpath;
		return file;
	}
	
	public String getBytes() {
		ByteArrayOutputStream boo = new ByteArrayOutputStream();
		writeToOS(boo);
		try {
			boo.flush();
		
		boo.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return boo.toString();
	}
}
