package dataSeries;

import java.io.Serializable;

public class KaplenMeierDataPoint implements Serializable, DataPoint {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int Event_Occured_Status=0, Censored_Status=1;
	
	private double serialTime=0;
	int status=0;
	
	public KaplenMeierDataPoint(double time, boolean censor) {
		serialTime=(time);
		this.status=  censor?  Censored_Status: Event_Occured_Status;
	}

	public boolean isCensored() {
		return status==Censored_Status;
	}

	public double getSerialTime() {
		return serialTime;
	}

	@Override
	public double getPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getValue() {
		return serialTime;
	}

	@Override
	public boolean isExcluded() {
		return false;
	}

	@Override
	public String getValueString() {
		// TODO Auto-generated method stub
		return this.getValue()+"";
	}

	

	
}
