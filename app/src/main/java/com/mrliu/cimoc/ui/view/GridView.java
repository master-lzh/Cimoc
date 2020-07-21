package com.mrliu.cimoc.ui.view;

import com.mrliu.cimoc.component.DialogCaller;
import com.mrliu.cimoc.component.ThemeResponsive;
import com.mrliu.cimoc.model.MiniComic;

import java.util.List;

/**
 * Created by Hiroshi on 2016/9/30.
 */

public interface GridView extends BaseView, DialogCaller, ThemeResponsive {

    void onComicLoadSuccess(List<MiniComic> list);

    void onComicLoadFail();

    void onExecuteFail();

}
