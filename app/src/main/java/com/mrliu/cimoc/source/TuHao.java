package com.mrliu.cimoc.source;

import com.mrliu.cimoc.App;
import com.mrliu.cimoc.core.Manga;
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

import java.util.LinkedList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by ZhiWen on 2019/02/25.
 */

public class TuHao extends MangaParser {

    public static final int TYPE = 24;
    public static final String DEFAULT_TITLE = "土豪漫画";

    public static Source getDefaultSource() {
        return new Source(null, DEFAULT_TITLE, TYPE, true);
    }

    public TuHao(Source source) {
        init(source, null);
    }

    @Override
    public Request getSearchRequest(String keyword, int page) {
        String url = "";
        if (page == 1) {
            url = "https://m.tuhaomh.com/e/search/index.php";
        }
        RequestBody requestBodyPost = new FormBody.Builder()
                .add("keyboard", keyword)
                .add("tbname","book")
                .add("show","title,writer,bookfilename")
                .add("tempid","1")
                .add("submit","")
                .build();
        return new Request.Builder()
                .addHeader("referer","https://m.tuhaomh.com/sousuo.php")
                .addHeader("origin","https://m.tuhaomh.com")
                .url(url).post(requestBodyPost)
                .build();
    }

    @Override
    public String getUrl(String cid) {
        return "https://m.tuhaomh.com/manhua/".concat(cid);
    }

    @Override
    protected void initUrlFilterList() {
        filter.add(new UrlFilter("m.tuhaomh.com", "\\w+", 0));
    }

    @Override
    public SearchIterator getSearchIterator(String html, int page) {
        Node body = new Node(html);
        return new NodeIterator(body.list("ul.comic-sort > li")) {
            @Override
            protected Comic parse(Node node) {

                String title = node.text("div > h3 > a");
                String cid = node.attr("div > div > a", "href");
                cid = cid.replace("/manhua/","").replace(".html","");
                String cover = node.attr("div > div > a > img", "data-src");
//                cover=cover.split("\\\"")[1];
                return new Comic(TYPE, cid, title, cover, null, null);
            }
        };
    }

    @Override
    public Request getInfoRequest(String cid) {
        String url = "https://m.tuhaomh.com/manhua/".concat(cid).concat(".html");
        return new Request.Builder().url(url).build();
    }

    @Override
    public void parseInfo(String html, Comic comic) {
        Node body = new Node(html);
        String cover = body.attr("div.comic-info > div.comic-item > div > img","data-src");
//        cover=cover.split("\\\"")[1];
        String intro = body.text("div.comic-detail > p");
        String title = body.text("div.comic-info > h1");

        String update = body.text("#updateTime");
        String author = body.text("span.author");

        // 连载状态
        boolean status = isFinish("连载");
        comic.setInfo(title, cover, update, intro, author, status);
    }

    @Override
    public List<Chapter> parseChapter(String html) {
        List<Chapter> list = new LinkedList<>();
        for (Node node : new Node(html).list("ul.chapterlist > li > a")) {
            String title = node.text();
            String path = node.hrefWithSplit(1);
            list.add(new Chapter(title, path));
        }
        return list;
    }

    @Override
    public Request getImagesRequest(String cid, String path) {
        String url = StringUtils.format("https://m.tuhaomh.com/%s/%s", cid, path.concat(".html"));
        return new Request.Builder().url(url).build();
    }

    @Override
    public List<ImageUrl> parseImages(String html) {
        List<ImageUrl> list = new LinkedList<>();

        Node body=new Node(html);
        List<Node> nodeList = body.list("div.right-menu > div > ul > li.bdlist > a");
        List<String> urls=new LinkedList<>();
        for (Node node:nodeList) {
            urls.add("https://m.tuhaomh.com"+node.href());
        }
        int cnt=1;
        for (String url:urls) {
            Request request=new Request.Builder().url(url).build();
            try {
                Thread.sleep(50);
                String html1 = Manga.getResponseBody(App.getHttpClient(), request);
                Node body1=new Node(html1);
                list.add(new ImageUrl(cnt++,body1.src("#comic_pic"),false));
            } catch (Manga.NetworkErrorException | InterruptedException e) {
                e.printStackTrace();
            }
        }
//        // 得到 https://mh2.wan1979.com/upload/jiemoren/1989998/
//        String prevStr = str.substring(0, str.length() - 8);
//
//        // 得到 0000
//        int lastStr = Integer.parseInt(str.substring(str.length() - 8, str.length() - 4));
//        int pagNum = Integer.parseInt(StringUtils.match("var pcount=(.*?);", html, 1));
//
//        if (str != null) {
//            try {
//                for (int i = lastStr; i < pagNum + lastStr; i++) {
//                    String url = StringUtils.format("%s%04d.jpg", prevStr, i);
////                  https://mh2.wan1979.com/upload/jiemoren/1989998/0000.jpg
//                    list.add(new ImageUrl(i + 1, url, false));
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        return list;
    }

    @Override
    public Request getCheckRequest(String cid) {
        return getInfoRequest(cid);
    }

    @Override
    public String parseCheck(String html) {
        // 这里表示的是更新时间
        Node body = new Node(html);

        return body.text("#updateTime");
    }

    @Override
    public Headers getHeader() {
        return Headers.of("Referer", "https://m.tuhaomh.com");
    }

}
