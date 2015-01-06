package sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import sort.tasks.Base;
import sort.tasks.MergeTask;

/**
 * Parallel Merge Sort
 * @author Richard Coan
 */
public class ParallelMergeSort extends Base
{
    //Global Private Varibles
    private static ExecutorService executor;
    private int m = 0;
    private boolean hybrid = false;
    private Comparable[] source;    
    private List<FutureTask<Comparable[]>> list = new ArrayList();
        
    /**
     * Initialize Parallel Merge Sort.
     * @param source the source array.
     */
    public ParallelMergeSort(Comparable[] source) 
    {
        this.source = source;
        if(executor == null) executor = Executors.newCachedThreadPool();
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
        Comparable[] results = new Comparable[source.length];
        
        //Bottom UP Merge Sort Start 
        for(int i = 0; i < source.length; i+=2) {            
            FutureTask<Comparable[]> ft = new FutureTask(new MergeTask(source[i], source[i+1]));
            list.add(ft);
            executor.execute(ft);
        }
        
        //Edge Case Queue.
        if(source.length % 2 == 1) {
            System.out.println("[DEBUG] Odd Values");
            FutureTask<Comparable[]> fa = list.remove(0);
            Comparable[] a;
            
            try {
                a = fa.get();
            } catch (ExecutionException | InterruptedException e) {
                System.out.println("[Error]:"+e.getCause());
                e.printStackTrace();
                return null;
            }
            
            Comparable[] b = new Comparable[1];
            b[0] = source[source.length - 1];
            
            FutureTask<Comparable[]> ft = new FutureTask(new MergeTask(a, b));
            list.add(ft);
            executor.execute(ft);
        }
        
        while(list.size() > 1) {
            FutureTask<Comparable[]> fa = list.remove(0);
            FutureTask<Comparable[]> fb = list.remove(0);
            
            Comparable[] a;
            Comparable[] b;
            
            try {
                a = fa.get();
                b = fb.get();
             } catch (ExecutionException | InterruptedException e) {
                System.out.println("[Error]:"+e.getCause());
                e.printStackTrace(); 
                return null;
             } 
            
            FutureTask<Comparable[]> ft = new FutureTask(new MergeTask(a,b));
            list.add(ft);
            executor.execute(ft);
        }
        executor.shutdown();
        //System.out.println("[DEBUG] Ending with " + list.size() + " items.");
        FutureTask<Comparable[]> ft = list.remove(0);
                
        try {
            while(!ft.isDone()) {}
            return ft.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        } 
    }
    
    
    /**
     * @see java.lang.Runnable
     */
    /*@Override
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
    }*/
    
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
        freq_end = pms.getFrequency(results);
        System.out.println(ANSI_BLUE+"Done!"+ANSI_RESET);
        
        //System.out.println(Arrays.toString(results));
        //System.out.println(freq_end.toString());
        System.out.println("[Completed] The algorithm took: " + ANSI_RED + (end - start) + ANSI_RESET  +" ms");
        System.out.println("[Completed] Frequency Match: "+ANSI_RED+((freq_start.equals(freq_end))?true:false)+ANSI_RESET);
        System.out.println("[Completed] Is Sorted? "+ANSI_RED+pms.isSorted(results)+ANSI_RESET);
        
        executor.shutdown();    //Shuting Down executor
    }
}