import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

  public static void main(String[] args) throws IOException {
    int port = 0; // PORT
    String dir = ""; // Public Directory

    while (true) {
      try {
        port = Integer.parseInt(args[0]); // PORT
        dir = args[1]; // Public Directory
        dir.toLowerCase();
        if (!dir.equals("public")) { // Directory as an argument
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
        try (Socket client = serverSocket.accept()) {
          System.out.println("Debug: got new message " + client.toString());
          InputStreamReader isr = new InputStreamReader(client.getInputStream());
          BufferedReader br = new BufferedReader(isr);
          StringBuilder request = new StringBuilder();
          String line; // Temp variable called line that holds one line at a time of our message
          line = br.readLine();
          while (!line.isBlank()) {
            request.append(line + "\r\n");
            line = br.readLine();
          }

          System.out.println("|------ REQUEST ------|");
          System.out.println(request);
          String firsline = request.toString().split("\n")[0];
          String resource = firsline.split(" ")[1];
          OutputStream clientOutput = client.getOutputStream();
          FileInputStream file;

          // ~~ FILES ~~
          try {
            if (!resource.contains(".")) { // Index.html
              file = new FileInputStream(dir + resource + "/index.html");
              clientOutput.write("HTTP/1.1 200 OK\r\n".getBytes());
              clientOutput.write("\r\n".getBytes());
              clientOutput.write(file.readAllBytes());

            } else if (resource.equals("/test.html")) { // Test for 500 Server Error
              // Triggering internal Error
              String str = String.format("<a href='http://127.0.0.1:%f/rick.html'><button>Visit Rick!</button></a>\r\n",
                  port);
              clientOutput.write("\r\n".getBytes());
              clientOutput.write(str.getBytes());
            } else {
              try {
                file = new FileInputStream(dir + resource);
                clientOutput.write("HTTP/1.1 200 OK\r\n".getBytes());
                clientOutput.write("\r\n".getBytes());
                clientOutput.write(file.readAllBytes());
                file.close();
              } catch (Exception e) {
                // 404 Not Found Error
                clientOutput.write("HTTP/1.1 404 Not Found\r\n".getBytes());
                clientOutput.write("\r\n".getBytes());
                clientOutput.write("<title>404 Not Found</title>\r\n".getBytes());
                clientOutput.write("<h1>Not Found</h1>".getBytes());
                clientOutput.write("<p>The requested URL was not found on this server</p>".getBytes());
                clientOutput.write("\r\n\r\n".getBytes());
                clientOutput.flush();
              }
            }

          } catch (Exception e) {
            // 500 Internal Server Error
            clientOutput.write("HTTP/1.1 500 Internal Server Error\r\n".getBytes());
            clientOutput.write("\r\n".getBytes());
            clientOutput.write("<title>500 Internal Server Error</title>\r\n".getBytes());
            clientOutput.write("<h1>Internal Server Error</h1>".getBytes());
            clientOutput.write(
                "<p>The server encountered an internal error or misconfiguration and was unable to complete your request.</p>"
                    .getBytes());
            clientOutput.write("\r\n\r\n".getBytes());
            clientOutput.flush();

          }
        }

      } catch (Exception e) {
        continue;
      }

    }

  }
}
