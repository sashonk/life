package ru.asocial.loader;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility class for loading templates from "http://beluch.ru"
 *
 */
public class Loader {
    public static void main(String[] argc) throws Exception {
        String urlbase = "http://beluch.ru";
        File dir = new File("C:\\WORK\\life_figures");
        dir.mkdirs();
        String letters = "1,a,b,v,g,d,e,zh,z,i,k,l,m,n,o,p,r,s,t,u,f,h,c,ch,sh,sc,ae,yu,ya";

        System.out.println("BEGIN");
        List<String> allFigures = new LinkedList<>();
        for (String letter: letters.split(",")) {
            String url = String.format("%s/life/lifelex/lexr_%s.htm", urlbase, letter);
            Document doc = Jsoup.connect(url).get();
            Elements aa = doc.select("a[name]");
            Iterator<Element> iterator = aa.iterator();
            while (iterator.hasNext()) {
                Element a = iterator.next();
                if (a.hasText() && a.text().trim().equals(":")){
                    String figureName = a.attr("name");
                    System.out.println(figureName);
                    Element p = a.parent();
                    if (!p.tagName().equalsIgnoreCase("p")) {
                        System.err.println("Ooups, wrong parent tag! Expected <p>, but was " + p.tagName());
                        continue;
                    }
                    Node nd = p.nextSibling();
                    while (true) {
                        if (nd == null) {
                            break;
                        }
                        if (nd instanceof Element) {
                            Element el = (Element)nd;
                            if (el.tagName().equalsIgnoreCase("pre")) {
                                String text = el.text();
                                System.out.println(text);
                                try (FileOutputStream fos = new FileOutputStream(new File(dir, figureName))) {
                                    IOUtils.write(text, fos);
                                    allFigures.add(figureName);
                                }
                                catch (IOException io) {
                                    io.printStackTrace();
                                }
                            }
                            break;
                        }
                        nd = nd.nextSibling();
                    }
                }
            }
        }

        try (FileOutputStream fos = new FileOutputStream(new File(dir, "templates.txt"))) {
            IOUtils.writeLines(allFigures, null,  fos);
        }
        catch (IOException io) {
            io.printStackTrace();
        }

        System.out.println("END");
    }
}
