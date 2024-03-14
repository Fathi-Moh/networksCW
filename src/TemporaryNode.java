// IN2011 Computer Networks
// Coursework 2023/2024
//
// Submission by
// YOUR_NAME_GOES_HERE
// YOUR_STUDENT_ID_NUMBER_GOES_HERE
// YOUR_EMAIL_GOES_HERE


import java.io.IOException;
import java.net.Socket;

// DO NOT EDIT starts
interface TemporaryNodeInterface {
    public boolean start(String startingNodeName, String startingNodeAddress);
    public boolean store(String key, String value);
    public String get(String key);
}
// DO NOT EDIT ends


public class TemporaryNode implements TemporaryNodeInterface {

    private Socket socket;

    public boolean start(String startingNodeName, String startingNodeAddress) {
	    try{
            // Create a socket and connect to the starting node's address
            String[] parts = startingNodeAddress.split(":");
            String ipAddress = parts[0];
            int port = Integer.parseInt(parts[1]);
            socket = new Socket(ipAddress, port);
            return true; // Connection to network is successful
        } catch (IOException e){
            System.err.println("Error connecting to network: " + e.getMessage());
            return false; // Connecting to network has failed
        }
    }

    public boolean store(String key, String value) {
	// Implement this!
	// Return true if the store worked
	// Return false if the store failed
	return true;
    }

    public String get(String key) {
	// Implement this!
	// Return the string if the get worked
	// Return null if it didn't
	return "Not implemented";
    }
}
