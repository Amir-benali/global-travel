package com.globalTravel.services.flight;

        import java.net.URI;
        import java.net.http.HttpClient;
        import java.net.http.HttpRequest;
        import java.net.http.HttpResponse;
        import java.util.ArrayList;
        import java.util.LinkedHashSet;
        import java.util.List;
        import java.util.Set;

        import org.json.JSONArray;
        import org.json.JSONObject;

        public class AirportService {

            private static final String API_URL = "https://airportgap.com/api/airports";

            public List<String> fetchAirportNames() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(API_URL))
                        .build();

                String responseBody = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
                JSONObject responseObject = new JSONObject(responseBody);
                JSONArray airportsArray = responseObject.getJSONArray("data");

                List<String> airportNames = new ArrayList<>();
                for (int i = 0; i < airportsArray.length(); i++) {
                    JSONObject airport = airportsArray.getJSONObject(i).getJSONObject("attributes");
                    airportNames.add(airport.getString("name"));
                }

                System.out.println("Fetched Airport Names: " + airportNames); // Debugging
                return airportNames;
            }

            public List<String> fetchAirportCountries() throws Exception {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(API_URL))
                        .build();

                String responseBody = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
                Set<String> airportCountries = new LinkedHashSet<>();
                JSONObject responseObject = new JSONObject(responseBody);
                JSONArray airportsArray = responseObject.getJSONArray("data");

                for (int i = 0; i < airportsArray.length(); i++) {
                    JSONObject airport = airportsArray.getJSONObject(i).getJSONObject("attributes");
                    airportCountries.add(airport.getString("country"));
                }

                System.out.println("Fetched Airport Countries: " + airportCountries); // Debugging
                return new ArrayList<>(airportCountries);
            }

        }