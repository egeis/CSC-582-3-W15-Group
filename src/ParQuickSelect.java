// Question 2
// Write a parallel Java program to find the k-th smallest number in a non-ordered array of numbers. 
// Again you are not allowed to sort the array first.

import java.util.Random;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ParQuickSelect
{
	public static AtomicInteger go = new AtomicInteger(); 
	
	public static void main(String[] args) throws InterruptedException
	{	
		System.out.print("Creating sample array...");
		
		// Create a sample array of specified size to run the algorithm on
		int[] sample = new int[10000000];
		
        for(int i = 0; i < sample.length; i++) 
        {
            sample[i] = (int) (new Random().nextDouble() * 100);
        }
		
        System.out.println("Done.");
        
        // k-th value
		int origK = 100;
		int k = origK;
		
		// Thread pool size set by number of available processors
		int processorCount = Runtime.getRuntime().availableProcessors();
		ExecutorService executor = Executors.newFixedThreadPool(processorCount);
		
		// CyclicBarrier created using thread pool size (which is number of available processors)
		CyclicBarrier cb = new CyclicBarrier(processorCount, new CyclicBarrierRunnable());
		
		int chunkSize = (int)Math.ceil(sample.length / (double)processorCount);
		boolean done = false;
		WorkerThread[] workArray = new WorkerThread[processorCount];
		int workArrayCounter = 0;
		
		int pV = generateRandom(sample);
		
		// Create worker threads and populate the worker thread array so they can be reused later
		for (int i = 0; !done; i += chunkSize, workArrayCounter++)
		{	
			WorkerThread wt;
			
			if (i + chunkSize >= sample.length)
			{
				wt = new WorkerThread(sample, i, sample.length - 1, pV, cb);
				done = true;
			}
			
			else
				wt = new WorkerThread(sample, i, i + chunkSize - 1, pV, cb);
			
			workArray[workArrayCounter] = wt;
			executor.execute(wt);
		}
		
		done = false;
		
		while(go.get() == 0) {}
		
		int leftOver = 0;
		String same = null;
		
		// Run partition on sub-arrays until there are less than 20 potential k-th values left in all sub-arrays combined
		while(!done)
		{
			int leftTotal = calculatePivotIndex(workArray);
			
			same = checkSameValues(workArray);
			
			// if there are 20 or less than 20 potential k-th values in all sub-arrays combined
			if(leftOver != 0 && leftOver <= 20)
				done = true;
			
			// if the remaining potential k-th values in all sub-arrays are of the same value
			else if(same != null)
				done = true;
			
			// if neither of the two previous cases, continue "partitioning" the sub-arrays
			else if(go.get() == 1)
			{
				go.set(0);
				
				pV = generateRandom(sample);
				
				if(k <= leftTotal)
					leftOver = goLeft(executor, workArray, pV);
					
				else
				{
					leftOver = goRight(executor, workArray, pV);
					k = k - leftTotal;
				}
				
				while(go.get() == 0) {}
			}
		}
		
		executor.shutdown();
		
		String kValue = null;
		
		// if the remaining potential k-th values in all sub-arrays are of the same value
		if(same != null)
			kValue = same;
		
		// if there are 20 or less than 20 potential k-th values in all sub-arrays combined
		else
		{
			int[] leftOverArr = fillLeftOverArr(workArray, sample, leftOver);
			int left = 0;
			int right = leftOverArr.length - 1;
			
			kValue = Integer.toString(QuickSelect.select(leftOverArr, left, right, k - 1));
		}
		
		System.out.println(origK + "-th smallest number is " + kValue);
		
		System.out.println("Done.");
	}
	
	// Copy potential k-th values in original array to a new array so sequential quick select algorithm can be run on it 
	// to determine the k-th value
	public static int[] fillLeftOverArr(WorkerThread[] wa, int[] origArr, int leftOver)
	{
		int[] array = new int[leftOver];
		int arrayIndex = 0;
		
		for(int i = 0; i < wa.length; i++)
		{
			if(wa[i].status)
			{
				for(int j = wa[i].left; j < wa[i].right + 1; j++)
				{
					array[arrayIndex] = origArr[j];
					arrayIndex++;
				}
			}
		}
		
		return array;
	}
	
	// Select left portion of the array to find the k-th value
	public static int goLeft(ExecutorService es, WorkerThread[] wArr, int pV)
	{
		int leftOver = 0;
		
		for(int i = 0; i < wArr.length; i++)
		{
			if(wArr[i].status)
			{
				if(wArr[i].left == wArr[i].storeIndex)
					wArr[i].status = false;
				
				else
				{
					wArr[i].right = wArr[i].storeIndex - 1;
					wArr[i].pivotValue = pV;
					leftOver += wArr[i].right - wArr[i].left + 1;
				}
			}
			
			es.execute(wArr[i]);
		}

		return leftOver;
	}
	
	// Select right portion of the array to find the k-th value
	public static int goRight(ExecutorService es, WorkerThread[] wArr, int pV)
	{
		int leftOver = 0;
		
		for(int i = 0; i < wArr.length; i++)
		{
			if(wArr[i].status)
			{
				if(wArr[i].right < wArr[i].storeIndex)
					wArr[i].status = false;
					
				else
				{
					wArr[i].left = wArr[i].storeIndex;
					wArr[i].pivotValue = pV;
					leftOver += wArr[i].right - wArr[i].left + 1;
				}
			}
			
			es.execute(wArr[i]);
		}

		return leftOver;
	}
	
	// Choose and return random value from the input list
	public static int generateRandom(int[] list)
	{
		int randomIndex = (int)(Math.random()*list.length);

		return list[randomIndex];
	}
	
	// Calculate the total number of values left of the pivot index
	public static int calculatePivotIndex(WorkerThread[] wt)
	{
		int total = 0;
		
		for(int i = 0; i < wt.length; i++)
		{
			if(wt[i].status)
				total += wt[i].storeIndex - wt[i].left;
		}
			
		return total;
	} 
	
	// Check if all 'status = true' sub-arrays are left with same values
	public static String checkSameValues(WorkerThread[] wt)
	{
		String temp = null;
		
		for(int i = 0; i < wt.length; i++)
		{
			if(wt[i].status)
			{
				if(!wt[i].sameValues)
					return null;
				
				else
				{
					if(i == 0)
						temp = wt[i].sValue;
					
					else if(!temp.equals(wt[i].sValue))
						return null;
				}
			}
		}
		
		return temp;
	}
}
