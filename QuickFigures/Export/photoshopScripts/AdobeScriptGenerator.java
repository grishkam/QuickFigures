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
 * Date Modified: Jan 6, 2021
 * Version: 2021.1
 */
package photoshopScripts;


import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import figureFormat.DirectoryHandler;
import illustratorScripts.ZIllustratorScriptGenerator;

/**this class generates text that can be run in adobe's extended script toolkit for illustrator or poerpoint java script*/
public class AdobeScriptGenerator {
	
	public static String outputFile = "output.jsx";
	
	public static AdobeScriptGenerator instance=new AdobeScriptGenerator();
	double x0=0;
	double y0=0;
	double scale=1;
	
	public double getScale() {
		return scale;
	}
	public void setScale(double d) {scale=d;}
	
	public String accumulatedscrip="";
	String pathOfImages="";
	boolean deleteonExit=true;
	
	public void setZero(int x, int y) {
		x0=x*scale; y0=y*scale;
	//	IJ.log("Zero is "+x+", "+y);
	}
	
	/**Generates a random integer*/
	static int createRandom() {
		return ((int)(10000*Math.random()));
	}
	
	void addScript(String... arg) {
		for (String st: arg) {
			accumulatedscrip+='\n'+st;
		}
	}
	
	
	public void execute() {
		//IssueLog.log(accumulatedscrip);
		savejsxAndRun(accumulatedscrip, DirectoryHandler.getDefaultHandler().getFigureFolderPath()+"/"+outputFile);
		accumulatedscrip="";
	}
	public  static void savejsxAndRun(String javascript, String directoryJSX){
		ZIllustratorScriptGenerator.saveString("#target photoshop"+'\n'+javascript, directoryJSX, false);
		try {Desktop.getDesktop().open(new File(directoryJSX));} catch (IOException e) {}
	}
	
	
	public  String setPSfgColor(Color c){
		if (c==null) return "";
		
		String output="app.foregroundColor.rgb.red="+c.getRed()+'\n';
		output+="app.foregroundColor.rgb.green="+c.getGreen()+'\n';
		output+="app.foregroundColor.rgb.blue="+c.getBlue()+'\n';
		addScript(output);
		
		return output;
	}
	public  String setPSbgColor(Color c){
		if (c==null) return "";
		String output="app.backgroundColor.rgb.red="+c.getRed()+'\n';
		output+="app.backgroundColor.rgb.green="+c.getGreen()+'\n';
		output+="app.backgroundColor.rgb.blue="+c.getBlue()+'\n';
		addScript(output);
		return output;
	}
	
	
}
