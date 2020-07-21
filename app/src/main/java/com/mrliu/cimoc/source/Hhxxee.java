package com.mrliu.cimoc.source;

import com.mrliu.cimoc.model.Chapter;
import com.mrliu.cimoc.model.Comic;
import com.mrliu.cimoc.model.ImageUrl;
import com.mrliu.cimoc.model.Source;
import com.mrliu.cimoc.parser.MangaParser;
import com.mrliu.cimoc.parser.NodeIterator;
import com.mrliu.cimoc.parser.SearchIterator;
import com.mrliu.cimoc.parser.UrlFilter;
import com.mrliu.cimoc.soup.Node;
import com.mrliu.cimoc.utils.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by FEILONG on 2017/12/21.
 */

public class Hhxxee extends MangaParser {

    public static final int TYPE = 59;
    public static final String DEFAULT_TITLE = "997700";

    public Hhxxee(Source source) {
        init(source, null);
    }

    private static final String[] servers = {
            "http://165.94201314.net/dm01/",
            "http://165.94201314.net/dm02/",
            "http://165.94201314.net/dm03/",
            "http://165.94201314.net/dm04/",
            "http://165.94201314.net/dm05/",
            "http://165.94201314.net/dm06/",
            "http://165.94201314.net/dm07/",
            "http://165.94201314.net/dm08/",
            "http://165.94201314.net/dm09/",
            "http://165.94201314.net/dm10/",
            "http://165.94201314.net/dm11/",
            "http://165.94201314.net/dm12/",
            "http://165.94201314.net/dm13/",
            "http://173.231.57.238/dm14/",
            "http://165.94201314.net/dm15/",
            "http://142.4.34.102/dm16/"
    };

    public static Source getDefaultSource() {
        return new Source(null, DEFAULT_TITLE, TYPE, true);
    }

    @Override
    public Request getSearchRequest(String keyword, int page) throws UnsupportedEncodingException {
        String url = "";
        if (page == 1)
            url = "http://99770.hhxxee.com/search/s.aspx";
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("tbSTxt", keyword)
                .build();
        return new Request.Builder().url(url)
                .post(requestBodyPost).build();
    }

    @Override
    public SearchIterator getSearchIterator(String html, int page) {
        Node body = new Node(html);
        return new NodeIterator(body.list(".cInfoItem")) {
            @Override
            protected Comic parse(Node node) {
                String cid = node.href(".cListTitle > a").substring("http://99770.hhxxee.com/comic/".length());
                String title = node.text(".cListTitle > span");
                title = title.substring(1, title.length() - 1);
                String cover = node.src(".cListSlt > img");
                String update = node.text(".cListh2 > span").substring(8);
                String author = node.text(".cl1_2").substring(3);
                return new Comic(TYPE, cid, title, cover, update, author);
            }
        };
    }

    @Override
    public String getUrl(String cid) {
        return "http://99770.hhxxee.com/comic/".concat(cid);
    }

    @Override
    protected void initUrlFilterList() {
        filter.add(new UrlFilter("99770.hhxxee.com","(\\d+)$"));
    }

    @Override
    public Request getInfoRequest(String cid) {
        String url = "http://99770.hhxxee.com/comic/".concat(cid);
        return new Request.Builder().url(url).build();
    }

    @Override
    public void parseInfo(String html, Comic comic) throws UnsupportedEncodingException {
        Node body = new Node(html);
        String title = body.text(".cTitle");
        String cover = body.src(".cDefaultImg > img");
        String update = "";
        String author = "";
        String intro = body.text(".cCon");
        boolean status = false;
        comic.setInfo(title, cover, update, intro, author, status);
    }

    @Override
    public List<Chapter> parseChapter(String html) {
        List<Chapter> list = new LinkedList<>();
        for (Node node : new Node(html).list("#subBookListAct > div")) {
            String title = node.text("a");
            String path = node.hrefWithSplit("a", 2);
            list.add(new Chapter(title, path));
        }
        return list;
    }

    @Override
    public Request getImagesRequest(String cid, String path) {
        String url = StringUtils.format("http://99770.hhxxee.com/comic/%s/%s/", cid, path);
        return new Request.Builder().url(url).build();
    }

    private int getPictureServers(String url) {
        return Integer.parseInt(StringUtils.match("ok\\-comic(\\d+)", url, 1)) - 1;
    }

    @Override
    public List<ImageUrl> parseImages(String html) {
        List<ImageUrl> list = new LinkedList<>();
        String str = StringUtils.match("var sFiles=\"(.*?)\"", html, 1);
        if (str != null) {
            try {
                String[] array = str.split("\\|");
                for (int i = 0; i != array.length; ++i) {
                    list.add(new ImageUrl(i + 1, servers[getPictureServers(array[i])] + array[i], false));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    @Override
    public Request getCheckRequest(String cid) {
        return getInfoRequest(cid);
    }

    @Override
    public String parseCheck(String html) {
        return new Node(html).text("div.book-detail > div.cont-list > dl:eq(2) > dd");
    }

    @Override
    public List<Comic> parseCategory(String html, int page) {
        List<Comic> list = new LinkedList<>();
        Node body = new Node(html);
        for (Node node : body.list("li > a")) {
            String cid = node.hrefWithSplit(1);
            String title = node.text("h3");
            String cover = node.attr("div > img", "data-src");
            String update = node.text("dl:eq(5) > dd");
            String author = node.text("dl:eq(2) > dd");
            list.add(new Comic(TYPE, cid, title, cover, update, author));
        }
        return list;
    }

    @Override
    public Headers getHeader() {
        return Headers.of("Referer", "http://99770.hhxxee.com");
    }


}
