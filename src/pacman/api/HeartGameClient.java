/*HeartGameClient is an API client that connects to the Heart Game REST API using Java HttpClient. 
It sends an HTTP request, receives a JSON response containing puzzle data, parses the JSON, and returns a 
Puzzle object containing the image URL, solution, and carrot reward.*/

package pacman.api;

import java.io.IOException;                        //this handles input/output errors
import java.net.URI;                               //URI represents a web address.
import java.net.http.HttpClient;                   //HttpClient is used to send HTTP requests to a server.
import java.net.http.HttpRequest;                  //This represents the HTTP request sent to the server.
import java.net.http.HttpResponse;                 //This represents the response from the server.
import java.net.http.HttpResponse.BodyHandlers;    //BodyHandlers tells Java how to read the response body.
import org.json.JSONObject;                        //This library is used to read JSON data.JSONObject converts that into Java objects.

/**
 * Client for the Heart Game API (by Marc Conrad).
 * API documentation: http://marcconrad.com/uob/heart/doc.php
 * Base URL: http://marcconrad.com/uob/heart/api.php
 * This class demonstrates interoperability by consuming a RESTful web service.
 */

public class HeartGameClient { //this class act as API client
    private static final String API_URL = "http://marcconrad.com/uob/heart/api.php?out=json&base64=no";//This stores the API endpoint URL.
    private final HttpClient httpClient;//sore http client. Response -> Send requests &Receive responses
    
    //constructor
    public HeartGameClient() {
        // Create http client
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)  //If the server redirects to another URL → follow it automatically
                .build();
    }

    //Download a puzzle from the API
    public Puzzle fetchPuzzle() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder() //start building HTTP request
                .uri(URI.create(API_URL))//set requested URL
                .build();                //Now HTTP request is ready
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());//Send request to api,wait for response,read as string
        if (response.statusCode() != 200) { //check response status.200=success
            throw new IOException("HTTP error: " + response.statusCode());
        }
        String json = response.body();               //get response body
        JSONObject obj = new JSONObject(json);       //convert JSON to object
        String imageUrl = obj.getString("question"); //get the puzzle img url
        int solution = obj.getInt("solution");       //extract solution
        int carrots = obj.getInt("carrots");
        return new Puzzle(imageUrl, solution, carrots);  //this gives puzzle object
    } 

    public static class Puzzle {           //store puzzle into
        private final String imageUrl;
        private final int solution;
        private final int carrots;
        //Constructor
        public Puzzle(String imageUrl, int solution, int carrots) {
            this.imageUrl = imageUrl;
            this.solution = solution;
            this.carrots = carrots;
        }

        //Getter Methods
        public String getImageUrl() { return imageUrl; }
        public int getSolution() { return solution; }
        public int getCarrots() { return carrots; }     //this method allows other classes to access puzzle data
    }
}