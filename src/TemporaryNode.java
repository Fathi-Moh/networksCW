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
import java.util.ArrayList;
import java.util.List;

// DO NOT EDIT starts
interface TemporaryNodeInterface {
    public boolean start(String startingNodeName, String startingNodeAddress);
    public boolean store(String key, String value);
    public String get(String key);
}
// DO NOT EDIT ends


public class TemporaryNode implements TemporaryNodeInterface {

    Socket socket;

    public String Echo(){
        try{
            // Check if the socket is not connected or if it is empty
            if (socket == null || !socket.isConnected()) {
                System.err.println("You are not connected to the network");
                return null;
            }
            //Create a reader and a writer to read and respond to request
            PrintWriter writerOut = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader readerIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //Sends an echo request
            writerOut.println("ECHO? ");
            //Gets the echo response and reads it
            String response = readerIn.readLine();
            //Closes the streams
            writerOut.close();
            readerIn.close();

            //returns the echo response
            return response;

        } catch(IOException e){
            System.err.println("Issues with performing the ECHO request: " + e.getMessage());
            return null;
        }
    }

    public boolean Notify(String nodeName, String nodeAddress) {
        try {
            // Check if the socket is not connected or if it is empty
            if (socket == null || !socket.isConnected()) {
                System.err.println("You are not connected to the network");
                return false;
            }
            // Create PrintWriter for socket communication
            PrintWriter writerOut = new PrintWriter(socket.getOutputStream(), true);

            // Send the notify request
            writerOut.println("NOTIFY");
            writerOut.println(nodeName);
            writerOut.println(nodeAddress);
            // Close the stream
            writerOut.close();

            // Returns true if the notify request was sent successfully
            return true;
        } catch (IOException e) {
            System.err.println("Error sending NOTIFY request: " + e.getMessage());
            return false;
        }
    }

    public List<String> nearest(String hashID) {
        try {
            // Check if the socket is not connected
            if (socket == null || !socket.isConnected()) {
                System.err.println("You are not connected to the network");
                return null;
            }
            // Create PrintWriter and BufferedReader so that it can write, read and respond
            PrintWriter writerOut = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader readerIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Create the nearest request
            String request = "NEAREST? " + hashID;
            // This sends the nearest request
            writerOut.println(request);
            // This reads the nearest response
            String response = readerIn.readLine();

            // Closes all the streams
            writerOut.close();
            readerIn.close();

            // You need to parse the response
            if (response != null && response.startsWith("NODES")) {
                List<String> nearestNodes = new ArrayList<>();
                String[] lines = response.split("\n");
                for (int i = 1; i < lines.length; i++) {
                    nearestNodes.add(lines[i]);
                }
                //Returns the nearest node
                return nearestNodes;
            } else {
                System.err.println("Invalid response from server");
                //Returns null if the response is invalid or lost
                return null;
            }
        } catch (IOException e) {
            System.err.println("Issue with finding the nearest node: " + e.getMessage());
            //Returns null if there is an issue with finding the nearest node
            return null;
        }
    }

    public boolean start(String startingNodeName, String startingNodeAddress) {
	    try{
            // Create a socket and connect to the starting node's address
            String[] sections = startingNodeAddress.split(":");
            String ipAddress = sections[0];
            int port = Integer.parseInt(sections[1]);
            socket = new Socket(ipAddress, port);
            // Return true if the 2D#4 network can be contacted
            return true;
        } catch (IOException e){
            System.err.println("Error connecting to network: " + e.getMessage());
            return false; // Return false if the 2D#4 network can't be contacted
        }
    }

    public void end(){
        try{
            if(socket != null && !socket.isClosed()){
                socket.close();
                System.out.println("Socket connection has been closed");
            }
        } catch(IOException e){
            System.err.println("Issue with closing the socket connection " + e.getMessage());
        }
    }

    public boolean store(String key, String value) {
        try {
            // Check if the socket is not connected or if it is empty
            if (socket == null || !socket.isConnected()) {
                System.err.println("You are not connected to the network");
                return false;
            }

            // Create PrintWriter and BufferedReader for socket communication
            PrintWriter writerOut = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader readerIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Create the PUT request
            String request = "PUT? " + key.lines().count() + " " + value.lines().count() + "\n" + key + value;
            // This sends the PUT request
            writerOut.println(request);
            // This gets the PUT response
            String response = readerIn.readLine();

            // Closes all the streams
            writerOut.close();
            readerIn.close();

            // Returns if the operation is successful
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
            // This would create the get response
            String request = "GET? " + key.lines().count() + "\n" + key;
            // This would send the get request
            writerOut.println(request);
            //This would get the request
            String response = readerIn.readLine();
            // This would close the streams
            writerOut.close();
            readerIn.close();

            if(response.startsWith("VALUE")){
                // Return the string if the get worked
                return response.substring(6).trim();
            } else{
                // Return null if the value could not be found which means NOPE
                return null;
            }
        } catch(IOException e){
            System.err.println("Issues with getting value for key" + e.getMessage());
            // Return null if it did not get the value for key
            return "Not implemented";
        }
    }
}