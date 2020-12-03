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
package applicationAdapters;

import java.io.File;

import javax.swing.JFrame;

import figureFormat.DirectoryHandler;
import imageDisplayApp.ImageWindowAndDisplaySet;
import imageDisplayApp.ImageDisplayIO;
import includedToolbars.ActionToolset1;
import includedToolbars.LayoutToolSet;
import includedToolbars.ObjectToolset1;
import layersGUI.GraphicTreeUI;
import logging.IssueLog;

public class ToolbarTester {

	public static void main(String[] args) {
		IssueLog.sytemprint=true;
		startToolbars(true);
	}
	
	public static void startToolbars(boolean appclose) {
		
		ObjectToolset1 toolset = showInnitial();
		if (appclose) {
			toolset.getframe().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
		
		}
	
	public static ObjectToolset1 showInnitial() {
		return  showToolSet();
	}
	
	/**shows both object and layout toolsets*/
	public static ObjectToolset1 showToolSet() {
		ObjectToolset1 ot = new ObjectToolset1();
		new LayoutToolSet().run("");
		new ActionToolset1().run("go");
		ot.run("hi");
		return ot;
	}

	
	
	public static ImageWindowAndDisplaySet showExample(boolean appclose) {
		startToolbars(appclose);
		 
		
		File example=new File(new DirectoryHandler().getFigureFolderPath()+"/example");
		
		if (example.exists()) {
		 try {
			return setSavedExample(example);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		} 
		{
			 IssueLog.log("Example File not found "+'\n'+example.getAbsolutePath());
			 return ImageWindowAndDisplaySet.createAndShowNew("Figure", 400,300);
		 }
		
		
		
		
		
	}

	private static ImageWindowAndDisplaySet setSavedExample(File example) {
		ImageWindowAndDisplaySet file = ImageDisplayIO.showFile(example);
		 ImageWindowAndDisplaySet.exampletree = new GraphicTreeUI(file.getImageAsWrapper());
		
		 ImageWindowAndDisplaySet. exampletree  .showTreeForLayerSet(file.getImageAsWrapper()) ;
		 
		//locatedObject object0 = file.getImageAsWrapper().getLocatedObjects().get(0);
		//PathGraphic p2=(PathGraphic) object0;
		//file.getImageAsWrapper().getGraphicLayerSet().add(p2.copy());
		//p2.setStrokeColor(Color.orange);
		//p2.getPoints().cullPointAndAdjustCurvature(p2.getPoints().get(1));
		 
		 
		 
		 return file;
	}
}
