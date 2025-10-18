package de.jostnet.tpex.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StatistikService {

    @Autowired
    private ToolService toolService;

    private long startunzipTime;

    private long unzipsize = 0;

    private long unzipcount = 0;

    public void startunzip() {
        startunzipTime = System.currentTimeMillis();
    }

    public void endunzip() {
        long endTime = System.currentTimeMillis();
        long duration = endTime - startunzipTime;
        log.info("Dauer des Entpackens: {} ms", toolService.formatLongLocalized(duration));
        log.info("Gesamtgröße entpackter Dateien: {}", toolService.formatBytes(unzipsize));
        log.info("Anzahl entpackter Dateien: {}", toolService.formatLongLocalized(unzipcount));
    }

    public void addUnzipSize(long size) {
        unzipsize += size;
        unzipcount++;
    }
}
