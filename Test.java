import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Test {
    public static void main(String[] args) throws Exception {
        String url = "http://127.0.0.1:8080/myendpoint"; // URL of the server endpoint
        String data = "param1=value1&param2=value2"; // POST data

        // Create a URL object and open an HTTP connection to the server
        URL urlObj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

        // Set the request method to POST and set other properties as needed
        con.setRequestMethod("POST");
        con.setDoOutput(true);

        // Write the POST data to the request body
        OutputStream os = con.getOutputStream();
        os.write(data.getBytes());
        os.flush();
        os.close();

        // Read the response from the server
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // Print the response
        System.out.println(response.toString());
    }
}
