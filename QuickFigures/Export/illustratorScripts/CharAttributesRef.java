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
 * Version: 2021.1
 */
package illustratorScripts;

import java.awt.Font;

/**a java class that generates scripts to create and modify a character attributes object in 
  adobe illustrator*/
public class CharAttributesRef extends IllustratorObjectRef {
	
	String setfont(String font) {
		if (font.equals("SansSerif")) font="SerifBold";
		String output="try{";
		output+=refname+".textFont = app.textFonts.getByName('"+font+"');} catch (err) {"
				+ findFont(font)+"}";
		addScript(output);
		return output;
	}
	
	private String findFont(String fullname) {
		String name=fullname.split(" ")[0].toLowerCase();
		String style=fullname.split(" ")[1].toLowerCase();
		String output="try{";
		output+="var iCount=app.textFonts.length;";
		output+="var textFonts=app.textFonts;";
		output+="for(var i=0; i<iCount; i++){";
		output+="var aFontName=textFonts[i].family.toLowerCase();";
		output+="var aFontStyle=textFonts[i].style.toLowerCase();";
		 output+="try{";
		output+="var found=true;";
		output+="if ((aFontStyle==='" +style +"') & (aFontName==="+"'"+name.toLowerCase()+"')) {"+refname+".textFont=textFonts[i];"+"}";
		
		output+="} catch (err) {alert('failed to find font '+i+aFontName+err);}";
		
		//output+="if (i=="+5+")"+"alert('looking for font'+'"+name+"');";
		//output+="if (i<"+5+")"+"alert('looking at font'+aFontName);";
		output+="};";
		
		output+="} catch (err) {alert('failed to find font');}";
		return output;
	}
	
	public String setStrikeThrough(boolean strike) {
		String output="try{";
		output+=refname+".strikeThrough = true;} catch (err) {}";
		addScript(output);
		return output;
	}
	
	public String setUnderline(boolean strike) {
		String output="try{";
		output+=refname+".underline = true;} catch (err) {}";
		addScript(output);
		return output;
	}
	
	/**makes the section of text into a superscript*/
	public String setSuperScript() {
		String output="try{";
		output+=refname+".baselinePosition = FontBaselineOption.SUPERSCRIPT;} catch (err) {}";
		addScript(output);
		return output;
		
		
	}
	
	/**makes the section of text into a superscript*/
	public String setSubScript() {
		String output="try{";
		output+=refname+".baselinePosition = FontBaselineOption.SUBSCRIPT;} catch (err) {}";
		addScript(output);
		return output;
		
		
	}
	
	/**makes the section of text into a superscript*/
	public String setNormalScript() {
		String output="try{";
		output+=refname+".baselinePosition = FontBaselineOption.NORMALBASELINE;} catch (err) {}";
		addScript(output);
		return output;
		
		
	}
	
	
	public String setfont(Font f) {
		
			String fontName = f.getFontName();
			//if(super.creativeCloud)fontName=fontName.replace(" ", "-");
			String o=setfont(fontName)+'\n' ;
		return o+ setfontSize(f.getSize());
	}
	
	
	/**Adds the font size to the script*/
	public String setfontSize(double font) {
		
		font*=getGenerator().scale;
		if (font==0) return "";
		String output="";
		output+=refname+".size = "+font+";";
		addScript(output);
		return output;
	}
	
}
