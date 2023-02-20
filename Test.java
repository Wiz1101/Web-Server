import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Test {

   public static void main(String[] args) throws Exception {

      // Define the URL of the endpoint you want to POST to
      URL url = new URL("http://127.0.0.1:8888/api/endpoint");

      // Open a connection to the URL
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();

      // Set the request method to POST
      conn.setRequestMethod("POST");

      // Set the request headers
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setRequestProperty("Accept", "application/json");

      // Enable the connection's output
      conn.setDoOutput(true);

      // Create the request body
      String requestBody = "{\"key1\":\"value1\",\"key2\":\"value2\"}";

      // Write the request body to the connection's output stream
      OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
      writer.write(requestBody);
      writer.flush();

      // Read the response from the connection's input stream
      BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String line;
      StringBuilder response = new StringBuilder();
      while ((line = reader.readLine()) != null) {
         response.append(line);
      }
      reader.close();

      // Print the response
      System.out.println(response.toString());
   }
}
