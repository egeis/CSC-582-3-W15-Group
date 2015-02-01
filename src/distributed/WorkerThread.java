package distributed;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class WorkerThread implements Runnable
{
	Comparable[] list;
	int left;
	int right;
	Comparable pivotValue;
	int storeIndex;
	CyclicBarrier cb;
	boolean goLeft;
	boolean status;
	boolean sameValues;
	String sValue;
	
	public WorkerThread(Comparable[] list, int left, int right, Comparable pivotValue, CyclicBarrier cb)
	{
		this.list = list;
		this.left = left;
		this.right = right;
		this.pivotValue = pivotValue;
		this.storeIndex = 0;
		this.cb = cb;
		this.goLeft = false;
		this.status = true;
		this.sameValues = false;
		this.sValue = null;
	}
	
	public void run() 
	{
		this.sameValues = same(list, left, right);
		
		if(this.status && !this.sameValues)
			this.storeIndex = QuickSelect.partition(list, left, right, pivotValue);
		
		try {
			cb.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Check if potential k-th values in this sub-array are all same values
	public boolean same(Comparable[] list, int left, int right)
	{
		Comparable temp = list[left];
		
		for(int i = left; i <= right; i++)
		{	
			if(temp != list[i])
				return false;
		}
		
		this.sValue = temp.toString();
		
		return true;
	}
}
