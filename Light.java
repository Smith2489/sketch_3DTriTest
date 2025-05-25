import java.util.*;
public class Light{
    private static final float[] ONE = {0, 0, -1, 0};
    private static final char[] VALID_TYPES = {'p', 'd', 's'};
    //Position if point light, rotation for directional light, both for spot light
    private float[] position = new float[3];
    private float[] rotation = new float[3];
    //Intensity
    //  0: ambient
    //  1: diffuse
    //  2: specular
    private float[] intensities = {1, 1, 1};
    //p = point, d = directional, s = spotlight
    private char lightType = 'p';
    //0 = ambient colour, 1 = diffuse colour, 2 = specular colour
    private float[][] lightColour = {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}};
    //0 = inner spread, 1 = outer spread
    private float[] spotlightSpread = {0, -1};
    private LinkedList<Action> actionList = new LinkedList<Action>();
    private Action tempAction;
    private float[] lightDir = {0, 0, 0};
    private boolean alwaysPerform = false;
    public Light(){
        position[0] = 0;
        position[1] = 0;
        position[2] = 0;
        rotation[0] = 0;
        rotation[1] = 0;
        rotation[2] = 0;
        intensities[0] = 1;
        intensities[1] = 1;
        intensities[2] = 1;
        for(byte i = 0; i < 3; i++){
            lightColour[i][0] = 1;
            lightColour[i][1] = 1;
            lightColour[i][2] = 1;
        }
        lightType = 'p';
        spotlightSpread[0] = 0;
        spotlightSpread[1] = -1;
    }
    public Light(float[] newPosition){
        position[0] = newPosition[0];
        position[1] = newPosition[1];
        position[2] = newPosition[2];
        intensities[0] = 1;
        intensities[1] = 1;
        intensities[2] = 1;
        rotation[0] = 0;
        rotation[1] = 0;
        rotation[2] = 0;
        for(byte i = 0; i < 3; i++){
            lightColour[i][0] = 1;
            lightColour[i][1] = 1;
            lightColour[i][2] = 1;
        }
        lightType = 'p';
        spotlightSpread[0] = 0;
        spotlightSpread[1] = -1;
    }
    public Light(float newX, float newY, float newZ){
        position[0] = newX;
        position[1] = newY;
        position[2] = newZ;
        intensities[0] = 1;
        intensities[1] = 1;
        intensities[2] = 1;
        rotation[0] = 0;
        rotation[1] = 0;
        rotation[2] = 0;
        for(byte i = 0; i < 3; i++){
            lightColour[i][0] = 1;
            lightColour[i][1] = 1;
            lightColour[i][2] = 1;
        }
        lightType = 'p';
        spotlightSpread[0] = 0;
        spotlightSpread[1] = -1;
    }


    public void addAction(LightAction newAction){
        if(newAction != null){
            newAction.setPos(position);
            newAction.setRot(rotation);
            newAction.setIntensities(intensities);
            newAction.setColour(lightColour);
            newAction.setDirection(lightDir);
            actionList.add(newAction);
        }
        else
            System.out.println("ERROR: ACTION CANNOT BE NULL");
    }
    public Action removeFirstAction(){
        return actionList.removeFirst();
    }
    public Action removeLastAction(){
        return actionList.removeLast();
    }
    public Action removeAction(int i){
        return actionList.remove(i);
    }
    public void clearActionList(){
        actionList.clear();
    }
    public boolean hasActions(){
        return !actionList.isEmpty();
    }
    public int numOfActions(){
        return actionList.size();
    }
    public void executeActions(){
        int length = actionList.size();
        for(int i = 0; i < length; i++){
            tempAction = actionList.removeFirst();
            actionList.add(tempAction);
            tempAction.perform();
        }
    }
    public void alwaysPerform(boolean perform){
        alwaysPerform = perform;
    }
    public boolean alwaysPerform(){
        return alwaysPerform;
    }
    public void setAmbientIntensity(float ambientIntensity){
        intensities[0] = Math.max(0, ambientIntensity);
    }
    public void setDiffuseIntensity(float diffuseIntensity){
        intensities[1] = Math.max(0, diffuseIntensity);
    }
    public void setSpecularIntensity(float specularIntensity){
        intensities[2] = Math.max(0, specularIntensity);
    }
    public float returnAmbientIntensity(){
        return intensities[0];
    }
    public float returnDiffuseIntensity(){
        return intensities[1];
    }
    public float returnSpecularIntensity(){
        return intensities[2];
    }
    public char returnType(){
        return lightType;
    }
    public void setType(char newType){
        if(newType >= 65 && newType <= 90)
            newType+=32;
        boolean isValid = true;
        for(int i = 0; i < VALID_TYPES.length; i++){
            if(newType == VALID_TYPES[i]){
                isValid = true;
                break;
            }
            else
                isValid = false;
        }
        if(!isValid){
            System.out.println("ERROR: NOT A VALID TYPE (MUST BE 'p', 'd', 's')");
            System.exit(-1);
        }
        else
            lightType = newType;
    }
    public void setInnerSpread(float spread){
        if(spread < 0)
            spread = 0;
        else if(spread > 180)
            spread = 180;
        spotlightSpread[0] = (float)Math.cos(MathExtentions.DEGS_TO_RADS*spread);
    }
    public float returnInnerSpread(){
        return spotlightSpread[0];
    }
    public void setOuterSpread(float spread){
        if(spread < 0)
            spread = 0;
        else if(spread > 180)
            spread = 180;
        spotlightSpread[1] = (float)Math.cos(MathExtentions.DEGS_TO_RADS*spread);
    }
    public float returnOuterSpread(){
        return spotlightSpread[1];
    }

    public void setLightColour(int r, int g, int b, byte index){
        lightColour[index][0] = (r & 0xFF)*0.003921569f;
        lightColour[index][1] = (g & 0xFF)*0.003921569f;
        lightColour[index][2] = (b & 0xFF)*0.003921569f;
    }
    public void setLightColour(int colour, byte index){
        colour&=0xFFFFFF;
        if(colour <= 0xFF)
            colour = (colour << 16)|(colour << 8)|colour;
        lightColour[index][0] = ((colour >>> 16) & 0xFF)*0.003921569f;
        lightColour[index][1] = ((colour >>> 8) & 0xFF)*0.003921569f;
        lightColour[index][2] = (colour & 0xFF)*0.003921569f;
    }
    public void setLightColour(int r, int g, int b){
        lightColour[0][0] = (r & 0xFF)*0.003921569f;
        lightColour[0][1] = (g & 0xFF)*0.003921569f;
        lightColour[0][2] = (b & 0xFF)*0.003921569f;
        lightColour[1][0] = lightColour[0][0];
        lightColour[1][1] = lightColour[0][1];
        lightColour[1][2] = lightColour[0][2];
        lightColour[2][0] = lightColour[0][0];
        lightColour[2][1] = lightColour[0][1];
        lightColour[2][2] = lightColour[0][2];
    }
    public void setLightColour(int colour){
        colour&=0xFFFFFF;
        if(colour <= 0xFF)
            colour = (colour << 16)|(colour << 8)|colour;
        lightColour[0][0] = ((colour >>> 16) & 0xFF)*0.003921569f;
        lightColour[0][1] = ((colour >>> 8) & 0xFF)*0.003921569f;
        lightColour[0][2] = (colour & 0xFF)*0.003921569f;
        lightColour[1][0] = lightColour[0][0];
        lightColour[1][1] = lightColour[0][1];
        lightColour[1][2] = lightColour[0][2];
        lightColour[2][0] = lightColour[0][0];
        lightColour[2][1] = lightColour[0][1];
        lightColour[2][2] = lightColour[0][2];
    }
    public void setPosition(float x, float y, float z){
        position[0] = x;
        position[1] = y;
        position[2] = z;
    }
    public void setPosition(float[] newPos){
        position[0] = newPos[0];
        position[1] = newPos[1];
        position[2] = newPos[2];
    }
    public float[] returnPosition(){
        return position;
    }
    public void setRotations(float alpha, float beta, float gamma){
        rotation[0] = alpha;
        rotation[1] = beta;
        rotation[2] = gamma;
    }
    public void setRotation(float[] newRot){
        rotation[0] = newRot[0];
        rotation[1] = newRot[1];
        rotation[2] = newRot[2];
    }
    public float[] returnRotation(){
        return rotation;
    }
    public void computeDirection(){
        Matrix lightDirMatrix = MatrixOperations.matrixMultiply(MVP.returnRotation(rotation), ONE);
        lightDir[0] = lightDirMatrix.returnData(0, 0);
        lightDir[1] = lightDirMatrix.returnData(1, 0);
        lightDir[2] = lightDirMatrix.returnData(2, 0);
        float[] tempDir = VectorOperations.vectorNormalization(lightDir);
        lightDir[0] = tempDir[0];
        lightDir[1] = tempDir[1];
        lightDir[2] = tempDir[2];
    }
    public float[] returnLightDirection(){
        return lightDir;
    }
    //Returns a specific colour of the light in an integer RGB format
    public int returnLightColour(byte index){
        return 0xFF000000|((int)(lightColour[index][0]*255) << 16)|((int)(lightColour[index][1]*255) << 8)|((int)(lightColour[index][2]*255));
    }
    //Returns a deep copy of a light's colour
    public float[][] returnLightColour(){
        float[][] returnColour = {{lightColour[0][0], lightColour[0][1], lightColour[0][2]},
                                  {lightColour[1][0], lightColour[1][1], lightColour[1][2]},
                                  {lightColour[2][0], lightColour[2][1], lightColour[2][2]}};
        return returnColour;
    }
    public float spreadBrightness(float dot){
        if(dot < spotlightSpread[1])
            return 0;
        if(dot >= spotlightSpread[0])
            return 1;
        float spreadDiff = spotlightSpread[1] - spotlightSpread[0];
        if(Math.abs(spreadDiff) <= 0.0001){
            if(dot < spotlightSpread[0])
                return 0;
            return 1;
        }
        return (spotlightSpread[1] - dot)/spreadDiff;
    }

    public boolean equals(Object o){
        if(o instanceof Light){
            Light l = (Light)o;
            boolean isEquals = true;
            for(byte i = 0; i < 3; i++){
                isEquals&=(Math.abs(lightColour[i][0]-l.lightColour[i][0]) <= 0.0001);
                isEquals&=(Math.abs(lightColour[i][1]-l.lightColour[i][1]) <= 0.0001);
                isEquals&=(Math.abs(lightColour[i][2]-l.lightColour[i][2]) <= 0.0001);
            }
            isEquals&=(Math.abs(position[0] - l.position[0]) <= 0.0001);
            isEquals&=(Math.abs(position[1] - l.position[1]) <= 0.0001);
            isEquals&=(Math.abs(position[2] - l.position[2]) <= 0.0001);
            isEquals&=(Math.abs(rotation[0] - l.rotation[0]) <= 0.0001);
            isEquals&=(Math.abs(rotation[1] - l.rotation[1]) <= 0.0001);
            isEquals&=(Math.abs(rotation[2] - l.rotation[2]) <= 0.0001);
            isEquals&=(Math.abs(intensities[0] - l.intensities[0]) <= 0.0001);
            isEquals&=(Math.abs(intensities[1] - l.intensities[1]) <= 0.0001);
            isEquals&=(Math.abs(intensities[2] - l.intensities[2]) <= 0.0001);
            isEquals&=(Math.abs(spotlightSpread[0] - l.spotlightSpread[0]) <= 0.0001);
            isEquals&=(Math.abs(spotlightSpread[1] - l.spotlightSpread[1]) <= 0.0001);
            isEquals&=(lightType == l.lightType);
            isEquals&=(alwaysPerform == l.alwaysPerform);
            return isEquals;
        }
        else
            return false;
    }

    public boolean equals(Light l){
        boolean isEquals = true;
        for(byte i = 0; i < 3; i++){
            isEquals&=(Math.abs(lightColour[i][0]-l.lightColour[i][0]) <= 0.0001);
            isEquals&=(Math.abs(lightColour[i][1]-l.lightColour[i][1]) <= 0.0001);
            isEquals&=(Math.abs(lightColour[i][2]-l.lightColour[i][2]) <= 0.0001);
        }
        isEquals&=(Math.abs(position[0] - l.position[0]) <= 0.0001);
        isEquals&=(Math.abs(position[1] - l.position[1]) <= 0.0001);
        isEquals&=(Math.abs(position[2] - l.position[2]) <= 0.0001);
        isEquals&=(Math.abs(rotation[0] - l.rotation[0]) <= 0.0001);
        isEquals&=(Math.abs(rotation[1] - l.rotation[1]) <= 0.0001);
        isEquals&=(Math.abs(rotation[2] - l.rotation[2]) <= 0.0001);
        isEquals&=(Math.abs(intensities[0] - l.intensities[0]) <= 0.0001);
        isEquals&=(Math.abs(intensities[1] - l.intensities[1]) <= 0.0001);
        isEquals&=(Math.abs(intensities[2] - l.intensities[2]) <= 0.0001);
        isEquals&=(Math.abs(spotlightSpread[0] - l.spotlightSpread[0]) <= 0.0001);
        isEquals&=(Math.abs(spotlightSpread[1] - l.spotlightSpread[1]) <= 0.0001);
        isEquals&=(lightType == l.lightType);
        isEquals&=(alwaysPerform == l.alwaysPerform);
        return isEquals;
    }
    public void copy(Object o){
        if(o instanceof Light){
            Light l = (Light)o;
            for(byte i = 0; i < 3; i++){
                lightColour[i][0] = l.lightColour[i][0];
                lightColour[i][1] = l.lightColour[i][1];
                lightColour[i][2] = l.lightColour[i][2];
            }
            position[0] = l.position[0];
            position[1] = l.position[1];
            position[2] = l.position[2];
            rotation[0] = l.rotation[0];
            rotation[1] = l.rotation[1];
            rotation[2] = l.rotation[2];
            spotlightSpread[0] = l.spotlightSpread[0];
            spotlightSpread[1] = l.spotlightSpread[1];
            lightType = l.lightType;
            intensities[0] = l.intensities[0];
            intensities[1] = l.intensities[1];
            intensities[2] = l.intensities[2];
            alwaysPerform = l.alwaysPerform;
        }
    }
    public void copy(Light l){
        for(byte i = 0; i < 3; i++){
            lightColour[i][0] = l.lightColour[i][0];
            lightColour[i][1] = l.lightColour[i][1];
            lightColour[i][2] = l.lightColour[i][2];
        }
        position[0] = l.position[0];
        position[1] = l.position[1];
        position[2] = l.position[2];
        rotation[0] = l.rotation[0];
        rotation[1] = l.rotation[1];
        rotation[2] = l.rotation[2];
        spotlightSpread[0] = l.spotlightSpread[0];
        spotlightSpread[1] = l.spotlightSpread[1];
        lightType = l.lightType;
        intensities[0] = l.intensities[0];
        intensities[1] = l.intensities[1];
        intensities[2] = l.intensities[2];
        alwaysPerform = l.alwaysPerform;
    }
}
