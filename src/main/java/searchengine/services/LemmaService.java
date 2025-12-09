package searchengine.services;

import java.io.IOException;
import java.util.Map;

public interface LemmaService {

    Map<String, Integer> getLemmas(String text) throws IOException;


    Map<String, Integer> getLemmasFromText(String html);
}
