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
    private Map<String, String> hashMap;

    public TemporaryNode(){
        hashMap = new HashMap<>();
    }
    // This method will update the map so that it now contains the new node information
    private void updateMap(String name, String hashID){
        hashMap.put(name, hashID);
    }

    // This method would calculate the distance between two hashID's
    private int distanceFromHash(String ID1, String ID2){
        String BHashID1 = conversion(ID1);
        String BHashID2 = conversion(ID2);
        String XORResults = XOR(BHashID1, BHashID2);

        return countLeadingZeros(XORResults);
    }

    // This method would convert a hexadecimal string to a binary string
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

    // This method would do a XOR operation on the two binary strings when converted
    private String XOR(String B1, String B2){
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < B1.length(); i++){
            char bit1 = B1.charAt(i);
            char bit2 = B2.charAt(i);
            if(bit1 == bit2){
                result.append("0");
            } else {
                result.append("i");
            }
        }
        return result.toString();
    }

    // This method will count all the leading zeros in the binary string
    private int countLeadingZeros(String bString){
        int counter = 0;
        for(int i = 0; i < bString.length(); i++){
            if(bString.charAt(i) == '0'){
                counter ++;
            } else {
                break;
            }
        }
        return counter;
    }


    // This method would find the closest node to the hashID we are looking for
    private String findingClosestNode(String finalHashID){
        String closestNode = null;
        int minimumDistance = Integer.MAX_VALUE;
        for(Map.Entry<String, String> entry : hashMap.entrySet()){
            String nodeName = entry.getKey();
            String hashID = entry.getValue();
            int distance = distanceFromHash(hashID, finalHashID);
            if(distance < minimumDistance){
                minimumDistance = distance;
                closestNode = nodeName;
            }
        }
        return closestNode;
    }


    public boolean start(String startingNodeName, String startingNodeAddress) {
	    try{
            // Create a socket and connect to the starting node's address
            String[] parts = startingNodeAddress.split(":");
            String ipAddress = parts[0];
            int port = Integer.parseInt(parts[1]);
            socket = new Socket(ipAddress, port);
            updateMap(startingNodeName, startingNodeAddress);
            return true; // Return true if the 2D#4 network can be contacted
        } catch (IOException e){
            System.err.println("Error connecting to network: " + e.getMessage());
            return false; // Return false if the 2D#4 network can't be contacted
        }
    }

    public boolean store(String key, String value) {
        try {
            // Check if the socket is not connected or if it is empty
            if (socket == null || !socket.isConnected()) {
                System.err.println("You are not connected to the network");
                return false;
            }

            // Calculate the hash of the key and find the closest node to that key
            String keyHash = conversion(key);
            String closestNode = findingClosestNode(keyHash);
            if(closestNode == null){
                System.err.println("There are 0 nodes available in the network");
                return false;
            }

            String[] nodeParts = closestNode.split(":");
            String nodeIPAddress = nodeParts[0];
            int nodePort = Integer.parseInt(nodeParts[1]);

            Socket closestNodeSocket = new Socket(nodeIPAddress, nodePort);

            // Create PrintWriter and BufferedReader for socket communication
            PrintWriter writerOut = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader readerIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Create the PUT request
            String request = "PUT? " + key.lines().count() + " " + value.lines().count() + "\n" + key + value;

            // Send the PUT request
            writerOut.println(request);

            // Get the PUT response
            String response = readerIn.readLine();

            // Close the streams
            writerOut.close();
            readerIn.close();
            closestNodeSocket.close();
            // Check if the store operation was successful
            return "SUCCESS".equals(response);
        } catch (IOException e) {
            System.err.println("Issues with storing keys and values" + e.getMessage());
            return false;
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
