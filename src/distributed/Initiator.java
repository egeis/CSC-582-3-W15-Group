package distributed;

import distributed.messages.Message;
import distributed.messages.Packet;
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
        
        //Create Sup Arrays
        Packet[] packArray = generateInitMessages(arr, pivotValue);
        
        int i = 0;
        for(Integer a : ports)
        {
            Packet p = packArray[i];
            System.out.println(p.toString());
            sendPacket(p,a);
            i++;
        }
        
        while(true) 
        {            
           //Logic for steps 5-9
            Socket socket;
            try {
                socket = server.accept();
                input = new ObjectInputStream(socket.getInputStream());
                Packet p = (Packet) input.readObject();
                
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(Initiator.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //Have all nodes sent stuff back?
                //Then do step 6.
                //
            
            
            if(completed) break;
        }
        
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
    public Packet[] generateInitMessages(Comparable[] sample, Comparable pV)
    {
        Packet[] packetArray = new Packet[ports.size()];
        int subArraySize = sample.length / ports.size();
        
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
    
    /**
     * 
     * @param p
     * @param a 
     */
    private void sendPacket(Packet p, Integer a)
    {        
        try {
            Socket socket = new Socket("localhost", a);
            output = new ObjectOutputStream(socket.getOutputStream());                        
            output.writeObject(p);
            output.flush();
            socket.close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Cannot Connect to PORT:"+a);
        }
    }
        
    /**
     * 
     * @param args 
     */
    public static void main(String[] args)
    {
        final int LENGTH = 900;
        final int K = 500;
        
        //Remote Workers
        ports.add(1212);
        ports.add(1213);
        ports.add(1214);
        
        Integer[] sample = sort.output.TestArray.generate(LENGTH);
        Initiator init = new Initiator(sample);
        init.start();
    }
}