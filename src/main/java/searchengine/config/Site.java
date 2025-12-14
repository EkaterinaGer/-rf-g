package searchengine.config;

import lombok.Getter;
import lombok.Setter;

import java.net.URL;

@Setter
@Getter
public class Site {
    private URL url;
    private String name;
    
    // Явные геттеры для совместимости
    public URL getUrl() {
        return url;
    }
    
    public String getName() {
        return name;
    }
    
    public void setUrl(URL url) {
        this.url = url;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}