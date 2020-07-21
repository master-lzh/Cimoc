package com.mrliu.cimoc.ui.view;

import com.mrliu.cimoc.model.MiniComic;

import java.util.List;

/**
 * Created by Hiroshi on 2016/8/21.
 */
public interface FavoriteView extends GridView {

    void OnComicFavorite(MiniComic comic);

    void OnComicRestore(List<MiniComic> list);

    void OnComicUnFavorite(long id);

    void onComicCheckSuccess(MiniComic comic, int progress, int max);

    void onComicCheckFail();

    void onComicCheckComplete();

    void onComicRead(MiniComic comic);

    void onHighlightCancel(MiniComic comic);

}
