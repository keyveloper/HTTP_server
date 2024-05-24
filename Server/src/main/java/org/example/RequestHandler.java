package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Data
public class RequestHandler {
    private final TextManager textManager = new TextManager();

    public Response handleRequest(Request request) {
        System.out.println("start to handleRequest!!");
        switch (request.getMethod()) {
            case "GET" -> {
                return handleGet(request.getUri());
            }
            case "POST" -> {
                return handlePost(request.getUri(), request.getBody());
            }
            case "DELETE" -> {
                return handleDelete(request.getUri());
            }

            default -> {
                return new Response(StatusCode.NOT_FOUND);
            }


        }
    }

    private Response handleGet(String uri) {
        System.out.println("<handleGET>\nuri: " + uri + "\n");
        if (uri.startsWith("/time")) {
            return responseGetTime();
        }

        if (uri.startsWith("/textall")) {
            System.out.println("response All text \nuri: " + uri);
            return responseGetAllText();
        }

        if (uri.startsWith("/text")) {
            return responseGetText(uri.substring(5));
        }

        if (uri.startsWith("/image")) {
            return null;
        }
        return null;
    }

    private Response responseGetText(String parameter) {
        if (parameter.startsWith("/")) {
            String textKey = parameter.substring(1);

            String body = textManager.get(textKey);
            textManager.print();
            System.out.println("<responseGetText>\ntextKey: " + textKey + "\nresult: " +  body);
            if (body == null) {
                return new Response(StatusCode.NOT_FOUND);
            }
            Response response = new Response(StatusCode.OK);
            response.setBodyLength(body.length());
            response.setBodyType("text/plain");
            response.setBody(body);
            return response;
        }
        // return error response;
        return new Response(StatusCode.NOT_FOUND);
    }

    private Response responseGetAllText() {
        System.out.println("\n<<start get All>>\n");
        if (textManager.isEmpty()) {
            System.out.println("textMap is empty");
            return new Response(StatusCode.NOT_FOUND);
        }
        String jsonBody = textManager.getAll();
        System.out.println("<getAll>\njsonBody: " + jsonBody);
        Response response = new Response(StatusCode.OK);
        response.setBodyLength(jsonBody.length());
        response.setBodyType("application/json");
        response.setBody(jsonBody);
        return response;
    }

    private Response handlePost(String uri, String body) {
        if (uri.startsWith("/text")) {
            return postText(uri.substring(5), body);
        }
        return new Response(StatusCode.NOT_FOUND);
    }

    private Response postText(String parameter, String body) {
        if (textManager.put(parameter.substring(1), body)) {
            textManager.print();
            return new Response(StatusCode.OK);
        }

        return new Response(StatusCode.SERVICE_UNAVAILABLE);
    }

    private Response handleDelete(String uri) {
        if (uri.startsWith("/text")) {
            return deleteText(uri.substring(5));
        }
        return new Response(StatusCode.NOT_FOUND);
    }

    private Response deleteText(String parameter) {
        if (textManager.remove(parameter.substring(1))) {
            return new Response(StatusCode.OK);
        }
        return new Response(StatusCode.NOT_FOUND);
    }

    private Response responseGetTime() {
        System.out.println("start to send Time response!! \n");
        Response response;
        LocalDateTime now = LocalDateTime.now();
        String formattedNow = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("now: " + formattedNow);

        // Json
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("currentTime", formattedNow);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonResponse = objectMapper.writeValueAsString(responseMap);

            response = new Response(StatusCode.OK);
            response.setBodyLength(jsonResponse.length());
            response.setBodyType("application/json");
            response.setBody(jsonResponse);
            return response;
        } catch (JsonProcessingException e) {
            System.out.println("Request Handler - responseTime error: " + e.getMessage());
        }
        return null;
    }
}