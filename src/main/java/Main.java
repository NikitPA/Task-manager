import server.HttpTaskServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        //new HTTP.KVServer().start();
        new HttpTaskServer().start();
    }
}

