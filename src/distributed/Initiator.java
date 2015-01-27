package distributed;

import com.sun.glass.ui.Application;
import distributed.messages.Packet;
import distributed.messages.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
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
    private static HashMap<Integer, Agent> agents = new HashMap<Integer, Agent>();
    
    private ServerSocket server;
    private ObjectOutputStream output;
    private ObjectInputStream input;
   
    private Initiator(Comparable[] arr)
    {        
        this.arr = arr;
        logger = Logger.getLogger(Initiator.class.getName());
    }
        
    public void start()
    {
        try {
            server = new ServerSocket(SERVER_PORT_NUMBER);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
                    
        while(true) 
        {            
           //Logic for steps 5 - 9
            
            if(completed) break;
        }
        
        //Calls for Node Termination.
        for(Agent a : agents.values())
        {
            if(a.active)
            {
                Packet p = Message.getPacket(Message.SET_TERMINATION);
                sendPacket(p,a);
            }
        }
            
        try {    
            server.close(); //Close the Server, causes Socket to throw Exception.
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }       
    }
      
    private void sendPacket(Packet p, Agent a)
    {        
        try {
            Socket socket = new Socket("localhost", a.PORT);
            output = new ObjectOutputStream(socket.getOutputStream());                        
            output.writeObject(p);
            a.success = true;
            a.retry = 0;
            a.lastSent = System.currentTimeMillis();

        } catch (IOException ex) {
            logger.log(Level.INFO, "Cannot Connect to PORT:"+a.PORT);
            a.retry += 1;
            if(a.success)
            {
                if(a.retry > 6) {
                    logger.log(Level.WARNING, null, ex);
                    a.active = false;
                }
            } else {
                if(a.retry > 11) {
                    logger.log(Level.WARNING, null, ex);
                    a.active = false;
                }
            }
        }
    }
    
    private void parsePacket(Packet p, int port)
    {
        int type = p.type;
        
        Agent a = agents.get(port);
        a.lastRecieved = System.currentTimeMillis();
        
        switch(type)
        {
            case Message.NODE_WAITING_NEXT:
                //DO STUFF...
                break;
            case Message.NODE_WAITING_INPUT:
                //DO STUFF...
                break;
            case Message.SET_RESULTS:
                //DO STUFF...
                break;
        }
    }
    
    /**
     * 
     * @param k
     * @return the kth value.
     */
    public Comparable getKthValue(int k) throws IndexOutOfBoundsException
    {
        if(k < 0 || k > (arr.length - 1) )
        {
            throw new IndexOutOfBoundsException();
        }
            
        int indexOfKth = -1;
        
        //Find Kth Value...
        
        return arr[indexOfKth];
    }
    
    public static void main(String[] args)
    {
        final int LENGTH = 1000;
        final int K = 500;
        
        //Remote Workers
        agents.put(1212, new Agent(1212));
        agents.put(1213, new Agent(1213));
        
        Integer[] sample = sort.output.TestArray.generate(LENGTH);
        Initiator init = new Initiator(sample);
        init.start();
        
        System.out.println("The Kth value (where k = "+K+") is: " + init.getKthValue(LENGTH));
    }
    
    private static class Agent {

        public Agent(int port) {
            this.PORT = port;
            this.lastSent = 0;
            this.lastRecieved = 0;
        }
        
        public long lastRecieved;
        public long lastSent;
        
        public final int PORT;
        
        public int retry = 0;
        public boolean success = false;
        public boolean active = true;
    }
}


