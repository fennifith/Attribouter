package me.jfenn.attribouter.data.info;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import me.jfenn.attribouter.data.github.GitHubData;
import me.jfenn.attribouter.interfaces.Mergeable;

public abstract class InfoData<T extends InfoData.ViewHolder> implements GitHubData.OnInitListener {

    private int layoutRes;
    private List<InfoData> children;
    private List<GitHubData> requests;

    private OnRequestListener listener;

    public InfoData(@LayoutRes int layoutRes) {
        this.layoutRes = layoutRes;
        requests = new ArrayList<>();
        children = new ArrayList<>();
    }

    void addChildren(XmlResourceParser parser) throws IOException, XmlPullParserException {
        while (parser.next() != XmlResourceParser.END_TAG) {
            if (parser.getEventType() == XmlResourceParser.START_TAG) {
                try {
                    Class<?> classy = Class.forName(parser.getName());
                    Constructor constructor = classy.getConstructor(XmlResourceParser.class);
                    addChild((InfoData) constructor.newInstance(parser));
                } catch (ClassNotFoundException e) {
                    Log.e("Attribouter", "Class name \"" + parser.getName() + "\" not found - you should probably check your configuration file for typos.");
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    Log.e("Attribouter", "Class \"" + parser.getName() + "\" definitely exists, but doesn't have the correct constructor. Check that you have defined one with a single argument - \'android.content.res.XmlResourceParser\'");
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (java.lang.InstantiationException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (ClassCastException e) {
                    Log.e("Attribouter", "Class \"" + parser.getName() + "\" has been instantiated correctly, but it must extend \'me.jfenn.attribouter.data.info.InfoData\' to be worthy of the great RecyclerView adapter.");
                    e.printStackTrace();
                }
            }
        }
    }

    InfoData addChild(InfoData child) {
        return addChild(children.size(), child);
    }

    InfoData addChild(int index, InfoData child) {
        if (!children.contains(child)) {
            children.add(index, child);
        } else {
            InfoData merger = children.get(children.indexOf(child));
            if (merger instanceof Mergeable) {
                ((Mergeable) merger).merge(child);
                return merger;
            } else {
                children.add(index, child);
            }
        }

        child.setOnRequestListener(listener);
        return child;
    }

    public List<InfoData> getChildren() {
        return children;
    }

    protected void addRequest(GitHubData request) {
        request.addOnInitListener(this);
        requests.add(request);
        if (listener != null)
            listener.onRequest(this, request);
    }

    public final void setOnRequestListener(OnRequestListener listener) {
        this.listener = listener;
        for (GitHubData request : requests)
            listener.onRequest(this, request);
    }

    public final List<GitHubData> getRequests() {
        return requests;
    }

    public final boolean hasRequest(GitHubData request) {
        return requests.contains(request);
    }

    public final int getLayoutRes() {
        return layoutRes;
    }

    public abstract T getViewHolder(View v);

    public abstract void bind(Context context, T viewHolder);

    @Override
    public void onInit(GitHubData data) {
    }

    @Override
    public void onFailure(GitHubData data) {
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View v) {
            super(v);
        }

    }

    public interface OnRequestListener {
        void onRequest(InfoData info, GitHubData request);
    }

}
