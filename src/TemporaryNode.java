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
import java.util.HashMap;
import java.util.Map;

// DO NOT EDIT starts
interface TemporaryNodeInterface {
    public boolean start(String startingNodeName, String startingNodeAddress);
    public boolean store(String key, String value);
    public String get(String key);
}
// DO NOT EDIT ends


public class TemporaryNode implements TemporaryNodeInterface {

    Socket socket;
    private Map<String, String> newHashID;

    public TemporaryNode(){
        newHashID = new HashMap<>();
    }
    // This method will update the map so that it now contains the new node information
    private void updateMap(String name, String hashID){
        newHashID.put(name, hashID);
    }

    private String conversion(String HashString){
        String binaryString = "";
        for(int i = 0; i < HashString.length(); i++){
            char hexChar = HashString.charAt(i);
            int hexValue = Character.digit(hexChar, 16);
            String binaryValue = String.format("%4s", Integer.toBinaryString(hexValue)).replace(' ', '0');
            binaryString += binaryValue;
        }
        return binaryString;
    }


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
            String request = "PUT? " + key.lines().count() + " " + value.lines().count() + "\n" + key + value;
            // This would send the put request
            writerOut.println(request);
            // This would get the put response
            String response = readerIn.readLine();
            // This would close the streams
            writerOut.close();
            readerIn.close();
            // This would check if the store has worked
            return "SUCCESS".equals(response); // Return true if the store worked
        } catch (IOException e){
            System.err.println("Issues with storing keys and values" + e.getMessage());
            return false; // Return false if the store failed
        }
    }

    public String get(String key) {
        // This would find the value corresponding to the given key
	    try{
            // This checks if the socket is not connected or if it is empty
            if(socket == null || !socket.isConnected()){
                System.err.println("You are not connected to the network");
                return null;
            }
            // This would create the get request
            PrintWriter writerOut = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader readerIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //
            String request = "GET? " + key.lines().count() + "\n" + key;
            // This would send the get request
            writerOut.println(request);
            //This would get the request
            String response = readerIn.readLine();
            // This would close the streams
            writerOut.close();
            readerIn.close();

            if(response.startsWith("VALUE")){
                return response.substring(6).trim(); // Return the string if the get worked
            } else{
                return null; // Return null meaning the value could not be found
            }
        } catch(IOException e){
            System.err.println("Issues with getting value for key" + e.getMessage());
            return null; // Return null if it didn't
        }
    }
}
