package TestActions.Cameras;
import Actions.ObjectActions.*;
import Renderer.Objects.SceneEntities.SceneObjects.*;
public class ManageSecondCamera extends CameraAction{
    private boolean cPressed = false;
    private Camera other;
    private byte outlineControl = 0;
    private float sceneMag = 0;
    private float[] sceneCentre = {0, 0, 0};
    public ManageSecondCamera(Camera newCamera){
      cPressed = false;
      other = newCamera;
      outlineControl = 0;
    }
    public void setSceneCentre(float mag, float[] centre){
        sceneMag = mag;
        sceneCentre[0] = centre[0];
        sceneCentre[1] = centre[1];
        sceneCentre[2] = centre[2];
    }
    public void init(){
      
    }
    public void perform(){
      if(keyPressed()){
        if(key() == 'c' && !cPressed){
            if((outlineControl & 16) == 0){
              outlineControl = (byte)((outlineControl & -13) | ((outlineControl+4 & 12)));
              hardSetPosition(0, 0, 0);
              hardSetRotation(0, 0, 0);
              cPressed = true;
          }
        }
      }
      else
        cPressed = false;
        
    if((outlineControl & 12) == 0){
      model.copy(other.returnModelMatrix());
      hardSetPosition(other.returnPosition());
      hardSetRotation(other.returnRotationDegrees());
      hardSetScale(other.returnScale());
      hardSetShear(other.returnShear());
    }
    else{
        //Secondary camera
        switch(outlineControl & 12){
          case 4:
            addToRotation(-frameRateNorm(), (byte)1);
            break;
          case 8:
            addToRotation(-0.5f*frameRateNorm(), (byte)0);
            break;
          case 12:
            addToRotation(-0.5f*frameRateNorm(), (byte)2);
            break;
        }
        float[] eye2Back = getBackward();
        hardSetPosition(sceneMag*eye2Back[0]+sceneCentre[0], sceneMag*eye2Back[1]+sceneCentre[1], sceneMag*eye2Back[2]+sceneCentre[2]);
      }
    }
  }
