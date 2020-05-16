package com.example.ebookdex.utils;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.example.ebookdex.models.BookImageModels;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static android.graphics.BitmapFactory.decodeStream;

public final class QueryUtils {
    private static final String TAG = "QueryUtils";
    //Query Google Books API and return a list of {@Link Book} objects.

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    public static ArrayList<BookImageModels> fetchBookData(String requestUrl) {
        //Create URL object
        URL url = createUrl(requestUrl);

        //Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(TAG, "Problem making the HTTP request.", e);
        }
        // Extract relevant fields from the JSON response and create a list of {@Link Book}s
        ArrayList<BookImageModels> books = extractFeatureFromJson(jsonResponse);
        //Return the list of books to the method in BooksLoader
        return books;
    }

    /**
     * Return a list of {@link Book} objects that has been built up from
     * parsing a JSON response.
     */
    private static ArrayList<BookImageModels> extractFeatureFromJson(String bookJSON) {
        //If the JSON String is empty or null, then return early
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        //Create an empty ArrayList that we can start adding books to
        ArrayList<BookImageModels> books = new ArrayList<>();

        //Try to parse the bookJSON. If there's a problem with the way the JSON
        //is formatted, a JSONException exception object will be thrown.
        //Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            //Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(bookJSON);

            //Extract the JSONArray associated with the key called "items"
            JSONArray bookArray = baseJsonResponse.getJSONArray("items");

            //For each book in the bookArray, create an {@Link book} object
            for (int i = 0; i < bookArray.length(); i++) {

                //initialising all variables
                JSONObject bookRecord;
                JSONObject volumeInfo;
                String title = null;
                String subTitle = null;
                JSONArray authorsArray;
                ArrayList<String> authors = new ArrayList<>();
                String pageCount = null;
                String averageRating = null;
                Bitmap smallThumbnail = null;

                bookRecord = bookArray.getJSONObject(i);
                volumeInfo = bookRecord.getJSONObject("volumeInfo");
                if (volumeInfo.has("title")) {
                    title = volumeInfo.getString("title");
                }
                //Check for the key, if it exists, getString
                if (volumeInfo.has("subtitle")) {
                    subTitle = volumeInfo.getString("subtitle");
                }

                //Check for the key, if it exists, getString
                if (volumeInfo.has("authors")) {
                    authorsArray = volumeInfo.getJSONArray("authors");

                    //Extract Author Names
                    for (int a = 0; a < authorsArray.length(); a++) {
                        String author = authorsArray.getString(a);
                        authors.add(author);
                    }
                }

                //If there is average rating, then only getString
                if (volumeInfo.has("averageRating")) {
                    averageRating = volumeInfo.getString("averageRating");
                } else {
                    averageRating = "-";
                }
                if (volumeInfo.has("pageCount")) {
                    pageCount = volumeInfo.getString("pageCount");
                }

                if (volumeInfo.has("imageLinks")) {
                    JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                    if (imageLinks.has("smallThumbnail")) {
                        smallThumbnail = fetchThumbnail(imageLinks.getString("smallThumbnail"));
                    }
                    Log.d(TAG, "extractFeatureFromJson: "+smallThumbnail);
                }

                //Create a new object with the title, pagecount and average rating
                BookImageModels book = new BookImageModels(
                        smallThumbnail);
                //Add the new Book to the search result array
                books.add(book);
            }


        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }
        //Finally return something
        return books;
    }

    //Returns new URL object from the given string URL.
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Problem building the URL ", e);
        }
        return url;
    }

    //Method that makes HTTP request and returns a String as the response
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        //If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);

            } else {
                Log.e(TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /*
     * For SmallThumbnail Loading
     */
    public static Bitmap fetchThumbnail(String requestUrl) {
        //Create URL object
        URL url = createUrl(requestUrl);

        //Perform HTTP request to the URL and receive a JSON response back
        Bitmap smallThumbnail = null;
        try {
            smallThumbnail = makeHttpRequestForBitmap(url);
        } catch (IOException e) {
            Log.e(TAG, "Problem making the HTTP request.", e);
        }
        Log.v("SmallThumbnail", String.valueOf(smallThumbnail));
        return smallThumbnail;
    }

    //Method that makes HTTP request and returns a Bitmap as the response
    private static Bitmap makeHttpRequestForBitmap(URL url) throws IOException {
        Bitmap bitmapResponse=null;

        //If the URL is null, then return early.
        if (url == null) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                bitmapResponse = decodeStream(inputStream);

            } else {
                Log.e(TAG, "Error response code bitmap: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return bitmapResponse;
    }
}
