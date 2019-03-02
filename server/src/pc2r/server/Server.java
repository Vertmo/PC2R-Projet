package pc2r.server;

import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;

import pc2r.commands.CommandParser;
import pc2r.game.Game;
import pc2r.game.GameState;

public class Server extends Thread {
    protected ServerSocket sc;
    private CommandParser parser;
    private GameState state;

    public Server(int port) {
        state = new GameState();
        parser = new CommandParser(state);
        try {
            sc = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("Server listening on port "+port);
    }

    public void run() {
        try {
            while(true) {
                Socket client = sc.accept();
                Client cl = new Client(client, parser);
                ClientSender cs = new ClientSender(cl, state);
                cl.start(); cs.start();
            }
        } catch(IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        // Parsing cmd args
        if(args.length != 1) {
            System.err.println("Usage: java Server <port>");
            System.exit(1);
        }
        int p = 0;
        try {
            p = Integer.parseInt(args[0]);
        } catch(NumberFormatException nf) {
            System.err.println("<port> should be a number, not " + args[0]);
            System.exit(1);
        }

        // Starting the server
        Server s = new Server(p);
        Game g = new Game(s.state);
        s.start(); g.start();
        try {
            s.join(); g.join();
        } catch(InterruptedException e) {}
    }
}
