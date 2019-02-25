package pc2r.server;

import java.util.Map;
import java.net.Socket;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

import pc2r.commands.CommandParser;
import pc2r.commands.ClientCommand;
import pc2r.commands.CommandParseException;

public class Client extends Thread {
    protected Socket client;
    protected CommandParser parser;
    protected BufferedReader in;
    protected PrintStream out;

    public Client(Socket client, CommandParser parser) {
        this.client = client;
        this.parser = parser;

        try {
            in = new BufferedReader(new InputStreamReader(new DataInputStream(client.getInputStream())));
            out = new PrintStream(client.getOutputStream());
        } catch (IOException e) {
            try { client.close(); } catch (IOException _e) {};
            System.err.println(e.getMessage());
            return;
        }
        this.start();
    }

    public void send(String s) {
        out.println(s);
    }

    public void run() {
        try {
            while(true) {
                String line = in.readLine();
                if(line == null) throw new IOException("");
                try {
                    ClientCommand cmd = parser.parse(line, this);
                    cmd.execute();
                } catch (CommandParseException e) { System.err.println(e); }
            }
        } catch(IOException e) {
        } finally { try {client.close();} catch(IOException _e) {}}
    }
}
