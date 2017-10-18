package com.stuart.hackatonproject.constant;

import android.support.annotation.IntDef;

import static com.stuart.hackatonproject.constant.ColorTypeDef.BLUE;
import static com.stuart.hackatonproject.constant.ColorTypeDef.GREEN;
import static com.stuart.hackatonproject.constant.ColorTypeDef.PURPLE;
import static com.stuart.hackatonproject.constant.ColorTypeDef.RED;

/**
 * @author normansyahputa on 4/25/17.
 */
@IntDef({BLUE, GREEN, RED, PURPLE})
public @interface ColorTypeDef {
    int BLUE = 1;
    int GREEN = 2;
    int RED = 3;
    int PURPLE = 4;
}
