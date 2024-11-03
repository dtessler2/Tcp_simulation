package tcp_simulation;
import java.net.*;
import java.util.*;
import java.io.*;

public class Server {


   public static void main(String[] args) throws IOException {
       
  args = new String[] {"30171"};
   
       if (args.length != 1) {
      System.err.println("Usage: java EchoServer <port number>");
           System.exit(1);
       }
     
       int portNumber = Integer.parseInt(args[0]);
       
       try (
    //Create a new socket for server
           ServerSocket serverSocket =
               new ServerSocket(Integer.parseInt(args[0]));
        //Create clientSocket - server gets input from client
           Socket clientSocket = serverSocket.accept();  
        //Gives access to output stream - server writes to other side in socket
           PrintWriter out =
               new PrintWriter(clientSocket.getOutputStream(), true);
        //Gives access to input stream - server reads from client - inputStream allows socket to receive data
           BufferedReader in = new BufferedReader(
               new InputStreamReader(clientSocket.getInputStream()));
       ) {
       
      Random rand = new Random();
           
      //Create the message
      String packet = "Hi, this is the server speaking. We are demonstrating"
      + " TCP protocol using Java code. This message will be delivered"
      + " to the client in unordered packets, and the client will reorder them.";
           //Divide the message into separate packets
      String[] packetArray = packet.split(" ");
           ArrayList<String> packets = new ArrayList<>(Arrays.asList(packetArray));
           final int STRING_SIZE = packets.size();
           
           concatenateIndexes(packets);
           
           //false until client gets all packets
           boolean isComplete = false;

           //create array of the packets in a random order
           ArrayList<String> randomOrderedPackets = new ArrayList<>(packets);
           Collections.shuffle(randomOrderedPackets);
           
           do {
          sendPackets(randomOrderedPackets, out, STRING_SIZE);
     
               try {
              //read from client which packets to resend
              ArrayList<String> packetsToResend = new ArrayList<>();
              String packetIndex;
              while((packetIndex = in.readLine()) != null && !packetIndex.equals("-1") && !packetsToResend.contains(packetIndex)) {
      packetsToResend.add(packetIndex);
      System.out.println("RECIEVED REQUEST FOR PACKET " + packetIndex);
      }
             
              for(String p : packetsToResend) {
              randomOrderedPackets.add(packets.get(Integer.parseInt(p)));
              }
                   Collections.shuffle(randomOrderedPackets);
               }
               catch (IOException e) {
              System.out.println("Exception caught when trying to listen on port "
              + portNumber + " or listening for a connection");
              System.out.println(e.getMessage());
               }  
               
               if(randomOrderedPackets.isEmpty()) {
              isComplete = true;
               }
           }while(!isComplete);
           
       } catch (IOException e) {
           System.out.println("Exception caught when trying to listen on port "
               + portNumber + " or listening for a connection");
           System.out.println(e.getMessage());
       }
   }
   
   public static void concatenateIndexes(ArrayList<String> packets) {
//Concatenate indexes onto each packet
       for(int index = 0; index < packets.size(); index++) {
      packets.set(index, index + " " + packets.get(index));
       }
   }
   
   public static void sendPackets(ArrayList<String> randomOrderedPackets, PrintWriter out, int stringSize) {
  Random rand = new Random();
  //Each packet is sent with 80% probability of being received by client.
  for(int i = 0; i < randomOrderedPackets.size(); i++) {
  //80% chance packet gets sent
      if(rand.nextInt(100) + 1 <= 80) {
           out.println(randomOrderedPackets.get(i));
           System.out.println("SENDING PACKET : " + randomOrderedPackets.get(i));
      }
  }
  out.println(String.valueOf(stringSize));
  out.println("-1");
 
  randomOrderedPackets.clear();
   }
}