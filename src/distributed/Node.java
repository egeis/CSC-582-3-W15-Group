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
public class Node {
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
    private int storeIndex = 0;
    
    private Comparable recommendLeft = null;
    private Comparable recommendRight = null;
    
    private Node()
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
    public void partition()
    {
        storeIndex = left;
        
        for(int i = left; i <= right; i++)
        {
            if (arr[i].compareTo(pv) == -1)
            {
                swap(storeIndex, i);
                storeIndex++;
            }
        }
        
        recommendLeft = null;
        recommendRight = null;
        
        if((storeIndex - left) > 0) 
        {
            recommendLeft = arr[(int)(Math.floor(Math.random() * (storeIndex - left)))];
            System.out.println("left recommendation: " + recommendLeft);
        }
        
        if((right - storeIndex + 1) > 0)
        {
            recommendRight = arr[(int)((storeIndex - 1) + (Math.floor(Math.random() * (right - storeIndex + 1))))];
            System.out.println("right recommendation: " + recommendRight);
        }
        
        System.out.println("store index: " + storeIndex);
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
    
    private void parsePacket(Packet p)
    {
        int type = p.type;
        Packet reply;
        
        switch(type)
        {
            case Message.SET_INPUT:
                NodeSetup s = (NodeSetup) p.pack;
                arr = s.sub;
                pv = s.pv;
                System.out.println(Arrays.toString(arr));
                System.out.println("Starting...pv:"+pv);
                
                right = arr.length - 1;
                partition();
                reply = Message.getPacket(Message.SET_RESULTS, storeIndex - left, right - storeIndex + 1, recommendRight, recommendLeft);
                sendPacket(reply);
                
                break;
            case Message.SET_GO_LEFT:
               NodePivot pl = (NodePivot) p.pack;
               pv = pl.pv;
                              
               right = storeIndex;
               System.out.println("SET_GO_LEFT...right:"+storeIndex+"PV:"+pv);
               System.out.println(Arrays.toString(arr));
                partition();
                reply = Message.getPacket(Message.SET_RESULTS, storeIndex - left, right - storeIndex + 1, recommendRight, recommendLeft);
                sendPacket(reply);
               
                break;
            case Message.SET_GO_RIGHT:
                NodePivot pr = (NodePivot) p.pack;
                pv = pr.pv;
                
                left = storeIndex;
                System.out.println("SET_GO_RIGHT...left:"+storeIndex+"PV:"+pv);
                System.out.println(Arrays.toString(arr));
                
                partition();
                reply = Message.getPacket(Message.SET_RESULTS, storeIndex - left, right - storeIndex + 1, recommendRight, recommendLeft);
                sendPacket(reply);
                
                break;
            case Message.SET_TERMINATION:
                shutdown = true;
            break;
            default:
                LOGGER.log(Level.SEVERE, null, "Unknown Packet: "+type+" "+p.toString());
        }
    }
    
    public static void main(String[] args)
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
        Node node = new Node();
    }
}