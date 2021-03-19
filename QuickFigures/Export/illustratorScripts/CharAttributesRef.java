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
 * Date Modified: Mar 18, 2021
 * Version: 2021.1
 */
package illustratorScripts;

import java.awt.Font;

import logging.IssueLog;

/**a java class that generates scripts to create and modify a character attributes object in 
  adobe illustrator*/
public class CharAttributesRef extends IllustratorObjectRef {
	
	/**Sets the font style and the family to fit the given font*/
	String setfontStyleandFamily(Font f) {
		String fullname = f.getFontName();
		if (fullname.equals("SansSerif")) fullname="SerifBold";
		String output="try{";
		output+=refname+".textFont = app.textFonts.getByName('"+fullname+"');} catch (err) {"
				+ findFont(fullname)+"}";
		addScript(output);
		return output;
	}
	
	/**sets the font. old version that may be removed or reused later
	 * @param family
	 * @param style
	 * @return
	 */
	 String setFont(String family, int fontStyle) {
		String styleName="Regular";
		if (fontStyle==Font.BOLD) styleName="Bold";
		if (fontStyle==Font.ITALIC) styleName="Italic";
		if (fontStyle==Font.ITALIC+Font.BOLD) styleName="Bold Italic";
		
		return setFont(family,styleName);
	}

	/**Adds series of code that helps find a font with the given name
	 * within the illustrator fonts
	 * given a full font name, sets the font*/
	private String findFont(String fullname) {
		String[] split = splitFontName(fullname);
		String string = split[0].trim();
		if (string.equals("SansSerif")) string="Liberation Sans";//illustrator is not great with this font
		if (string.equals("Monospaced")) string="Liberation Mono";
		
		String name=string.toLowerCase();
		String style="Regular";
		if (split.length>1)style=split[1].toLowerCase();
		else {style="Regular";}
		if(split.length>2)style+=" "+split[2].toLowerCase();
		
		return setFont(name, style);
	}

	/**divides the font name into style and family sections, may not work for all fonts
	 * @param fullname
	 * @return
	 */
	private String[] splitFontName(String fullname) {
		
		
		if (fullname.endsWith(".bolditalic")) return new String[] {fullname.substring(0,fullname.length()-11), "Bold Italic"};
		if (fullname.endsWith(" bold italic")) return new String[] {fullname.substring(0,fullname.length()-11), "Bold Italic"};
		if (fullname.endsWith(" Bold Italic")) return new String[] {fullname.substring(0,fullname.length()-11), "Bold Italic"};
		if (fullname.endsWith(".bold")) return new String[] {fullname.substring(0,fullname.length()-5), "Bold"};
		if (fullname.endsWith(" Bold")) return new String[] {fullname.substring(0,fullname.length()-5), "Bold"};
		if (fullname.endsWith(".italic")) return new String[] {fullname.substring(0,fullname.length()-6), "Italic"};
		if (fullname.endsWith(" Italic")) return new String[] {fullname.substring(0,fullname.length()-6), "Italic"};
		return fullname.split(" ");
	}

	/**
	 * @param familyName
	 * @param style
	 * @return
	 */
	public String setFont(String familyName, String style) {
		String output="try{";
		output+="var iCount=textFonts.length;";
		//output+="var textFonts=textFonts;";
		output+="var notfound=true;";
		output+="for(var i=0; i<iCount; i++){";
		output+="var aFontName=textFonts[i].family.toLowerCase();";
		output+="var aFontStyle=textFonts[i].style.toLowerCase();";
		 output+="try{";
		
		output+="if (notfound & (aFontStyle==='" +style.toLowerCase() +"') & (aFontName==="+"'"+familyName.toLowerCase()+"')) {"+refname+".textFont=textFonts[i];"+"notfound=false;}";
		output+="else if (notfound & (aFontStyle=='" +style.toLowerCase() +"') & (aFontName=="+"'"+familyName.toLowerCase()+"')) {"+refname+".textFont=textFonts[i];"+"notfound=false;}";
		output+= "if (('"+style+"'==='Regular')&(aFontStyle==='Regular')) {alert('regular font style'); break;}";
				
		output+="} catch (err) {alert('failed to find font '+i+aFontName+err);}";
		
	//	int loc=50;
		//output+="if (i=="+loc+")"+"alert('looking for font'+'"+name+"');";
		//output+="if (i=="+loc+")"+"alert('looking at font'+aFontName);";
		output+="};";
		
		//output+="if(notfound) alert('failed to find font');";
		output+="} catch (err) {alert('failed to find font');}";
		return output;
	}
	
	public String setStrikeThrough(boolean strike) {
		String output="try{";
		if (strike)
		output+=refname+".strikeThrough = true;} catch (err) {}";
		else output+=refname+".strikeThrough = false;} catch (err) {}";
		addScript(output);
		return output;
	}
	
	public String setUnderline(boolean strike) {
		String output="try{";
		if(strike)
			output+=refname+".underline = true;} catch (err) {}";
			else 
			output+=refname+".underline = false;} catch (err) {}";
		
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
		
			
			//if(super.creativeCloud)fontName=fontName.replace(" ", "-");
			String o=setfontStyleandFamily(f)+'\n' ;
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
