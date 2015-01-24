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
    
    public final static int SERVER_PORT_NUMBER = 8484;
    private static List<Agent> agents = new ArrayList<Agent>();
    
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
        
        Thread inbound = new Thread() {
            @Override
            public void run()
            {
                try {
                    while(true)
                    {
                        //Blocks Until a something is sent.
                        Socket socket = server.accept();   
                        input = new ObjectInputStream(socket.getInputStream());
                        Packet p = (Packet) input.readObject();

                        //Do Something with the Packet.
                        System.out.println(p.toString());
                        parsePacket(p, socket.getPort());

                        //Close the Socket
                        input.close();
                    }
                } catch (IOException | ClassNotFoundException ex) {
                    if(server.isClosed())
                        logger.log(Level.INFO, null, "Connection Terminated");
                    else
                        logger.log(Level.SEVERE, null, ex);
                }
            }
        };
            
        inbound.start();
            
        //Send out the initial data.
        
        //Handle the remaining operations.
        /**
         * 1) Check the Status of each node if the heartbeat inc. is reached.
         * 2) Something that can be done for a specific node, IE output something.
         * 3) At the end of the for-loop do something globally.
         * 4) repeat if not completed.
         */
        while(true && agents.size() > 0) 
        {
            for(Agent a : agents)
            {
                if(a.active) {
                    if(System.currentTimeMillis() - a.heartbeat > 1000 )
                    {
                        Packet p = new Packet();
                        p.type = Message.GET_STATE;
                        sendPacket(p,a);

                        a.heartbeat = System.currentTimeMillis();
                    }

                    //Do Stuff per node.
                    //Additional logic on what and how do it is necessary...
                }
            }

            //Do something else globally, which is looped every iteration.
            
            if(completed) break;
        }
            
        try {    
            server.close(); //Close the Server, causes Socket to throw Exception.
            inbound.join(); //Stops the thread.
        } catch (IOException | InterruptedException ex) {
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
        
        switch(type)
        {
           //DO stuff...
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
        int length = 1000;
        int k = 500;
        
        //Remote Workers
        agents.add(new Agent(1212));
        agents.add(new Agent(1213));
        
        Integer[] sample = sort.output.TestArray.generate(length);
        Initiator node = new Initiator(sample);
        node.start();
        
        System.out.println("The Kth value (where k = "+k+") is: " + node.getKthValue(length));
    }
    
    private static class Agent {

        public Agent(int port) {
            this.PORT = port;
            this.heartbeat = System.currentTimeMillis();
        }
        
        public final int PORT;
        public long heartbeat;
        public int retry = 0;
        public boolean success = false;
        public boolean active = true;
    }
}


