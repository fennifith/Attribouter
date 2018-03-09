package me.jfenn.attribouter.data.github;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;

public abstract class GitHubData {

    private GitHubThread thread;
    private Gson gson;

    private OnInitListener listener;

    GitHubData(String url) {
        gson = new GsonBuilder()
                .registerTypeAdapter(getClass(), new MootInstanceCreator(this))
                .create();

        thread = new GitHubThread(this, url);
        thread.start();
    }

    private void init(String json) {
        initJson(gson, json);
        onInit();
        if (listener != null)
            listener.onInit(this);
    }

    protected void initJson(Gson gson, String json) {
        gson.fromJson(json, getClass());
    }

    protected void onInit() {
    }

    public final void setOnInitListener(OnInitListener listener) {
        this.listener = listener;
    }

    public final void interruptThread() {
        if (thread.isAlive() && !thread.isInterrupted())
            thread.interrupt();
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

        private GitHubThread(GitHubData data, String url) {
            this.data = data;
            this.url = url;
        }

        @Override
        public void run() {
            BufferedReader reader = null;
            try {
                StringBuilder builder = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(new URL(url).openConnection().getInputStream()));

                String line;
                while ((line = reader.readLine()) != null)
                    builder.append(line);

                data.init(builder.toString());
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

        void onFailure(GitHubData data);
    }

}
