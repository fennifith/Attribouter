package me.jfenn.attribouter.data.github;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public abstract class GitHubData {

    private String url;
    private GitHubThread thread;
    private Gson gson;
    private boolean isInitialized;

    private List<OnInitListener> listeners;

    GitHubData(String url) {
        this.url = url;
        listeners = new ArrayList<>();

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
        gson.fromJson(json, getClass());
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
    public final void startInit() {
        thread = new GitHubThread(this, url);
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

        return this;
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

        private GitHubData data;
        private String url;
        private StringBuilder builder;

        private GitHubThread(GitHubData data, String url) {
            this.data = data;
            this.url = url;
            builder = new StringBuilder();
        }

        @Override
        public void run() {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(new URL(url).openConnection().getInputStream()));

                String line;
                while ((line = reader.readLine()) != null)
                    builder.append(line);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        data.init(builder.toString());
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public interface OnInitListener {
        void onInit(GitHubData data);

        void onFailure(GitHubData data); //TODO: actually calling this method when something fails might be nice
    }

}
