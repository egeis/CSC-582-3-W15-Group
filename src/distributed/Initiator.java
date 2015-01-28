package distributed;

import distributed.messages.Message;
import distributed.messages.Packet;
import distributed.messages.store.NodePivot;
import distributed.messages.store.NodeResults;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 */
public class Initiator {
    private static Logger logger;

    private Comparable[] arr;    
    private boolean completed = false;
    private long lastbeat = System.currentTimeMillis();
    
    public final static int SERVER_PORT_NUMBER = 8484;
    private static List<Integer> ports = new ArrayList<Integer>();
    
    private ServerSocket server;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    
    private static int K;
    private static int originalK;
   
    /**
     * 
     * @param arr 
     */
    private Initiator(Comparable[] arr)
    {        
        this.arr = arr;
        logger = Logger.getLogger(Initiator.class.getName());
    }
       
    /**
     * 
     */
    public void start()
    {
        try {
            server = new ServerSocket(SERVER_PORT_NUMBER);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        
        //Initial Pivot Valie
        Comparable pivotValue = arr[(int) (Math.floor(Math.random() * arr.length))];
        System.out.println(pivotValue);
        
        //Create Sub Arrays
        Packet[] packArray = generateInitMessages(pivotValue);
        
        int index = 0;
        for(Integer a : ports)
        {
            Packet p = packArray[index];
            System.out.println(p.toString());
            sendPacket(p,a);
            index++;
        }
        
        NodeResults[] nr = new NodeResults[ports.size()];
        int counter = 0;
        
        while(true) 
        {       
           //Logic for steps 5-9
            Socket socket;
            Packet p = new Packet();
            Packet replyPacket = new Packet();
            
            try {
                socket = server.accept();
                
                input = new ObjectInputStream(socket.getInputStream());
                p = (Packet) input.readObject();
                socket.close();
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(Initiator.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            nr[counter] = (NodeResults) p.pack;
            
            counter++;
            
            if(counter >= ports.size())
            {
                int left = 0;
                int right = 0;
                
                for (int j = 0; j < nr.length; j++)
                {
                    left += nr[j].leftValues;
                    right += nr[j].rightValues;
                }
                
                if (K == 1)
                {
                    replyPacket = Message.getPacket(Message.GET_VALUE);
                    completed = true;
                }
                
                else if (K <= left)
                    replyPacket = Message.getPacket(Message.SET_GO_LEFT, nr[0].leftValues);
                   
                else
                {
                    replyPacket = Message.getPacket(Message.SET_GO_RIGHT, nr[0].rightValues);
                    K -= left;
                }
                
                for (int i = 0; i < ports.size(); i++)
                {
                    sendPacket(replyPacket, ports.get(i));
                }
                
                counter = 0;
                
                //SEND STUFF TO NODEs
            }            
            if(completed) break;
        }
        
        counter = 0;
        NodePivot[] np = new NodePivot[ports.size()];
        
        while(true) 
        {  
            Socket socket;
            Packet p = new Packet();
            
            try {
                socket = server.accept();
                
                input = new ObjectInputStream(socket.getInputStream());
                p = (Packet) input.readObject();
                socket.close();
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(Initiator.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            np[counter] = (NodePivot) p.pack;
            
            counter++;
            
            if(counter >= ports.size()) 
                break;
        }
        
        Comparable kValue = null;
        
        for(int i = 0; i < np.length; i++)
        {
            if(np[i].pv != null)
            {
                kValue = np[i].pv;
                break;
            }
        }
        
        System.out.println(originalK + "-th value is " + kValue.toString());
        logger.log(Level.INFO, originalK + "-th value is " + kValue.toString());
                
        //Calls for Node Termination.
        for(Integer a : ports)
        {
            Packet p = Message.getPacket(Message.SET_TERMINATION);
            sendPacket(p, a);
        }
            
        try {    
            server.close(); //Close the Server, causes Socket to throw Exception.
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }    
    }
     
    /**
     * 
     * @param sample
     * @param pV
     * @return 
     */
    public Packet[] generateInitMessages(Comparable pV)
    {
        Packet[] packetArray = new Packet[ports.size()];
        int subArraySize = arr.length / ports.size();
        
        int j = 0;
        for(int i = 0; i < ports.size(); i++)
        {
            Comparable[] subArray1 = new Integer[subArraySize];
            System.arraycopy(arr, j, subArray1, 0, subArraySize);
            
            packetArray[i] = Message.getPacket(Message.SET_INPUT, subArray1, pV);
            j += subArraySize;
        }

        return packetArray; 
    }
    
    public Comparable choosePivot(int type, NodeResults[] nr)
    {
        if (type == Message.SET_GO_LEFT)
            return nr[0].leftValues;
        
        else
            return nr[0].rightValues;
    }
    
    /**
     * 
     * @param p
     * @param a 
     */
    private void sendPacket(Packet p, Integer port)
    {        
        try {
            Socket socket = new Socket("localhost", port);
            output = new ObjectOutputStream(socket.getOutputStream());                        
            output.writeObject(p);
            output.flush();
            socket.close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Cannot Connect to PORT:"+port);
        }
    }
        
    /**
     * 
     * @param args 
     */
    public static void main(String[] args)
    {
        final int LENGTH = 900;
        originalK = 500;
        K = originalK;
        
        //Remote Workers
        ports.add(1212);
        //ports.add(1213);
        //ports.add(1214);
        
        Integer[] sample = sort.output.TestArray.generate(LENGTH);
        Initiator init = new Initiator(sample);
        init.start();
    }
}