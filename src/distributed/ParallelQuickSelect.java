package distributed;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ParallelQuickSelect
{
	public static AtomicInteger go = new AtomicInteger(); 
        public int processorCount;
	public ExecutorService executor;
        public CyclicBarrier cb;
        public WorkerThread[] workArray;
        public int leftTotal;
        public int rightTotal;
        public Comparable[] original;
        
        public ParallelQuickSelect()
        {
            // Thread pool size set by number of available processors
            //processorCount = Runtime.getRuntime().availableProcessors();
            processorCount = 2;
            executor = Executors.newFixedThreadPool(processorCount);
            
            // CyclicBarrier created using thread pool size (which is number of available processors)
            cb = new CyclicBarrier(processorCount, new CyclicBarrierRunnable());
            
            workArray = new WorkerThread[processorCount];
           
            leftTotal = 0;
        }
        
        public Comparable setRecommendLeft()
        {
            Comparable[] leftValues = new Comparable[leftTotal];
            int counter = 0;
            
            for (int i = 0; i < workArray.length; i++)
            {
                WorkerThread wt = workArray[i];
                
                if (wt.status)
                {
                    for (int j = wt.left; j < wt.storeIndex; j++)
                    {
                        leftValues[counter] = wt.list[j];
                        counter++;
                    }
                }
            }
            
            return leftValues[(int)(Math.random() * (leftValues.length - 1))]; 
        }
        
        public Comparable setRecommendRight()
        {
            Comparable[] rightValues = new Comparable[rightTotal];
            int counter = 0;
            
            for (int i = 0; i < workArray.length; i++)
            {
                WorkerThread wt = workArray[i];

                if (wt.status)
                {
                    for (int j = wt.storeIndex; j <= wt.right; j++)
                    {
                        rightValues[counter] = wt.list[j];
                        counter++;
                    }
                }
            }
            
            return rightValues[(int)(Math.random() * (rightValues.length - 1))]; 
        }
        
        public void initialize(Comparable[] sample, Comparable pV)
        {
            original = sample;
            int chunkSize = (int)Math.ceil(sample.length / (double)processorCount);
            boolean done = false;
            
            int workArrayCounter = 0;
            
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
            
            while(go.get() == 0) {}
            
            printStuff();
            
            leftTotal = calculateLeftTotal(workArray);
            rightTotal = calculateRightTotal(workArray);
            
            System.out.println("pV: " + pV);
            System.out.println("Left Total: " + leftTotal);
            System.out.println("Right Total: " + rightTotal);
        }
        
        public void printStuff()
        {
            for (int i = 0; i < workArray.length; i++)
            {
                WorkerThread wt = workArray[i];
                Arrays.toString(wt.list);
                System.out.println("store index: " + wt.storeIndex);
                System.out.println("left: " + wt.left);
                System.out.println("right: " + wt.right);
            }
        }
        
	public void goParallel(Comparable pV, boolean goLeft) throws InterruptedException
	{
            System.out.println();
            if (goLeft)
                System.out.println("go left");
            
            else
                System.out.println("go right");
            
            
            if (goLeft)
                goLeft(pV);
            
            else
                goRight(pV);
            while(go.get() == 0) {}
            
            
            printStuff();
            
            leftTotal = calculateLeftTotal(workArray);
            rightTotal = calculateRightTotal(workArray);
            
            System.out.println("pV: " + pV);
            System.out.println("Left Total: " + leftTotal);
            System.out.println("Right Total: " + rightTotal);
		// Thread pool size set by number of available processors
		//int processorCount = Runtime.getRuntime().availableProcessors();
		//ExecutorService executor = Executors.newFixedThreadPool(processorCount);
		
		// CyclicBarrier created using thread pool size (which is number of available processors)
		//CyclicBarrier cb = new CyclicBarrier(processorCount, new CyclicBarrierRunnable());
		
		//int chunkSize = (int)Math.ceil(sample.length / (double)processorCount);
		//boolean done = false;
		//WorkerThread[] workArray = new WorkerThread[processorCount];
		//int workArrayCounter = 0;
		
		//int pV = generateRandom(sample);
		
		// Create worker threads and populate the worker thread array so they can be reused later
		/*for (int i = 0; !done; i += chunkSize, workArrayCounter++)
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
		}*/
		
		//done = false;
		
		//while(go.get() == 0) {}
		
		//int leftOver = 0;
		//String same = null;
		
		// Run partition on sub-arrays until there are less than 20 potential k-th values left in all sub-arrays combined
		//while(!done)
		//{
			//int leftTotal = calculatePivotIndex(workArray);
			
/*			same = checkSameValues(workArray);
			
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
		//}
		
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
*/	}
	
        // if all WorkerThreads have no more active values, 'value' returns null
        // if at least one WorkerThread has an active value, 'value' returns the value
        public Comparable empty()
        {
            Comparable value = null;
            
            for (int i = 0; i < workArray.length; i++)
            {
                WorkerThread wt = workArray[i];
                
                if (wt.status)
                    value = wt.list[wt.left];
            }
            
            return value;
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
	public int goLeft(Comparable pV)
	{
            int leftOver = 0;
		
            for(int i = 0; i < workArray.length; i++)
            {
		if(workArray[i].status)
		{
                    if(workArray[i].left == workArray[i].storeIndex)
			workArray[i].status = false;
				
                    else
                    {
			workArray[i].right = workArray[i].storeIndex - 1;
			workArray[i].pivotValue = pV;
			leftOver += workArray[i].right - workArray[i].left + 1;
                    }
		}
			
                executor.execute(workArray[i]);
            }

            return leftOver;
	}
	
	// Select right portion of the array to find the k-th value
	public int goRight(Comparable pV)
	{
            int leftOver = 0;
		
            for(int i = 0; i < workArray.length; i++)
            {
		if(workArray[i].status)
		{
                    if(workArray[i].right < workArray[i].storeIndex)
			workArray[i].status = false;
					
                    else
                    {
			workArray[i].left = workArray[i].storeIndex;
			workArray[i].pivotValue = pV;
			leftOver += workArray[i].right - workArray[i].left + 1;
                    }
                }
			
		executor.execute(workArray[i]);
            }

            return leftOver;
	}
	
	// Choose and return random value from the input list
/*	public static int generateRandom(int[] list)
	{
		int randomIndex = (int)(Math.random()*list.length);

		return list[randomIndex];
	}
*/	
	// Calculate the total number of values left of the pivot index
	public static int calculateLeftTotal(WorkerThread[] wt)
	{
		int total = 0;
		
		for(int i = 0; i < wt.length; i++)
		{
			if(wt[i].status)
				total += wt[i].storeIndex - wt[i].left;
		}
			
		return total;
	} 
        
        // Calculate the total number of values right of the pivot index
	public static int calculateRightTotal(WorkerThread[] wt)
	{
            int total = 0;
		
            for(int i = 0; i < wt.length; i++)
            {
                if(wt[i].status)
                    total += wt[i].right - wt[i].storeIndex + 1;
            }
			
            return total;
	} 
	
	// Check if all 'status = true' sub-arrays are left with same values
	public String checkSameValues()
	{
		String temp = null;
		
		for(int i = 0; i < workArray.length; i++)
		{
			if(workArray[i].status)
			{
				if(!workArray[i].sameValues)
					return null;
				
				else
				{
					if(i == 0)
						temp = workArray[i].sValue;
					
                                        else if(temp != null)
                                        {
                                            if(!temp.equals(workArray[i].sValue))
                                                return null;
                                        }
				}
			}
		}
		
		return temp;
	}    
}
