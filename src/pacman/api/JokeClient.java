/*The JokeClient class connects to the JokeAPI using Java's HttpClient. It sends an HTTP request to the API endpoint, 
receives a JSON response containing a joke, parses the JSON using JSONObject, and creates a Joke object containing 
the joke text, category, and ID. The API filters offensive jokes and supports both single and two-part jokes.*/

package pacman.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import org.json.JSONObject;

/*This class is responsible for:
1.connecting to Joke API
2.fetching jokes
3.returning joke data*/

public class JokeClient {
    private static final String API_URL = "https://v2.jokeapi.dev/joke/Any?blacklistFlags=nsfw,religious,political,racist,sexist,explicit&type=twopart,single";
    private final HttpClient httpClient; //Used to send HTTP requests to the API server

    //Constructor
    public JokeClient() {
        this.httpClient = HttpClient.newBuilder()   //This creates a new HttpClient builder.
                .followRedirects(HttpClient.Redirect.ALWAYS)//If the API redirects to another URL,follow the redirect automatically.
                .build();//Builds the final HttpClient object.
    }

    //fetches a joke from the API. Returns: Joke object
    public Joke fetchJoke() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()  //Creates a new HTTP request.
                .uri(URI.create(API_URL))               //Sets the API URL
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36") //Adds a User-Agent header.Header tells serse This request is from a browser-like client
                .build();//Builds the final HTTP request.
        
        //This sends the request to the API.
        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());//convert response body into String
        
        if (response.statusCode() != 200) {
            throw new IOException("HTTP error: " + response.statusCode());
        }
        
        //Get Response Body
        String json = response.body();             //Stores the response JSON in a string.
        JSONObject obj = new JSONObject(json);     //Converts JSON string into a JSONObject.Now Java can extract values.
        
        String jokeText;
        String type = obj.getString("type");
        
        if ("single".equals(type)) {
            jokeText = obj.getString("joke");
        } else {
            // twopart joke: combine setup and delivery
            String setup = obj.getString("setup");
            String delivery = obj.getString("delivery");
            jokeText = setup + "\n\n" + delivery;
        }
        
        String category = obj.getString("category");
        int id = obj.getInt("id");
        
        return new Joke(jokeText, category, id);
    }

    //Inner class
    public static class Joke {
        private final String joke;
        private final String category;
        private final int id;

        public Joke(String joke, String category, int id) {
            this.joke = joke;
            this.category = category;
            this.id = id;
        }

        public String getJoke() { return joke; }
        public String getCategory() { return category; }
        public int getId() { return id; }
    }
}