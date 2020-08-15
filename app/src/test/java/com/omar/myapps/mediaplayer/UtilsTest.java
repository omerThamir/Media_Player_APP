package com.omar.myapps.mediaplayer;

import org.junit.Test;

import static org.junit.Assert.*;

public class UtilsTest {

    @Test    public void add2n()  {
        int input1=1;
        int   input2=2;
        int expecepted=4;

        int output=  Utils.Add2n(input1,input2) ;
        assertEquals (output,expecepted);
    }
}