import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

  public static void main(String[] args) throws IOException {
    int port; // PORT
    String dir; // Public Directory
    while (true) {
      try {
        port = Integer.parseInt(args[0]); // PORT
        dir = args[1]; // Public Directory
        File directory = new File(dir);
        if (dir.contains(".")) { // Mitigating directory traversal 
          System.out.println("ERROR: '.' in the name of the Directory is Not Allowed!");
          System.out.println("* Example: Server 8888 public");
          break;
        } else if (!directory.isDirectory()){
          System.out.println("ERROR: The directory does not exist. Such an embarrassment!");
          System.out.println("* Example: Server 8888 public");
          break;
        }
      } catch (Exception e) {
        System.out.println(e);
        System.out.println("ERROR: Not enough arguments provided or invalid argument!");
        System.out.println("* Usage: epic_server [port] [serving_directory]");
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
          System.out.println("|~~~~  REQUEST  ~~~~|");
          System.out.println(request);
          String firsline = request.toString().split("\n")[0];
          String resource = firsline.split(" ")[1];
          OutputStream clientOutput = client.getOutputStream();
          FileInputStream file;

          // Checking Content Type
          String contentType = "text/html";

          // ~~ FILES ~~
          try {

            if (!resource.contains(".")) { // Index.html

              try {
                File filename = new File(dir + resource + "/index.html");

                file = new FileInputStream(filename);
                String header = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: " + contentType + "\r\n" +
                    "Content-Length: " + filename.length() + "\r\n" +
                    "\r\n";

                clientOutput.write(header.getBytes());
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

            } else if (resource.equals("/test.html")) { // Test for 500 Internal Server Error
              // Triggering internal Error
              int str = Integer.parseInt("LNU");
              System.out.println(str);
              // -------------------------
              clientOutput.write("\r\n\r\n".getBytes());

            } else {
              try {
                File filename = new File(dir + resource);
                if (resource.endsWith(".png")) {
                  contentType = "image/png";
                }
                file = new FileInputStream(filename);
                String header = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: " + contentType + "\r\n" +
                    "Content-Length: " + filename.length() + "\r\n" +
                    "\r\n";

                clientOutput.write(header.getBytes());
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
