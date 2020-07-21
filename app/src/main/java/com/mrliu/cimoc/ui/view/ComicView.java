package com.mrliu.cimoc.ui.view;

import com.mrliu.cimoc.component.DialogCaller;
import com.mrliu.cimoc.component.ThemeResponsive;
import com.mrliu.cimoc.model.Tag;

import java.util.List;

/**
 * Created by Hiroshi on 2016/10/11.
 */

public interface ComicView extends BaseView, ThemeResponsive, DialogCaller {

    void onTagLoadSuccess(List<Tag> list);

    void onTagLoadFail();

}
