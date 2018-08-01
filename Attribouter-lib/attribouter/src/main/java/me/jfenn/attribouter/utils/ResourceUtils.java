package me.jfenn.attribouter.utils;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.TypedValue;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ResourceUtils {

    public static void setImage(Context context, String identifier, ImageView imageView) {
        Integer resInt = getResourceInt(context, identifier);
        if (resInt != null) {
            imageView.setImageResource(resInt);
        } else {
            Glide.with(context).load(identifier).into(imageView);
        }
    }

    public static void setImage(Context context, String identifier, @DrawableRes int defaultRes, ImageView imageView) {
        Integer resInt = getResourceInt(context, identifier);
        if (resInt != null) {
            imageView.setImageResource(resInt);
        } else if (identifier != null) {
            Glide.with(context).load(identifier).into(imageView);
        } else {
            imageView.setImageResource(defaultRes);
        }
    }

    @Nullable
    public static String getString(Context context, @Nullable String identifier) {
        if (identifier != null && identifier.startsWith("^"))
            identifier = identifier.substring(1);

        Integer resource = getResourceInt(context, identifier);
        if (identifier != null && identifier.startsWith("@"))
            identifier = null;

        return resource != null ? context.getString(resource) : identifier;
    }

    @Nullable
    public static Integer getResourceInt(Context context, @Nullable String identifier) {
        if (identifier != null && identifier.startsWith("^"))
            identifier = identifier.substring(1);

        if (identifier != null && identifier.startsWith("@")) {
            identifier = identifier.substring(1);
            if (identifier.contains("/")) {
                String[] identifiers = identifier.split("/");
                if (identifiers[0].length() > 0 && identifiers[1].length() > 0) {
                    int res = context.getResources().getIdentifier(identifiers[1], identifiers[0], context.getPackageName());
                    return res == 0 ? null : res;
                }
            } else {
                try {
                    return Integer.parseInt(identifier);
                } catch (NumberFormatException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    @StyleRes
    public static int getThemeResourceAttribute(Context context, @AttrRes int attr, @StyleRes int defaultTheme) {
        TypedValue value = new TypedValue();
        if (context.getTheme().resolveAttribute(attr, value, false))
            return value.resourceId;
        else return defaultTheme;
    }

}
