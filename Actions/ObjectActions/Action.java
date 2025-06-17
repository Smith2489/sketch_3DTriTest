package Actions.ObjectActions;
import Maths.LinearAlgebra.*;
import Renderer.Objects.Physics.*;
import Renderer.ScreenDraw.MVP;
public abstract class Action{
    protected static float speed = 0;
    private static int counter = 0;
    protected float[] pos = {0, 0, 0};
    protected float[] rot = {0, 0, 0};
    protected Physics physics = new Physics(pos, rot);
    protected boolean reverseVertical = false;
    protected boolean reverseHorizontal = false;
    protected Matrix model = new Matrix();
    private int timerPos = 0;
    private int timerRot = 0;
    private boolean posShakeStarted = false;
    private boolean rotShakeStarted = false;
    private float[] oldPos = {0, 0, 0};
    private float[] oldRot = {0, 0, 0};
    public abstract void perform();
    public static void setRatePerFrame(float newSpeed){
        speed = newSpeed;
    }
    public boolean positionShakeStarted(){
        return posShakeStarted;
    }
    public boolean rotationShakeStarted(){
        return rotShakeStarted;
    }
    protected void shakePosition(float radius, int time){
        if(!posShakeStarted){
            oldPos[0] = pos[0];
            oldPos[1] = pos[1];
            oldPos[2] = pos[2];
            timerPos = Math.max(0, time);
            posShakeStarted = true;
        }
        if(timerPos > 0){
            pos[0] = oldPos[0]+(float)(Math.random()*(radius*2) - radius);
            pos[1] = oldPos[1]+(float)(Math.random()*(radius*2) - radius);
            pos[2] = oldPos[2]+(float)(Math.random()*(radius*2) - radius);
            timerPos--;
        }
        else{
            pos[0] = oldPos[0];
            pos[1] = oldPos[1];
            pos[2] = oldPos[2];
            posShakeStarted = false;
        }
    }
    protected void shakeRotation(float radius, int time){
        if(!rotShakeStarted){
            oldRot[0] = rot[0];
            oldRot[1] = rot[1];
            oldRot[2] = rot[2];
            timerRot = Math.max(0, time);
            rotShakeStarted = true;
        }
        if(timerRot > 0){
            rot[0] = oldRot[0]+(float)(Math.random()*(radius*2) - radius);
            rot[1] = oldRot[1]+(float)(Math.random()*(radius*2) - radius);
            rot[2] = oldRot[2]+(float)(Math.random()*(radius*2) - radius);
            timerRot--;
        }
        else{
            rot[0] = oldRot[0];
            rot[1] = oldRot[1];
            rot[2] = oldRot[2];
            rotShakeStarted = false;
        }
    }

    protected float[] getForward(){
        float[] forward = {model.returnData(0, 2), model.returnData(1, 2), model.returnData(2, 2)};
        forward =  VectorOperations.vectorNormalization3D(forward);
        forward[0]-=0.0001f;
        forward[1]-=0.0001f;
        forward[2]-=0.0001f;
        return forward;
    }
    protected float[] getBackward(){
        float[] backward = {-model.returnData(0, 2), -model.returnData(1, 2), -model.returnData(2, 2)};
        backward = VectorOperations.vectorNormalization3D(backward);
        backward[0]-=0.0001f;
        backward[1]-=0.0001f;
        backward[2]-=0.0001f;
        return backward;
    }
    protected float[] getRight(){
        float[] right = {model.returnData(0, 0), model.returnData(1, 0), model.returnData(2, 0)};
        right =  VectorOperations.vectorNormalization3D(right);
        right[0]-=0.0001f;
        right[1]-=0.0001f;
        right[2]-=0.0001f;
        return right;
    }
    protected float[] getLeft(){
        float[] left = {-model.returnData(0, 0), -model.returnData(1, 0), -model.returnData(2, 0)};
        left =  VectorOperations.vectorNormalization3D(left);
        left[0]-=0.0001f;
        left[1]-=0.0001f;
        left[2]-=0.0001f;
        return left;
    }
    protected float[] getUp(){
        float[] up = {-model.returnData(0, 1), -model.returnData(1, 1), -model.returnData(2, 1)};
        up =  VectorOperations.vectorNormalization3D(up);
        up[0]-=0.0001f;
        up[1]-=0.0001f;
        up[2]-=0.0001f;
        return up;
    }
    protected float[] getDown(){
        float[] down = {model.returnData(0, 1), model.returnData(1, 1), model.returnData(2, 1)};
        down =  VectorOperations.vectorNormalization3D(down);
        down[0]-=0.0001f;
        down[1]-=0.0001f;
        down[2]-=0.0001f;
        return down;
    }

    public void init(){
        System.out.println(counter+": NO INITIALIZATION METHOD SET");
        counter++;
    }

    public void setPos(float[] newPos){
        pos = newPos;
    }
    public void setRot(float[] newRot){
        rot = newRot;
    }
    public void setPhysics(Physics newPhysics){
        physics = newPhysics;
    }
    public void setMatrix(Matrix newModel){
        model = newModel;
    }
    protected float dist(float[] otherPos){
        if(pos.length != 3 || otherPos.length != 3){
            System.out.println("ERROR: WRONG NUMBER OF DIMENSIONS");
            System.exit(1);
        }
        float diffX2 = (otherPos[0]-pos[0])*(otherPos[0]-pos[0]);
        float diffY2 = (otherPos[1]-pos[1])*(otherPos[1]-pos[1]);
        float diffZ2 = (otherPos[2]-pos[2])*(otherPos[2]-pos[2]);
        return (float)Math.sqrt(diffX2+diffY2+diffZ2);
    }
    protected float dist(float x, float y, float z){
        if(pos.length != 3){
            System.out.println("ERROR: WRONG NUMBER OF DIMENSIONS");
            System.exit(1);
        }
        float diffX2 = (x-pos[0])*(x-pos[0]);
        float diffY2 = (y-pos[1])*(y-pos[1]);
        float diffZ2 = (z-pos[2])*(z-pos[2]);
        return (float)Math.sqrt(diffX2+diffY2+diffZ2);
    }

    protected void matrixTransform(){
        if(rotShakeStarted)
            model.copy(MatrixOperations.matrixMultiply(MVP.returnTranslation(oldPos), MVP.returnRotation(oldRot)));
    }

    protected void addToPosition(float rate, float[] directional){
        if(!posShakeStarted){
            pos[0]+=(directional[0]*rate);
            pos[1]+=(directional[1]*rate);
            pos[2]+=(directional[2]*rate);
        }
        else{
            oldPos[0]+=(directional[0]*rate);
            oldPos[1]+=(directional[1]*rate);
            oldPos[2]+=(directional[2]*rate);
        }
    }
    protected void lookAt(float[] point, float maxDist, byte axis){
        if(point.length >= 3){
            if(Math.abs(pos[0]-point[0]) > 0.0001 && Math.abs(point[1]-pos[1]) > 0.0001 && Math.abs(pos[2]-point[2]) > 0.0001){
                float[] camToPoint = {point[0]-pos[0], point[1]-pos[1], point[2]-pos[2]};
                float dist = VectorOperations.vectorMagnitude(camToPoint);
                if(dist >= 1 && dist <= maxDist){
                    float[][] rotVec = {{0, camToPoint[1], camToPoint[2]}, 
                                        {camToPoint[0], 0, camToPoint[2]}, 
                                        {camToPoint[0], camToPoint[1], 0}};
                    byte down = (byte)((Math.abs(camToPoint[2]) > Math.abs(camToPoint[0])) ? 2 : 0);
                    byte oppDown = (byte)(down^2);
                    float[] useVec = {oppDown >>> 1, 0, down >>> 1};
                    if(rotVec[oppDown][down] <= 0)
                        useVec[down] = -1;
    
                    if(camToPoint[0] <= 0.0001){
                        if(camToPoint[2] > 0.0001){
                            rotVec[1][0] = camToPoint[2];
                            rotVec[1][2] = -camToPoint[0];
                        }
                        else{
                            rotVec[1][0] = -camToPoint[0];
                            rotVec[1][2] = -camToPoint[2];
                        }
                    }
                    else
                        if(camToPoint[2] <= 0.0001){
                            rotVec[1][0] = -camToPoint[2];
                            rotVec[1][2] = camToPoint[0];
                        }
    
                    rotVec[oppDown] = VectorOperations.vectorNormalization3D(rotVec[oppDown]);
                    rotVec[1] = VectorOperations.vectorNormalization3D(rotVec[1]);
                    float[] camToPointAngles = {VectorOperations.returnAngleUnit(rotVec[oppDown], useVec, true),
                                                VectorOperations.returnAngleUnit(rotVec[1], VectorOperations.ELEM_K, true)};
                    
                    if(reverseVertical)
                        camToPointAngles[0]*=-1;
                    if(reverseHorizontal)
                        camToPointAngles[1]*=-1;
                    float add = 0;
                    if(axis != 0){
                        if(camToPoint[2] > 0.0001){
                            if(camToPoint[0] <= 0.0001)
                                add = -90;
                        }
                        else{
                            if(camToPoint[0] > 0.0001)
                                add = 90;
                            else
                                add = 180;
                        }
                    }
                    else{
                        if(camToPoint[1] <= 0.0001)
                            add = -camToPointAngles[0]*2;
                    }
                    rot[axis] = camToPointAngles[axis]+add;
                    if(rot[axis] < 0)
                        rot[axis]+=360;
                    else if(rot[axis] > 360)
                        rot[axis]-=360;
                }
            }
        }
        else{
            System.out.println("ERROR: TOO FEW DIMENSIONS FOR LOOK-AT POINT");
            System.exit(-1);
        }
    }
    protected void lookAt(float[] point, byte axis){
        if(point.length >= 3){
            if(Math.abs(pos[0]-point[0]) > 0.0001 && Math.abs(point[1]-pos[1]) > 0.0001 && Math.abs(pos[2]-point[2]) > 0.0001){
                float[] camToPoint = {point[0]-pos[0], point[1]-pos[1], point[2]-pos[2]};
                float dist = VectorOperations.vectorMagnitude(camToPoint);
                if(dist >= 1){
                    float[][] rotVec = {{0, camToPoint[1], camToPoint[2]}, 
                                        {camToPoint[0], 0, camToPoint[2]}, 
                                        {camToPoint[0], camToPoint[1], 0}};
                    byte down = (byte)((Math.abs(camToPoint[2]) > Math.abs(camToPoint[0])) ? 2 : 0);
                    byte oppDown = (byte)(down^2);
                    float[] useVec = {oppDown >>> 1, 0, down >>> 1};
                    if(rotVec[oppDown][down] <= 0)
                        useVec[down] = -1;
    
                    if(camToPoint[0] <= 0.0001){
                        if(camToPoint[2] > 0.0001){
                            rotVec[1][0] = camToPoint[2];
                            rotVec[1][2] = -camToPoint[0];
                        }
                        else{
                            rotVec[1][0] = -camToPoint[0];
                            rotVec[1][2] = -camToPoint[2];
                        }
                    }
                    else
                        if(camToPoint[2] <= 0.0001){
                            rotVec[1][0] = -camToPoint[2];
                            rotVec[1][2] = camToPoint[0];
                        }
    
                    rotVec[oppDown] = VectorOperations.vectorNormalization3D(rotVec[oppDown]);
                    rotVec[1] = VectorOperations.vectorNormalization3D(rotVec[1]);
                    float[] camToPointAngles = {VectorOperations.returnAngleUnit(rotVec[oppDown], useVec, true),
                                                VectorOperations.returnAngleUnit(rotVec[1], VectorOperations.ELEM_K, true)};
                    if(reverseVertical)
                        camToPointAngles[0]*=-1;
                    if(reverseHorizontal)
                        camToPointAngles[1]*=-1;
                    float add = 0;
                    if(axis != 0){
                        if(camToPoint[2] > 0.0001){
                            if(camToPoint[0] <= 0.0001)
                                add = -90;
                        }
                        else{
                        if(camToPoint[0] > 0.0001)
                            add = 90;
                        else
                            add = 180;
                    }
                }
                else{
                    if(camToPoint[1] <= 0.0001)
                        add = -camToPointAngles[0]*2;
                }
                rot[axis] = camToPointAngles[axis]+add;
                if(rot[axis] < 0)
                    rot[axis]+=360;
                else if(rot[axis] > 360)
                    rot[axis]-=360;
                }
            }
        }
        else{
            System.out.println("ERROR: TOO FEW DIMENSIONS FOR LOOK-AT POINT");
            System.exit(-1);
        }
    }
    protected void lookAt(float[] point, float maxDist){
        if(point.length >= 3){
            if(Math.abs(point[0]-pos[0]) > 0.0001 && Math.abs(point[1]-pos[1]) > 0.0001 && Math.abs(pos[2]-point[2]) > 0.0001){
                float[] camToPoint = {point[0]-pos[0], point[1]-pos[1], point[2]-pos[2]};
                float dist = VectorOperations.vectorMagnitude(camToPoint);
                if(dist >= 1 && dist <= maxDist){
                float[][] rotVec = {{0, camToPoint[1], camToPoint[2]}, 
                                    {camToPoint[0], 0, camToPoint[2]}, 
                                    {camToPoint[0], camToPoint[1], 0}};
                byte down = (byte)((Math.abs(camToPoint[2]) > Math.abs(camToPoint[0])) ? 2 : 0);
                byte oppDown = (byte)(down^2);
                float[] useVec = {oppDown >>> 1, 0, down >>> 1};
    
                if(rotVec[oppDown][down] <= 0)
                    useVec[down] = -1;
    
    
                if(camToPoint[0] <= 0.0001){
                    if(camToPoint[2] > 0.0001){
                        rotVec[1][0] = camToPoint[2];
                        rotVec[1][2] = -camToPoint[0];
                    }
                    else{
                        rotVec[1][0] = -camToPoint[0];
                        rotVec[1][2] = -camToPoint[2];
                    }
                }
                else
                    if(camToPoint[2] <= 0.0001){
                        rotVec[1][0] = -camToPoint[2];
                        rotVec[1][2] = camToPoint[0];
                    }
                rotVec[oppDown] = VectorOperations.vectorNormalization3D(rotVec[oppDown]);
                rotVec[1] = VectorOperations.vectorNormalization3D(rotVec[1]);
                float[] camToPointAngles = {VectorOperations.returnAngleUnit(rotVec[oppDown], useVec, true),
                                            VectorOperations.returnAngleUnit(rotVec[1], VectorOperations.ELEM_K, true)};
    
                if(reverseVertical)
                    camToPointAngles[0]*=-1;
                if(reverseHorizontal)
                    camToPointAngles[1]*=-1;
                float add = 0;
                if(camToPoint[1] <= 0.0001)
                    add = -camToPointAngles[0]*2;
                rot[0] = camToPointAngles[0]+add;
                if(rot[0] < 0)
                    rot[0]+=360;
                else if(rot[0] > 360)
                    rot[0]-=360;
                add = 0;
    
                if(camToPoint[2] > 0.0001){
                    if(camToPoint[0] <= 0.0001)
                        add = -90;
                }
                else{
                    if(camToPoint[0] > 0.0001)
                        add = 90;
                    else
                        add = 180;
                }
                rot[1] = camToPointAngles[1]+add;
                if(rot[1] < 0)
                    rot[1]+=360;
                else if(rot[1] > 360)
                    rot[1]-=360;
    
                }
            }
        }
        else{
            System.out.println("ERROR: TOO FEW DIMENSIONS FOR LOOK-AT POINT");
            System.exit(-1);
        }
    }
    protected void lookAt(float[] point){
        if(point.length >= 3){
            if(Math.abs(point[0]-pos[0]) > 0.0001 && Math.abs(point[1]-pos[1]) > 0.0001 && Math.abs(pos[2]-point[2]) > 0.0001){
                float[] camToPoint = {point[0]-pos[0], point[1]-pos[1], point[2]-pos[2]};
                float dist = VectorOperations.vectorMagnitude(camToPoint);
                if(dist >= 1){
                    float[][] rotVec = {{0, camToPoint[1], camToPoint[2]}, 
                                        {camToPoint[0], 0, camToPoint[2]}, 
                                        {camToPoint[0], camToPoint[1], 0}};
                byte down = (byte)((Math.abs(camToPoint[2]) > Math.abs(camToPoint[0])) ? 2 : 0);
                byte oppDown = (byte)(down^2);
                float[] useVec = {oppDown >>> 1, 0, down >>> 1};
    
                if(rotVec[oppDown][down] <= 0)
                    useVec[down] = -1;
    
    
                if(camToPoint[0] <= 0.0001){
                    if(camToPoint[2] > 0.0001){
                        rotVec[1][0] = camToPoint[2];
                        rotVec[1][2] = -camToPoint[0];
                    }
                    else{
                        rotVec[1][0] = -camToPoint[0];
                        rotVec[1][2] = -camToPoint[2];
                    }
                }
                else
                    if(camToPoint[2] <= 0.0001){
                        rotVec[1][0] = -camToPoint[2];
                        rotVec[1][2] = camToPoint[0];
                }
                rotVec[oppDown] = VectorOperations.vectorNormalization3D(rotVec[oppDown]);
                rotVec[1] = VectorOperations.vectorNormalization3D(rotVec[1]);
                float[] camToPointAngles = {VectorOperations.returnAngleUnit(rotVec[oppDown], useVec, true),
                                            VectorOperations.returnAngleUnit(rotVec[1], VectorOperations.ELEM_K, true)};
    
                if(reverseVertical)
                    camToPointAngles[0]*=-1;
                if(reverseHorizontal)
                    camToPointAngles[1]*=-1;
                float add = 0;
                if(camToPoint[1] <= 0.0001)
                    add = -camToPointAngles[0]*2;
                rot[0] = camToPointAngles[0]+add;
                if(rot[0] < 0)
                    rot[0]+=360;
                else if(rot[0] > 360)
                    rot[0]-=360;
                add = 0;
    
                if(camToPoint[2] > 0.0001){
                    if(camToPoint[0] <= 0.0001)
                        add = -90;
                }
                else{
                    if(camToPoint[0] > 0.0001)
                        add = 90;
                    else
                        add = 180;
                }
                rot[1] = camToPointAngles[1]+add;
                if(rot[1] < 0)
                    rot[1]+=360;
                else if(rot[1] > 360)
                    rot[1]-=360;
    
                }
            }
        }
        else{
            System.out.println("ERROR: TOO FEW DIMENSIONS FOR LOOK-AT POINT");
            System.exit(-1);
        }
    }
}