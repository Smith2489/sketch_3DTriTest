package Actions.ObjectActions;
import Renderer.Objects.SceneEntities.SceneObjects.Light;
public abstract class LightAction extends Action {
    protected float[] intensities = {1, 1};
    private float[][] colour = {{1, 1, 1}, {1, 1, 1}};

    public void setIntensities(float[] newIntensities){
        intensities = newIntensities;
    }
    public void setColour(float[][] newColour){
        colour = newColour;
    }
   
    protected void setColour(int newColour, int index){
        if(index > 0){
            newColour&=0xFFFFFF;
            if(newColour <= 0xFF)
                newColour = 0xFF000000|(newColour << 16)|(newColour << 8)|newColour;
            else
                newColour|=0xFF000000;
            colour[index-1][0] = ((newColour >>> 16) & 0xFF)*0.003921569f;
            colour[index-1][1] = ((newColour >>> 8) & 0xFF)*0.003921569f;
            colour[index-1][2] = (newColour & 0xFF)*0.003921569f;
        }
        else
            Light.setAmbientColour(newColour);
    }
    //Returns a deep copy of a light's colour
    public float[][] returnLightColour(){
        float[][] returnColour = {Light.returnAmbientColour(),
                                  {colour[1][0], colour[1][1], colour[1][2]},
                                  {colour[2][0], colour[2][1], colour[2][2]}};
        return returnColour;
    }
}