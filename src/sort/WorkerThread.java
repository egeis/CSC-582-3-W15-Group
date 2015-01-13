// Question 2
// Write a parallel Java program to find the k-th smallest number in a non-ordered array of numbers. 
// Again you are not allowed to sort the array first.

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class WorkerThread implements Runnable
{
	int[] list;
	int left;
	int right;
	int pivotValue;
	int storeIndex;
	CyclicBarrier cb;
	boolean goLeft;
	boolean status;
	boolean sameValues;
	String sValue;
	
	public WorkerThread(int[] list, int left, int right, int pivotValue, CyclicBarrier cb)
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
	public boolean same(int[] list, int left, int right)
	{
		int temp = list[left];
		
		for(int i = left; i <= right; i++)
		{	
			if(temp != list[i])
				return false;
		}
		
		this.sValue = Integer.toString(temp);
		
		return true;
	}
}
