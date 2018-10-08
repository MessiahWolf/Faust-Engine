/**
 * Copyright (c) 2013, Robert Cherry * All rights reserved.
 *
 * This file is part of the Faust Engine.
 *
 * The Faust Engine is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * The Faust Engine is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * the Faust Engine. If not, see <http://www.gnu.org/licenses/>.
 */
package io.resource;

import core.coll.CollisionWrapper;
import core.world.LightSource;
import core.world.WorldResource;
import core.world.WorldAction;
import core.world.Actor;
import core.world.Animation;
import core.world.Backdrop;
import core.world.Illustration;
import core.world.WorldItem;
import core.world.RoomLayer;
import core.world.Room;
import core.world.WorldObject;
import core.world.Tileset;
import core.world.WorldScript;
import io.util.FileUtils;
import java.awt.Point;
import java.awt.Polygon;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Robert A. Cherry
 */
public class ResourceWriter {

    private ResourceWriter() {
        // Static constructor override
    }

    public static File write(ResourceDelegate delegate, WorldResource resource) {

        // Ensure the data structure of the temp folder.
        delegate.performStructureCheck();

        // Grab the class
        final Class closs = resource.getClass();

        // Solving for different types of resources
        if (closs == Actor.class) {

            // Cast a world actor
            final Actor actor = (Actor) resource;

            // Write the world actor
            return writeActor(actor, delegate.getActorDirectory(), actor.getReferenceName());
        } else if (WorldItem.class.isAssignableFrom(closs)) {

            // Cast to a world item
            final WorldItem item = (WorldItem) resource;

            // Write the world item
            return writeItem(item, delegate.getItemDirectory(), item.getReferenceName());
        } else if (closs == Room.class) {

            // Cast to a world cell
            final Room room = (Room) resource;

            // Write the world cell
            return writeRoom(room, delegate.getCellDirectory());
        } else if (Illustration.class.isAssignableFrom(closs)) {

            // Cast to an scenic object
            final Illustration graphic = (Illustration) resource;

            // Write the animated sprite
            return writeGraphic(delegate, graphic);
        } else if (closs == WorldScript.class) {

            // Cast to a world script
            final WorldScript script = (WorldScript) resource;

            // Write the script
            return writeScript(script, delegate.getScriptDirectory(), script.getReferenceName());
        } else if (closs == LightSource.class) {
            
            //
            final LightSource light = (LightSource) resource;
            
            //
            return writeLight(light, delegate.getTemplateDirectory(), light.getReferenceName());
        }

        // Returning nothing otherwise
        return null;
    }

    private static File writeActor(Actor actor, String directory, String referenceName) {

        // Create a new Document to save
        final Document doc = createDocument();

        // Define Root Node to identify it when it is being read
        final Element root = (Element) doc.createElement(validateString(actor.getClass().getSimpleName()));

        // Append attribute node onto the child
        for (Map.Entry<String, Object> map : actor.getAttributeMap().entrySet()) {
            root.setAttribute(map.getKey(), String.valueOf(map.getValue()));
        }

        // Append Item drop list node onto Actor node as a child
        root.appendChild(deriveItemListElement(doc, actor.getItemMap()));

        // Append that node onto the root
        root.appendChild(deriveActionElement(doc, actor.getAnimationMap()));

        // Append Actor node onto the XMLDocument as a child
        doc.appendChild(root);

        // Finally create the XMLDocument
        return streamDefinedExtension(doc, directory, referenceName, ResourceReader.XML_EXTENSION);
    }

    private static File writeItem(WorldItem item, String directory, String referenceName) {

        // Create a new Document to save
        final Document document = createDocument();

        // Define Root Node to identify it when it is being read
        final Element root = (Element) document.createElement(validateString(item.getClass().getSimpleName()));

        // ?Remove x and y
        item.getAttributeMap().remove("x");
        item.getAttributeMap().remove("y");

        // Append attribute node onto the child
        for (Map.Entry<String, Object> map : item.getAttributeMap().entrySet()) {
            root.setAttribute(map.getKey(), String.valueOf(map.getValue()));
        }

        root.appendChild(deriveStatElement(document, item.getStatMap()));

        // Append that node onto the root
        root.appendChild(deriveActionElement(document, item.getAnimationMap()));

        // Append Actor node onto the XMLDocument as a child
        document.appendChild(root);

        // Our output File
        final File file = streamXMLExtension(document, directory, referenceName);

        // Finally create the XMLDocument
        return file;
    }

    private static File writeLight(LightSource light, String directory, String referenceName) {

        // Create a new Document to save
        final Document document = createDocument();

        // Define Root Node to identify it when it is being read
        final Element root = (Element) document.createElement(validateString(light.getClass().getSimpleName()));

        // Append attribute node onto the child
        for (Map.Entry<String, Object> map : light.getAttributeMap().entrySet()) {
            root.setAttribute(map.getKey(), String.valueOf(map.getValue()));
        }

        // Append Actor node onto the XMLDocument as a child
        document.appendChild(root);

        // Our output File
        final File file = streamXMLExtension(document, directory, referenceName);

        // Finally create the XMLDocument
        return file;
    }

    private static File writeRoom(Room room, String directory) {

        // Create a new Document to save
        final Document doc = createDocument();

        // Define the fMap Element of this XMLDocument
        final Element root = (Element) doc.createElement(validateString("Room"));

        // First set all the attributes of the fMap
        for (Map.Entry<String, Object> set : room.getAttributeMap().entrySet()) {
            root.setAttribute(set.getKey(), String.valueOf(set.getValue()));
        }

        // Lay the background
        root.appendChild(deriveBackgroundElement(doc, room.getBackgroundList()));

        // Grab all the Layers in the fMap
        root.appendChild(deriveLayerElement(doc, room.getLayerList()));

        // Then Append fMap node to the completed XMLDocument
        doc.appendChild(root);

        // Finally call Stream to create the file
        return streamDefinedExtension(doc, directory, room.getReferenceName(), ResourceReader.XML_EXTENSION);
    }

    private static File writeScript(WorldScript script, String directory, String referenceName) {

        // Create an empty Document
        final Document document = createDocument();

        //
        final Element root = (Element) document.createElement(validateString("Script"));

        // Save all the attributes of the FWorld Wrapper class for Box2D's world.
        for (Map.Entry<String, Object> map : script.getAttributeMap().entrySet()) {
            root.setAttribute(map.getKey(), String.valueOf(map.getValue()));
        }

        // Append that to the document
        document.appendChild(root);

        // Finally call stream to create the xml document
        return streamDefinedExtension(document, directory, referenceName, ResourceReader.XML_EXTENSION);
    }

    private static File writeGraphic(ResourceDelegate delegate, Illustration object) {

        // Output File
        File graphicFile = null;

        // Grab some things from the graphic
        final Class closs = object.getClass();
        final HashMap<String, Object> map = object.getAttributeMap();
        final String referenceName = object.getReferenceName();

        // Locate the temp folder
        String tempFolder = delegate.getCacheDirectory();

        // Switch through-out
        if (object instanceof Backdrop) {
            tempFolder = delegate.getBackdropDirectory();
        } else if (object instanceof Tileset) {
            tempFolder = delegate.getTilesetDirectory();
        } else if (object instanceof Animation) {
            tempFolder = delegate.getAnimationDirectory();
        }

        // Write the properties to cache
        if (new File(tempFolder).exists()) {

            // Write the graphic file
            graphicFile = writeGraphic(object, map, tempFolder, referenceName);
        }

        // Creates an archive of all the chosen files in the output directory
        return graphicFile;
    }

    private static File writeGraphic(Illustration closs, HashMap<String, Object> map, String directory, String referenceName) {

        // Create a new Document to save
        final Document document = createDocument();

        // Setting some attributes of the fMap
        final Element element = (Element) document.createElement(validateString(closs.getClass().getSimpleName()));

        // Set these attributes
        for (Map.Entry<String, Object> set : map.entrySet()) {

            //
            final String property = set.getKey();
            final String value = String.valueOf(set.getValue());

            // Set the attribute
            element.setAttribute(property, value);
        }

        // Append the polygon node.
        if (closs instanceof Animation) {

            final Animation animation = (Animation) closs;
            // System.out.println("Attemptiong to append to animation.");

            //
            if (animation.getWrapper() != null) {
                element.appendChild(deriveCollisionListElement(document, animation.getWrapper()));
            }
        }

        // Append to Document
        document.appendChild(element);

        //
        final File file = streamXMLExtension(document, directory, referenceName);

        // Call Stream to
        return file;
    }

    public static Element deriveStatElement(Document document, HashMap<String, Object> map) {

        // Create the new content element
        final Element stat = document.createElement(validateString("Stats"));

        // Save its attributes as well
        for (Map.Entry<String, Object> set : map.entrySet()) {
            stat.setAttribute(set.getKey(), String.valueOf(set.getValue()));
        }

        // Return our completed element
        return stat;
    }

    private static Element deriveActionElement(Document document, HashMap<WorldAction, Animation> map) {

        // Our 
        final Element actions = (Element) document.createElement(validateString("Actions"));

        // Iterate over collection
        for (Map.Entry<WorldAction, Animation> set : map.entrySet()) {

            // Grab the current action from the action map
            final WorldAction action = set.getKey();

            // Grab the corresponding animation from the action map
            final Animation animation = set.getValue();

            // Our element
            final Element element = (Element) document.createElement(validateString("Action"));

            // If there is no animation; for now skip it
            if (animation == null) {
                continue;
            }

            // Store the name of the action and information to trace the animation
            element.setAttribute("action", action.name());
            element.setAttribute("animationPackageId", animation.getPackageID());
            element.setAttribute("animationEditorId", animation.getReferenceID());

            // Give to parent
            actions.appendChild(element);
        }

        // Return our completed action list
        return actions;
    }

    private static Element deriveCollisionListElement(Document document, CollisionWrapper wrapper) {

        //
        final Element collisionElement = (Element) document.createElement(validateString("Collision"));

        // The nested array
        final Object[][] data = wrapper.getData();

        //
        for (int i = 0; i < data.length; i++) {

            // Data[animation index][0] - The polygons and their data
            final Polygon[] polygons = (Polygon[]) data[i][0];
            final HashMap<Point, Integer> map = (HashMap<Point, Integer>) data[i][1];
            final String[] regions = (String[]) data[i][2];
            final double[] multipliers = (double[]) data[i][3];
            // System.out.println("Writer Precision: " + data[i][4]);
            final int precision = (int) data[i][4];
            int j = -1;

            //
            final Element indexElement = (Element) document.createElement(String.valueOf("Index"));
            indexElement.setAttribute("n", String.valueOf(i));
            indexElement.setAttribute("pointcount", String.valueOf(map == null ? 0 : map.size()));
            indexElement.setAttribute("polycount", String.valueOf(polygons == null ? 0 : polygons.length));
            indexElement.setAttribute("precision", String.valueOf(precision));

            //
            if (map != null && polygons != null) {

                // Appending the points and their datum
                for (Map.Entry<Point, Integer> m : map.entrySet()) {

                    //
                    final Element pointElement = (Element) document.createElement("Point");
                    final Point p = m.getKey();
                    pointElement.setAttribute("x", String.valueOf(p.x));
                    pointElement.setAttribute("y", String.valueOf(p.y));
                    pointElement.setAttribute("type", String.valueOf(m.getValue()));

                    // Add the point to the index element
                    indexElement.appendChild(pointElement);
                }

                // Appending the polygons after
                for (Polygon polygon : polygons) {

                    // Used to keep map entries lined up with array indexes.
                    j++;

                    // Finally
                    final Element polygonElement = (Element) document.createElement(String.valueOf("Polygon"));

                    // Set the name of the polygon region and the multiplier
                    polygonElement.setAttribute("name", regions[j]);
                    polygonElement.setAttribute("mult", String.valueOf(multipliers[j]));

                    // Adding all npoints.
                    for (int k = 0; k < polygon.npoints; k++) {

                        //
                        final Element coord = (Element) document.createElement(String.valueOf("Coord"));
                        coord.setAttribute("x", String.valueOf(polygon.xpoints[k]));
                        coord.setAttribute("y", String.valueOf(polygon.ypoints[k]));

                        // Append to polygon node
                        polygonElement.appendChild(coord);
                    }

                    // Append the polygon to the index.
                    indexElement.appendChild(polygonElement);
                }
            }

            // Append that to the main collision block
            collisionElement.appendChild(indexElement);
        }

        //
        return collisionElement;
    }

    private static Element deriveItemListElement(Document document, HashMap<WorldItem, Double> map) {

        // Create a node that stores this actor's chance to drop items
        final Element drops = (Element) document.createElement(validateString("Drops"));

        // Write out Drop List
        for (Map.Entry<WorldItem, Double> set : map.entrySet()) {

            // Quick reference
            final WorldItem item = set.getKey();

            // Grab attributes of the item
            final String referenceID = item.getReferenceName();
            final String packageID = item.getPackageID();
            final String displayName = item.getDisplayName();

            // Grab the chance to drop the item; not fully integrated
            final String chance = String.valueOf(set.getValue());

            // Create a node for the item
            final Element element = (Element) document.createElement(validateString(displayName));

            // Set as an attribute of the item
            element.setAttribute("chance", chance);
            element.setAttribute("referenceID", referenceID);
            element.setAttribute("packageID", packageID);

            // Append as a node
            drops.appendChild(element);
        }

        // Return our completed drop list
        return drops;
    }

    public static Element deriveBackgroundElement(Document document, ArrayList<Backdrop> list) {

        // Create the new scenery element
        final Element scenery = document.createElement(validateString("scenery"));

        // Iterate over the collection of backgrounds
        for (int i = 0; i < list.size(); i++) {

            // Our current background
            final Backdrop current = list.get(i);

            // Create an element for each background
            final Element element = document.createElement(validateString("Background"));

            // Save its attributes along with it
            for (Map.Entry<String, Object> map : current.getAttributeMap().entrySet()) {
                element.setAttribute(map.getKey(), String.valueOf(map.getValue()));
            }

            // Add to the scenery
            scenery.appendChild(element);
        }

        // Return our completed element
        return scenery;
    }

    public static Element deriveWorldCellElement(Document document, ArrayList<Room> list) {

        // Create the new content element
        final Element content = document.createElement(validateString("content"));

        // Iterate over the collection of maps
        for (int i = 0; i < list.size(); i++) {

            // Grab the current map
            final Room worldCell = list.get(i);

            // Our wrapped map
            final Element element = document.createElement(validateString("WorldCell"));

            // Save its attributes as well
            for (Map.Entry<String, Object> set : worldCell.getAttributeMap().entrySet()) {
                element.setAttribute(set.getKey(), String.valueOf(set.getValue()));
            }

            // Append as well its SHA1 CheckSum
            element.setAttribute("cellCheckSum", worldCell.getSHA1CheckSum());

            // Append to the world parent
            content.appendChild(element);
        }

        // Return our completed element
        return content;
    }

    public static Element deriveLayerElement(Document document, ArrayList<RoomLayer> list) {

        // Create a new content element;
        final Element content = document.createElement(validateString("content"));

        // Then Store Layer(s)
        for (int i = 0; i < list.size(); i++) {

            // Current Layer in fMap
            final RoomLayer layer = list.get(i);

            // Grab all the fMap Objects in the Layer
            final ArrayList<WorldObject> objectList = layer.getInhabitants();
            final ArrayList<LightSource> lightList = layer.getLightList();

            // Create a new Node in the fMap Tree for this Layer
            final Element elementLayer = document.createElement(validateString("Layer"));

            // First set all the attributes of the Layer
            for (Map.Entry<String, Object> set : layer.getAttributeMap().entrySet()) {
                elementLayer.setAttribute(set.getKey(), String.valueOf(set.getValue()));
            }

            // Then Store worldObject(s)
            for (int j = 0; j < objectList.size(); j++) {

                // Element for storing the worldObject
                Element elementObject;

                // Current worldObject in Layer
                final WorldObject object = objectList.get(j);

                //
                String closs = object.getClass().getSimpleName();

                //
                elementObject = (Element) document.createElement(validateString(closs));

                // Place attribute list
                for (Map.Entry<String, Object> set : object.getAttributeMap().entrySet()) {
                    elementObject.setAttribute(set.getKey(), String.valueOf(set.getValue()));
                }

                // Make sure it includes its location
                elementObject.setAttribute("x", String.valueOf(object.getX()));
                elementObject.setAttribute("y", String.valueOf(object.getY()));

                // Append worldObject node onto the Layer node as child
                elementLayer.appendChild(elementObject);
            }

            // Then Store worldObject(s)
            for (int j = 0; j < lightList.size(); j++) {

                // Element for storing the worldObject
                Element elementObject;

                // Current worldObject in Layer
                final LightSource source = lightList.get(j);

                //
                String closs = source.getClass().getSimpleName();

                //
                elementObject = (Element) document.createElement(validateString(closs));

                // Place attribute list
                for (Map.Entry<String, Object> set : source.getAttributeMap().entrySet()) {
                    elementObject.setAttribute(set.getKey(), String.valueOf(set.getValue()));
                }

                // Make sure it includes its location
                elementObject.setAttribute("x", String.valueOf(source.getX()));
                elementObject.setAttribute("y", String.valueOf(source.getY()));
                elementObject.setAttribute("angle", String.valueOf(source.getAngle()));

                // Append worldObject node onto the Layer node as child
                elementLayer.appendChild(elementObject);
            }

            // Append the entire node with all the content onto this node
            content.appendChild(elementLayer);
        }

        // Return the list of layers and all its fMap objects
        return content;
    }

    public static File streamDefinedExtension(Document document, String directory, String referenceName, String extension) {

        // Addon the xml extension for recognization; won't addObject if it doesnt need to
        referenceName = FileUtils.extend(referenceName, extension);

        // Create the file on the disk
        final File file = new File(directory, referenceName);

        try {

            // Write the Formatted Data into an XML Document
            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            final Transformer transformer = transformerFactory.newTransformer();

            // Optional -- Just makes sure the transformer properly indents each node.
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            final DOMSource source = new DOMSource(document);

            // Write over File
            final StreamResult streamResult = new StreamResult(file);

            // Normalize again
            document.normalizeDocument();

            // Apply the Transform
            transformer.transform(source, streamResult);
        } catch (TransformerException te) {
            JOptionPane.showMessageDialog(null, "Failed to save " + file.getAbsolutePath() + "\n" + te);

            // Show me the error
            throw new RuntimeException(te);
        } finally {
            // Close this file stream
            FileUtils.close(file);
        }

        // Return the created XML File
        return file;
    }

    public static File streamXMLExtension(Document document, String directory, String referenceName) {

        // Addon the xml extension for recognization; if won't addObject it if it doesn't need it
        referenceName = FileUtils.extend(referenceName, ResourceReader.XML_EXTENSION);

        // Create the file on the disk
        final File file = new File(directory, referenceName);

        try {

            // Write the Formatted Data into an XML Document
            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            final Transformer transformer = transformerFactory.newTransformer();

            // Optional -- Just makes sure the transformer properly indents each node.
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            final DOMSource source = new DOMSource(document);

            // Write over File
            final StreamResult streamResult = new StreamResult(file);

            // Normalize again
            document.normalizeDocument();

            // Apply the Transform
            transformer.transform(source, streamResult);
        } catch (TransformerException te) {
            JOptionPane.showMessageDialog(null, "Failed to save " + file.getAbsolutePath());

            // Show me the error
            throw new RuntimeException(te);
        }

        // Return the created XML File
        return file;
    }

    private static String validateString(String string) {

        // Our String builder
        final StringBuilder builder = new StringBuilder();

        // The String must exist
        if (string == null) {
            return null;
        }

        // Iterate over the input string
        for (int i = 0; i < string.length(); i++) {

            // The current character in the name
            final char current = string.charAt(i);

            // Disallow the use of Spaces
            if (current != ' ') {

                // Append as long as it is not a space
                builder.append(current);
            }
        }

        return builder.toString();
    }

    public static Document createDocument() {

        // Our output document
        Document document = null;

        // Dunno yet
        final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;

        //
        try {

            // Attempt to create a new document builder
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ResourceWriter.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Document Builder must exist
        if (docBuilder != null) {

            // Here is our new document
            document = docBuilder.newDocument();
        }

        // Return the document created
        return document;
    }
}
