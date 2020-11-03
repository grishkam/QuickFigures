package illustratorScripts;

import java.awt.Font;

public class CharAttributesRef extends IllustratorObjectRef {
	
	String setfont(String font) {
		if (font.equals("SansSerif")) font="SerifBold";
		String output="try{";
		output+=refname+".textFont = app.textFonts.getByName('"+font+"');} catch (err) {}";
		addScript(output);
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
	
	
	public String setfont(Font f) {
		
			String o=setfont(f.getFontName())+'\n' ;
		return o+ setfontSize(f.getSize());
	}
	

	
	String setfontSize(double font) {
		
		font*=getGenerator().scale;
		if (font==0) return "";
		String output="";
		output+=refname+".size = "+font+";";
		addScript(output);
		return output;
	}
	
}
