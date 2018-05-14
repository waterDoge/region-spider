package com.ata.region.task;

import com.ata.region.entity.Region;
import com.ata.region.repository.RegionRepository;
import com.ata.region.spider.Parser;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class Task {
    RegionRepository repository;

    public Task(RegionRepository repository) {
        this.repository = repository;
    }

    @Scheduled(fixedDelay = 1000000000L)
    public void run() throws IOException, InterruptedException {
        Region region = new Region();
        region.setId(0L);
        region.setName("中国");
        region.setLevel(0);
        region.setParentId(-1L);
        final List<Region> regions = Parser.PROVINCE.parse(Parser.COUNTY, "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2016/index.html", 0L);
        region.setChildren(regions);
        repository.save(region);
    }
}
