package com.mrliu.cimoc.ui.view;

import com.mrliu.cimoc.component.DialogCaller;

/**
 * Created by Hiroshi on 2016/8/21.
 */
public interface SettingsView extends BaseView, DialogCaller {

    void onFileMoveSuccess();

    void onExecuteSuccess();

    void onExecuteFail();

}
