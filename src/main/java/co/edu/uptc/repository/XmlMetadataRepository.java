package co.edu.uptc.repository;

import co.edu.uptc.dto.DocumentDTO;
import co.edu.uptc.dto.DocumentMetadataDTO;
import co.edu.uptc.exception.RepositoryException;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class XmlMetadataRepository {

    private final File xmlFile;

    public XmlMetadataRepository(String filePath) {
        this.xmlFile = new File(filePath);
        ensureDirectoryExists();
    }

    private void ensureDirectoryExists() {
        File parent = xmlFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }

    public void saveAll(List<DocumentDTO> documents) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.newDocument();

            Element rootElement = doc.createElement("documents");
            doc.appendChild(rootElement);

            for (DocumentDTO d : documents) {
                Element docElem = doc.createElement("document");
                docElem.setAttribute("id", d.getId());

                Element title = doc.createElement("title");
                title.setTextContent(d.getTitle());
                docElem.appendChild(title);

                Element path = doc.createElement("path");
                path.setTextContent(d.getPath());
                docElem.appendChild(path);

                Element totalWords = doc.createElement("totalWords");
                totalWords.setTextContent(String.valueOf(d.getTotalWords()));
                docElem.appendChild(totalWords);

                if (d.getMetadata() != null) {
                    Element meta = doc.createElement("metadata");

                    Element author = doc.createElement("author");
                    author.setTextContent(d.getMetadata().getAuthor());
                    meta.appendChild(author);

                    Element category = doc.createElement("category");
                    category.setTextContent(d.getMetadata().getCategory());
                    meta.appendChild(category);

                    Element creationDate = doc.createElement("creationDate");
                    creationDate.setTextContent(d.getMetadata().getCreationDate());
                    meta.appendChild(creationDate);

                    docElem.appendChild(meta);
                }

                rootElement.appendChild(docElem);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(xmlFile);
            transformer.transform(source, result);

        } catch (Exception e) {
            throw new RepositoryException("Error guardando metadatos XML", e);
        }
    }

    public List<DocumentDTO> loadAll() {
        List<DocumentDTO> list = new ArrayList<>();
        if (!xmlFile.exists()) return list;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("document");
            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) nNode;

                    String id = elem.getAttribute("id");
                    String title = getTagValue("title", elem);
                    String path = getTagValue("path", elem);
                    int totalWords = Integer.parseInt(getTagValue("totalWords", elem));

                    DocumentMetadataDTO metaDTO = null;
                    NodeList metaList = elem.getElementsByTagName("metadata");
                    if (metaList.getLength() > 0) {
                        Element metaElem = (Element) metaList.item(0);
                        metaDTO = new DocumentMetadataDTO(
                            getTagValue("author", metaElem),
                            getTagValue("category", metaElem),
                            getTagValue("creationDate", metaElem)
                        );
                    }

                    list.add(new DocumentDTO(id, title, path, totalWords, metaDTO));
                }
            }
        } catch (Exception e) {
            throw new RepositoryException("Error cargando metadatos XML", e);
        }

        return list;
    }

    private String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList != null && nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            if (node != null) return node.getTextContent();
        }
        return "";
    }
}
