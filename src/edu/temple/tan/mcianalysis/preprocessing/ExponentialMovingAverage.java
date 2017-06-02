package edu.temple.tan.mcianalysis.preprocessing;

/**
 * 
 */
public class ExponentialMovingAverage {
	
	private double alpha;
	private int sampleSize;
	private EMAValue topValue;
	
	/**
	 * 
	 * @param alpha
	 * @param sampleSize
	 */
	public ExponentialMovingAverage (double alpha, int sampleSize) {
		EMAValue first = null, prev = null;
		for (int i = 0; i < sampleSize; i++) {
			EMAValue value = new EMAValue();
			if (first == null) first = value;
			value.setPrevious(prev);
			prev = value;
		}

		this.sampleSize = sampleSize;
		this.topValue = prev;
		this.topValue.setNext(first);
		this.alpha = alpha;
	}
	
	/**
	 * 
	 * @param newValue
	 * @return
	 */
	public double getAverage(double newValue) {
		topValue.setPreviousValue(newValue);
		topValue = topValue.getPrevious();
		
		if (topValue.getPreviousValue() == 0.0d) {
			return topValue.getPreviousValue();
		} else {
			// calculate exponential average of sample
			int i = 0;
			double total = 0.0d, offset = (1 - alpha);
			EMAValue currentValue = topValue;
			
			while (i < sampleSize) {
				total += (Math.pow(offset, i)) * currentValue.getValue();
				currentValue = currentValue.getNext();
				i++;
			}
			
			return (total * alpha);
		}
	}
	
	/**
	 * implement collection of EMA values as a ring with next / prev pointers
	 */
	private class EMAValue {
		
		private EMAValue prev = null, next = null;
		private double val = 0.0d;
		
		public void setValue(double value) { this.val = value; }
		
		public double getValue() { return this.val; }
		
		public void setPreviousValue(double prevValue) {
			if (prev == null) prev = new EMAValue();
			prev.setValue(prevValue);
			prev.setNext(this);
		}
		
		public double getPreviousValue() { return this.prev.getValue(); }
		
		public void setPrevious(EMAValue prev) {
			this.prev = prev;
			if (prev != null) prev.next = this;
		}
		
		public EMAValue getPrevious() { return this.prev; }
		
		public void setNext(EMAValue next) {
			this.next = next;
			if (next != null) next.prev = this;
		}
		
		public EMAValue getNext() { return this.next; }
		
	}

}