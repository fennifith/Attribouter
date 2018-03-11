package me.jfenn.attribouter.utils;

import android.content.Context;
import android.support.annotation.Nullable;

public class ResourceUtils {

    @Nullable
    public static String getString(Context context, @Nullable String identifier) {
        if (identifier != null && identifier.startsWith("^"))
            identifier = identifier.substring(1);

        if (identifier != null && identifier.startsWith("@")) {
            identifier = identifier.substring(1);
            if (identifier.contains("/")) {
                String[] identifiers = identifier.split("/");
                if (identifiers[0].length() > 0 && identifiers[1].length() > 0) {
                    int stringRes = context.getResources().getIdentifier(identifiers[1], identifiers[0], context.getPackageName());
                    return stringRes == 0 ? null : context.getString(stringRes);
                }
            } else {
                try {
                    return context.getString(Integer.parseInt(identifier));
                } catch (NumberFormatException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
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
