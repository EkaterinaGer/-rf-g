package searchengine.services;

import searchengine.model.SiteTable;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public interface ApiService {


    void startIndexing(AtomicBoolean indexingProcessing);


    void stopIndexing();

    List<SiteTable> getAllSitesStatus();

    boolean isIndexing();
}
