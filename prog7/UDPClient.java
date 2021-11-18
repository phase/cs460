
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UDPClient implements Runnable {

    // buffer size
    final static int BUF_SIZE = 1024;

    // default address/port to server
    static final String defaultServerAddress = "127.0.0.1";
    static final int defaultServerPort = 23657;

    // address/port to server
    String serverAddress;
    int serverPort;

    // client DatagramSocket
    DatagramSocket clientSocket;

    // packet to take data
    DatagramPacket packet;

    // message to be sent
    String message = "Hello from client";

    
    /*
     * Parameterized constructor
     */
    public UDPClient(String serverAddress, int serverPort) {
        super();
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;

        try {
            clientSocket = new DatagramSocket();
        } catch (SocketException ex) {
            Logger.getLogger(UDPClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    /*
     * Default constructor
     */
    public UDPClient() {
        this(defaultServerAddress, defaultServerPort);
    }

    
    /*
     * Implementation of interface Runnable
     */
    public void run() {
        // prepare packet to be sent to server
        try {
            packet = new DatagramPacket(message.getBytes(), message.length(), InetAddress.getByName(serverAddress), serverPort);
        } catch (UnknownHostException ex) {
            Logger.getLogger(UDPClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        // send packet to server
        try {
            clientSocket.send(packet);
        } catch (IOException ex) {
            Logger.getLogger(UDPClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        // prepare packet to take message from server
        byte[] buf = new byte[BUF_SIZE];
        packet = new DatagramPacket(buf, buf.length);

        // receive packet from server
        try {
            clientSocket.receive(packet);
        } catch (IOException ex) {
            Logger.getLogger(UDPClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // print message from client
        System.out.println("Server says: " + new String(packet.getData(), 0, packet.getLength()));

        // close socket
        clientSocket.close();
    }

    
    /*
     * main()
     */
    public static void main(String[] args) throws IOException {
        UDPClient client = new UDPClient();
        client.run();
    }
}
