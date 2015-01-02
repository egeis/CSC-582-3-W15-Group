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
    private static int insertion_depth;
        
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
     * @param m Sublist size maximum to begin insertion Sort.  Must be greater than 2.
     */
    public ParallelMergeSort(Comparable[] source, int m) 
    {
        super(source);
        
        //Calculate
        int c = source.length;
        while(c > m && m > 1) {
            c = c / 2;
            insertion_depth++;
        } 
        
        System.out.println("Target Insertion Depth: "+insertion_depth+" Using M:"+m);
        
        if(executor == null) executor = Executors.newCachedThreadPool();
    }
    
    /**
     * Initialize Parallel Merge Sort.
     * @param source The source array.
     * @param depth The current depth.
     * @param start The starting index.
     * @param end The last index.
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
        if(end <= start) return;    //Recursive Ending        
        int mid = start + (end - start) / 2;
        
        //Insertion Sort for M sized or less arrays.
        if(depth >= insertion_depth && insertion_depth != 0) {
            System.out.println("Insertion Sort: Depth: "+depth+" Start:"+start+" End:"+end);
            InsertionSort isa = new InsertionSort(source, start, mid);
            InsertionSort isb = new InsertionSort(source, mid+1, end);
            
            isa.run();
            isb.run();
        } else
        
        //Parallel Merge Sort
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
        final int length = 40;
        Integer[] test = new Integer[length];
        Random rand = new Random();
        ParallelMergeSort pms;
        Map<Comparable, Integer> freq_start;
        Map<Comparable, Integer> freq_end;
        
        /*
        System.out.println("Merge Sort");
         //Create the Random Array.
        for(int i = 0; i < length; i++) {
            test[i] = (int) (rand.nextDouble() * 10);
        }
        
        //Initialze Parallel Merge Sort.
        pms = new ParallelMergeSort(test);
        freq_start = pms.getFrequency();
        
        System.out.println(Arrays.toString(test));
        System.out.println(freq_start.toString());
        
        //Start Running Merge Sort.
        System.out.print(ANSI_BLUE+"Starting Sort..."+ANSI_RESET);
        start = System.currentTimeMillis(); //Start Time
        pms.run();
        end = System.currentTimeMillis();   //End Time
        freq_end = pms.getFrequency();
        System.out.println(ANSI_BLUE+"Done!"+ANSI_RESET);
        
        System.out.println(freq_end.toString());
        System.out.println(Arrays.toString(test));
        System.out.println("[Completed] The algorithm took: " + ANSI_RED + (end - start) + ANSI_RESET  +" ms");
        System.out.println("[Completed] Frequency Match: "+ANSI_RED+((freq_start.equals(freq_end))?true:false)+ANSI_RESET);
        System.out.println("[Completed] Is Sorted? "+ANSI_RED+pms.isSorted()+ANSI_RESET);
        */ 
        
        System.out.println("Merge Sort with Insertion Sort");
         //Create the Random Array.
        for(int i = 0; i < length; i++) {
            test[i] = (int) (rand.nextDouble() * 10);
        }
        
        //Initialze Parallel Merge Sort.
        pms = new ParallelMergeSort(test,10);
        freq_start = pms.getFrequency();
        
        System.out.println(Arrays.toString(test));
        System.out.println(freq_start.toString());
        
        //Start Running Merge Sort.
        System.out.print(ANSI_BLUE+"Starting Sort..."+ANSI_RESET);
        start = System.currentTimeMillis(); //Start Time
        pms.run();
        end = System.currentTimeMillis();   //End Time
        freq_end = pms.getFrequency();
        System.out.println(ANSI_BLUE+"Done!"+ANSI_RESET);
        
        System.out.println(freq_end.toString());
        System.out.println(Arrays.toString(test));
        System.out.println("[Completed] The algorithm took: " + ANSI_RED + (end - start) + ANSI_RESET  +" ms");
        System.out.println("[Completed] Frequency Match: "+ANSI_RED+((freq_start.equals(freq_end))?true:false)+ANSI_RESET);
        System.out.println("[Completed] Is Sorted? "+ANSI_RED+pms.isSorted()+ANSI_RESET);
        
        executor.shutdown();    //Shuting Down executor
    }
}