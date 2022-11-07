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
/**
 * Author: Greg Mazo
 * Date Modified: April 11, 2021
 * Version: 2022.2
 */
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

/**this class is used to Serialize objects and write them to an output stream*/
public class GraphicEncoder {
	Object targetObject;
	String graphicpath=".gra";

	/**creates an encoder for the object*/
	public GraphicEncoder(Object g) {
		targetObject=g;
	}

	
	
	public Object getItemToBeEncoded() {
		if(targetObject!=null)
		return targetObject;
		
		return null;
	}
	
	/**writes the serializable object to the stream*/
	public void writeToOS(OutputStream os) {
		writeToOS(os, this.getItemToBeEncoded());
	}
	
	/**writes the object to an output stream*/
	public static void writeToOS(OutputStream os, Object o) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(os);
				oos.writeObject(o);
			
			oos.flush();
			
		} catch (Exception e) {
			IssueLog.logT(e);
		}
		
	}
	
	/**reads and object from an input stream*/
	public static Object readFromIS(InputStream os) {
		try {
			ObjectInputStream oos = new ObjectInputStream(os);
				Object object = oos.readObject();
			
			return object;
			
		} catch (Exception e) {
			IssueLog.logT(e);
		}
		return null;
	}
	

	/**writes the object to the file path given*/
	public void writeToFile(String file) {
		file=modifyString(file);
		
		//IssueLog.log("attempting to save graphics at ", file);
		try{
		FileOutputStream fo = new FileOutputStream(new File(file));
		writeToOS(fo);
		fo.flush();
		fo.close();
		} catch (Throwable T) {IssueLog.logT(T);}
	}
	
	/**reads object from the given file path and returns it was a layer
	 * @see GraphicLayerPane*/
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
			if (f==null||!f.exists()) {return output;}
			FileInputStream fo = new FileInputStream(f);
			Object ob = readFromIS(fo);
			output=ob;
			fo.close();
			} catch (Throwable T) {
				IssueLog.logT(T);
				IssueLog.log("Problem reading ");
			}
		
		return output;
	}
	
	/**reads a zoomable graphic from a file path*/
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
			} catch (Throwable T) {IssueLog.logT(T);}
		
		return output;
	}
	
	/**prompts the user to select a file to open and returns a zoomalbe graphic*/
	public ZoomableGraphic readFromUserSelectedFile() {
		return readGraphicFromFile(FileChoiceUtil.getOpenFile().getAbsolutePath());
	}
	
	/**adds an extension to the file*/
	String modifyString(String file) {
		if (file.endsWith(graphicpath)) return file;
		if (file.endsWith(".tif")) file=file.replace(".tif", graphicpath);
		else file+=graphicpath;
		return file;
	}
	
	/**returns as a byte array transformed into a string*/
	public String getBytes() {
		ByteArrayOutputStream boo = new ByteArrayOutputStream();
		writeToOS(boo);
		try {
			boo.flush();
		
		boo.close();
		} catch (IOException e) {
			IssueLog.logT(e);
		}
		return boo.toString();
	}
}
