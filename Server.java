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
        private String nickname;


        public ConnectionHandler(Socket client){
            this.client = client;
        }

        // handle the client request
        @Override
        public void run() {
            
            
            try {
                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                //out.println("Enter a nickname : ");
                nickname = in.readLine();
                //out.print("Hi");

                System.out.println(nickname+" connected!");
                

                while(in.readLine()!=null){
                    System.out.println(in.readLine());
                }
                
            } catch (IOException e) {
                
            }
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