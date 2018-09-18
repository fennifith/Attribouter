package me.jfenn.attribouter.utils;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.annotation.StyleableRes;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import me.jfenn.attribouter.R;

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
    public static int getThemeResourceAttribute(Context context, @StyleableRes int styleable, @StyleRes int defaultTheme) {
        TypedArray array = context.obtainStyledAttributes(null, R.styleable.AttribouterTheme, 0, defaultTheme);
        int id = array.getResourceId(styleable, defaultTheme);
        array.recycle();
        return id;
    }

}
