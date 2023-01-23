package main;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Main {

	static ArrayList<Noticia> noticias;

	public static void main(String[] args) {

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setIgnoringComments(true);
		builderFactory.setCoalescing(true);

		String[] webLinks = { "http://ep00.epimg.net/rss/elpais/portada.xml",
				"https://e00-elmundo.uecdn.es/elmundo/rss/portada.xml" };

		noticias = new ArrayList<>();

		try {
			for (String webLink : webLinks) {
				Document dom = UtilidadesXML.gernerararbolDOMURL(webLink);

				NodeList fetchedNoticias = dom.getElementsByTagName("item");
				for (int i = 0; i < fetchedNoticias.getLength(); i++) {
					noticias.add(new Noticia((Element) fetchedNoticias.item(i)));
				}
			}

			noticias.sort((a, b) -> a.compareTo(b.getCalendarPubDate()));

			generateHtml();

			System.out.println("Done");

		} catch (

		Exception e) {
			e.printStackTrace();
		}
	}

	private static void generateHtml() {
		String html = "<!DOCTYPE HTML>" + "<html lang=\"es\">" + "<head>" + "<meta charset=\"utf-8\">"
				+ "<title>Mis Noticias</title>" + "</head>" + "<body>";

		for (Noticia noticia : noticias) {
			html += noticia.getHtmlNoticia();
		}

		html += "</body>" + "</html>";

		try {
			BufferedWriter writer = Files.newBufferedWriter(Paths.get("index.html"), Charset.forName("utf-8"));
			writer.write(html);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
