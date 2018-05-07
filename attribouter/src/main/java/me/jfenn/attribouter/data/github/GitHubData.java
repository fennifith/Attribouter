package me.jfenn.attribouter.data.github;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public abstract class GitHubData {

    private String url;
    private GitHubThread thread;
    private Gson gson;
    private boolean isInitialized;

    private List<OnInitListener> listeners;
    private List<String> tags;

    GitHubData(String url) {
        this.url = url;
        listeners = new ArrayList<>();
        tags = new ArrayList<>();

        gson = new GsonBuilder()
                .registerTypeAdapter(getClass(), new MootInstanceCreator(this))
                .create();
    }

    /**
     * Called once the request to github servers has been successfully completed.
     *
     * @param json the json response
     */
    private void init(String json) {
        initJson(gson, json);
        onInit();
        isInitialized = true;
        for (OnInitListener listener : listeners) {
            listener.onInit(this);
        }
    }

    /**
     * Initializes the values in the class from the json string. Exists only to be
     * overridden if necessary.
     * @param gson the gson object
     * @param json the json string
     */
    protected void initJson(Gson gson, String json) {
        try {
            gson.fromJson(json, getClass());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            Log.e("Attribouter", "Error parsing JSON from " + url);
        }
    }

    /**
     * Called once the object has finished being initialized. Exists only to be overriden
     * if necessary.
     */
    protected void onInit() {
    }

    /**
     * Starts the network request thread, should only be called once.
     */
    public final void startInit(Context context, String token) {
        thread = new GitHubThread(context, token, this, url);
        thread.start();
    }

    public final boolean isInitialized() {
        return isInitialized;
    }

    /**
     * Merge this data's listeners with another. Should only be called if the
     * two are of the exact same class.
     *
     * @param data the data to merge with
     * @return a somewhat pointless "this", only to make it blatantly obvious which GitHubData actually contains the end result
     */
    public final GitHubData merge(GitHubData data) {
        for (OnInitListener listener : data.listeners) {
            if (!listeners.contains(listener))
                listeners.add(listener);
        }

        for (String tag : data.tags)
            addTag(tag);

        return this;
    }

    public final void addTag(String tag) {
        if (!tags.contains(tag))
            tags.add(tag);
    }

    public final List<String> getTags() {
        return tags;
    }

    public final void addOnInitListener(OnInitListener listener) {
        listeners.add(listener);
    }

    public final void removeOnInitListener(OnInitListener listener) {
        listeners.remove(listener);
    }

    public final void interruptThread() {
        if (thread != null && thread.isAlive() && !thread.isInterrupted())
            thread.interrupt();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof GitHubData && ((GitHubData) obj).url.equals(url);
    }

    private static class MootInstanceCreator implements InstanceCreator<GitHubData> {

        private GitHubData instance;

        public MootInstanceCreator(GitHubData instance) {
            this.instance = instance;
        }

        @Override
        public GitHubData createInstance(Type type) {
            return instance;
        }
    }

    private static class GitHubThread extends Thread {

        private File cacheFile;
        private GitHubData data;
        private String url;
        private String token;
        private Gson gson;

        private GitHubThread(Context context, String token, GitHubData data, String url) {
            this.data = data;
            this.url = url;
            this.token = token;
            gson = new GsonBuilder().setLenient().create();
            cacheFile = new File(context.getCacheDir(), ".attriboutergithubcache");
        }

        @Override
        public void run() {
            Map<String, Object> cache = null;
            if (Math.abs(System.currentTimeMillis() - cacheFile.lastModified()) < 864000000) {
                StringBuilder cacheBuilder = new StringBuilder();
                Scanner cacheScanner = null;
                try {
                    cacheScanner = new Scanner(cacheFile);
                    while (cacheScanner.hasNext()) {
                        cacheBuilder.append(cacheScanner.nextLine());
                    }

                    cache = gson.fromJson(cacheBuilder.toString(), new HashMap<String, Object>().getClass());
                } catch (IOException ignored) {
                } catch (Exception e) {
                    cacheFile.delete(); //probably a formatting error
                }

                if (cacheScanner != null)
                    cacheScanner.close();

                if (cache != null && cache.containsKey(url)) {
                    Object cached = cache.get(url);
                    if (cached instanceof String) {
                        callInit((String) cached);
                        return;
                    }
                }
            }

            HttpURLConnection connection = null;
            BufferedReader jsonReader = null;
            StringBuilder jsonBuilder = new StringBuilder();
            try {
                connection = (HttpURLConnection) new URL(url).openConnection();
                if (token != null)
                    connection.setRequestProperty("Authorization", "token " + token);

                jsonReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                if (connection.getResponseCode() == 403) {
                    jsonReader.close();
                    return;
                }

                String line;
                while ((line = jsonReader.readLine()) != null)
                    jsonBuilder.append(line);
            } catch (IOException e) {
                e.printStackTrace();
                jsonBuilder = null;
            }

            if (connection != null) {
                connection.disconnect();
            }

            if (jsonReader != null) {
                try {
                    jsonReader.close();
                } catch (IOException ignored) {
                }
            }

            if (jsonBuilder != null) {
                String json = jsonBuilder.toString();
                callInit(json);

                if (cache == null)
                    cache = new HashMap<>();

                cache.put(url, json);

                PrintWriter cacheWriter = null;
                try {
                    cacheWriter = new PrintWriter(cacheFile);
                    cacheWriter.println(gson.toJson(cache));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (cacheWriter != null)
                    cacheWriter.close();
            }
        }

        private void callInit(final String json) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    data.init(json);
                }
            });
        }
    }

    public interface OnInitListener {
        void onInit(GitHubData data);

        void onFailure(GitHubData data); //TODO: actually calling this method when something fails might be nice
    }

}
