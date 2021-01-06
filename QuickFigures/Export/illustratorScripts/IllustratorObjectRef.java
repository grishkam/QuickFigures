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
package illustratorScripts;

import java.awt.Color;
import java.awt.geom.Point2D;

import locatedObject.RectangleEdgePositions;

/**a java class that generates scripts to create and modify an object in 
adobe illustrator*/
public class IllustratorObjectRef implements RectangleEdgePositions{
	
	boolean addScripts=true;
	
	ZIllustratorScriptGenerator getGenerator() {
		return 	ZIllustratorScriptGenerator.instance;
	}
	
  void addScript(String... arg) {
	if (addScripts)  getGenerator().addScript(arg);
  }


  
	boolean set=false;
	public String refname="item"+ZIllustratorScriptGenerator.createRandom()+ZIllustratorScriptGenerator.createRandom();
	
	String getAssignment() {
		return "var "+refname+" =";
	}
	
	double invertY(double y) {
		return -y;
	}
	
	/**sets the top and left cordinate*/
	public String setLeftandTop(double left, double top, int xCorrection, int ycorrection) {
		left*=getGenerator().scale;
		top*=getGenerator().scale;
		left+=xCorrection+getGenerator().x0;
		top+=ycorrection;
		String output="";
		if (getGenerator().invertvertical) top=invertY(top);
		top+=+getGenerator().y0;
		output+=refname+".top="+top+";"+'\n';
		output+=refname+".left="+left+";"+'\n';
		addScript(output);
		return output;
	}
	public String setLeftandTop(double d, double d2) {
		return setLeftandTop(d,  d2, 0, 0);
	}
	
	String pointToJSarray(Point2D double1) {
		double x=double1.getX();
		double y=double1.getY();
		return pointToJSarray(x, y);
	}
	
	String pointToJSarray(double x, double y) {
		y*=getGenerator().scale;
		x*=getGenerator().scale;
		x+=getGenerator().x0;
		
		if (getGenerator().invertvertical) y=invertY(y);
		y+=getGenerator().y0;
		return "["+x+","+y+"]";
	}
	
	public String setStrokeColor(Color c) {
		if (c==null) return "";
		String output="var newRGBColor = new RGBColor();"+'\n';
		output+="newRGBColor.red="+c.getRed()+";"+'\n'; 
		output+="newRGBColor.green="+c.getGreen()+";"+'\n'; 
		output+="newRGBColor.blue="+c.getBlue()+";"+'\n'; 
		output+=refname+".strokeColor = newRGBColor;";
		addScript(output);
		return output;
	}
	
	public String setNoColorStroke() {
		String output=refname+".strokeColor = new NoColor();";
		addScript(output);
		return output;
	}
	
	public String setFillColor(Color c) {
		if (c==null) return "";
		String output="var newRGBColor = new RGBColor();"+'\n';
		output+="newRGBColor.red="+c.getRed()+";"+'\n'; 
		output+="newRGBColor.green="+c.getGreen()+";"+'\n'; 
		output+="newRGBColor.blue="+c.getBlue()+";"+'\n'; 
		output+=refname+".fillColor = newRGBColor";
		addScript(output);
		return output;
	}
	
	public String setNoColorFill() {
		String output=refname+".fillColor = new NoColor();";
		addScript(output);
		return output;
	}
	
	public String rotate(double angle, boolean changPositions, boolean changefill, boolean changeGradient, boolean changeStroke, String about) {
	String output=refname+".rotate("+angle+","+changPositions +","+changefill+","+changeGradient+","+changeStroke+","+about+");";
	addScript(output);
	return output;
	}
	
	public String rotate(double angle) {
		String output=refname+".rotate("+angle+");";
		addScript(output);
		return output;
		}
	
	public String translate(double x, double y) {
		String output=refname+".translate("+x+","+invertY(y)+");";
		addScript(output);
		return output;
		}
	
	static String startTry="try{";
	static String endTry="} catch (err) {}";
	
	public String startTryCatch() {
		String output="try{";
		addScript(output);
		return output;
		}
	public String endTryCatch() {
		String output="} catch (err) {}";
		addScript(output);
		return output;
		}
	
	public String scale(double amount) {
		return resize(amount, amount) ;
	}
	public String resize(double xamount, double yamount) {
		String output=refname+".resize("+xamount+","+yamount+");";
		addScript(output);
		return output;
		}
	
	
	
	public String setName(String name) {
		if (name.contains('\n'+"")) name=name.split(""+'\n')[0];
		String output=startTry+refname+".name='"+name+"';"+endTry;
		addScript(output);
		return output;
		}
	
	
	public String rotate(double angle, String about) {
		return rotate(angle, false, false, false, false, about);
		}
	
	public String rotate(double angle, int about) {
		return rotate(angle, getTransformation(about));
		}
	
	
	public static String getTransformation(int i) {
		String out="Transformation.";
		if (i==CENTER) {return out+"CENTER";}
		if (i==UPPER_LEFT) {return out+"TOPLEFT";}
		if (i==LEFT) {return out+"LEFT";}
		if (i==LOWER_LEFT) {return out+"BOTTOMLEFT";}
		if (i==UPPER_RIGHT) {return out+"TOPRIGHT";}
		if (i==RIGHT) {return out+"RIGHT";}
		if (i==LOWER_RIGHT) {return out+"BOTTOMRIGHT";}
		if (i==TOP) {return out+"TOP";}
		if (i==BOTTOM) {return out+"BOTTOM";}
		return out+"CENTER";
		
	}
	/**
	BOTTOM
	LEFT
	BOTTOMLEFT
	RIGHT
	BOTTOMRIGHT
	TOP
	CENTER
	TOPLEFT
	DOCUMENTORIGIN TOPRIGHT*/
	
	public String addTag(String name, String value) {
		TagRef tr=new TagRef();
		String output=tr.setToNewTag(this);
		tr.setName(name);
		tr.setValue(value);
		return output;
	}
	
}
