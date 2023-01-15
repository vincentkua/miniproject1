package task2;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ShopServer {

    public static void main(String[] args) throws IOException {
        Path cithPath= Paths.get("");
        File cith=cithPath.toFile();
        String currentuser = "" ;
        
        int port = 3000; // default port
        String directorypath = "shoppingcart"; // default directory
        if (args.length == 2) {
            directorypath = args[0];
            port = Integer.parseInt(args[1]);
        }

        System.out.println("Starting " + directorypath + " Server on Port " + port);
        ServerSocket server = new ServerSocket(port);
        System.out.println("Using " + directorypath + " directory for persistence");

        while (true) {

            Socket sock = server.accept();

            try {

                while (true) {
                    boolean stop = false;
                    System.out.println("Connection Received...");
                    OutputStream os = sock.getOutputStream();
                    DataOutputStream dos = new DataOutputStream(os);
                    InputStream is = sock.getInputStream();
                    DataInputStream dis = new DataInputStream(is);
                    currentuser = dis.readUTF(); // get the user request

                    dos.writeUTF("Connected to " + directorypath + " server at localserver on " + currentuser + " port " + port); // send the connection status to client once connected
 
                    cithPath = Paths.get("./"+directorypath + "/" + currentuser + ".cart"); 
                    cith = cithPath.toFile();
                    if (!cith.exists()){
                        System.err.println( currentuser + ".cart File not found...");
                        dos.writeUTF(""); // return empty array 
                    } else{
                        FileReader fr = new FileReader(cith);
                        BufferedReader br = new BufferedReader(fr);
                        dos.writeUTF(br.readLine()); // assume only the 1st line consist the data...
                        // String line;
                        // while(null != (line =br.readLine())){
                        //     dos.writeUTF(line); 
                        // }
                        br.close();
                        fr.close();
                        System.out.println(currentuser + " Shopping List Loaded");

                    }

                    // Do Something
                    while (!stop) {
                        String newdata = dis.readUTF();
                        if(newdata.equals("exit")){
                            stop = true;
                            break;
                        }else{
                            FileWriter wr = new FileWriter(cith);
                            wr.write(newdata);
                            wr.flush();
                            wr.close();    
                            dos.writeUTF("cart content saved to " + currentuser );
                            System.out.println(currentuser + " Shopping List Updated");

                        }

                    }

                    System.out.println("Terminating client connection");
                    try {
                        sock.close();
                        System.out.println("Terminated");
                        break;
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());

                    }

                }

            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }

        }

    }

}
