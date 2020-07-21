package com.mrliu.cimoc.ui.view;

import com.mrliu.cimoc.model.Chapter;
import com.mrliu.cimoc.model.Comic;
import com.mrliu.cimoc.model.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hiroshi on 2016/8/21.
 */
public interface DetailView extends BaseView {

    void onComicLoadSuccess(Comic comic);

    void onChapterLoadSuccess(List<Chapter> list);

    void onLastChange(String chapter);

    void onParseError();

    void onTaskAddSuccess(ArrayList<Task> list);

    void onTaskAddFail();

}
