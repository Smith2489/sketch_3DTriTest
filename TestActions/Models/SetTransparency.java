package TestActions.Models;
import Actions.ObjectActions.*;
import Renderer.Objects.SceneEntities.Camera;
public class SetTransparency extends ModelAction{
    private Camera object;
    //ORIGINALS: 360, 1800
    private static final float MIN_DIST = 360;
    private static final float MAX_DIST = 1800;
    public SetTransparency(Camera newCam){
      object = newCam; 
    }
    public void init(){

    }
    public void perform(){
      float tint = dist(object.returnPosition());
      if(tint > MIN_DIST)
        setModelTint(1 - ((tint)/MAX_DIST)); 
      else
        setModelTint(1);
    }
  }
