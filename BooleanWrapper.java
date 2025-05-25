public class BooleanWrapper {
    public boolean val = false;
    public BooleanWrapper(){
        val = false;
    }
    public BooleanWrapper(boolean newVal){
        val = true;
    }
    public String toString(){
        return Boolean.toString(val);
    }
}
