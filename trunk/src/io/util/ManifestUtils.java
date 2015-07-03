/**
    Copyright (c) 2013, Robert Cherry    
    
    All rights reserved.
  
    This file is part of the Faust Engine.

    The Faust Engine is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    The Faust Engine is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with the Faust Engine.  If not, see <http://www.gnu.org/licenses/>.
*/
package io.util;

import io.resource.DataRef;
import io.resource.ResourceMolder;
import io.resource.ResourceWriter;
import io.resource.DataPackage;
import io.resource.ResourceReader;
import java.io.File;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author Robert A. Cherry
 */
public class ManifestUtils {

    public static File writeManifest(String outputFolder, DataPackage dataPackage) {

        // Create the file
        File file = new File("manifest");

        // Create a document from that
        final Document doc = ResourceWriter.createDocument();

        // Grab the root node
        final Element rootNode = doc.createElement("Package");
        rootNode.setAttribute("author", dataPackage.getAuthor());
        rootNode.setAttribute("email", dataPackage.getEmail());
        rootNode.setAttribute("version", dataPackage.getVersion());
        rootNode.setAttribute("referenceID", dataPackage.getReferenceId());
        rootNode.setAttribute("displayName", dataPackage.getDisplayName());
        rootNode.setAttribute("referenceName", dataPackage.getReferenceName());

        // Grab the citaitons
        final DataRef[] references = dataPackage.getCitations();

        // Write on that document
        for (int i = 0; i < references.length; i++) {

            // Grab the current resource
            final DataRef reference = references[i];

            // Write that reference down
            final Element element = doc.createElement(reference.getResource().getClass().getSimpleName());
            element.setAttribute("referenceID", reference.getEditorId());
            element.setAttribute("displayName", reference.getDisplayName());
            element.setAttribute("referenceName", reference.getEditorName());

            // Add to root node
            rootNode.appendChild(element);
        }

        // Doc adopt root node
        doc.appendChild(rootNode);

        // Write the file using a custom extension (XML)
        file = ResourceWriter.streamDefinedExtension(doc, outputFolder, file.getName(), ResourceReader.XML_EXTENSION);

        // Stream this out
        return file;
    }

    public static String extractElement(File manifest, String elementName) {

        // Output Object
        String element = null;

        // Ask
        if (manifest != null) {

            // Begin to read the file
            final Document doc = ResourceMolder.moldDocument(manifest);

            // Grab the root node
            final Node node = doc.getDocumentElement();

            // Grab the editor Id from the node's attributes
            element = node.getAttributes().getNamedItem(elementName).getNodeValue();
        }

        // Return the found id
        return element;
    }
}
