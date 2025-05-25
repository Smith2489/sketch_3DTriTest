public class ShortWrapper {
    public short val = 0;
    public ShortWrapper(){
        val = 0;
    }
    public ShortWrapper(short newVal){
        val = newVal;
    }
    public String toString(){
        return Short.toString(val);
    }
}
