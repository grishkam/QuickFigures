package illustratorScripts;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import figureFormat.DirectoryHandler;
import logging.IssueLog;

public class ZIllustratorScriptGenerator {
	
	public static ZIllustratorScriptGenerator instance=new ZIllustratorScriptGenerator();
	double x0=0;
	double y0=0;
	double scale=1;
	
	public double getScale() {
		return scale;
	}
	public void setScale(double d) {scale=d;}
	
	public String accumulatedscrip="";
	private String pathOfImages="/temp/";
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
		IssueLog.log("File inside "+directoryJSX);
		saveString("#target illustrator"+'\n'+javascript, directoryJSX, false);
		try {Desktop.getDesktop().open(new File(directoryJSX));} catch (IOException e) {}
	}

	public String getPathOfImages() {
		File f=new File(pathOfImages);
		if (!f.exists()) {
			f.mkdirs();
		}
		
		return pathOfImages;
	}
	public void setPathOfImages(String pathOfImages) {
		this.pathOfImages = pathOfImages;
	}

	boolean invertvertical=true;
	
	
	
	 public static String saveString(String string, String path, boolean append) {
	        
	        try {
	            BufferedWriter out = new BufferedWriter(new FileWriter(path, append));
	            out.write(string);
	            out.close();
	        } catch (Exception e) {
	            IssueLog.log(e);
	        }
	        return null;
	    }
		
	}
	



