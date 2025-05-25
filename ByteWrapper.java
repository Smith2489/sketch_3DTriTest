public class ByteWrapper {
    public byte val = 0;
    public ByteWrapper(){
        val = 0;
    }
    public ByteWrapper(byte newVal){
        val = newVal;
    }
    public String toString(){
        return Byte.toString(val);
    }
}
