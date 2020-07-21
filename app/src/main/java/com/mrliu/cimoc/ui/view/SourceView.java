package com.mrliu.cimoc.ui.view;

import com.mrliu.cimoc.component.ThemeResponsive;
import com.mrliu.cimoc.model.Source;

import java.util.List;

/**
 * Created by Hiroshi on 2016/8/21.
 */
public interface SourceView extends BaseView, ThemeResponsive {

    void onSourceLoadSuccess(List<Source> list);

    void onSourceLoadFail();

}
