package dataSeries;

import java.io.Serializable;

public interface DataPoint extends Serializable {
	
	
	public double getPosition();
	public double getValue();
	public String getValueString() ;
	
	public boolean isExcluded();
	//public void setExcluded(boolean excluded) ;

}
