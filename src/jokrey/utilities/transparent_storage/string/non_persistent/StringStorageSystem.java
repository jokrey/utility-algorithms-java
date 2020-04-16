package jokrey.utilities.transparent_storage.string.non_persistent;

import jokrey.utilities.transparent_storage.StorageSystemException;
import jokrey.utilities.transparent_storage.TransparentStorage;

/**
 * Implementation of TransparentStorage wrapping every single call to an internal StringBuilder.
 *    (this actually happens with almost one to one delegation, because a string builder is already an almost perfect StorageSystem for our uses)
 *
 * @author jokrey
 */
public class StringStorageSystem implements StringStorage {
    private StringBuilder content;
    public StringStorageSystem() {
        content = new StringBuilder();
    }
    public StringStorageSystem(int initialCapacity) {
        content = new StringBuilder(initialCapacity);
    }
    public StringStorageSystem(String initialContent) {
        content = new StringBuilder(initialContent);
    }

    @Override public int len(String part) { return part.length(); }
    @Override public void close() {
        content=null;
    }
    @Override public void clear() {
        content = new StringBuilder();
    }
    @Override public void setContent(String content) {
        this.content = new StringBuilder(content);
    }
    @Override public String getContent() {
        return content.toString();
    }
    @Override public StringStorageSystem delete(long start, long end) {
        if(start>Integer.MAX_VALUE || end>Integer.MAX_VALUE)
            throw new StorageSystemException("StringStorageSystem only supports integer sized content");
        content.delete((int) start, (int) end);
        return this;
    }
    @Override public StringStorageSystem append(String val) {
        content.append(val);
        return this;
    }
    @Override public TransparentStorage<String> insert(long start, String val) {
        content.insert((int) start, val);
        return this;
    }
    @Override public String sub(long start, long end) {
        return content.substring((int) start, (int) end);
    }
    @Override public StringStorageSystem set(long start, String part, int off, int len) {
        content.delete((int)start, (int) (start+part.length()<contentSize()? start+part.length():contentSize()));
        content.insert((int) start, part, off, len);
        return this;
    }
    @Override public StringStorageSystem set(long start, String part, int off) {
        return set(start, part, off, part.length()-off);
    }
    @Override public StringStorageSystem set(long start, String part) {
        return set(start, part, 0);
    }
    @Override public long contentSize() {
        return content.length();
    }
    @Override public boolean isEmpty() {
        return contentSize() == 0;
    }
    @Override public int hashCode() {
        return getContent().hashCode();
    }
    @Override public boolean equals(Object obj) {
        return obj instanceof StringStorageSystem && efficient_equals(content, ((StringStorageSystem)obj).content);
    }



    /**
     * Rant:
     * WHAT THE HELL WHY THE HELL DO I HAVE TO IMPLEMENT THIS??????????
     * HOW DID NOBODY WRITING THE STRING BUILDER CLASS THINK OF IMPLEMENTING A BLOODY equals METHOD?????
     * YOU LITERALLY LEARN TO DO THAT IN JAVA 101...
     * JESUS CHRIST SOME OF THESE PEOPLE ARE MORONS
     * I MEAN THE STRING BUILDER CLASS WAS INVENTED FOR EFFICIENCY. BECAUSE STRING IMMUTABILITY RENDERS THEM SAFE;BUT SLOW,
     *     SO DO ANY OF THEM THINK THAT MAYBE CREATING TWO STRING FOR SIMPLE COMPARISON IS EFFICIENT???
     *     OR DID THEY JUST THINK: NO000OO, NOBODY WILL EVER NEED TO COMPARE STRING BUILDERS....
     *     TO BE FAIR IT PROBABLY WAS THIS; BUT WHY???
     *     DID THEY THINK WE'D JUST USE THEM TO BUILD STRINGS AND THEN USE STRINGS?
     *     TO BE FAIR IT PROBABLY WAS THIS; BUT DIDN'T THEY THINK WE'D NEED TO CONTINUALlY BUILD STRINGS AT SOME POINT?
     */
    public static boolean efficient_equals(StringBuilder sb1, StringBuilder sb2) {
        int l1 = sb1.length();
        int l2 = sb2.length();
        for(int i=0;l1==l2 && i < l1;i++)
            if(sb1.charAt(i) != sb2.charAt(i))
                return false;
        return true;
    }



    @Override public long indexOf(String s, long from) {
        return content.indexOf(s, (int) from);
    }
    @Override public long lastIndexOf(String s, long backwardsFrom) {
        return content.lastIndexOf(s, (int) backwardsFrom);
    }
}
