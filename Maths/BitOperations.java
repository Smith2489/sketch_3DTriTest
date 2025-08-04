package Maths;

public class BitOperations{
    public static byte reverse(byte x){
        byte output = 0;
        for(int i = 0; i < 8; i++){
            output<<=1;
            output|=(x & 1);
            x>>>=1;
        }
        return output;
    }
    public static short reverse(short x){
        short output = 0;
        for(int i = 0; i < 16; i++){
            output<<=1;
            output|=(x & 1);
            x>>>=1;
        }
        return output;
    }
    public static int reverse(int x){
        int output = 0;
        for(int i = 0; i < 32; i++){
            output<<=1;
            output|=(x & 1);
            x>>>=1;
        }
        return output;
    }
    public static long reverse(long x){
        long output = 0;
        for(int i = 0; i < 64; i++){
            output<<=1;
            output|=(x & 1);
            x>>>=1;
        }
        return output;
    }
    public static short interleave(byte x, byte y){
        short output = 0;
        for(int i = 0; i < 16; i+=2){
            output|=((x & 1) << i)|((y & 1) << (i+1));
            x>>>=1;
            y>>>=1;
        }
        return output;
    }
    public static int interleave(short x, short y){
        int lowerHalf = (int)x;
        int upperHalf = (int)y;
        //Solution found on: https://stackoverflow.com/questions/39490345/interleave-bits-efficiently
        //Do note how the original number is OR'd with itself shifted up 8 bits for the first go around
        //Bitwise ANDs take priority over bitwise ORs, so the program needs to be forced into prioritising the OR
        lowerHalf = ((lowerHalf | (lowerHalf << 8)) & 0x00FF00FF);
        lowerHalf = ((lowerHalf | (lowerHalf << 4)) & 0x0F0F0F0F);
        lowerHalf = ((lowerHalf | (lowerHalf << 2)) & 0x33333333);
        lowerHalf = ((lowerHalf | (lowerHalf << 1)) & 0x55555555);

        upperHalf = ((upperHalf | (upperHalf << 8)) & 0x00FF00FF);
        upperHalf = ((upperHalf | (upperHalf << 4)) & 0x0F0F0F0F);
        upperHalf = ((upperHalf | (upperHalf << 2)) & 0x33333333);
        upperHalf = ((upperHalf | (upperHalf << 1)) & 0x55555555);

        return (upperHalf << 1) | lowerHalf;
    }

    public static long interleave(int x, int y){
        long lowerHalf = (long)x;
        long upperHalf = (long)y;
        //Solution found on: https://stackoverflow.com/questions/39490345/interleave-bits-efficiently
        //Do note how the original number is OR'd with itself shifted up 8 bits for the first go around
        //Bitwise ANDs take priority over bitwise ORs, so the program needs to be forced into prioritising the OR
        lowerHalf = ((lowerHalf | (lowerHalf << 16)) & 0x0000FFFF0000FFFFL);
        lowerHalf = ((lowerHalf | (lowerHalf << 8)) & 0x00FF00FF00FF00FFL);
        lowerHalf = ((lowerHalf | (lowerHalf << 4)) & 0x0F0F0F0F0F0F0F0FL);
        lowerHalf = ((lowerHalf | (lowerHalf << 2)) & 0x3333333333333333L);
        lowerHalf = ((lowerHalf | (lowerHalf << 1)) & 0x5555555555555555L);

        upperHalf = ((upperHalf | (upperHalf << 16)) & 0x0000FFFF0000FFFFL);
        upperHalf = ((upperHalf | (upperHalf << 8)) & 0x00FF00FF00FF00FFL);
        upperHalf = ((upperHalf | (upperHalf << 4)) & 0x0F0F0F0F0F0F0F0FL);
        upperHalf = ((upperHalf | (upperHalf << 2)) & 0x3333333333333333L);
        upperHalf = ((upperHalf | (upperHalf << 1)) & 0x5555555555555555L);

        return (upperHalf << 1) | lowerHalf;
    }
}
