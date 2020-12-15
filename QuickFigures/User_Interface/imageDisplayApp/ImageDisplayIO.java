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

/**Serializes figures and saves to a file. 
  De-serializes those figures and creates figure display windows*/
public class ImageDisplayIO {
	public static GraphicContainingImage readFromFile(File f) {
		//GraphicEncoder encoder = new GraphicEncoder(theSet.getGraphicLayerSet());
		FileInputStream fo;
		GraphicContainingImage output=null;
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
				 
				output= new GraphicContainingImage(g,b);
				output.onItemLoad(output.getLayer());
			}
			
		
			fo.close();
			//return true;
		} catch (Exception e) {
			if (e instanceof java.lang.ClassNotFoundException) {
				FileChoiceUtil.OkOrNo("Class not found. "+"File likely saved with earlier version");
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
	
	
	

	public static boolean writeToFile(File f, GraphicContainingImage theSet) {
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
	
	public static ImageWindowAndDisplaySet showFile(File f) {
		if (f==null) return null;
			GraphicContainingImage set = ImageDisplayIO.readFromFile(f);
			if (set==null) return null;
			return new ImageWindowAndDisplaySet(set);
	}
	

}
