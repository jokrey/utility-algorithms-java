package jokrey.utilities.command.line.helper;

import jokrey.utilities.encoder.type_transformer.NotTransformableException;
import jokrey.utilities.encoder.type_transformer.string.TypeToStringTransformer;

import static jokrey.utilities.encoder.tag_based.helper.ReflectionHelper.getWrap;

public class Argument {
    private static final TypeToStringTransformer trans = new TypeToStringTransformer();

    private final Class<?> type;
    public Argument(Class<?> type) {
        this.type = type;
    }
    public Argument(String arg) {
        this(String.class);
        raw = arg;
    }

    private String raw = null;

    public static Argument[] ensureInputExists(Argument[] templates, String[] args) {
        if(templates==null)
            return from(args);
        if(args.length == templates.length)
            return templates;
        Argument[] temp = new Argument[args.length];
        System.arraycopy(templates, 0, temp, 0, Math.min(temp.length, templates.length));
        for(int i=templates.length;i<args.length;i++) //only triggered if templates < args
            temp[i] = new Argument(args[i]);
        return temp;
    }

    protected boolean set(String s) {
        try {
            raw=s;
            Object detrans = trans.detransform(s, type);

            return type == null || getWrap(type).isInstance(detrans);
        } catch (NotTransformableException e) {
            return false; //will have still set raw, which is desired...
        }
    }
    protected void clear() {
        raw=null;
    }

    public <E>E get(Class<E> type) {
        return trans.detransform(raw, type);
    }

    @SuppressWarnings("unchecked")
    public <E>E get() { return (E) get(type); }
    public <E>E g() { return get(); }

    public String getRaw() {return raw;}
    public String raw() {return raw;}
    public String r() {return raw;}

    public boolean isSet() {return raw!=null;}
    public boolean is() {return raw!=null;}


    @Override public String toString() {
        return "<arg:"+type.getSimpleName()+">";
    }





    public static void clear(Argument[] temp) {
        if(temp!=null)
            for(Argument a : temp)
                a.clear();
    }
    public static Argument[] from(String[] args) {
        Argument[] from = new Argument[args.length];
        for(int i=0;i<args.length;i++)
            from[i] = new Argument(args[i]);
        return from;
    }
    public static Argument[] with(Class<?>... types) {
        Argument[] with = new Argument[types.length];
        for(int i=0;i<types.length;i++)
            with[i] = new Argument(types[i]);
        return with;
    }
    public static Argument[] noargs() {
        return new Argument[0];
    }
    public static Object[] getAll(Argument[] args) {
        Object[] objects = new Object[args.length];
        for(int i=0;i<args.length;i++)
            objects[i] = args[i].get();
        return objects;
    }
}
