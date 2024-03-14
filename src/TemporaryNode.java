// IN2011 Computer Networks
// Coursework 2023/2024
//
// Submission by
// Name: Fathi Mohamed
// ID: 220007064
// Email: Fathi.Mohamed@city.ac.uk


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import static java.lang.System.in;
import static java.lang.System.out;

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
            return true; // Return true if the 2D#4 network can be contacted
        } catch (IOException e){
            System.err.println("Error connecting to network: " + e.getMessage());
            return false; // Return false if the 2D#4 network can't be contacted
        }
    }

    public boolean store(String key, String value) {
        // This Java exception would try to connect
	    try{
            // This checks if the socket is not connected or if it is empty
            if(socket == null || !socket.isConnected()){
                System.err.println("You are not connected to the network");
                return false;
            }
            // This would allow a put request to be sent, so it can store the keys and values
            PrintWriter writerOut = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader readerIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // This would create the put request
            String request = "PUT?" + key.lines().count() + " " + value.lines().count() + "\n" + key + value;
            // This would send the put request
            writerOut.println(request);
            // This would get the put response
            String response = readerIn.readLine();
            // This would check if the store has worked
            return "SUCCESS".equals(response); // Return true if the store worked
        } catch (IOException e){
            System.err.println("Issues with storing keys and values" + e.getMessage());
            return false; // Return false if the store failed
        }
    }

    public String get(String key) {
	// Implement this!
	// Return the string if the get worked
	// Return null if it didn't
	return "Not implemented";
    }
}
