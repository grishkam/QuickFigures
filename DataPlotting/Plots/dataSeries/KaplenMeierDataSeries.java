package dataSeries;

import java.util.ArrayList;
import java.util.HashMap;

public class KaplenMeierDataSeries implements DataSeries {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ArrayList<KaplenMeierDataPoint> data=new ArrayList<KaplenMeierDataPoint> ();
	
	double studyEnd=400;

	private double pOff;

	private String name="Kaplan-Meier";
	public KaplenMeierDataSeries() {}
	
	public KaplenMeierDataSeries(String name2, ArrayList<KaplenMeierDataPoint> nums) {
		name=name2;
		data=nums;
		
		matchStudyEndToDataEnd(nums);
		
	}

	public void matchStudyEndToDataEnd(ArrayList<KaplenMeierDataPoint> nums) {
		for(KaplenMeierDataPoint p: nums) {
			if (p.getSerialTime()>studyEnd) studyEnd= p.getSerialTime();
		}
	}

	public static KaplenMeierDataSeries createExampleData() {
		KaplenMeierDataSeries out = new KaplenMeierDataSeries();
		out.setName("Example data 1");
		out.studyEnd=500;
		out.data.add(new KaplenMeierDataPoint(450, false));
		out.data.add(new KaplenMeierDataPoint(50, false));
		out.data.add(new KaplenMeierDataPoint(90, false));
		out.data.add(new KaplenMeierDataPoint(110, true));
		out.data.add(new KaplenMeierDataPoint(380, true));
		out.data.add(new KaplenMeierDataPoint(130, false));
		out.data.add(new KaplenMeierDataPoint(200, false));
		out.data.add(new KaplenMeierDataPoint(200, false));
		out.data.add(new KaplenMeierDataPoint(300, false));
		out.data.add(new KaplenMeierDataPoint(340, false));
		out.data.add(new KaplenMeierDataPoint(400, false));
		out.data.add(new KaplenMeierDataPoint(502, false));
		out.data.add(new KaplenMeierDataPoint(500, false));
		out.data.add(new KaplenMeierDataPoint(75, false));
		
		return out;
	}
	
	public static KaplenMeierDataSeries createExampleData2() {
		KaplenMeierDataSeries out = new KaplenMeierDataSeries();
		out.studyEnd=500;
		out.setName("Example data 2");
		out.data.add(new KaplenMeierDataPoint(450, false));
		out.data.add(new KaplenMeierDataPoint(500, false));
		out.data.add(new KaplenMeierDataPoint(1200, false));
		out.data.add(new KaplenMeierDataPoint(350, false));
		out.data.add(new KaplenMeierDataPoint(280, true));
		out.data.add(new KaplenMeierDataPoint(130, false));
		out.data.add(new KaplenMeierDataPoint(200, false));
		out.data.add(new KaplenMeierDataPoint(200, false));
		out.data.add(new KaplenMeierDataPoint(300, false));
		out.data.add(new KaplenMeierDataPoint(340, false));
		out.data.add(new KaplenMeierDataPoint(400, false));
		out.data.add(new KaplenMeierDataPoint(502, false));
		out.data.add(new KaplenMeierDataPoint(500, false));
		out.data.add(new KaplenMeierDataPoint(365, false));
		
		return out;
	}
	
	/**
	private ArrayList<Double> getAllVaidDays() {
		ArrayList<Double> out=new ArrayList<Double> ();
		for(double d=0; d<=studyEnd; d++)  out.add(d);
		return out;
	}
	*/
	double getLongestTime() {
		double longest = 0;
		for(KaplenMeierDataPoint d: data) {
			if (!d.isExcluded()&&d.getSerialTime()>longest) longest=d.getSerialTime();
		}
		return longest;
	}
	
	public double getNumberAliveatTime(double theTime) {
		int total=0;
		for(KaplenMeierDataPoint d: data) {
			if ((d.getSerialTime()>theTime||theTime==0)
					&&!d.isExcluded()) 
				total+=1;
		}
		return total;
	}
	
	/**returns the number of events that occurred at the given time*/
	public double getNumberCensoredAtTime(double theTime) {
		int total=0;
		for(KaplenMeierDataPoint d: data) {
			if (d.getSerialTime()==theTime
					&&d.isCensored()
					&&!d.isExcluded()
					) total+=1;
		}
		return total;
	}
	
	/**returns the number of events that occurred at the given time*/
	private double getNumberEventsAtTime(double theTime) {
		int total=0;
		for(KaplenMeierDataPoint d: data) {
			if (d.getSerialTime()==theTime
					&&!d.isCensored()
					&&!d.isExcluded()
					) total+=1;
		}
		return total;
	}
	
	/**returns the number at risk the given time*/
	private double getNumberAtRiskAtTime(double theTime) {
		int total=0;
		for(KaplenMeierDataPoint d: data) {
			if (d.getSerialTime()>=theTime
					&& !d.isExcluded()) total+=1;
		}
		return total;
	}
	
	/**Computes a Kaplan-Meier estimator at the given time point*/
	public double getEstimatorAtTime(double endTime) {
		double output=1;
		for(int i=0; i<=endTime; i++) {
			double nDeaths=getNumberEventsAtTime(i);
			double nAtRisk=getNumberAtRiskAtTime(i);
			double factor=1-nDeaths/nAtRisk;
			output*=factor;
		}
		return output;
		
	}
	
	/**
	public double getPercentAtTime(double theTime) {
		return getNumberAliveatTime(theTime)/getNumberAliveatTime(0);
	}
	*/
	@Override
	public Basic1DDataSeries getIncludedValues() {
		double[] p = getAllPositions();
		double[] out = new double[p.length] ;
		for(double d=0; d<out.length; d++) this.getValue((int) d);
		return new Basic1DDataSeries("Kaplan curve values", out);
		
	}

	/**returns the percent survival on day x*/
	@Override
	public Basic1DDataSeries getValuesForPosition(double position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int length() {
		return data.size();
	}

	/**Returns the kaplan meier estimator for the time point*/
	@Override
	public double getValue(int i) {
		return this.getEstimatorAtTime(i);
	}

	@Override
	public double getPosition(int i) {
		return i;
	}
	
	public KaplenMeierDataPoint getPointAt(int i) {
		return data.get(i);
	}
	
	@Override
	public DataPoint getDataPoint(int i) {
		 return data.get(i);
	}

	@Override
	public double[] getAllPositions() {
		double[] out = new double[(int)studyEnd+1];
		for(double d=0; d<=studyEnd; d++) out[(int)d]=d;;
		return out;
	}

	@Override
	public double[] getAllPositionsInOrder() {
		return getAllPositions();
	}

	@Override
	public double getPositionOffset() {
		return pOff;
	}

	@Override
	public void setPositionOffset(double o) {
		pOff=o;

	}

	@Override
	public HashMap<Double, Double> getValueOffsetMap() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setName(String name) {
		this.name=name;
		
	}
	
	public String getName() {return name;}

	public void replaceData(KaplenMeierDataSeries novel) {
		data=novel.data;
		matchStudyEndToDataEnd(novel.data);
	}

}
