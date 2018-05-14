package com.ata.region;

import com.ata.region.entity.Region;
import com.ata.region.spider.Parser;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class ParserTests {
    @Test
    public void test() throws InterruptedException {
        try {
            final List<Region> regions = Parser.CITY.parse(Parser.COUNTY, "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2016/65.html", 0L);
            System.out.println(regions);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
