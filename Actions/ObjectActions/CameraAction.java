package Actions.ObjectActions;
import Wrapper.*;
public abstract class CameraAction extends ObjectAction{
    private IntWrapper colour = new IntWrapper(0xFFFFFF);
    private float[] invColour = {1, 1, 1};
    private FloatWrapper drawDistance = new FloatWrapper();
    private BooleanWrapper alwaysMultiply = new BooleanWrapper(false);
    
    public void setColour(IntWrapper newColour, float[] newInvColour){
        colour = newColour;
        invColour = newInvColour;
    }
    public void setDrawDistance(FloatWrapper newDist){
        drawDistance = newDist;
    }
    public void setAlwaysMultiply(BooleanWrapper alwaysMult){
        alwaysMultiply = alwaysMult;
    }

    //A pair of functions which set the camera colour
    protected void colour(int rgb){
        rgb&=0xFFFFFF;
        if(rgb <= 0xFF){
            if(rgb > 0){
                invColour[0] = 255f/rgb;
                invColour[1] = 255f/rgb;
                invColour[2] = 255f/rgb;
            }
            else{
                invColour[0] = 0;
                invColour[1] = 0;
                invColour[2] = 0;
            }
            colour.val = 0xFF000000 | (rgb << 16) | (rgb << 8) | rgb;
        }
        else{
            if(((rgb >>> 16) & 0xFF) > 0)
                invColour[0] = 255f/((rgb >>> 16) & 0xFF);
            else  
                invColour[0] = 0;
            if(((rgb >>> 8) & 0xFF) > 0)
                invColour[1] = 255f/((rgb >>> 8) & 0xFF);
            else  
                invColour[1] = 0;
            if((rgb & 0xFF) > 0)
                invColour[2] = 255f/(rgb & 0xFF);
            else  
                invColour[2] = 0;
            colour.val = 0xFF000000 | rgb;
        }
    }

    protected void colour(int red, int green, int blue){
        red = (red & 0xFF);
        green = (green & 0xFF);
        blue = blue & 0xFF;
        if(red > 0)
            invColour[0] = 255f/red;
        else
            invColour[0] = 0;
        if(green > 0)
            invColour[1] = 255f/green;
        else
            invColour[1] = 0;
        if(blue > 0)
            invColour[2] = 255f/blue;
        else
            invColour[2] = 0;
        colour.val = 0xFF000000 | (red << 16) | (green << 8) | blue;
    }
    protected float[] returnInvColour(int otherColour){
        float[] outputColour = {1, 1, 1};
        if(colour.val == otherColour && !alwaysMultiply.val){
          outputColour[0] = invColour[0];
          outputColour[1] = invColour[1];
          outputColour[2] = invColour[2];
        }
        return outputColour;
    }
    protected int colour(){
        return colour.val;
    } 
    protected void alwaysMultiply(){
        alwaysMultiply.val = true;
    }
    protected void sometimesMultiply(){
        alwaysMultiply.val = false;
    }
    protected void drawDistance(float newDist){
        if(Float.isNaN(newDist) || Float.isInfinite(newDist))
            drawDistance.val = 2000;
        else
            drawDistance.val = Math.abs(newDist);
    }
    protected float drawDistance(){
        return drawDistance.val;
    }
}
