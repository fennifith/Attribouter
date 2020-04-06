package me.jfenn.attribouter.provider.reflect;

import androidx.annotation.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ClassInstantiator<T> {

    private Class<T> tClass;

    public ClassInstantiator(Class<T> tClass) {
        this.tClass = tClass;
    }

    @Nullable
    public T instantiate(Object... parameters) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?>[] classes = new Class[parameters.length];
        for (int i = 0; i < parameters.length; i++)
            classes[i] = parameters[i].getClass();

        Constructor<T> constructor = tClass.getConstructor(classes);
        return constructor.newInstance(parameters);
    }

    @Nullable
    public static ClassInstantiator fromString(String className) throws ClassNotFoundException {
        return new ClassInstantiator(Class.forName(className));
    }

}
