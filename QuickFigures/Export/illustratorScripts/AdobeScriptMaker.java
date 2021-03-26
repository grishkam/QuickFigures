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
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import appContext.ImageDPIHandler;
import applicationAdapters.ImageWorkSheet;
import applicationAdapters.PixelWrapper;
import graphicalObjects.FigureDisplayWorksheet;
import logging.IssueLog;
import messages.ShowMessage;
import photoshopScripts.AdobeScriptGenerator;
import ultilInputOutput.FileChoiceUtil;

/**A class that generates liens of javascript code for an Adobe extened script toolkit*/
public class AdobeScriptMaker {

	
	public static ArtLayerRef aref;
	
	/**the evantual resolution of images the go to illustrator and photosho*/
	public int ppiResolution=(int) ImageDPIHandler.idealPanelPixelDesity();
	
	public AdobeScriptGenerator sgen=new AdobeScriptGenerator();
	public  int x0=0;
	public  int y0=0;
	public  void setZero(Point pt){int x=(int) pt.getX();int y=(int) pt.getY(); setZero(x,y);}
	public  void setZero(int x, int y) {x0=x; y0=y; sgen.setZero(x0, y0);}
	


	public  IllustratorDocRef makeRefForWrapper(ImageWorkSheet iw, boolean makenew) {
		IllustratorDocRef d=new IllustratorDocRef();
		
		if (makenew)
		d.createDocumentScript(true, iw.getCanvasDims().getWidth(), iw.getCanvasDims().getHeight()); else d.setReftoActiveDocument() ;
		
		d.getRasterEffectOps().setResolution(ppiResolution);
		
		if (makenew) ZIllustratorScriptGenerator.instance.setZero(0,  (int)(iw.getCanvasDims().getHeight()));

		aref=new ArtLayerRef();
		aref.createNewRef(d);
		aref.setName(iw.getTitle());
		d.getRasterEffectOps().setResolution(ppiResolution);
		
		return d;
	}
	
	
	
	
	/**generate an illustrator script to replicate the objects in the worksheet
	 * @param worksheet the worksheet containing the objects
	 * @param makenew set to true if a new illustator document should be created
	 * @param saveFile specifies where illustrator should save the file*/
	public void sendWrapperToills(ImageWorkSheet worksheet, boolean makenew, File saveFile) {
		IllustratorDocRef ref = makeRefForWrapper(worksheet, makenew);
		sentToIlls(worksheet,aref);
		if (makenew) 
			{
			if(saveFile==null) 
				saveFile=new File(AdobeScriptGenerator.outputPathAI());
			if(saveFile.getAbsolutePath().toLowerCase().endsWith("eps"))
				ref.saveAsEPS(saveFile);
			else
			if(saveFile.getAbsolutePath().toLowerCase().endsWith("pdf"))
				ref.saveAsEPS(saveFile);
			else
				ref.saveAs(saveFile);
			};
	}
	
	
	
	

	/**Add the given worksheet to the artlayer*/
	protected void sentToIlls(Object worksheet, ArtLayerRef aref) {
		IssueLog.log("making illustrator script for "+worksheet);
		
		if (worksheet instanceof FigureDisplayWorksheet) {
			FigureDisplayWorksheet mont2 = (FigureDisplayWorksheet)worksheet;
			if (mont2.getTopLevelLayer() instanceof IllustratorObjectConvertable) {
				((IllustratorObjectConvertable) mont2.getTopLevelLayer()).toIllustrator(aref);
			}
			
		}
}
	
	
	
	
	
	
	
	
	
	
	
	
	/**second half of the file is for photoshop versions, these are outdated but may be replaced later*/
	
	
	/**creates a document. Written for the photoshop version in PS*/
	private String createImageInPSifNeeded(String name, int width, int height, int ppiResolution) {
		String output='\n'+"if (app.documents.length==0 ||"+true+") {var mergedDoc = app.documents.add("+width+", "+height+", "+ppiResolution+", '"+name+"');}"+'\n';;
		return output;
	}
	
	  String newLayerSet(String title) {
			 return  '\n'+"var layerSetRef = app.activeDocument.layerSets.add()"+'\n'+"layerSetRef.name='"+title+"'"+'\n';
		 }
		  
		  String newLayerSubSet(String title, String refname) {
			  if (title==null||refname==null) return "";
				 return  '\n'+"var "+refname+" = layerSetRef.layerSets.add()"+'\n'+refname+".name='"+title+"'"+'\n';
			 }
		  
		  String newLayerSubSetForImages(String title) {
			  if (title==null) return "";
				 return  '\n'+"var "+ "pasteImageLayer"+" = layerSetRef.layerSets.add()"+'\n'+ "pasteImageLayer"+".name='"+title+"'"+'\n';
			 }

			public String drawPolygonInPS(java.awt.Polygon p, String name, boolean link, boolean fill, int stroke, Color strokeColor, Color fillColor) {
				
				String output=  newLayer(name)+makeSelectionInPhtoshopS(p.xpoints, p.ypoints, 0, name);
				output="var myLayer=app.activeDocument.activeLayer"+'\n'+output;
				
				if (fill) output+='\n'+sgen.setPSfgColor( fillColor)+'\n'+"app.activeDocument.selection.fill(app.foregroundColor)";
				if (!fill) {output+='\n'+
						sgen.setPSfgColor(strokeColor)+
					"app.activeDocument.selection.stroke(app.foregroundColor, "+stroke+")"+'\n'+
					'\n';}
				if (link) output+='\n'+"myLayer.link(app.activeDocument.activeLayer)";
				return output;	
			}
			

			/***/
			public  String makeSelectionInPhtoshopS(int[] xs, int[] ys, int copy, String newlayer) {	
				String javascript=		"";
				if (newlayer!=null) {
					newLayer(newlayer);
				}
				javascript+="selRegion = Array(Array(" +(x0+xs[0])+ ", "+(y0+ys[0])+")";
				for (int i=1; i<xs.length&&i<ys.length; i++) javascript+=
					", Array("+(x0+xs[i])+", "+(y0+ys[i])+")";
				javascript+=	"); "+'\n';
				javascript+="app.activeDocument.selection.select(selRegion);";
				if (copy==1) javascript+='\n'+"selRegion.copy(true)";
				return javascript;
			}
			
			public String newLayer(String newlayer) {
				return "try{var layerRef=layerSetRef.artLayers.add()} catch(err){var layerRef = app.activeDocument.artLayers.add()}"+'\n'+"layerRef.name='"+newlayer+"'"+'\n';
			}
			

			/**phososhops layerSet for Text*/
			 public String newLayerSetForText(String title, String refname) {
				 String output=newLayerSubSet(title,  refname);
				 textLayerSetRefName=refname;
				 return output;
			 }
			 public void defaultLayerSetForText() {	 
				 textLayerSetRefName="layerSetRef";
			 }
			 
			 /**the layer set that will contain the text items has a name*/
			public String textLayerSetRefName="layerSetRef";
			
			/**creates a new text item in the layerset called 'textLayerSetRefName' if the javascript variable has a layer set .
			   */
			public String newTextLayer() {
				return "try{var newTextLayer="+textLayerSetRefName+".artLayers.add()} catch(err){var newTextLayer = app.activeDocument.artLayers.add();}";
			}

			private boolean correct=true;
			private double performFontsizeCorrection(Font font) {
				if (!correct) return font.getSize();
				return (font.getSize()*(((double) 72)/ppiResolution));
			}
			/**generates a photoshop script to create a new text item in photoshop. It then plates it at position x,y relative to the zero point.
			   The isItBold, and isItItal refer to the faux bold and faux italic features in ps. They cannot be used to translate 
			   normal bold and italic text.
			   */
			public  String sendStringToPhotoshop(String text, Color color, Font font, int x, int y, boolean link, boolean Vertical, double rotate) {
				if (text==null||text.equals("")) return "";
				String fontsize="new UnitValue( '"+font.getSize()+ " px"+"' )";
				fontsize=""+performFontsizeCorrection(font) ;

				
				String javascript=
					"var isItVeritcal="+(Vertical? "Direction.VERTICAL":"Direction.HORIZONTAL")+";"+'\n'+
					"var isItBold="+(font.getStyle()==7)+";"+'\n'+
					"var isItItal="+(font.getStyle()==7)+";"+'\n'+
				"var cred="+color.getRed()+";"+'\n'+
				"var cblue="+color.getBlue()+";"+'\n'+
				"var cgreen="+color.getGreen()+";"+'\n'+
				"var textString='"+text+"';"+'\n'+
				"var x="+(x+x0)+";"+'\n'+
				"var y="+(y+y0)+";"+'\n'+
				"var sizeOffon="+fontsize+";"+'\n'+
				"var fontFam='"+font.getFontName()+"';"+'\n'+
				"var link="+link+'\n'+
				"var rotangle="+rotate+";"+'\n';
				javascript="var myLayer=app.activeDocument.activeLayer"+'\n'+javascript+newTextLayer();
				
				return javascript+textFileAsString("WorkingWithText")+(link? '\n'+"myLayer.link(app.activeDocument.activeLayer)":"");
			}
			
			/**Given the name of a text file in the same .jar package as this file,
			  returns a string with all lines of the text file.*/
			String textFileAsString(String name) {
				InputStream urlToDictionary;		
				urlToDictionary = this.getClass().getClassLoader().getResourceAsStream( name);	
				if (urlToDictionary==null) ShowMessage.showMessages("File with name "+name+" is not found in jar");
							
				return FileChoiceUtil.readStringFrom(urlToDictionary);
				
			}
			
			
			

			/**given the directory of an image file, a layer name and cordinates, this returns a String that
			   can be run in an exended javascript to paste the image into point x,y of the open document*/
			public  String pasteFileIntoPSDocument(String directory, String name, int x, int y, boolean link, boolean run) {
				String out=textFileAsString("openImageandPasteJavaScript");	
				String javascript="var directory1 = '"+directory+"'"+'\n'+
				"var layerName='"+name+"';"+'\n'+
				"var h="+(x+x0)+";"+'\n'+
				"var v="+(y+y0)+";"+'\n'+
				"var linkTo="+link+";"+'\n';
				if (run) savejsxAndRun(javascript);
				return javascript+out+'\n';
			}
			

			
			 private static String saveString(String string, String path, boolean append) {
			        if (path==null || path.equals("")) {
			        	
			            return null;
			        }
			        try {
			            BufferedWriter out = new BufferedWriter(new FileWriter(path, append));
			            out.write(string);
			            out.close();
			        } catch (IOException e) {
			            return ""+e;
			        }
			        return null;
			    }
			
			String directoryJSX="temp"+ ".jsx";
			public  void savejsxAndRun(String javascript){
				saveString("#target photoshop"+'\n'+javascript, directoryJSX, false);
				try {Desktop.getDesktop().open(new File(directoryJSX));} catch (IOException e) {}
			}

			
			 public static ArrayList<Image> unwrapArray2(ArrayList<PixelWrapper> stack1) {
					ArrayList<Image> out = new ArrayList<Image>() ;
					for(PixelWrapper o: stack1) {
						
							out.add(o.image());
						
					}
					return out;
				}
			 
				
			
				public  String trimfilename(String name ) {
					try {
						if (name==null) name="_nameless_";
						if (name.contains(";"))name=name.split(";")[0];
						if (name.contains("."))name=name.split(".")[0];} catch (java.lang.ArrayIndexOutOfBoundsException aio) {
					
						}
						return name;
				}

				
			
				 
				 
			 
			
}
