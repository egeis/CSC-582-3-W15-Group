package distributed.messages;

import distributed.messages.store.*;

/**
 * Creates the Packet Object to send across nodes.
 * @author Richard Coan
 */
public class Message {
    //Node STATE Messages
    public final static int NODE_STARTING = 1;          // NODE STATE is starting.
    public final static int NODE_WAITING_INPUT = 2;     // NODE STATE is waiting for input.
    public final static int NODE_READY =  3;            // NODE STATE is ready to start. (****NOT Necessary****)
    public final static int NODE_BUSY = 4;              // NODE STATE is busy.
    public final static int NODE_ERROR = 5;             // NODE STATE for an error.
    public final static int NODE_WAITING_NEXT = 6;      // Node STATE is waiting for next step.
    public final static int NODE_SHUTDOWN = 10;         // NODE STATE is terminating.
    
    //SETTER Messages
    public final static int SET_INPUT = 11;      //Setup the input array
    public final static int SET_START = 12;      //Start Working..
    public final static int SET_GO_LEFT = 13;    //GO LEFT
    public final static int SET_GO_RIGHT = 14;   //GO Right
    public final static int SET_RESULTS = 15;
    public final static int SET_TERMINATION = 20;//Starts the Node Shutdown
    
    //GETTER Messages
    public final static int GET_STATE = 21;      //Requests NODE Reply with STATE.
        
    /**
     * Gets a Packet containing a message type, and Node Setup information.
     * @param type of message
     * @param a the sub array.
     * @param pv the pivot value.
     * @return the packet object.
     */
    public static Packet getPacket(int type, Comparable[] a, Comparable pv)
    {
        Packet p = new Packet();
        p.type = type;
        
        NodeSetup s = new NodeSetup(); 
        s.pv = pv;
        s.sub = a;
        p.pack = s;
        
        return p;
    }
    
    /**
     * 
     * @param type
     * @param leftValues
     * @param rightValues
     * @param storeValue
     * @return 
     */
    public static Packet getPacket(int type, int leftValues, int rightValues, int storeValue)
    {
        Packet p = new Packet();
        p.type = type;
        
        NodeResults r = new NodeResults();
        r.leftValues = leftValues;
        r.rightValues = rightValues;
        r.storeValue = storeValue;
        
        p.pack = r;
        
        return p;
    }
    
    /**
     * Gets a Packet containing only a message type.
     * @param type of message.
     * @return 
     */
    public static Packet getPacket(int type)
    {
        Packet p = new Packet();
        p.type = type;
        p.pack = null;
        return p;
    }
}