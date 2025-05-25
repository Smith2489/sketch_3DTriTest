public class DoubleWrapper {
    public double val = 0;
    public DoubleWrapper(){
        val = 0;
    }
    public DoubleWrapper(double newVal){
        val = newVal;
    }
    public String toString(){
        return Double.toString(val);
    }
}
