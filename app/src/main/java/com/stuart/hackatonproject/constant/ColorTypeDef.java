package com.stuart.hackatonproject.constant;

import android.support.annotation.IntDef;

import static com.stuart.hackatonproject.constant.ColorTypeDef.GREEN;
import static com.stuart.hackatonproject.constant.ColorTypeDef.PURPLE;
import static com.stuart.hackatonproject.constant.ColorTypeDef.RED;

/**
 * @author normansyahputa on 4/25/17.
 */
@IntDef({GREEN, RED, PURPLE})
public @interface ColorTypeDef {
    int GREEN = 1;
    int RED = 2;
    int PURPLE = 3;
}
