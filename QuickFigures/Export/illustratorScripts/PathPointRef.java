package illustratorScripts;

public class PathPointRef extends IllustratorObjectRef {

	
	private PathItemRef parent;

	public PathPointRef(PathItemRef parent) {
		this.setParent(parent);
		String out=this.getAssignment()+parent.refname+".pathPoints.add()";
		this.addScript(out);
		
	}

	public PathItemRef getParent() {
		return parent;
	}

	public void setParent(PathItemRef parent) {
		this.parent = parent;
	}
	
	public String setleftDirection(double x, double y) {
		String out = this.refname+".leftDirection"+equalNumberArray(x,y); ;
		this.addScript(out);
		return out;
		
	}
	
	public String setrightDirection(double x, double y) {
		String out = this.refname+".rightDirection"+equalNumberArray(x,y); 
		this.addScript(out);
		return out;
		
	}
	
	public String setAnchor(double x, double y) {
		String out = this.refname+".anchor"+equalNumberArray(x,y); 
		this.addScript(out);
		return out;
		
	}
	
String equalNumberArray(double x, double y) {
		return "="+pointToJSarray(x,y)+";";
	}
	
}
