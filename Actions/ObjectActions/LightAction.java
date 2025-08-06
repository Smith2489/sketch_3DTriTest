package Actions.ObjectActions;
public abstract class LightAction extends Action {
    protected float[] intensities = {1, 1, 1};
    private float[][] colour = {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}};
    
    public void setIntensities(float[] newIntensities){
        intensities = newIntensities;
    }
    public void setColour(float[][] newColour){
        colour = newColour;
    }
   
    protected void setColour(int newColour, int index){
        newColour&=0xFFFFFF;
        if(newColour <= 0xFF)
            newColour = 0xFF000000|(newColour << 16)|(newColour << 8)|newColour;
        else
            newColour|=0xFF000000;
        colour[index][0] = ((newColour >>> 16) & 0xFF)*0.003921569f;
        colour[index][1] = ((newColour >>> 8) & 0xFF)*0.003921569f;
        colour[index][2] = (newColour & 0xFF)*0.003921569f;
    }
    //Returns a deep copy of a light's colour
    public float[][] returnLightColour(){
        float[][] returnColour = {{colour[0][0], colour[0][1], colour[0][2]},
                                  {colour[1][0], colour[1][1], colour[1][2]},
                                  {colour[2][0], colour[2][1], colour[2][2]}};
        return returnColour;
    }
}