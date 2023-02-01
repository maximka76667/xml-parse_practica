package main;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Noticia {

	private String title;
	private String description;
	private String link;
	private String pubDate;
	// <title>
	// <description>
	// <link>
	// <pubDate>

	// Formateadores para fechas declarados como static para ahorrar memoria
	public static SimpleDateFormat dateFormatter1 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
	public static SimpleDateFormat dateFormatter2 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm zzz", Locale.US);
	public static SimpleDateFormat stringFormatter = new SimpleDateFormat("EEEE, dd 'de' MMMM 'del' yyyy, HH:mm",
			Locale.getDefault());

	public Noticia(Element itemXML) throws Exception {
		this.title = itemXML.getElementsByTagName("title").item(0).getTextContent();
		this.description = itemXML.getElementsByTagName("description").item(0).getTextContent();
		this.link = itemXML.getElementsByTagName("link").item(0).getTextContent();
		this.pubDate = itemXML.getElementsByTagName("pubDate").item(0).getTextContent();
	}

	// Convierte el String strFechaPub a Calendar
	public Calendar getCalendarPubDate() {
		Calendar c = Calendar.getInstance();
		try {
			try {
				c.setTime(dateFormatter1.parse(this.pubDate));
			} catch (Exception e) {
				c.setTime(dateFormatter2.parse(this.pubDate));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}

	// Devuelve strFechaPub en formato: miércoles, 10 de febrero del 2013, 21:16
	public String getStringPubDate() {
		try {
			return stringFormatter.format(getCalendarPubDate().getTime());
		} catch (Exception e) {
			return "";
		}
	}

	public String getHtmlNoticia() {
		String template = "<div>" + "<h2><a href=$link>$title</a></h2>" + "<p>$description</p>" + "<p>$pubDate</p>"
				+ "</div>";

		template = template.replace("$title", this.title).replace("$description", this.description)
				.replace("$link", this.link).replace("$pubDate", this.pubDate);

		return template;

	}

	public Element addItem(Document xmlDomTree) {
		Element itemElement = xmlDomTree.createElement("item");

		addElement(xmlDomTree, itemElement, "title", this.title);
		addElement(xmlDomTree, itemElement, "description", this.description);
		addElement(xmlDomTree, itemElement, "link", this.link);
		addElement(xmlDomTree, itemElement, "pubDate", this.pubDate);

		return itemElement;
	}

	public void addElement(Document xmlDomTree, Element item, String tag, String content) {
		Element element = xmlDomTree.createElement(tag);
		element.setTextContent(content);
		item.appendChild(element);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	public int compareTo(Calendar otherDate) {
		if (getCalendarPubDate().after(otherDate)) {
			return -1;
		}
		if (getCalendarPubDate().before(otherDate)) {
			return 1;
		}
		return 0;
	}

}
