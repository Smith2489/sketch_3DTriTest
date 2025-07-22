package TestActions.Lights;
import Actions.ObjectActions.*;
import Renderer.Objects.SceneEntities.*;
public class CopyLight extends LightAction{
    private Light tempLight;
    public CopyLight(Light newLight){
      tempLight = newLight; 
    }
    public void init(){
      
    }
    public void perform(){
      hardSetPosition(tempLight.returnPosition());
      hardSetRotation(tempLight.returnRotation());
    }
  }
