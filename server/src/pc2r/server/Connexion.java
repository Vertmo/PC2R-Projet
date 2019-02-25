package pc2r.server;

import java.util.Map;
import java.net.Socket;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class Connexion extends Thread {
    protected Socket client;
    protected BufferedReader in;
    protected PrintStream out;

    public Connexion(Socket client) {
        this.client = client;

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

    public void run() {
        try {
            while(true) {
                String line = in.readLine();
                if(line == null) throw new IOException("");
                String[] cmd = line.split(" ");
                // TODO
                out.println("OK");
            }
        } catch(IOException e) {
            System.out.println("Connexion ended");
        } finally { try {client.close();} catch(IOException _e) {}}
    }
}
