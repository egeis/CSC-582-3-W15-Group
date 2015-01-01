package sort;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Parallel Merge Sort
 * @author Richard Coan
 */
public class ParallelMergeSort extends MergeSort
{
    //Global Private Varibles
    private static ExecutorService executor;
    private int depth = 0;
    
    //Global Public Varibles
    public static int MAX_SIZE = 0;
      
    /**
     * Initialize Parallel Merge Sort.
     * @param source the source array.
     */
    public ParallelMergeSort(Comparable[] source) 
    {
        super(source);
        if(executor == null) executor = Executors.newCachedThreadPool();
    }
    
    /**
     * Initialize Parallel Merge Sort.
     * @param source the source array.
     * @param depth the current depth.
     * @param start the starting index.
     * @param end the last index.
     */
    public ParallelMergeSort(Comparable[] source, int depth, int start, int end)
    {
        super(source, start, end);
        this.depth = depth;
        if(executor == null) executor = Executors.newCachedThreadPool();
    }

    
    /**
     * @see java.lang.Runnable
     */
    @Override
    public void run()
    {
        if(end <= start || end - start < 2) return;    //Recursive Ending
        int mid = start + (end - start) / 2;
        
        //System.out.println("INFO: Start:"+start+" End:"+end);
        if((end - start) <= MAX_SIZE && MAX_SIZE != 0) {
            System.out.println("S:"+"Range Size"+(end - start)+" Start:"+start+" End:"+end);
            InsertionSort.sort(source, start, end);
            
            return;
        }        
        
        if(depth < 2)
        {
            ParallelMergeSort pmsa = new ParallelMergeSort(source, depth+1, start, mid);
            ParallelMergeSort pmsb = new ParallelMergeSort(source, depth+1, mid+1, end);
            
            Future<?> fa = executor.submit(pmsa);
            Future<?> fb = executor.submit(pmsb);
            
            try
            {
                //Retrieves the Future Completion (unhandled) and waits if necessary.
                fa.get();
                fb.get();
            }
            catch (ExecutionException | InterruptedException e)
            {
                System.out.println("[Error] An Exception took place while sorting.  Defaulting to manual run().");
                e.printStackTrace();
                
                //Runs the Sub Arrays Merge Sort as prompted by an Exception.
                pmsa.run();
                pmsb.run();
            }
        }
        else
        {
            ParallelMergeSort pmsa = new ParallelMergeSort(source, depth+1, start, mid);
            ParallelMergeSort pmsb = new ParallelMergeSort(source, depth+1, mid+1, end);
     
            pmsa.run();
            pmsb.run();            
        }
        
        merge(source, start, mid, end); 
    }
    
    /**
     * The Main Java Method.
     * @param args command-line argument. (Unused).
     */
    public static void main(String[] args)
    {        
        //Initialize Local Varibles and Classes.
        long start, end;
        final int length = 50;
        Integer[] test = new Integer[length];
        Random rand = new Random();
        
        Map<Comparable, Integer> freq_start;
        Map<Comparable, Integer> freq_end;
        
         //Create the Random Array.
        for(int i = 0; i < length; i++) {
            test[i] = Math.abs(rand.nextInt());
        }
        
        //Initialze Parallel Merge Sort.
        ParallelMergeSort pms = new ParallelMergeSort(test);
        MAX_SIZE = 2;
        freq_start = pms.getFrequency();
        
        System.out.println("[Starting] Is Sorted? "+ pms.isSorted());
        System.out.println("[Starting] Frequency: "+freq_start.toString());
        
        System.out.println(Arrays.toString(test));
        
        //Start Running Merge Sort.
        System.out.print(ANSI_BLUE+"Starting Sort..."+ANSI_RESET);
        start = System.currentTimeMillis(); //Start Time
        pms.run();
        end = System.currentTimeMillis();   //End Time
        freq_end = pms.getFrequency();
        System.out.println(ANSI_BLUE+"Done!"+ANSI_RESET);
                
        System.out.println(Arrays.toString(test));
        System.out.println("[Completed] Frequency: "+freq_end.toString());
        System.out.println("[Completed] The algorithm took: " + ANSI_RED + (end - start) + ANSI_RESET  +" ms");
        System.out.println("[Completed] Frequency Match: "+ANSI_RED+((freq_start.equals(freq_end))?true:false)+ANSI_RESET);
        System.out.println("[Completed] Is Sorted? "+ANSI_RED+pms.isSorted()+ANSI_RESET);
        
        executor.shutdown();    //Shuting Down executor
    }
}