import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPServer implements Runnable
{   
    // buffer size
    final int BUF_SIZE = 1024;
    
    // port to listen on
    int port;
    static final int defaultPort = 23657;
    
    // server DatagramSocket
    DatagramSocket serverSocket;
    
    // packet to take data
    DatagramPacket packet;
    
    // message to be sent
    String message = "Hello from Server";
    
    
    /*
     * Parameterized constructor
     */
    public UDPServer(int port)
    {
        super();
        this.port = port;
        
        try {
            serverSocket = new DatagramSocket(this.port);
        } catch (SocketException ex) {
            Logger.getLogger(UDPServer.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(-1);
        }
    }

    /*
     * Default constructor
     */
    public UDPServer()
    {
        this(defaultPort);
    }
    
    
    /*
     * Implementation of interface Runnable
     */   
    public void run()
    {
        // forever - server loop
        while (true) {
            
            // prepare packet to take data from the client
            byte[] buf = new byte[BUF_SIZE];
            packet = new DatagramPacket(buf, buf.length);

            // receive packet from client
            try {
                serverSocket.receive(packet);
            } catch (IOException ex) {
                Logger.getLogger(UDPServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            // prepare packet to be sent to client
            message = new String(packet.getData(), 0, packet.getLength());
            
            // print message from client
            System.out.println("Client says: " + message);
            
            // send packet to client
            packet = new DatagramPacket(message.getBytes(), message.length(), packet.getAddress(), packet.getPort());
            try {            
                serverSocket.send(packet);
            } catch (IOException ex) {
                Logger.getLogger(UDPServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }         
    }
    
    
    /*
     * main()
     */
    public static void main (String[] args) throws IOException
    {
        UDPServer server = new UDPServer();
        server.run();
    }
}
