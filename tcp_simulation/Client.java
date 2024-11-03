package tcp_simulation;
import java.io.*;
import java.util.ArrayList;
import java.net.*;

import java.io.*;
import java.util.ArrayList;
import java.net.*;

public class Client {

   public static void main(String[] args) throws IOException {
   
  //Hard code IP and Port
  args = new String[] {"127.0.0.1", "30171"};
       
       if (args.length != 2) {
           System.err.println(
               "Usage: java Client <localhost> <port number>");
           System.exit(1);
       }

       String hostName = args[0];
       int portNumber = Integer.parseInt(args[1]);

       try (
           //create client socket
           Socket clientSocket = new Socket(hostName, portNumber);
      //Stream to write text requests to server
           PrintWriter out =
           //Output stream for clientSocket - writes to outputStream - writes to other side in socket
               new PrintWriter(clientSocket.getOutputStream(), true);
           //inputStream reads text response from server
           BufferedReader in =
               new BufferedReader(
                   new InputStreamReader(clientSocket.getInputStream()));
      //standard input stream to get user's requests
           BufferedReader stdIn =
               new BufferedReader(
                   new InputStreamReader(System.in))
       ) {
         
      String packet;
         
      //instantiateArrayList to keep track of the received packets
      ArrayList<String> packetsReceived = new ArrayList<>();
         
      //While don't have all packets, get missing packets from server
      try {
      ArrayList<Integer> missingIndices = new ArrayList<>();
      do {
      //Read in the desired packets from the server
      while((packet = in.readLine()) != null && !packet.equals("-1")) {
      if(!packetsReceived.contains(packet)) {
      packetsReceived.add(packet);
      System.out.println("PACKET " + packet + " RECIEVED");
      }
      }
     
      missingIndices = getMissingIndices(packetsReceived, out);
       
      //send server missing packets
      for(int index = 0; index < missingIndices.size(); index++) {
      out.println(missingIndices.get(index));
      System.out.println("REQUESTING PACKET " + missingIndices.get(index));
      }
      out.println("-1");
      //While packets are still missing, program runs again
      } while(!missingIndices.isEmpty());
     
      //put packets in order
      String other;
      for(int x = 0; x < packetsReceived.size(); x++) {
      for(int y = x+1; y < packetsReceived.size(); y++) {
      if(Integer.parseInt(packetsReceived.get(x).substring(0, packetsReceived.get(x).indexOf(" ")))
      > Integer.parseInt(packetsReceived.get(y).substring(0, packetsReceived.get(y).indexOf(" ")))){
      other = packetsReceived.get(x);
      packetsReceived.set(x, packetsReceived.get(y));  
      packetsReceived.set(y, other);
      }
      }
      }
      String stringToDisplay = getFullString(packetsReceived);
      System.out.println("\n" + stringToDisplay);
      }
      catch(IOException e) {
      System.out.println("ERROR");
      System.exit(1);
      }
       }
       catch (UnknownHostException ex) {
       System.err.println("Don't know about host " + hostName);
       System.exit(1);
       }
       catch (IOException e) {
          System.err.println("Couldn't get I/O for the connection to " + hostName);
          System.exit(1);
       }
   }
   
   public static ArrayList<Integer> getMissingIndices(ArrayList<String> packetsReceived, PrintWriter out) {
       //get arrayList of indices.
  ArrayList<Integer> indicesReceived = new ArrayList<>();

  for(String packet : packetsReceived) {
  int breakpoint = packet.indexOf(" ");
  if(breakpoint > 0) {
  indicesReceived.add(Integer.parseInt(packet.substring(0, breakpoint)));
  }
  }
 
  //check arrayList of indices for missing indices
  ArrayList<Integer> missingIndices = new ArrayList<>();
  if(!packetsReceived.isEmpty()) {
  for (int index = 0; index < Integer.parseInt(packetsReceived.get(packetsReceived.size()-1)); index++) {
          if (!indicesReceived.contains(index)) {
              missingIndices.add(index);
              System.out.println("MISSING PACKET " + index);
          }
      }
  packetsReceived.remove(packetsReceived.size()-1);
  }
 
        return missingIndices;
   }
   
   public static String getFullString(ArrayList<String> packetsReceived) {
  StringBuilder str = new StringBuilder();
  //remove numbers of packets and create string of all packets
  int breakpoint;
  for(String s : packetsReceived) {
  breakpoint = s.indexOf(" ");
  str.append(s.substring(breakpoint + 1) + " ");
  }
  return str.toString();
   }
}