package distributed;

import distributed.messages.Message;
import distributed.messages.Packet;
import distributed.messages.store.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
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
    private int status = 1;
        
    private ServerSocket server;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    
    private Comparable[] arr;
    private Comparable pv;
    
    private boolean running = false;
         
    private Node()
    {
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
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
    
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
                LOGGER.log(Level.SEVERE, null, "Unknown Packet: "+type+" "+p.toString());
        }
    }
    
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            PORT_NUMBER = Integer.parseInt(args[0]);
        } else {
            System.out.println("Please include a port number as an argument.");
            System.out.println("Press any key to quit.");
            Scanner s = new Scanner(System.in);s.next();
            System.exit(1);
        }
        
        LOGGER = Logger.getLogger(Node.class.getName()+PORT_NUMBER);
        Node node = new Node();
    }
}