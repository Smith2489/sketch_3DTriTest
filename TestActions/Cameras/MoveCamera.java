package TestActions.Cameras;
import Actions.ObjectActions.*;
public class MoveCamera extends CameraAction{
  public float[] lookAtPoint = {0, 3, 9};
    public void init(){
      
    }
    public void perform(){
      float[] eyeForward = getForward();
      float[] eyeRight = getRight();
      if(keyPressed()){
        switch(key()){
          case 'i':
            addToRotation(0.5f*speed, (byte)0);
            break;
          case 'k':
            addToRotation(-0.5f*speed, (byte)0);
            break;
          case 'j':
            addToRotation(-0.5f*speed, (byte)1);
            break;
          case 'l':
            addToRotation(0.5f*speed, (byte)1);
            break; 
          case '1':
            addToPosition(0.05f*speed, eyeForward);
          break;
          case '2':
            addToPosition(-0.05f*speed, eyeForward);
            break;
          case '9':
            addToPosition(-0.05f*speed, eyeRight);
            break;
          case '0':
            addToPosition(0.05f*speed, eyeRight);
            break;
        }
      }
      //lookAt(lookAtPoint);
      if(mousePressed()){
        if(leftButton()){
          if(mouseX() >= ((width() >>> 1) + 50))
            addToPosition(0.05f*speed, eyeRight);
          else if(mouseX() <= ((width() >>> 1) - 50))
            addToPosition(-0.05f*speed, eyeRight);
          if(mouseY() >= ((height() >>> 1)+50))
            addToPosition(-0.05f*speed, eyeForward);
          else if(mouseY() <= ((height() >>> 1)-50))
            addToPosition(0.05f*speed, eyeForward);
        }
        if(rightButton()){
          if(mouseX() >= ((width() >>> 1) + 50))
            addToRotation(0.5f*speed, (byte)1);
          else if(mouseX() <= ((width() >>> 1) - 50))
            addToRotation(-0.5f*speed, (byte)1);
          if(mouseY() >= ((height() >>> 1)+50))
            addToRotation(-0.5f*speed, (byte)0);
          else if(mouseY() <= ((height() >>> 1)-50))
            addToRotation(0.5f*speed, (byte)0);
        }
      }
      float[] tempRot = getRotDegrees();
      if(tempRot[0] >= 360)
        rotateMinus360((byte)0);
      else if(tempRot[0] < 0)
        rotatePlus360((byte)0);
      if(tempRot[1] >= 360){
        rotateMinus360((byte)1);
      }
      else if(tempRot[1] < 0)
        rotatePlus360((byte)1);
    }
  }
