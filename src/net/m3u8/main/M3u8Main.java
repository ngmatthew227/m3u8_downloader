package net.m3u8.main;

import net.m3u8.download.M3u8DownloadFactory;
import net.m3u8.listener.DownloadListener;
import net.m3u8.utils.Constant;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author liyaling
 * @email ts_liyaling@qq.com
 * @date 2019/12/14 16:02
 */

public class M3u8Main {

    private static final String M3U8URL = "https://www.141mov.com/vod-read-id-28803.html";
    private static final String Base_URL = "https://www.141mov.com";

    public static void main(String[] args) throws IOException {
        Document document = Jsoup.connect(M3U8URL).get();
        List<String> fileNameList = new ArrayList<>();
        List<String> m3u8List = new ArrayList<>();
        Map<String, String> m3u8Map = new HashMap<>();
        Elements links = document.select(".detail-play-list").select("a");
        for (Element link : links) {
            String linkHref = Base_URL + link.attr("href");
            Document eqHtml;
            try {
                eqHtml = Jsoup.connect(linkHref).get();

            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                continue;
            }
            String m3u8Tag = eqHtml.select("#cms_player").select("script").get(0).dataNodes().get(0).getWholeData();
            m3u8Tag = m3u8Tag.replaceAll("\\\\", "");
            Pattern p = Pattern.compile("\"url\":\"(.*?)\",");
            Matcher m = p.matcher(m3u8Tag);
            String m3u8Url = null;
            if (m.find()) {
                m3u8Url = m.group(1); // this variable should contain the link URL
            }

            if (m3u8Url != null) {
                fileNameList.add(link.text());
                m3u8List.add(m3u8Url);
                m3u8Map.put(link.text(), m3u8Url);
            }
        }
        System.out.println(m3u8Map);
        downloadList(fileNameList, m3u8Map, "C://find_qin", m3u8List);

    }

    public static void downloadList(List<String> fileNameList, Map<String, String> m3u8Map,
                                    String basePath, List<String> m3u8List) {

        String fileName = fileNameList.get(0);
        String m3u8Url = m3u8Map.get(fileName);
        System.out.println(basePath);

        M3u8DownloadFactory.M3u8Download m3u8Download = M3u8DownloadFactory.getInstance(m3u8Url, m3u8List);
        //??????????????????
        m3u8Download.setDir(basePath);
        //??????????????????
        m3u8Download.setFileName(fileName);
        //???????????????
        m3u8Download.setThreadCount(500);
        //??????????????????
        m3u8Download.setRetryCount(100);
        //?????????????????????????????????????????????
        m3u8Download.setTimeoutMillisecond(10000L);
        /*
        ??????????????????
        ????????????NONE INFO DEBUG ERROR
        */
        m3u8Download.setLogLevel(Constant.ERROR);
        //??????????????????????????????????????????
        m3u8Download.setInterval(500L);
        //?????????????????????
      /*  Map<String, Object> headersMap = new HashMap<>();
        headersMap.put("Content-Type", "text/html;charset=utf-8");
        m3u8Download.addRequestHeaderMap(headersMap);*/
        //???????????????
        m3u8Download.addListener(new DownloadListener() {
            @Override
            public void start() {
                System.out.println("???????????????");
            }

            @Override
            public void process(String downloadUrl, int finished, int sum, float percent) {
//                System.out.println("???????????????" + downloadUrl + "\t?????????" + finished + "???\t??????" + sum + "???\t?????????" + percent + "%");
            }

            @Override
            public void speed(String speedPerSecond) {
//                System.out.println("???????????????" + speedPerSecond);
            }

            @Override
            public void end() {
                System.out.println("????????????");
            }
        });
        //????????????
        m3u8Download.start();
    }
}
