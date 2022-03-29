import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        //new KVServer().start();
        new HttpTaskServer().start();
    }
}

