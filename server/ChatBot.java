//https://github.com/cheahjs/free-llm-api-resources
//https://console.groq.com/playground

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Module that manages access to the external ChatBot API.
 */
public abstract class ChatBot{

    /**
     * Adress towards the external API for HTTP requests.
     */
    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";
    /**
     * Key that allows us to use the API.
     */
    private static final String API_KEY = "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";
    /**
     * Model of the ChatBot we tell the API to use.
     */
    private static final String API_MODEL = "llama3-8b-8192";

    /**
     * The variable that contains the prompt message for the ChatBot.
     * It doesn't have to be used but it's like some space in the memory where we can place the prompt for later uses.
     */
    public static String prompt;


    /**
     * Asks our ChatBot's response to some message.
     * 
     * @param prompt Message to send to the chat bot.
     * @return Response from the ChatBot
     */
    public static String getResponse(String prompt) {
        String output = "[no response]"; // default response in case the API is broken (or if we did things wrong)

        try {

            // JSON content used to define what we want when doing the HTTP request
            String body = "{||'||messages||'||: [{||'||role||'||: ||'||user||'||, ||'||content||'||: ||'||" + prompt.trim() + "||'||}],||'||model||'||: ||'||" + ChatBot.API_MODEL + "||'||}";

            body = body.replace("||'||", "\""); // the "||'||" signs in the message are transformed to double quotes => "
            System.out.println(body);

            // Preparing the request to send to the server (API)
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ChatBot.API_URL)) // target URI
                    .header("Content-Type", "application/json") // some info in the "header" of our request
                    .header("Authorization", "Bearer " + ChatBot.API_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(body)) // we choose to HTTP request with POST
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            // We send the request
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());

            // The following line is really bad in practice but works in most test cases so I don't care for that prototype.
            output = (response.body().split("\"content\":\"")[1].split("\"},")[0]);
            System.out.println("output => " + output);

        } catch (IOException | InterruptedException e) {
            System.out.println("["+e.getClass()+"] problem with chatgpt");
        }

        return output;
    }

}