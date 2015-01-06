package sort.tasks;

import java.util.concurrent.Callable;

/**
 * A callable Merge Sort task. 
 * @author Richard Coan
 */
public class MergeTask extends Base implements Callable<Comparable[]>
{
    private Comparable[] a;
    private Comparable[] b;
    private boolean done = false;
    
    /**
     * Initializes the MergeTask.
     * @param a Sorted left side.
     * @param b Sorted right side.
     */
    public MergeTask(Comparable[] a, Comparable[] b)
    {
        this.a = a;
        this.b = b;
    }
    
    /**
     * Initializes the MergeTask.
     * @param a Edge Case with a length of 1.
     */
    public MergeTask(Comparable[] a)
    {
        this.a = a;
        if(a.length > 1) throw new IllegalArgumentException("[Array Too Large] Use a smaller M-size or an alternative initial sort.");
        done = true;
    }
    
    /**
     * Initializes the MergeTask.
     * @param a the left comparable element.
     * @param b the right comparable element.
     */
    public MergeTask(Comparable a, Comparable b)
    {
        this.a = new Comparable[1];
        this.b = new Comparable[1];
        this.a[0] = a;
        this.b[0] = b;
    }
    
    @Override
    public Comparable[] call() throws Exception
    {
        if(done) return a;  //Edge Case...
        
        //Initialize Local Varibles
        Comparable[] results = new Comparable[a.length+b.length];
        int k = 0, i = 0, j = 0;
        
        //Merge Sort
        while(i < a.length && j < b.length)
        {
            if(b[j].compareTo(a[i]) < 0)
            {
                results[k] = (b[j]);
                j++;
                k++;
            }
            else
            {
                results[k] = (a[i]);
                i++;
                k++;
            }
        }

        while(i < a.length)
        {
            results[k] = (a[i]);
            i++;
            k++;
        }

        while(j < b.length)
        {
            results[k] = (b[j]);
            j++;
            k++;
        }
        
        return results;
    }    
}