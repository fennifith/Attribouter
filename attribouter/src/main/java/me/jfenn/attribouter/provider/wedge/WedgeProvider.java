package me.jfenn.attribouter.provider.wedge;

import android.content.Context;

import java.util.List;

import me.jfenn.attribouter.wedges.Wedge;

public interface WedgeProvider {

    List<Wedge> getWedges(Context context);

}
