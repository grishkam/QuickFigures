/**
 * Author: Greg Mazo
 * Date Created: April 28, 2021
 * Date Modified: April 28, 2021
 * Version: 2021.2
 */
package plotParts.stats;

import java.util.ArrayList;

import org.apache.commons.math3.stat.inference.ChiSquareTest;

import dataSeries.KaplanMeierDataSeries;
import messages.ShowMessage;

/**
 rough draft for a log rankk test implementation
 */
public class LogRank {
	
	
	
	private double pValue;

	/**constructor that performs a log rank, this implementation is new and needs more testing
	 * to make sure it is accurate*/
	public LogRank(KaplanMeierDataSeries a, KaplanMeierDataSeries b) {
		ArrayList<Double> allValues = getAllValues(a, b);
		
		long[] allObserved=new long[ ] {0,0};
		double[] allExpected=new double[] {0,0};
		
		for(double d: allValues) {
			double risked = a.getNumberAtRiskAtTime(d)+b.getNumberAtRiskAtTime(d);
			double deads = a.getNumberEventsAtTime(d)+b.getNumberEventsAtTime(d);
			double chanceDeath = deads/risked;
			
			double observedA = a.getNumberEventsAtTime(d);
			double expectedA = a.getNumberAtRiskAtTime(d)*chanceDeath;
			allObserved[0]+= (long) observedA ;
			allExpected[0]+= expectedA ;
			
			
			double observedB = b.getNumberEventsAtTime(d);
			double expectedB = b.getNumberAtRiskAtTime(d)*chanceDeath;
			allObserved[1]+= (long) observedB ;
			allExpected[1]+= expectedB ;
			
			
		}
		pValue=new ChiSquareTest().chiSquareTest(allExpected, allObserved);
		
		
		
		
		
	}

	/**returns all times during the survival
	 * @param a
	 * @param b
	 * @return
	 */
	protected ArrayList<Double> getAllValues(KaplanMeierDataSeries a, KaplanMeierDataSeries b) {
		ArrayList<Double> allValues = new ArrayList<Double>();
		
		for(Double v1: a.getAllPositions()) {
			if(!allValues.contains(v1)&&a.hasPointAtTime(v1))
				allValues.add(v1);
		}
		for(Double v1: b.getAllPositions()) {
			if(!allValues.contains(v1)&&b.hasPointAtTime(v1))
				allValues.add(v1);
		}
		allValues.sort(null);
		
		
		return allValues;
	}

	public double getPValue() {
		ShowMessage.showOptionalMessage("Log rank", false, "I,plementation of the log rank test for survival is a work in progress", "use with caution");
		return pValue;
	}
	
	
	
}
