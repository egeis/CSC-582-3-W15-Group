package distributed;

import distributed.messages.Message;
import distributed.messages.Packet;
import distributed.messages.store.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class ParNode {
    private static final int INIT_PORT_NUMBER = Initiator.SERVER_PORT_NUMBER;
    
    private static int PORT_NUMBER;
    private static Logger LOGGER;
    
    private boolean shutdown = false;
        
    private ServerSocket server;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    
    private Comparable[] arr;
    private Comparable pv;
    
    private int left = 0;
    private int right = 0;
    //private int storeIndex = 0;
    
    private Comparable recommendLeft = null;
    private Comparable recommendRight = null;
    
    private ParallelQuickSelect pqs = new ParallelQuickSelect();
    
    private boolean goLeft = false;
    
    private ParNode() throws InterruptedException
    {        
        try {
            server = new ServerSocket(PORT_NUMBER);
            
            System.out.println(server.getInetAddress());
            System.out.println(server.getLocalPort());
            
            while(!shutdown)
            {
                Socket socket = server.accept();
                input = new ObjectInputStream(socket.getInputStream());
                Packet p = (Packet) input.readObject();
                parsePacket(p);
                socket.close();
            }
            
            System.out.println("Exiting");
            
            server.close();
        } catch (IOException | ClassNotFoundException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Swaps two values in the array.
     * @param x
     * @param y
     */
     public void swap(int x, int y)
    {
        Comparable temp = arr[y];

        arr[y] = arr[x];
        arr[x] = temp;
    }

     /**
      * Partition
      * @return the store index.
      */
    public boolean partition(boolean initialRun) throws InterruptedException
    {
        String same = null;
        
        if(initialRun)
            pqs.initialize(arr, pv);
        
        else
        {
            same = pqs.checkSameValues();

            if(same == null)
            {
                pqs.goParallel(pv, goLeft);
                /*
                storeIndex = left;

                for(int i = left; i <= right; i++)
                {
                    if (arr[i].compareTo(pv) == -1)
                    {
                        swap(storeIndex, i);
                        storeIndex++;
                    }
                }
                */
            }
        }
        
        recommendLeft = null;
        recommendRight = null;

        if(pqs.leftTotal > 0) 
            recommendLeft = pqs.setRecommendLeft();

        if(pqs.rightTotal > 0)
            recommendRight = pqs.setRecommendRight();

        if (same == null)
            return false;
        
        else
            return true;
    }
    
    private boolean checkSameValue()
    {
        boolean same = true;
        Comparable temp = arr[left];
        
        for (int i = left; i <= right; i++)
        {
            if (arr[i] != temp)
                same = false;
        }
        
        return same;
    }
    
    /**
     * Sends a packet of data to the initiator.
     * @param p the packet.
     */
    private void sendPacket(Packet p)
    {        
        try {
            Socket out = new Socket("localhost", INIT_PORT_NUMBER);
            output = new ObjectOutputStream(out.getOutputStream()); 
            output.writeObject(p);
            output.flush();
            out.close();
        } catch (IOException ex) {
           LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
    private void parsePacket(Packet p) throws InterruptedException
    {
        int type = p.type;
        Packet reply;
        
        switch(type)
        {
            case Message.SET_INPUT:
                NodeSetup s = (NodeSetup) p.pack;
                arr = s.sub;
                pv = s.pv;
                right = arr.length - 1;
                
                if(partition(true))
                    reply = Message.getPacket(Message.SET_SAMEVALUE);
                
                else 
                    reply = Message.getPacket(Message.SET_RESULTS, pqs.leftTotal, pqs.rightTotal, recommendRight, recommendLeft);
                
                sendPacket(reply);
                
                break;
            case Message.SET_GO_LEFT:
                NodePivot pl = (NodePivot) p.pack;
                pv = pl.pv;
                goLeft = true;              
                //right = storeIndex - 1;
                
                if(partition(false))
                    reply = Message.getPacket(Message.SET_SAMEVALUE);
                
                else 
                    reply = Message.getPacket(Message.SET_RESULTS, pqs.leftTotal, pqs.rightTotal, recommendRight, recommendLeft);
                
                sendPacket(reply);
               
                break;
            case Message.SET_GO_RIGHT:
                NodePivot pr = (NodePivot) p.pack;
                pv = pr.pv;
                goLeft = false;
                //left = storeIndex;
                
                if(partition(false))
                    reply = Message.getPacket(Message.SET_SAMEVALUE);
                
                else 
                    reply = Message.getPacket(Message.SET_RESULTS, pqs.leftTotal, pqs.rightTotal, recommendRight, recommendLeft);
                
                sendPacket(reply);
                
                break;
            case Message.GET_VALUE:
                Comparable value = pqs.empty();
                
                if (value == null)
                    reply = Message.getPacket(Message.SET_KVALUE, null);
             
                else
                    reply = Message.getPacket(Message.SET_KVALUE, value);
                
                sendPacket(reply);
                
                break;
            case Message.SET_TERMINATION:
                shutdown = true;
            break;
            default:
                LOGGER.log(Level.SEVERE, null, "Unknown Packet: "+type+" "+p.toString());
        }
    }
    
    public static void main(String[] args) throws InterruptedException
    {
        if(args.length > 0)
        {
            PORT_NUMBER = Integer.parseInt(args[0]);
        } 
        else 
        {
            System.exit(1);
        }
        
        LOGGER = Logger.getLogger(Node.class.getName()+PORT_NUMBER);
        ParNode node = new ParNode();
    }
}