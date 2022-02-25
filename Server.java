//package server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class Server implements Runnable{


    private ArrayList<ConnectionHandler> connections;
    private ServerSocket server;
    private Boolean done;
    private ExecutorService pool;

    public Server(){
        connections = new ArrayList<>();
        done = false;
    }

    //accept the client request 
    @Override
    public void run() {
        try {
            server = new ServerSocket(4444);
            pool = Executors.newCachedThreadPool();
            while(!done){
                Socket client = server.accept();
                ConnectionHandler handler = new ConnectionHandler(client);
                connections.add(handler);
                pool.execute(handler);
            }
        } catch (IOException e) {
            // TODo
        }
        
    }

    class ConnectionHandler implements Runnable{

        private Socket client;
        private BufferedReader in;
        private PrintWriter out;
        private String newClient;


        public ConnectionHandler(Socket client){
            this.client = client;
        }

        // handle the client request
        @Override
        public void run() {
            
            JSONParser parser = new JSONParser();
            try {
                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                newClient = in.readLine();
                //out.println("Adel moves to MainHall-s1");
                JSONObject clientJsonObject;
                try {
                    clientJsonObject = (JSONObject) parser.parse(newClient);
                    String typeClient = (String) clientJsonObject.get("type");
                    String identity = (String) clientJsonObject.get("identity");

                    System.out.println(identity+" connected!");

                    JSONObject newIdentify = newIdentify(identity);
                    System.out.println(newIdentify);

                } catch (ParseException e1) {
                    //
                }

                
                while(in.readLine()!=null){
                    
                    String reader = in.readLine();
                    
                    try {
                        JSONObject jsonObject = (JSONObject) parser.parse(reader);
                        String type = (String) jsonObject.get("type");
                        System.out.println(type);

                    } catch (ParseException e) {
                        // TODO 
                    }
                }
                
            } catch (IOException e) {
                
            }
        }

        public boolean isAlphaNumeric(String s) {
            return s != null && s.matches("^[a-zA-Z0-9]*$");
        }

        public boolean checkOtherServers(String identity){
            return false;
        }

        public void broadcastRoomChange(String identity, String roomid){
            JSONObject broadcastmessage = new JSONObject();
            broadcastmessage.put("type", "newidentity");
            broadcastmessage.put("identity", identity);
            broadcastmessage.put("former", "");
            broadcastmessage.put("roomid", roomid);


        }
        public JSONObject newIdentify(String identity){
            JSONObject newIdentity = new JSONObject();
            newIdentity.put("type", "newidentity");
            newIdentity.put("approved", true);
            if ((isAlphaNumeric(identity)==false) || (3>identity.length() && identity.length()>16) || (checkOtherServers(identity)==true)){
                newIdentity.replace("approved", false);
            }

            broadcastRoomChange(identity, "MainHall-s1");
            return newIdentity;
            

        }

        public void sendMessage(String message){
            out.println(message);
        }
        
    }

    public static void main(String [] args){
        System.out.println("Hello Guys!!");
        Server server = new Server();
        server.run();
    }

   
}