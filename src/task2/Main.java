package task2;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        Path cithPath= Paths.get("");
        File cith=cithPath.toFile();
        List<String> cart = new LinkedList<>();
        Console cons = System.console();
        String currentuser = "";

        if (args.length == 0) {
            System.out.println("Please indicate the directory...(default: shoppingcart)");
        } else {
            String directorypath = args[0];

            while (true) {
                String inputcommand = cons.readLine(">");
                String[] inputarray = inputcommand.trim().split(" ");

                if (inputarray.length == 2 & inputarray[0].equals("load")) {
                    cart.clear();
                    currentuser = inputarray[1];
                    cithPath = Paths.get("./"+directorypath + "/" + currentuser + ".cart"); // Path to the file in shoppingcart 
                    cith = cithPath.toFile();

                    if (!cith.exists()){
                        System.err.println( inputarray[1] + " Shopping List Loaded (Empty Record)");
                    } else{
                        FileReader fr = new FileReader(cith);
                        BufferedReader br = new BufferedReader(fr);
                        String line;
                        while(null != (line =br.readLine())){
                            cart.add(line);
                        }
                        br.close();
                        fr.close();
                        System.out.println(inputarray[1] + " Shopping List Loaded");

                    }
                }else if(currentuser.equals("")){
                    System.out.println("Please load a user cart (eg. load fred)");         

                } else if(inputarray.length == 1 & inputarray[0].equals("list")){
                    if (cart.size() == 0) {
                        System.out.println("Your cart is empty");
                    } else {
                        for (int i = 0; i < cart.size(); i++) {
                            System.out.printf("%d. %s \n", i + 1, cart.get(i));
                        }
                    }
    
                } else if (inputarray.length >= 2 && inputarray[0].equals("add")) {
    
                    for (int i = 1; i < inputarray.length; i++) {
                        String itemtoadd = inputarray[i].replace(",", "");
                        if (cart.contains(itemtoadd)) {
                            System.out.println("You have " + itemtoadd + " in your cart");
                        } else {
                            cart.add(itemtoadd);
                            System.out.println(itemtoadd + " added to the cart");
                        }
    
                    }
    
                } else if (inputarray.length == 2 && inputarray[0].equals("delete")) {
                   
    
                    if (Integer.parseInt(inputarray[1]) > cart.size()) {
                        System.out.println("inccorrect item index");
                    } else {
                        int deleteindex = Integer.parseInt(inputarray[1]) - 1;
                        String itemtodelete = cart.get(deleteindex);
                        cart.remove(deleteindex);
                        System.out.println(itemtodelete + " removed from the cart");
                    }
                }else if (inputarray.length == 1 && inputarray[0].equals("save")) {
                    FileWriter wr = new FileWriter(cith);

                    if (cart.size() == 0) {
                        wr.write("");
                    } else {
                        for (int i = 0; i < cart.size(); i++) {
                            wr.write(cart.get(i) + "\n");
                        }
                    }
                    System.out.println("cart content saved to " + currentuser);
                    wr.flush();
                    wr.close();


                } else if (inputarray.length == 1 && inputarray[0].equals("exit")){
                    currentuser = "";
                    cart.clear();
                    break;
                    
                } else{
                    System.out.println("Invalid Command , Please try again...");
                }

            }

        }

    }

}