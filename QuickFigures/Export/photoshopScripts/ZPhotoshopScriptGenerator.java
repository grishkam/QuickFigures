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
package photoshopScripts;


import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import figureFormat.DirectoryHandler;
import illustratorScripts.ZIllustratorScriptGenerator;


public class ZPhotoshopScriptGenerator {
	public static ZPhotoshopScriptGenerator instance=new ZPhotoshopScriptGenerator();
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
	public static void main(String [ ] args)
	{
	
	}
	
	
	public void execute() {
		//IssueLog.log(accumulatedscrip);
		savejsxAndRun(accumulatedscrip, DirectoryHandler.getDefaultHandler().getFigureFolderPath()+"/"+"output.jsx");
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
		//IssueLog.log2("will set photoshop color", output);
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
