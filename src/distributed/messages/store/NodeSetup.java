package distributed.messages.store;

import java.io.Serializable;

/**
 * Node Setup information, Sub-array and Pivot Value.
 * @author Richard Coan
 */
public class NodeSetup implements Serializable {
    public Comparable pv = 0;
    public Comparable[] sub = null;
}