package Wrapper;
public class CharWrapper{
    public char val = 0;
    public CharWrapper(){
        val = 0;
    }
    public CharWrapper(char newVal){
        val = newVal;
    }
    public String toString(){
        return Character.toString(val);
    }
}