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
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

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
        private Context context;

        private static final int NUM_OF_PERMITS = 1;
        private Semaphore cacheToHttpSemaphore;
        private boolean continueHttpThread = false;
        private Thread cacheThread;

        private GitHubThread(Context context, String token, GitHubData data, String url) {
            this.data = data;
            this.url = url;
            this.token = token;
            this.context = context;
            cacheToHttpSemaphore = new Semaphore(NUM_OF_PERMITS, true);
            cacheThread = startCacheThread();
        }

        @Override
        public void run() {
            try {
                cacheToHttpSemaphore.acquire();

                //in the event cache thread did not run before http thread (current thread)
                if (cacheThread.isAlive()) {
                    try {
                        //release lock so cache thread can run
                        cacheToHttpSemaphore.release();
                        //wait for cache thread to finish
                        cacheThread.join();
                        //get lock for this thread
                        cacheToHttpSemaphore.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
               if (continueHttpThread)
                   doHttpConnection();
            } finally {
                cacheToHttpSemaphore.release();
            }
        }

        private Thread startCacheThread() {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        cacheToHttpSemaphore.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    try {
                        doCacheInspection();
                    } finally {
                        cacheToHttpSemaphore.release();
                    }
                }
            });
            thread.setPriority(Thread.MAX_PRIORITY);
            thread.start();
            return thread;
        }

        private void doCacheInspection() {
            File dir = new File(context.getCacheDir() + "/.attribouter/github");
            if (!dir.exists())
                dir.mkdirs();

            cacheFile = new File(dir, url.replace("/", ".") + ".json");

            String cache = null;
            if (Math.abs(System.currentTimeMillis() - cacheFile.lastModified()) < 864000000) {
                StringBuilder cacheBuilder = new StringBuilder();
                Scanner cacheScanner = null;
                try {
                    cacheScanner = new Scanner(cacheFile);
                    while (cacheScanner.hasNext()) {
                        cacheBuilder.append(cacheScanner.nextLine());
                    }

                    cache = cacheBuilder.toString();
                } catch (IOException ignored) {
                } catch (Exception e) {
                    cacheFile.delete(); //probably a formatting error
                }

                if (cacheScanner != null)
                    cacheScanner.close();

                if (cache != null) {
                    callInit(cache);
                } else continueHttpThread = true;
            } else continueHttpThread = true;
        }

        private void doHttpConnection(){
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
                    connection.disconnect();
                    return;
                }

                String line;
                while ((line = jsonReader.readLine()) != null) {
                    if(isInterrupted()){
                        connection.disconnect();
                        jsonReader.close();
                        return;
                    }
                    jsonBuilder.append(line);
                }
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

                PrintWriter cacheWriter = null;
                try {
                    cacheWriter = new PrintWriter(cacheFile);
                    cacheWriter.println(json);
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
