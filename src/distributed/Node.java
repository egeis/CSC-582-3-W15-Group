package distributed;

import distributed.messages.Packet;
import distributed.messages.Message;
import distributed.messages.store.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class Node {
    
    private static Logger logger;
    private static int PORT_NUMBER = 1212;
    private static final int INIT_PORT_NUMBER = Initiator.SERVER_PORT_NUMBER;
    
    private boolean shutdown = false;
    private int status = 1;
        
    private ServerSocket server;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    
    private Comparable[] arr;
    private Comparable pv;
    
    private boolean running = false;
                
    private static final String[] STATUS_MESSAGE = { 
        "NODE_NULL",        //0 - Should never be set to 0.
        "NODE_STARTING",    //1
        "NODE_WAITING",     //2
        "NODE_READY",       //3
        "NODE_BUSY",        //4
        "NODE_ERROR",       //5
        "NODE_UNKNOWN",     //6 - Unused
        "NODE_UNKNOWN",     //7 - Unused
        "NODE_UNKNOWN",     //8 - Unused
        "NODE_UNKNOWN",     //9 - Unused
        "NODE_SHUTDOWN"     //10
    }; 
         
    private Node()
    {
        Logger.getLogger(Node.class.getName()+PORT_NUMBER);
        status = Message.NODE_STARTING;
        
        try {
            server = new ServerSocket(PORT_NUMBER);
            
            while(!shutdown)
            {
                Socket socket = server.accept();
                input = new ObjectInputStream(socket.getInputStream());
                Packet p = (Packet) input.readObject();
                parsePacket(p);
            }
            
            server.close();
        } catch (IOException | ClassNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
    
    private synchronized void sendPacket(Packet p)
    {        
        try {
            Socket out = new Socket("localhost", INIT_PORT_NUMBER);
            output = new ObjectOutputStream(out.getOutputStream()); 
            output.writeObject(p);
            output.flush();
            out.close();
        } catch (IOException ex) {
           logger.log(Level.SEVERE, null, ex);
        }
    }
    
    private void parsePacket(Packet p)
    {
        int type = p.type;
        
        switch(type)
        {
            case Message.GET_STATE:
                Packet r = new Packet();
                r.type = status;
                sendPacket(r);
                break;
            case Message.SET_INPUT:
                NodeSetup s = (NodeSetup) p.pack;
                arr = s.sub;
                pv = s.pv;
                status = Message.NODE_READY;
                break;
            case Message.SET_GO_LEFT:
                
                break;
            case Message.SET_GO_RIGHT:
                
                break;
            case Message.SET_START: 
                if(!running)
                {
                    running = true;
                    
                    //Handle Starting the quick select somehow.
                    
                } else {
                    //Handle Duplicate start attempt???? or not.
                }
                break;
            case Message.SET_TERMINATION:
                shutdown = true;
            break;
                
            default:
                logger.log(Level.SEVERE, null, "Unknown Packet: "+type+" "+p.toString());
        }
    }
    
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            PORT_NUMBER = Integer.parseInt(args[0]);
        }
        
        logger = Logger.getLogger(Node.class.getName()+PORT_NUMBER);
        Node node = new Node();
    }
}