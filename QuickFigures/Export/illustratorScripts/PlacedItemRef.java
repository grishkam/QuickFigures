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
 * Date Modified: Mar 5, 2021
 * Version: 2021.2
 */
package illustratorScripts;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import figureFormat.DirectoryHandler;
import logging.IssueLog;

/**a java class that generates scripts to create and modify a placed item object in 
adobe illustrator*/
public class PlacedItemRef extends IllustratorObjectRef{
	/**
	public static String pathOfDocumentsFolder() {
		String docfolder=new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
		IssueLog.log("home is "+ new JFileChooser().getFileSystemView().getHomeDirectory());
		if (IssueLog.isWindows() && docfolder.contains("Documents")) return docfolder;
		return new JFileChooser().getFileSystemView().getDefaultDirectory().toString()+"/Documents";
	}
	*/
	
	
	/**when given a referance to an illustrator object with a pathitems collection, creates a script to 
	 att a new pathitem*/
	public String createItem(IllustratorObjectRef layer) {
		set=true;
		String output="";
		output+='\n'+"var "+refname+" ="+layer.refname+".placedItems.add();";
		addScript(output);
		return output;
	}
	
	String setToFile(String path) {
		super.startTryCatch();
		
		String output = createFileRef(path);
		
		output+=refname+".file=fileRef;";
		addScript(output);
		
		super.endTryCatch("'Cannot find file'+"+"fileRef.toString");
		return output;
	}


	

	public static String trimfilename(String name ) {
		try {
			if (name==null) name="_nameless_";
			if (name.contains(";"))name=name.split(";")[0];
			if (name.contains("."))name=name.split(".")[0];} catch (java.lang.ArrayIndexOutOfBoundsException aio) {}
			return name;
	}

	
	/**This code saves an image as a file and returns a script to paste the 
	   file contents into a specified position in illustrator.*/
	public  File prepareImageForJavaScript(Image colorProcessor, String name, double x, double y, boolean link) {
		
		File f=prepareFile(colorProcessor, getGenerator().getPathOfImages()+ name);
		String absolutePath = f.getAbsolutePath();
			setToFile(absolutePath);
		if (getGenerator().scale!=1) {
			
			scale(getGenerator().scale*100);
			}
		setLeftandTop(x, y); 
		
		if (getGenerator().deleteonExit) f.deleteOnExit();
		return f;
	}
	
	/**This code saves an image as a file and returns a script to paste the 
	   file contents into a specified position in illustrator.
	public  File prepareImageForJavaScript(Image ip, String name, double x, double y, boolean link) {
		return prepareImageForJavaScript(new  ColorProcessor(ip),  name, x, y, link);
	}*/
	
	public String embed() {
		String output = refname+".embed();";
		addScript(output);
		return output;
	}
	
	public void prepareFiles() {}
	
	
	static String createTerminatedString(String name) {
		name=trimfilename(name);
		if (name.contains(""+'\n')) { 
		   name=name.split('\n'+"")[0];
		}
		
		return name;
	}
	
	/**prepared and ij1 image processor for placement into illustrator*/
	public static File prepareFile(Image colorProcessor, String name) {
		
		try {
		name=createTerminatedString(name);
		
		//+ZIllustratorScriptGenerator.createRandom();
		//ImagePlus imp = new ImagePlus(name, colorProcessor);
		
		
		/**
		imp.getFileInfo().pixelDepth=300;
		imp.getFileInfo().unit="inch";
		imp.getFileInfo().pixelWidth=((double) 1)/300;
		imp.getFileInfo().pixelHeight=((double) 1)/300;
		FileSaver saved=new FileSaver(imp) ;
		*/
		if (name.contains(":")) {name=name.replace(":", " ");}
		if (name.contains("/")) {name=name.replace("/", " ");}
		if (name.contains("!")) {name=name.replace("!", " ");}
		if (name.contains("|")) {name=name.replace("|", " ");}
		if (name.contains("*")) {name=name.replace("*", " ");}
		if (name.contains("?")) {name=name.replace("?", " ");}
		if (name.contains(">")) {name=name.replace(">", " ");}
		if (name.contains("<")) {name=name.replace("<", " ");}
		if (name.contains(".zvi")) {name=name.replace(" zvi", " ");}
		
		String directory=DirectoryHandler.getDefaultHandler().getTempFolderPath()+"/"+name+""+((int)(1000000000*Math.random()))+".png";
		 
		File f=(new File(directory)); 
		
		f.mkdirs();//in the event that the directory for the file is absent creates it
		
		RenderedImage rd = (RenderedImage) colorProcessor;
		
		IssueLog.log("Trying to save file "+rd);
		IssueLog.log("as "+directory);
		if (rd.getWidth()==0 ||rd.getHeight()==0) {
			IssueLog.log("problem with image file "+directory);
			
		}
		if (f==null || rd==null) 
			{IssueLog.log("problem with savind of file "+directory);}
			
		ImageIO.write(rd, "PNG",f);
			
			return f;
		} catch (IOException e) {
			IssueLog.logT(e);
			return null;
		}
		
	
	}
	

}
