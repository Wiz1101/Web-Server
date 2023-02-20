import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args) throws IOException {
    int port = 0;
    String dir = "";

    while (true) {
      try {
        port = Integer.parseInt(args[0]); // PORT
        dir = args[1]; // Public Directory
        dir.toLowerCase();
        if (!dir.equals("public")) {
          System.out.println("ERROR: Incorrect Directory Name!");
          break;
        }
      } catch (Exception e) {
        System.out.println(e);
        System.out.println("ERROR: Please provide valid arguments!");
        break;
      }
      // ~~ SERVER ~~
      try (ServerSocket serverSocket = new ServerSocket(port)) {
        System.out.println("Server started.\n Listening for messages.");
        while (true) {
          try (Socket client = serverSocket.accept()) {
            System.out.println("Debug: got new message " + client.toString());
            InputStreamReader isr = new InputStreamReader(client.getInputStream());
            BufferedReader br = new BufferedReader(isr);

            StringBuilder request = new StringBuilder();

            String line; // Temp variable called line that holds one line a t a time of our message
            line = br.readLine();
            while (!line.isBlank()) {
              request.append(line + "\r\n");
              line = br.readLine();
            }
            // String[] parts = line.split(" ");
            // String version = parts[1];

            System.out.println("--REQUEST--");
            System.out.println(request);

            String firsline = request.toString().split("\n")[0];
            String resource = firsline.split(" ")[1];
            System.out.println(resource);
            OutputStream clientOutput = client.getOutputStream();
            FileInputStream file;

            if (resource.equals("/archlinux")) {
              file = new FileInputStream(dir + "/img/archlinux.png");
              clientOutput.write("HTTP/1.1 200 OK\r\n".getBytes());
              clientOutput.write("\r\n".getBytes());
              clientOutput.write(file.readAllBytes());
              clientOutput.flush();
            } else if (resource.equalsIgnoreCase("/home")) {
              file = new FileInputStream(dir + "/html/home.html");
              clientOutput.write("HTTP/1.1 200 OK\r\n".getBytes());
              clientOutput.write("\r\n".getBytes());
              clientOutput.write(file.readAllBytes());
              clientOutput.flush();
            } else if (resource.equalsIgnoreCase("/signup")) {
              file = new FileInputStream(dir + "/html/signup.htm");
              clientOutput.write("HTTP/1.1 200 OK\r\n".getBytes());
              clientOutput.write("\r\n".getBytes());
              clientOutput.write(file.readAllBytes());
              clientOutput.flush();
            } else if (resource.equalsIgnoreCase("/")) {
              file = new FileInputStream(dir + "/html/first.html");
              clientOutput.write("HTTP/1.1 200 OK\r\n".getBytes());
              clientOutput.write("\r\n".getBytes());
              clientOutput.write(file.readAllBytes());
              clientOutput.flush();
            } else if (resource.equalsIgnoreCase("/test")) {
              clientOutput.write("HTTP/1.1 302 Found\r\n".getBytes());
              clientOutput.write("\r\n".getBytes());
              clientOutput.write("<h1>Found</h1>".getBytes());
              clientOutput.write("<p>The requested URL was found click here to redirect</p>".getBytes());
              String str = String.format("<a href='http://127.0.0.1:%d/rick'><button>Visit Rick</button></a>\r\n",
                  port);
              clientOutput.write(str.getBytes());
              clientOutput.write("\r\n".getBytes());
              clientOutput.flush();
            } else if (resource.equalsIgnoreCase("/rick")) {
              file = new FileInputStream(dir + "/html/302.html");
              clientOutput.write("HTTP/1.1 200 OK\r\n".getBytes());
              clientOutput.write("\r\n".getBytes());
              clientOutput.write(file.readAllBytes());
              clientOutput.flush();
            } else {
              file = new FileInputStream(dir + "/html/404.html");
              clientOutput.write("HTTP/1.1 404 Not Found\r\n".getBytes());
              clientOutput.write("\r\n".getBytes());
              clientOutput.write(file.readAllBytes());
              clientOutput.flush();
            }
            client.close();
            System.err.println("Client connection closed!");

          }

        }

      } catch (Exception e) {
        ServerSocket serverSocket = new ServerSocket(port);
        Socket client = serverSocket.accept();
        OutputStream clientOutput = client.getOutputStream();
        FileInputStream file= new FileInputStream(dir + "/html/500.html");
        clientOutput.write("HTTP/1.1 500 Server Error\r\n".getBytes());
        clientOutput.write("\r\n".getBytes());
        clientOutput.write(file.readAllBytes());
        clientOutput.flush();
        serverSocket.close();
        file.close();
        client.close();

      }

    }

  }
}
