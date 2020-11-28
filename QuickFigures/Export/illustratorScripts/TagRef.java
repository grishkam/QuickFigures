package illustratorScripts;

public class TagRef extends IllustratorObjectRef {

	public String setToNewTag(IllustratorObjectRef angle) {
		//String output=refname+".name="+angle+";";
		String output=getAssignment()+angle.refname+".tags.add();";
		addScript(output);
		return output;
		}
	
	
	public String setName(String name) {
		String output=refname+".name='"+name+"';";
		addScript(output);
		return output;
		}
	
	public String setValue(String value) {
		String output=refname+".value='"+value+"';";
		addScript(output);
		return output;
		}
	
	
	
}
