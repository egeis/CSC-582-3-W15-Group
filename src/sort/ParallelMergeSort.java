package sort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Parallel Merge Sort
 * @author Richard Coan
 */
public class ParallelMergeSort<T> extends MergeSort<T>
{
    //Global Private Varibles
    private static ExecutorService executor;
    private int depth = 0;
    private int max_depth = 0;
      
    /**
     * Initialize Parallel Merge Sort.
     * @param list the source array.
     * @param compare a comparator.
     * @param max_depth [Optional] the maximum depth before Insertion Sort is used.
     */
    public ParallelMergeSort(List<T> list, Comparator<T> compare, int max_depth) 
    {
        this(list, compare);
        this.max_depth = max_depth;
    }
    
    /**
     * Initialize Parallel Merge Sort.
     * @param list the source array.
     * @param compare a comparator.
     * @param depth [Optional] the current depth.
     * @param max_depth [Optional] the maximum depth before Insertion Sort is used.
     */
    public ParallelMergeSort(List<T> list, Comparator<T> compare, int depth, int max_depth)
    {
        this(list, compare);
        this.depth = depth;
        this.max_depth = max_depth;
    }
    
    /**
     * Initialize Parallel Merge Sort.
     * @param list the source array.
     * @param compare a comparator.
     */
    public ParallelMergeSort(List<T> list, Comparator<T> compare)
    {
        super(list, compare);
        if(executor == null) executor = Executors.newCachedThreadPool();
    }

    /**
     * @see java.lang.Runnable
     */
    @Override
    public void run()
    {
        if(list.size() < 2) return; //Recurssion End.   
                
        //Dont over do the number of threads, limit parallel to upper parts of recussion.
        if(depth < 2)
        {
            //Divides the Array into two Sub Arrays to Merge Sort.
            ParallelMergeSort<T> pmsa = new ParallelMergeSort( list.subList(0, list.size()/2), compare, (depth+1), max_depth );
            ParallelMergeSort<T> pmsb = new ParallelMergeSort( list.subList(list.size()/2, list.size()), compare, (depth+1), max_depth );

            //Retrieves future varibles from the submitted runnable.
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

            //Merge Sorts the Results.
            merge(pmsa.get(), pmsb.get());
        } else {
            if(depth >= max_depth && max_depth != 0) {
                //Do some other sort like insertion.
            } else {
                //Divides the Array into two Sub Arrays to Merge Sort.
                ParallelMergeSort<T> pmsa = new ParallelMergeSort( list.subList(0, list.size()/2), compare, (depth+1), max_depth );
                ParallelMergeSort<T> pmsb = new ParallelMergeSort( list.subList(list.size()/2, list.size()), compare, (depth+1), max_depth );

                //Runs the Sub Arrays Merge Sort.
                pmsa.run();
                pmsb.run();
                
                //Merge Sorts the Results.
                merge(pmsa.get(), pmsb.get());
            }
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
        List<Integer> test = new ArrayList();
        Random rand = new Random();
        
        Comparator<Integer> compare = new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b){
               return a.compareTo(b);
            }
            
            @Override
            public boolean equals(Object a){
               return this == a;
            }
           };
        
        //Create the Random Array.
        for(int i = 0; i < length; i++) {
            test.add( (int) (rand.nextDouble() * 10) );
        }
        
        //Initialze Parallel Merge Sort.
        ParallelMergeSort pms = new ParallelMergeSort(test, compare);
        
        //Start Running Merge Sort.
        System.out.println("[Starting] Is Sorted? "+ pms.isSorted(test, compare));
        System.out.println("[Starting] Array Size:"+test.size());
        
        start = System.currentTimeMillis(); //Start Time
        pms.run();
        end = System.currentTimeMillis();   //End Time
        
        System.out.println("[Completed] The algorithm took: " + (end - start) + " ms");
        System.out.println("[Completed] Is Sorted? "+ pms.isSorted(test, compare));
        System.out.println("[Completed] Array Size:"+test.size());
        
        executor.shutdown();    //Shuting Down executor
    }
}