package www.baidu.com.zhangxu19.myapplication;

import android.view.View;

/**
 * Created by zhangxu19 on 16/12/28.
 */

public interface IClientOpenPopupBtnClick {

    void openOuterClientThisTime(View view);

    void openOuterClientAllways(View view);

    void openOuterClientNoMoreHint(View view);

    void openOuterClientCancel(View view);
}
