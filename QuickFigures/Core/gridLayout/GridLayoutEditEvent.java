package gridLayout;

public class GridLayoutEditEvent implements GridEditEventTypes{

	
	
	private int type;
	private double arg1;
	private double arg2;;
	
	public  GridLayoutEditEvent(GridLayout l, int type, double n1, double n2) {
		this.setType(type);
		this.setArg1(n1);
		this.setArg2(n2);
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public double getArg1() {
		return arg1;
	}

	public void setArg1(double n1) {
		this.arg1 = n1;
	}

	public double getArg2() {
		return arg2;
	}

	public void setArg2(double n2) {
		this.arg2 = n2;
	}
	
	
	
}
