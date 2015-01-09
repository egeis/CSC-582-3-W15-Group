package sort;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import sort.output.AnsiColor;
import sort.output.TestArray;
import sort.tasks.Base;
import sort.tasks.InsertionTask;
import sort.tasks.MergeTask;

/**
 * Concurrent Merge Sort with Hybrid Alternative.
 * @author Richard Coan
 */
public class ParallelMergeSort extends Base
{
    //Global Private Varibles
    private static ThreadPoolExecutor executor;
    private static CompletionService service;
    private int m = 0;
    private Comparable[] source; 
    private int initSort;

    //Constent Hybrid Sort Types.
    public static final int MERGE_SORT = 0;
    public static final int INSERTION_SORT = 1;
     
    /**
     * Initialize Parallel Merge Sort.
     * @param source the source array.
     */
    public ParallelMergeSort(Comparable[] source) 
    {
        this.source = source;
        this.m = 2;
        this.initSort = MERGE_SORT;
        if(executor == null) executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);
        if(service == null) service = new ExecutorCompletionService(executor);
    }
    
    /**
     * Initialize Parallel Merge Sort.
     * @param source the source array.
     * @param initSort the selected hybrid sort.
     * @param m Sublist size of the Hybrid Sort. 
     */
    public ParallelMergeSort(Comparable[] source, int initSort, int m) 
    {        
        this(source);
        this.initSort = initSort;
        this.m = m;
        
        //Checks if the M-size is valid for the given initial sort;
        switch(initSort)
        {
            case 0: //Merge Sort
                if( m > 2) throw new IllegalArgumentException("[Merge Sort] M-size too large.");
                if( m <= 0) throw new IllegalArgumentException("[Merge Sort] M-size too small.");
                break;
            case 1: //Insertion Sort
                if( m < 2) throw new IllegalArgumentException("[Insertion Sort] M-size too small.");
                break;                
        }
    }
    
    /**
     * Start sorting the initialized array.
     * @return the sorted result or null if the sort failed. 
     */
    public Comparable[] sort()
    {
        int tasks = 0;
        
        /** Generate the initial tasks. **/
        int i = 0;
        for( ; i < source.length; i+=m)
        {            
            if(initSort == 0)
                service.submit(new MergeTask(source[i], source[i+1]));
            else if(initSort == 1)
                service.submit(new InsertionTask(Arrays.copyOfRange(source, i, i+m)));
            
            tasks++;            
        }
        
        /** Detects any Edge Cases and generates a task **/
        i -= m;
        if(i + m >= source.length && i + m < source.length)
        {
            Comparable[] last = Arrays.copyOfRange(source, i, (source.length - 1));
            
            if(initSort == 0) service.submit(new MergeTask(last));
            else if(initSort == 1) service.submit(new InsertionTask(last));
            
            tasks++;
        }

        /** Merge Sorts all completed tasks until there is only one left **/
        while(tasks > 1)
        {
            Comparable[] a;
            Comparable[] b;

            try
            {
                Future<Comparable[]> fa = service.take(); 
                Future<Comparable[]> fb = service.take(); 
                a = fa.get();
                b = fb.get();
            }
            catch (ExecutionException | InterruptedException e)
            {
                System.out.println("[Error]:"+e.getCause());
                e.printStackTrace();
                return null; //Sorting Failed.
            }

            service.submit(new MergeTask(a,b));
            tasks--;
        }
          
        /** retrieves the final results **/
        try
        {
            Future<Comparable[]> ft = service.take();
            executor.shutdown();
            return ft.get();
        } 
        catch (ExecutionException | InterruptedException e)
        {
            System.out.println("[Error]:"+e.getCause());
            e.printStackTrace();
            return null;    //Sorting Failed.
        }
    }
        
    /**
     * The Main Java Method.
     * @param args command-line argument. (Unused).
     */
    public static void main(String[] args)
    {        
        /*Initialize Local Varibles and Classes.*/
        long start, end;
        final int length = 1000000;
        ParallelMergeSort pms;
        Map<Comparable, Integer> freq_start;
        Map<Comparable, Integer> freq_end;
        
        Integer[] test = TestArray.generate(length);  
        
        /*Initialze Parallel Merge Sort.*/
        pms = new ParallelMergeSort(test);
        freq_start = pms.getFrequency(test);
        
        /*Start Running Merge Sort.*/
        System.out.print(AnsiColor.BLUE+"Starting Sort..."+AnsiColor.RESET);
        start = System.currentTimeMillis(); //Start Time
        Comparable[] results = pms.sort();
        end = System.currentTimeMillis();   //End Time
        System.out.println(AnsiColor.BLUE+"Done!"+AnsiColor.RESET);
        
        /*Completed output results*/
        freq_end = pms.getFrequency(results);
        System.out.println("[Completed] The algorithm took: " + AnsiColor.RED + (end - start) + AnsiColor.RESET  +" ms");
        System.out.println("[Completed] Frequency Match: "+AnsiColor.RED+(freq_start.equals(freq_end))+AnsiColor.RESET);
        System.out.println("[Completed] Is Sorted? "+AnsiColor.RED+pms.isSorted(results)+AnsiColor.RESET);
    }
}