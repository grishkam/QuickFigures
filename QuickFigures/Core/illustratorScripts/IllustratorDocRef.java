package illustratorScripts;

import java.awt.Dimension;

public class IllustratorDocRef extends IllustratorObjectRef {

	
		public String createDocumentScript(boolean RGB, double width, double height) {
			String colorspace="DocumentColorSpace.CMYK";
			if (RGB)colorspace="DocumentColorSpace.RGB";
			set=true;
			width *=getGenerator().scale;
			height*=getGenerator().scale;
			String output="var "+refname+" = app.documents.add("+colorspace+","+width+","+height+");";
			addScript(output);
			return output;
		
}
		
		public String createDocumentScript(boolean RGB, Dimension d) {
			return createDocumentScript(RGB, d.width, d.height);
		}
		
		
		
	public 	String setReftoActiveDocument() {
			set=true;
			String output=getAssignment()+" app.activeDocument;";
			addScript(output);
			return output;
		}
		
		String setToOpen(String path) {
			set=true;
			String output= getAssignment()+"app.open('"+path+"');";
			
			addScript(output);
			 return output;
		}
		String setToPSFile(String path) {
			set=true;
			String output="var fileRef = File( '"+path+"'); " +'\n'+
			"if (fileRef != null) {var "+refname+" = open(fileRef, DocumentColorSpace.RGB);}";
			addScript(output);
			 return output;
		}
		
		public RasterEffectOpsRef getRasterEffectOps() {
			RasterEffectOpsRef out=new RasterEffectOpsRef();
			out.setToDocument(this);
			return out;
		}
		
		
		

}