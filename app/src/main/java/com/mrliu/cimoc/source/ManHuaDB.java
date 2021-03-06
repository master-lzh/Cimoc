package com.mrliu.cimoc.source;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mrliu.cimoc.model.Chapter;
import com.mrliu.cimoc.model.Comic;
import com.mrliu.cimoc.model.ImageUrl;
import com.mrliu.cimoc.model.Source;
import com.mrliu.cimoc.parser.MangaParser;
import com.mrliu.cimoc.parser.NodeIterator;
import com.mrliu.cimoc.parser.SearchIterator;
import com.mrliu.cimoc.parser.UrlFilter;
import com.mrliu.cimoc.soup.Node;
import com.mrliu.cimoc.utils.DecryptionUtils;
import com.mrliu.cimoc.utils.StringUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Headers;
import okhttp3.Request;

/**
 * Created by ZhiWen on 2019/02/25.
 */

public class ManHuaDB extends MangaParser {

    public static final int TYPE = 46;
    public static final String DEFAULT_TITLE = "漫画DB";

    public static Source getDefaultSource() {
        return new Source(null, DEFAULT_TITLE, TYPE, true);
    }

    public ManHuaDB(Source source) {
        init(source, null);
    }

    @Override
    public Request getSearchRequest(String keyword, int page) throws UnsupportedEncodingException {
        String url = "";
        if (page == 1) {
            url = StringUtils.format("https://www.manhuadb.com/search?q=%s", keyword);
        }
        return new Request.Builder().url(url).build();
    }

    @Override
    public String getUrl(String cid) {
        return "https://www.manhuadb.com/manhua/".concat(cid);
    }

    @Override
    protected void initUrlFilterList() {
        filter.add(new UrlFilter("www.manhuadb.com"));
    }

    @Override
    public SearchIterator getSearchIterator(String html, int page) {
        Node body = new Node(html);
        return new NodeIterator(body.list("a.d-block")) {
            @Override
            protected Comic parse(Node node) {
                String cid = node.hrefWithSplit(1);
                String title = node.attr("title");
                String cover = node.attr("img", "src");
                return new Comic(TYPE, cid, title, cover, null, null);
            }
        };
    }

    @Override
    public Request getInfoRequest(String cid) {
        String url = "https://www.manhuadb.com/manhua/".concat(cid);
        return new Request.Builder().url(url).build();
    }

    @Override
    public void parseInfo(String html, Comic comic) throws UnsupportedEncodingException {
        Node body = new Node(html);
        String title = body.text("h1.comic-title");
//        String cover = body.src("div.cover > img"); // 这一个封面可能没有
        String cover = body.src("td.comic-cover > img");
        String author = body.text("a.comic-creator");
        String intro = body.text("p.comic_story");
        boolean status = isFinish(body.text("a.comic-pub-state"));

        String update = body.text("a.comic-pub-end-date");
        if (update == null || update.equals("")) {
            update = body.text("a.comic-pub-date");
        }
        comic.setInfo(title, cover, update, intro, author, status);
    }

    @Override
    public List<Chapter> parseChapter(String html) {
        List<Chapter> list = new LinkedList<>();
        for (Node node : new Node(html).list("#comic-book-list > div > ol > li > a")) {
            String title = node.attr("title");
            String path = node.hrefWithSplit(2);
            list.add(0, new Chapter(title, path));
        }
        return list;
    }

    @Override
    public Request getImagesRequest(String cid, String path) {
        String url = StringUtils.format("https://www.manhuadb.com/manhua/%s/%s.html", cid, path);
        return new Request.Builder().url(url).build();
    }

    @Override
    public List<ImageUrl> parseImages(String html) {
        List<ImageUrl> list = new ArrayList<>();
        Document document = Jsoup.parse(html);

        //获取页面定义的图片host和pre
        Element urlDefine = document.select(".vg-r-data").first();
        String imageHost = urlDefine.attr("data-host").trim();
        String imagePre = urlDefine.attr("data-img_pre").trim();

        //获取页面定义的图片信息变量img_data
        String imageArrDataDefine = document.getElementsByTag("script").eq(7).first().html();
        imageArrDataDefine = imageArrDataDefine.substring(16, imageArrDataDefine.length() - 2);
        //进行base64转换
        try {
            imageArrDataDefine = DecryptionUtils.base64Decrypt(imageArrDataDefine);
        } catch (Exception e) {
            e.printStackTrace();
            return list;
        }

        JSONArray imageList = JSON.parseArray(imageArrDataDefine);
        JSONObject image;
        int total = imageList.size();
        for (int i = 0; i < total; i++) {
            image = imageList.getJSONObject(i);
            list.add(new ImageUrl(image.getIntValue("p"), imageHost + imagePre + image.getString("img"), false));
        }
        return list;
    }

    @Override
    public Request getLazyRequest(String url) {
        return null;
    }

    @Override
    public String parseLazy(String html, String url) {
        return null;
    }

    @Override
    public Request getCheckRequest(String cid) {
        return getInfoRequest(cid);
    }

    @Override
    public String parseCheck(String html) {
        // 这里表示的是更新时间
        Node body = new Node(html);
        String update = body.text("a.comic-pub-end-date");
        if (update == null || update.equals("")) {
            update = body.text("a.comic-pub-date");
        }
        return update;
    }

    @Override
    public Headers getHeader() {
        return Headers.of("Referer", "https://www.manhuadb.com");
    }

}
