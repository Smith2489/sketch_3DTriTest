public class LongWrapper {
    public long val = 0;
    public LongWrapper(){
        val = 0;
    }
    public LongWrapper(long newVal){
        val = newVal;
    }
    public String toString(){
        return Long.toString(val);
    }
}
