package org.voidptr.bionicgopher.model;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by errant on 11/14/17.
 */

public class GopherUri {
    private String protocol;
    private String host;
    private Integer port;
    private String search;
    private List<String> pathElements;

    public static GopherUri copy(GopherUri old){
        GopherUri newUri = new GopherUri();
        newUri.setProtocol(old.getProtocol());
        newUri.setHost(old.getHost());
        newUri.setPort(old.getPort());
        newUri.setPathElements(new ArrayList<>(old.getPathElements()));
        return newUri;
    }

    public GopherUri() {
        pathElements = new ArrayList<>();
    }

    public GopherUri(String nProtocol, String nHost, Integer nPort) {
        protocol = nProtocol;
        host = nHost;
        port = nPort;
        pathElements = new ArrayList<>();
    }

    public GopherUri(String uri){
        pathElements = new ArrayList<>();
        parseFromString(uri);
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public List<String> getPathElements() {
        return pathElements;
    }

    public void setPathElements(List<String> pathElements) {
        this.pathElements = new ArrayList<>();
        for(String element : pathElements) {
            if(element != null && !element.isEmpty()){
                String cleanElement = element;
                if(cleanElement.startsWith("/")){
                    cleanElement = cleanElement.substring(1);
                }
                if(cleanElement.endsWith("/")){
                    cleanElement = cleanElement.substring(0, cleanElement.length()-2);
                }
                this.pathElements.add(cleanElement);
            }
        }
    }

    private void parseFromString(String raw){
        String remaining = raw;
        if(raw.contains("://")) {
            //protocol specified, use it
            protocol = raw.substring(0, raw.indexOf(':'));
            remaining = raw.substring(raw.indexOf(':')+3);
        }

        if(remaining.contains(":")) {
            //We have a port
            int portend = 0;
            host = remaining.substring(0, remaining.indexOf(':'));
            if (remaining.contains("/")) {
                portend = remaining.indexOf('/');
            } else {
                portend = remaining.length();
            }
            port = Integer.valueOf(remaining.substring(remaining.indexOf(':')+1, portend));
            remaining = remaining.substring(portend);
        }else{
            if(remaining.contains("/")) {
                host = remaining.substring(0, remaining.indexOf('/'));
                remaining = remaining.substring(remaining.indexOf('/'));
            }else{
                host = remaining;
                remaining = "";
            }

        }

        if(remaining.contains("/")) {
            String[] pathEle = remaining.split("/");
            for(String pathe : pathEle) {
                if(!pathe.isEmpty()){
                    pathElements.add(pathe);
                }
            }
        }
    }

    public String getPath(){
        StringBuilder output = new StringBuilder();

        output.append("/");

        Iterator ele = pathElements.iterator();
        while(ele.hasNext()) {
            output.append(ele.next());
            if(ele.hasNext()){
                output.append('/');
            }
        }

        return output.toString();
    }

    public String toString(){
        StringBuilder builder = new StringBuilder();
        if(protocol != null && !protocol.isEmpty()) {
            builder.append(protocol);
            builder.append("://");
        }
        builder.append(host);
        if(port != null && port != 0) {
            builder.append(":");
            builder.append(port.toString());
        }
        builder.append("/");
        if(!pathElements.isEmpty()) {
            Iterator i = pathElements.iterator();
            while(i.hasNext()){
                builder.append(i.next());
                if(i.hasNext()){
                    builder.append("/");
                }
            }
        }
        if(search != null && search.isEmpty()) {
            builder.append("?");
            builder.append(search);
        }
        return builder.toString();
    }

    public Uri toUri() {
        return Uri.parse(toString());
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
