package distributed.messages;

import java.io.Serializable;

/**
 *
 * @author Richard Coan
 */
public class Packet implements Serializable {
    public int type = 0;
    public Object pack = null;
    
    @Override
    public String toString()
    {
        return "[Packet "+type+" <"+((pack==null)?"null":pack.toString())+">]";
    }    
}
