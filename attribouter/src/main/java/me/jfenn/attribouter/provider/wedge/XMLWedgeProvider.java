package me.jfenn.attribouter.provider.wedge;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.XmlRes;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import me.jfenn.attribouter.provider.reflect.ClassInstantiator;
import me.jfenn.attribouter.wedges.Wedge;

public class XMLWedgeProvider implements WedgeProvider {

    @XmlRes
    private int fileRes;

    public XMLWedgeProvider(@XmlRes int fileRes) {
        this.fileRes = fileRes;
    }

    @Nullable
    private Wedge getWedge(String className, XmlResourceParser parser) {
        ClassInstantiator instantiator;

        try {
            instantiator = ClassInstantiator.fromString(className);
        } catch (ClassNotFoundException e) {
            Log.e("Attribouter", "Class name \"" + className + "\" not found - you should probably check your configuration file for typos.");
            e.printStackTrace();
            return null;
        }

        try {
            return (Wedge) instantiator.instantiate(parser);
        } catch (NoSuchMethodException e) {
            Log.e("Attribouter", "Class \"" + className + "\" definitely exists, but doesn't have the correct constructor. Check that you have defined one with a single argument - \'" + XmlResourceParser.class.getName() + "\'.");
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            Log.e("Attribouter", "Class \"" + className + "\" has been instantiated correctly, but it must extend \'" + Wedge.class.getName() + "\' to be worthy of the great RecyclerView adapter.");
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Wedge> getWedges(Context context) {
        XmlResourceParser parser = context.getResources().getXml(fileRes);
        List<Wedge> wedges = new ArrayList<>();

        try {
            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() == XmlPullParser.START_TAG) {
                    Wedge wedge = getWedge(parser.getName(), parser);
                    if (wedge != null)
                        wedges.add(wedge);
                }

                parser.next();
            }
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        } finally {
            parser.close();
        }

        return wedges;
    }

}
