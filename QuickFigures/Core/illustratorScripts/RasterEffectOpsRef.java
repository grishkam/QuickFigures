package illustratorScripts;

public class RasterEffectOpsRef extends IllustratorDocRef {

	public String setToDocument(IllustratorDocRef ref) {
		String output=getAssignment()+ref.refname+".rasterEffectSettings;";
		addScript(output);
		return output;
	}
	
	public String setResolution(double d) {
		String output = refname+".resolution="+d+";";
		addScript(output);
		return output;
	}
}
