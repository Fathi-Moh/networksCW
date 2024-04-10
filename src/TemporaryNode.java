// IN2011 Computer Networks
// Coursework 2023/2024
//
// Submission by
// Fathi Mohamed
// 220007064
// Fathi.Mohamed@city.ac.uk

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
        try {
            // The starting node address is to be split to get the IP address and port
            String[] sections = startingNodeAddress.split(":");
            String ipAddress = sections[0];
            int port = Integer.parseInt(sections[1]);

            // Creates a TCP connection with the starting node
            socket = new Socket(ipAddress, port);

            // This will send out a START message
            PrintWriter pWriterOut = new PrintWriter(socket.getOutputStream(), true);
            pWriterOut.println("START 1 " + startingNodeName);

            // This will read and check for response
            BufferedReader bfReaderIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response = bfReaderIn.readLine();

            // Checks if a START message has been received or not
            if (response != null && response.startsWith("START")) {
                System.out.println("Connected to the 2D#4 network");
                // Return true if the 2D#4 network can be contacted
                return true;
            } else {
                System.err.println("Failed to start communication with starting node");
                // Return false if the 2D#4 network can't be contacted
                return false;
            }
        } catch (IOException e) {
            // prints a line saying there is an issue with performing the start method
            System.err.println("Error connecting to starting node: " + e.getMessage());
            return false;
        }
    }

    public boolean store(String key, String value) {
        try {
            // Creating the PUT request
            String putRequest = "PUT? " + key.split("\n").length + " " + value.split("\n").length + "\n" + key + value;

            // writing and sending the PUT request to the fullNode
            PrintWriter pWriterOut = new PrintWriter(socket.getOutputStream(), true);
            pWriterOut.println(putRequest);

            // checks if there has been a response to the request
            BufferedReader bfReaderIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response = bfReaderIn.readLine();

            // Checks if the response is a SUCCESS or has FAILED
            if (response != null && response.equals("SUCCESS")) {
                // Return true if the store worked
                return true;
            } else if (response != null && response.equals("FAILED")) {
                System.err.println("Store unsuccessful, issues with message being refused");
                // Return false if the store failed
                return false;
            } else {
                // Prints a line saying there is an issue with receiving the response from the fullNode
                System.err.println("Invalid response received");
                return false;
            }
        } catch (IOException e) {
            // Prints a line stating there are issues with the store method functionality
            System.err.println("Issue storing key-value pair: " + e.getMessage());
            return false;
        }
    }

    public String get(String key) {
        try {
            // Creating the GET request
            String getRequest = "GET? " + key.split("\n").length + "\n" + key;

            // This will write and send the GET request
            PrintWriter pWriterOut = new PrintWriter(socket.getOutputStream(), true);
            pWriterOut.println(getRequest);

            // Reads and checks if there has been a response to the request
            BufferedReader bfReaderIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String response = bfReaderIn.readLine();

            // This checks if the output would be a string of value or will not work and output NOPE returning null
            if (response != null && response.startsWith("VALUE")) {
                // retrieving the value
                int value = Integer.parseInt(response.split(" ")[1]);
                StringBuilder valueBuilder = new StringBuilder();
                for (int i = 0; i < value; i++) {
                    valueBuilder.append(bfReaderIn.readLine());
                    if (i < value - 1)
                        valueBuilder.append("\n");
                }
                // Return the string if the get worked
                return valueBuilder.toString();
            } else if (response != null && response.equals("NOPE")) {
                System.err.println("No value found for key: " + key);
                // Return null if it didn't
                return null;
            } else {
                // Prints a line stating the value has not been received
                System.err.println("Invalid response received");
                return null;
            }
        } catch (IOException e) {
            // Prints a line stating that there has been an error with getting the value meaning an issue with the get method
            System.err.println("Error retrieving value for key: " + e.getMessage());
            return null;
        }
    }
}
