package main;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

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

			String html = "<!DOCTYPE HTML>" + "<html lang=\"es\">" + "<head>" + "<meta charset=\"utf-8\">"
					+ "<title>Mis Noticias</title>" + "</head>" + "<body>";

			Document xmlDomTree = UtilidadesXML.generararbolDOMvacio();

			Element rssElement = xmlDomTree.createElement("rss");
			rssElement.setAttribute("version", "2.0");
			xmlDomTree.appendChild(rssElement);

			Element channelElement = xmlDomTree.createElement("channel");

			Element channelTitle = xmlDomTree.createElement("title");
			channelTitle.setTextContent("El nombre de nuestro canal/feed/fuente");
			channelElement.appendChild(channelTitle);

			Element channelDescription = xmlDomTree.createElement("description");
			channelDescription.setTextContent("Descripción del contenido a ofrecer a los usuarios");
			channelElement.appendChild(channelDescription);

			Element channelLink = xmlDomTree.createElement("link");
			channelLink.setTextContent("URL del sitio Web");
			channelElement.appendChild(channelLink);

			rssElement.appendChild(channelElement);

			for (Noticia noticia : noticias) {
				html += noticia.getHtmlNoticia();
				channelElement.appendChild(noticia.addItem(xmlDomTree));
			}

			html += "</body>" + "</html>";

			BufferedWriter writer = Files.newBufferedWriter(Paths.get("index.html"), Charset.forName("utf-8"));
			writer.write(html);
			writer.close();

			System.out.println("El fichero 'index.html' se ha generado correctamente");

			// Transformaremos el árbol DOM en un String y lo guardamos en un fichero
			// TransformerFactory nos permitirá crear el transformador
			TransformerFactory transFact = TransformerFactory.newInstance();
			transFact.setAttribute("indent-number", 4);
			// Sangría de 4 espacios para cada nivel de anidamiento
			Transformer trans = transFact.newTransformer();
			/*
			 * Transformará el árbol DOM dado como un objeto DOMSource(1) en un String por
			 * medio de un objeto StreamResult(2)
			 */
			trans.setOutputProperty(OutputKeys.INDENT, "yes"); // Habilitados las sangrías
			/*
			 * 1. Preparamos el árbol DOM y generamos el objeto DOMSource.
			 */
			xmlDomTree.normalize();
			xmlDomTree.setXmlStandalone(true);
			// Indicamos que el documento XML no depende de otros.
			DOMSource domSource = new DOMSource(xmlDomTree); // Creamos el objeto DOMSource
			// 2. Creamos el objeto StringWriter (String modificable) donde escribirá el
			// objeto StreamResult
			StringWriter salidaStrings = new StringWriter();
			StreamResult sr = new StreamResult(salidaStrings);
			trans.transform(domSource, sr); // Generamos la transformación a String
			// Mostramos el documento XML por pantalla

			/*
			 * Guardamos ahora el string en un fichero, para ello creamos un fichero de
			 * salida con codificación UTF-8
			 */

			Path rutaFicheroSalida = Paths.get("index.xml");
			if (!Files.exists(rutaFicheroSalida)) {
				rutaFicheroSalida = Files.createFile(Paths.get("index.xml"));
			}

			Charset cs = Charset.forName("utf-8");
			BufferedWriter ficheroSalida = Files.newBufferedWriter(rutaFicheroSalida, cs);
			// Escribimos el documento XML
			ficheroSalida.write(salidaStrings.toString());
			ficheroSalida.close();
			System.out.println("El fichero 'index.xml' se ha generado correctamente");

			System.out.println("Done");

		} catch (

		Exception e) {
			e.printStackTrace();
		}
	}

}
