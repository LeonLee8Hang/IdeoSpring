package com.ideonet.beans.util;

import com.ideonet.exception.IdeoException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


public class BeanUtils {

    public static <T> T instantiateClass(Constructor<T> ctor, Object... args){
        try {
            ctor.setAccessible(true);
            return ctor.newInstance(args);
        }
        catch (InstantiationException ex) {
            throw new IdeoException("'"+ctor.getName()+"',Is it an abstract class?", ex);
        }
        catch (IllegalAccessException ex) {
            throw new IdeoException("'"+ctor.getName()+",Is the constructor accessible?", ex);
        }
        catch (IllegalArgumentException ex) {
            throw new IdeoException("'"+ctor.getName()+",Illegal arguments for constructor", ex);
        }
        catch (InvocationTargetException ex) {
            throw new IdeoException("'"+ctor.getName()+",Constructor threw exception", ex.getTargetException());
        }
    }
}
