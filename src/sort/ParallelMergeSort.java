package sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import sort.tasks.Base;
import sort.tasks.MergeTask;

/**
 * Parallel Merge Sort
 * @author Richard Coan
 */
public class ParallelMergeSort extends Base
{
    //Global Private Varibles
    private static ThreadPoolExecutor executor;
    private static CompletionService service;
    private int m = 0;
    private boolean hybrid = false;
    private Comparable[] source;    
        
    /**
     * Initialize Parallel Merge Sort.
     * @param source the source array.
     */
    public ParallelMergeSort(Comparable[] source) 
    {
        this.source = source;
        if(executor == null) executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);
        if(service == null) service = new ExecutorCompletionService(executor);
    }
    
    /**
     * Initialize Parallel Merge Sort.
     * @param source the source array.
     * @param m Sublist size maximum to begin insertion Sort.  Must be greater than 2.
     */
    public ParallelMergeSort(Comparable[] source, int m) 
    {        
        this(source);
        if(m > 1) this.m = m;
        else throw new IllegalArgumentException("M size too small.");
        this.hybrid = true;
    }
    
    public Comparable[] sort() {
        int tasks = 0;
        
        for(int i = 0; i < source.length; i+=2) {            
            service.submit(new MergeTask(source[i], source[i+1]));
            tasks++;
        }
        
        //Edge Case Queue.
        if(source.length % 2 == 1) {
            Comparable[] a;
            
            try {
                Future<Comparable[]> fa = service.take(); 
                a = fa.get();
            } catch (ExecutionException | InterruptedException e) {
                System.out.println("[Error]:"+e.getCause());
                e.printStackTrace();
                return null;
            }
            
            Comparable[] b = new Comparable[1];
            b[0] = source[source.length - 1];
            
            service.submit(new MergeTask(a,b));
        }
        
        while(tasks > 1) {
            Comparable[] a;
            Comparable[] b;
            
            try {
                Future<Comparable[]> fa = service.take(); 
                Future<Comparable[]> fb = service.take(); 
                a = fa.get();
                b = fb.get();
            } catch (ExecutionException | InterruptedException e) {
                System.out.println("[Error]:"+e.getCause());
                e.printStackTrace();
                return null;
            }
            
            service.submit(new MergeTask(a,b));
            tasks--;
        }
                          
        try {
            Future<Comparable[]> ft = service.take();
            return ft.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
        
    /**
     * The Main Java Method.
     * @param args command-line argument. (Unused).
     */
    public static void main(String[] args)
    {        
        //Initialize Local Varibles and Classes.
        long start, end;
        final int length = 1000000;
        Integer[] test = new Integer[length];
        Random rand = new Random();
        ParallelMergeSort pms;
        Map<Comparable, Integer> freq_start;
        Map<Comparable, Integer> freq_end;
        
         //Create the Random Array.
        for(int i = 0; i < length; i++) {
            test[i] = (int) (rand.nextDouble() * 10);
        }
        
        //Initialze Parallel Merge Sort.
        pms = new ParallelMergeSort(test, 10);
        freq_start = pms.getFrequency(test);
        //System.out.println(freq_start.toString());
        //System.out.println(Arrays.toString(test));
        
        //Start Running Merge Sort.
        System.out.print(ANSI_BLUE+"Starting Sort..."+ANSI_RESET);
        start = System.currentTimeMillis(); //Start Time
        Comparable[] results = pms.sort();
        end = System.currentTimeMillis();   //End Time
        System.out.println(ANSI_BLUE+"Done!"+ANSI_RESET);
        
        //Completed Running...Output Results
        freq_end = pms.getFrequency(results);
        //System.out.println(Arrays.toString(results));
        //System.out.println(freq_end.toString());
        System.out.println("[Completed] The algorithm took: " + ANSI_RED + (end - start) + ANSI_RESET  +" ms");
        System.out.println("[Completed] Frequency Match: "+ANSI_RED+((freq_start.equals(freq_end))?true:false)+ANSI_RESET);
        System.out.println("[Completed] Is Sorted? "+ANSI_RED+pms.isSorted(results)+ANSI_RESET);
                
        executor.shutdown();    //Shuting Down executor
    }
}