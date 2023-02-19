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
 * Date Modified: Jan 12, 2021
 * Version: 2023.1
 */
package imageDisplayApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import graphicalObjects_LayerTypes.GraphicLayerPane;
import logging.IssueLog;
import ultilInputOutput.FileChoiceUtil;
import ultilInputOutput.FileFinder;

/**Serializes all objects in a worksheet and saves to a file. 
  De-serializes those figures and creates figure display windows.
  although this method of saving is sensitive to changes in the
  code, it is also the most effective to implement. (Without need for devising
  an entirely new file format)*/
public class ImageDisplayIO {
	public static StandardWorksheet readFromFile(File f) {
		FileInputStream fo;
		StandardWorksheet output=null;
		FileFinder.setWorkingDir(f);
		
		if (!f.exists()) {
			IssueLog.log("file"+f+" is non existent");
		}
		try {
			fo = new FileInputStream(f);
			ObjectInputStream oos = new ObjectInputStream(fo);
			Object o1=oos.readObject();
			Object o2=oos.readObject();
			
			
			//("read objects from file and will try to use them "+'\n'+o1.getClass()+" "+o2.getClass());
			if (o1 instanceof GraphicLayerPane&& o2 instanceof BasicImageInfo) {
				 GraphicLayerPane g=(GraphicLayerPane) o1;
				 BasicImageInfo b=(BasicImageInfo) o2;
				 
				output= new StandardWorksheet(g,b);
				output.onItemLoad(output.getLayer());
			}
			
		
			fo.close();
			//return true;
		} catch (Exception e) {
			if (e instanceof java.lang.ClassNotFoundException) {
				FileChoiceUtil.OkOrNo("Class not found. "+"File likely saved with earlier version");
			}
			if (e instanceof java.io.StreamCorruptedException) {
				FileChoiceUtil.OkOrNo("File type wrong "+" cannot read that file");
			}
			IssueLog.logT(e);;
			return null;
		}
		
		output.setSavePath(f.getAbsolutePath());
		String name = f.getName();
		if(name.endsWith(".ser")) name=name.replace(".ser", "");
		output.setTitle(name);
		
		return output;
		
		
	}
	
	
	
	/**saves the worksheet to a file*/
	public static boolean writeToFile(File f, StandardWorksheet theSet) {
		FileOutputStream fo;
		try {
			fo = new FileOutputStream(f);
			
			ObjectOutputStream oos = new ObjectOutputStream(fo);
			
			oos.writeObject(theSet.getLayer());
			oos.writeObject(theSet.getBasics());
			
		oos.flush();
		fo.close();
			return true;
		} catch (Exception e) {
			IssueLog.log("problem occued when saving ");
			IssueLog.logT(e);
			return false;
		}
		
		
	}
	
	/**opens a saved worksheet*/
	public static ImageWindowAndDisplaySet showFile(File f) {
		if (f==null) return null;
			StandardWorksheet set = ImageDisplayIO.readFromFile(f);
			if (set==null) return null;
			return new ImageWindowAndDisplaySet(set);
	}
	

}
