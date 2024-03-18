// IN2011 Computer Networks
// Coursework 2023/2024
//
// Submission by
// Name: Fathi Mohamed
// ID: 220007064
// Email: Fathi.Mohamed@city.ac.uk


import java.io.IOException;
import java.net.ServerSocket;

// DO NOT EDIT starts
interface FullNodeInterface {
    public boolean listen(String ipAddress, int portNumber);
    public void handleIncomingConnections(String startingNodeName, String startingNodeAddress);
}
// DO NOT EDIT ends


public class FullNode implements FullNodeInterface {


    public boolean listen(String ipAddress, int portNumber) {
        try {
            // Create a ServerSocket and bind it to the specified IP address and port
            ServerSocket serverSocket = new ServerSocket(portNumber);
            System.out.println("Listening for incoming connections on " + ipAddress + ":" + portNumber);
            return true;
        } catch (IOException e) {
            System.err.println("Error creating ServerSocket: " + e.getMessage());
            return false;
        }
    }
    
    public void handleIncomingConnections(String startingNodeName, String startingNodeAddress) {

	return;
    }
}
