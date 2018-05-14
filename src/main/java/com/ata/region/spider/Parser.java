package com.ata.region.spider;

import com.ata.region.entity.Region;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.retry.annotation.Retryable;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Parser {
    VILLAGE(".villagetr", false, null), TOWN(".towntr", 4, VILLAGE), COUNTY(".countytr", 3, TOWN), CITY(".citytr", 2, COUNTY), PROVINCE(".provincetr", true, CITY);

    public static List<Region> parse(String url, Parser parser, final Parser deepness, final long parentId) throws IOException, InterruptedException {
        return parser.parse(deepness, url, parentId);
    }

    public static List<Region> parse(String url, Parser parser, final long parentId) throws IOException, InterruptedException {
        final Connection connect = Jsoup.connect(url);
        return parse(url, parser, Parser.VILLAGE, parentId);
    }

    private String elementSelector;
    private int level = 1;
    private String childElementSelector = "td > a";
    private Function<Elements, String> idFunc = elements -> elements.first().text();
    private Function<Elements, String> hrefFunc = elements -> elements.first().absUrl("href");
    private Function<Elements, String> nameFunc = elements -> elements.last().text();
    private Function<Elements, String> typeFunc = elements -> this.hasType ? elements.get(2).text() : "";
    private boolean hasType = false;
    private boolean isProvince = false;
    private Parser next;

    Parser(String elementSelector, int level, Parser next) {
        this.elementSelector = elementSelector;
        this.level = level;
        this.next = next;
    }

    Parser(String elementSelector, boolean isProvince, Parser next) {
        this.elementSelector = elementSelector;
        if (isProvince) {
            this.isProvince = true;
            idFunc = elements -> {
                final String apply = hrefFunc.apply(elements);
                return apply.replace(".html", "").replaceAll("^.+/", "");
            };
            this.next = next;
        } else {
            level = 5;
            hasType = true;
        }
    }

    private boolean hasNext() {
        return next != null;
    }

    private Stream getStream(Document document) {
        return isProvince ? document.select(elementSelector).parallelStream().flatMap(element -> element.select(childElementSelector).parallelStream()) :
                document.select(elementSelector).parallelStream().map(element -> element.select(childElementSelector));
    }

    private List<Region> parseProvince(Document document, final Parser deepness) {
        final Elements elements = document.select(elementSelector);
        return elements.stream().flatMap(element -> element.select(childElementSelector).stream()).map(e -> {
            Region region = new Region();
            region.setLevel(level);
            region.setParentId(0L);
            if (e == null) {
                return null;
            }
            final Long id = Long.valueOf(e.attr("href").replace(".html", ""));
            region.setId(id);
            final String name = e.text();
            region.setName(name);
            System.out.println(String.format("%d -- %s", id, name));

            if (this != deepness && hasNext()) {
                final String url = e.absUrl("href");
                try {
                    region.setChildren(next.parse(deepness, url, id));
                } catch (IOException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            return region;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }


    private List<Region> parse(Document document, final Parser deepness, final long parentId) {
        final Elements elements = document.select(elementSelector);
        return elements.stream().map(element -> element.select(childElementSelector)).map(es -> {
            Region region = new Region();
            region.setLevel(level);
            region.setParentId(parentId);
            if (es == null || es.size() == 0) {
                return null;
            }
            final Long id = Long.valueOf(idFunc.apply(es));
            region.setId(id);
            final String name = nameFunc.apply(es);
            region.setName(name);
            System.out.println(String.format("%d -- %s", id, name));

            if (this != deepness && hasNext()) {
                final String url = hrefFunc.apply(es);
                try {
                    region.setChildren(next.parse(deepness, url, id));
                } catch (IOException | InterruptedException e) {
                    //retry
                    try {
                        region.setChildren(next.parse(deepness, url, id));
                    } catch (IOException | InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            region.setType(typeFunc.apply(es));
            return region;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public List<Region> parse(final Parser deepness, String url, final long parentId) throws IOException, InterruptedException {
        System.out.println(url);
        Thread.sleep(150 + ThreadLocalRandom.current().nextInt(100));
        if (StringUtil.isBlank(url)) {
            return new ArrayList<>();
        }
        final Connection connect = Jsoup.connect(url);
        final Document document = connect.get();
        document.charset(Charset.forName("gbk"));
        return isProvince ? parseProvince(document, deepness) : this.parse(document, deepness, parentId);
    }
}
