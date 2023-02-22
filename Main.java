import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Main {

  public static void main(String[] args) throws IOException {
    int port = 0; // PORT
    String dir = ""; // Public Directory

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
          
          System.out.println("--REQUEST--");
          System.out.println(request);
          String firsline = request.toString().split("\n")[0];
          String resource = firsline.split(" ")[1];
          OutputStream clientOutput = client.getOutputStream();
          FileInputStream file;
          File f = new File(resource);
          
          try {
            if ((f.exists() && f.isFile())) {
              file = new FileInputStream(dir + f);
              clientOutput.write("HTTP/1.1 200 OK\r\n".getBytes());
              clientOutput.write("\r\n".getBytes());
              clientOutput.write(file.readAllBytes());
            } else if (resource.equals("")) { 
              file = new FileInputStream(dir + "first.html");
              clientOutput.write("HTTP/1.1 200 OK\r\n".getBytes());
              clientOutput.write("\r\n".getBytes());
              clientOutput.write(file.readAllBytes());
            } else if (resource.equalsIgnoreCase("/500")) {
              file = new FileInputStream(dir + "/html/500.html");
              clientOutput.write("HTTP/1.1 500 Server Error\r\n".getBytes());
              clientOutput.write("\r\n".getBytes());
              clientOutput.write(file.readAllBytes());
            } else if (resource.equalsIgnoreCase("/test")) {
              String str = String.format("<a href='http://127.0.0.1:%d/rick'><button>Visit Rick!</button></a>\r\n",
                  port);
              // 302 Found
              file = new FileInputStream(dir + "/html/302.html");
              clientOutput.write("HTTP/1.1 302 Found\r\n".getBytes());
              clientOutput.write("\r\n".getBytes());
              clientOutput.write(file.readAllBytes());
              clientOutput.write(str.getBytes());

            } else if (resource.equalsIgnoreCase("/test500")) { // Generate an Error for 500
              String str = String.format("<a href='http://127.0.0.1:%f/rick'><button>Visit Rick!</button></a>\r\n",
                  port);
              clientOutput.write(str.getBytes());
            } else {
              // 404 Not Found Error
              file = new FileInputStream(dir + "/html/404.html");
              clientOutput.write("HTTP/1.1 404 Not Found\r\n".getBytes());
              clientOutput.write("\r\n".getBytes());
              clientOutput.write(file.readAllBytes());
            }
          
            client.close();
            System.err.println("Client connection closed!");

          } catch (Exception e) {
            // 500 Internal Server Error
            file = new FileInputStream(dir + "/html/500.html");
            clientOutput.write("HTTP/1.1 500 Server Error\r\n".getBytes());
            clientOutput.write("\r\n".getBytes());
            clientOutput.write(file.readAllBytes());
          }
        
        }

      } catch (Exception e) {
        continue;
      }

    }
    
  }
  public static ArrayList<String> getFiles(String root) {
    ArrayList<String> filenames = new ArrayList<>();
    try {
      File path = new File(System.getProperty("user.dir") + root + "/");
      File[] files = path.listFiles();
      for (File f:files) {
        if (f.isFile()) {
          String name = f.getPath();
          filenames.add(name);
        } else if (f.isDirectory()) {
          ArrayList<String> subdir = getFiles(f.getPath());
          for (String file: subdir){
            filenames.add(file);
          }
        }
      } 
    } finally {}
    return filenames;
  }
}
