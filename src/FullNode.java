// IN2011 Computer Networks
// Coursework 2023/2024
//
// Submission by
// Fathi Mohamed
// 220007064
// Fathi.Mohamed@city.ac.uk

import java.util.Map;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

// DO NOT EDIT starts
interface FullNodeInterface {
    public boolean listen(String ipAddress, int portNumber);
    public void handleIncomingConnections(String startingNodeName, String startingNodeAddress);
}
// DO NOT EDIT ends


public class FullNode implements FullNodeInterface {

    ServerSocket serverSocket;
    Socket socket;
    private Map<String, String> storage;
    private Map<String, String> networkMap;

    public FullNode() {
        this.storage = new HashMap<>();
        this.networkMap = new HashMap<>();
    }

    public boolean listen(String ipAddress, int portNumber) {
        try {
            serverSocket = new ServerSocket(portNumber);
            System.out.println("FullNode is listening for incoming connections on " + ipAddress + ":" + portNumber);
            // Return true if the node can accept incoming connections
            return true;
        } catch (IOException e) {
            System.err.println("Error listening for incoming connections: " + e.getMessage());
            // Return false otherwise
            return false;
        }
    }

    public void handleIncomingConnections(String startingNodeName, String startingNodeAddress) {
        try {
            while (true) {
                // This will accept all incoming connections
                socket = serverSocket.accept();
                // Read and write response messages to the Temporary nodes
                PrintWriter pWriterOut = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader bfReaderIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String response = bfReaderIn.readLine();

                // This checks if the response is not null before running all the responses
                if (response != null) {
                    // Using switch case for response handling
                    switch (response) {
                        case "ECHO?":
                            respondToEchoRequest(socket);
                            break;
                        case "PUT?":
                            respondToPutRequest(bfReaderIn, pWriterOut);
                            break;
                        case "GET?":
                            respondToGetRequest(bfReaderIn, pWriterOut);
                            break;
                        case "NEAREST?":
                            respondToNearestRequest(bfReaderIn, pWriterOut);
                            break;
                        case "START":
                            // The starting node will be stored in the network map
                            networkMap.put(startingNodeName, startingNodeAddress);
                            pWriterOut.println("START 1" + startingNodeName);
                            break;
                        case "NOTIFY?":
                            // The node name and the address are read and extracted from the NOTIFY request
                            String nodeName = bfReaderIn.readLine();
                            String nodeAddress = bfReaderIn.readLine();
                            // New nodes will then be stored in the network map
                            networkMap.put(nodeName, nodeAddress);
                            // When the address has been known we respond with NOTIFIED
                            pWriterOut.println("NOTIFIED");
                            break;
                        case "END":
                            // socket and steams have both been closed
                            socket.close();
                            bfReaderIn.close();
                            pWriterOut.close();
                            break;
                        default:
                            // A print line to handle unrecognised responses
                            System.err.println("Unknown response, cannot identify");
                            break;
                    }
                } else {
                    // If the response is null an error print line will be displayed and the socket and streams will close
                    System.err.println("Received null response. The connection is now closed.");
                    socket.close();
                    bfReaderIn.close();
                    pWriterOut.close();
                }
            }
        } catch (IOException e) {
            /*
            If there are issues with the processing of the handleIncomingConnection method then there will
               be an error message displayed
             */
            System.err.println("Issues with handling incoming connections: " + e.getMessage());
        }
    }

    // Respond to ECHO? correctly
    private void respondToEchoRequest(Socket socket) throws IOException {
        /*
        This will send a response to the requester saying that the connection is still active and that
         the responder is still working correctly
         */
        PrintWriter pWriterOut = new PrintWriter(socket.getOutputStream(), true);
        pWriterOut.println("OHCE");
    }

    // This method will respond to the PUT? message correctly
    private void respondToPutRequest(BufferedReader in, PrintWriter out) throws IOException {
        /*
        This will send a response letting the requester know that it found 3 nodes
        that are closer to the hashID
         */
        int keyLines = Integer.parseInt(in.readLine().split(" ")[1]);
        int valueLines = Integer.parseInt(in.readLine().split(" ")[1]);

        StringBuilder keyBuilder = new StringBuilder();
        for (int i = 0; i < keyLines; i++) {
            keyBuilder.append(in.readLine()).append("\n");
        }
        String key = keyBuilder.toString();

        StringBuilder valueBuilder = new StringBuilder();
        for (int i = 0; i < valueLines; i++) {
            valueBuilder.append(in.readLine()).append("\n");
        }
        String value = valueBuilder.toString();

        // (Key, Value) pair are to be stored
        storage.put(key, value);

        // Send message to the requester with response SUCCESS
        out.println("SUCCESS");
    }

    // This method will respond to GET? message correctly
    private void respondToGetRequest(BufferedReader in, PrintWriter out) throws IOException {
        /*
        This will send a response to the requester informing it that it has the value stored
        with the requested key. If it does not have the value with the requested key then it will respond with
        a different message
         */
        int keyLines = Integer.parseInt(in.readLine().split(" ")[1]);

        StringBuilder keyBuilder = new StringBuilder();
        for (int i = 0; i < keyLines; i++) {
            keyBuilder.append(in.readLine()).append("\n");
        }
        String key = keyBuilder.toString();

        // Check if the key exists in storage
        String value = storage.get(key);

        // A statement that checks if the value has been stored with the requested key or not and sends the response
        if (value != null) {
            // Respond with the message VALUE
            out.println("VALUE " + value.split("\n").length);
            out.print(value);
        } else {
            // Respond with the message NOPE
            out.println("NOPE");
        }
    }

    // This method will respond to NEAREST? message correctly
    private void respondToNearestRequest(BufferedReader in, PrintWriter out) throws IOException {
        /*
        This will send a response to the requester letting it know the number of full nodes names and addresses that
        it is closest to the key. If the hashID is not close to any node then it will respond with NOPE
         */
        String hashId = in.readLine().split(" ")[1];

        // Finds three nodes with the closest hashID
        List<String> nearestNodes = locateClosestNodes(hashId);
        if (nearestNodes.isEmpty()) {
            // This will respond with NOPE
            out.println("NOPE");
        } else {
            // This will respond with NEAREST
            StringBuilder responseBuilder = new StringBuilder("NODES" + nearestNodes.size() + "\n");
            for (String node : nearestNodes) {
                responseBuilder.append(node).append("\n");
            }
            out.println(responseBuilder.toString());
        }
    }

    // This is a method that calculates the distance between 2 hashIDs
    private int distanceBetweenHashID(String hashID1, String hashID2) {
        int distance = 0;
        for (int i = 0; i < hashID1.length(); i++) {
            if (hashID1.charAt(i) != hashID2.charAt(i)) {
                distance = 256 - i;
                break;
            }
        }
        // Returns the distance between the 2 hashIDs
        return distance;
    }

    private List<String> locateClosestNodes(String hashId) {
        /*
        This method will find 3 nodes with the closest hashID and insert them to the nearestNodes
        list. It will calculate the difference between the provided hashID and each node's hashID
         */
        List<String> nearestNodes = new ArrayList<>();
        Map<String, Integer> hashIdDifferences = new HashMap<>();
        for (Map.Entry<String, String> entry : networkMap.entrySet()) {
            String nodeName = entry.getKey();
            byte[] nodeHashId;
            String nodeHash = "";
            try {
                nodeHashId = HashID.computeHashID(nodeName);
                nodeHash = new String(nodeHashId, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                System.err.println("Error with supporting coding: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Issue with converting node hash to byte array: " + e.getMessage());
                return nearestNodes;
            }

            int difference = distanceBetweenHashID(nodeHash, hashId);
            hashIdDifferences.put(nodeName, difference);
        }

        // The nodes will be sorted based off the difference in the calculation and will add the 3 closest nodes to the list
        List<Map.Entry<String, Integer>> sortedNodes = new ArrayList<>(hashIdDifferences.entrySet());
        sortedNodes.sort(Map.Entry.comparingByValue());
        int count = 0;
        for (Map.Entry<String, Integer> entry : sortedNodes) {
            if (count >= 3) {
                break;
            }
            nearestNodes.add(entry.getKey());
            count++;
        }
        // This will return the list of the nearest nodes
        return nearestNodes;
    }
}
