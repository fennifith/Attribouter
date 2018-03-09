package me.jfenn.attribouter.utils;

import android.content.Context;
import android.support.annotation.Nullable;

public class ResourceUtils {

    @Nullable
    public static String getString(Context context, @Nullable String identifier) {
        if (identifier != null && identifier.startsWith("@")) {
            identifier = identifier.substring(1);
            if (identifier.contains("/")) {
                int stringRes = context.getResources().getIdentifier(context.getApplicationInfo().packageName + ":" + identifier, "string", null);
                return stringRes == 0 ? null : context.getString(stringRes);
            } else return null;
        } else return identifier;
    }

    @Nullable
    public static Integer getResourceInt(Context context, @Nullable String identifier) {
        if (identifier != null && identifier.startsWith("@") && identifier.contains("/")) {
            identifier = identifier.substring(1);
            int res = context.getResources().getIdentifier(context.getApplicationInfo().packageName + ":" + identifier, null, null);
            return res == 0 ? null : res;
        } else return null;
    }

}
