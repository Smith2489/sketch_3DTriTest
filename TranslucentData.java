public class TranslucentData {
    private byte id = 0;
    private float zPos = Float.NaN;
    private boolean noDepth = false;
    private int originalIndex = -1;
    public TranslucentData(){
        id = 0;
        zPos = Float.NaN;
        noDepth = false;
        originalIndex = -1;
    }
    public TranslucentData(byte newId, float newZ, boolean depthDisabled, int newIndex){
        id = newId;
        zPos = newZ;
        noDepth = depthDisabled;
        originalIndex = newIndex;
    }
    public void setData(byte newId, float newZ, boolean depthDisabled, int newIndex){
        id = newId;
        zPos = newZ;
        noDepth = depthDisabled;
        originalIndex = newIndex;
    }
    public void setID(byte newId){
        id = newId;
    }
    public void setZPos(float newZ){
        zPos = newZ;
    }
    public void setOriginalIndex(int newIndex){
        originalIndex = newIndex;
    }
    public void setNoDepth(boolean depthDisabled){
        noDepth = depthDisabled;
    }
    public byte returnID(){
        return id;
    }
    public float returnZ(){
        return zPos;
    }
    public int returnOriginalIndex(){
        return originalIndex;
    }
    public boolean returnNoDepth(){
        return noDepth;
    }
    public String toString(){
        String type = "TYPE: ";
        switch(id){
            case 1:
                type+="TRIANGLE";
                break;
            case 2:
                type+="BILLBOARDED SPRITE";
                break;
            case 3:
                type+="LINE";
                break;
            case 4:
                type+="DOT";
                break;
            default:
                type+="NONE";
                break;
        }
        String zString = "Z-POSITION: "+zPos;
        String depthDisabledString = "DEPTH DISABLED: "+noDepth;
        String originalIndexString = "ORIGINAL INDEX: "+originalIndex;
        return type+"\n"+zString+"\n"+depthDisabledString+"\n"+originalIndexString;
    }
    public boolean equals(Object o){
        if(o instanceof TranslucentData){
            TranslucentData d = (TranslucentData)o;
            boolean isEquals = true;
            isEquals&=(id == d.id);
            isEquals&=(Math.abs(zPos - d.zPos) <= 0.0001);
            isEquals&=(noDepth == d.noDepth);
            isEquals&=(originalIndex == d.originalIndex);
            return isEquals;
        }
        else
            return false;
    }
    public void copy(Object o){
        if(o instanceof TranslucentData){
            TranslucentData d = (TranslucentData)o;
            id = d.id;
            zPos = d.zPos;
            noDepth = d.noDepth;
            originalIndex = d.originalIndex;
        }
    }
    public boolean equals(TranslucentData d){
        boolean isEquals = true;
        isEquals&=(id == d.id);
        isEquals&=(Math.abs(zPos - d.zPos) <= 0.0001);
        isEquals&=(noDepth == d.noDepth);
        isEquals&=(originalIndex == d.originalIndex);
        return isEquals;
    }
    public void copy(TranslucentData d){
        id = d.id;
        zPos = d.zPos;
        noDepth = d.noDepth;
        originalIndex = d.originalIndex;
    }
}
