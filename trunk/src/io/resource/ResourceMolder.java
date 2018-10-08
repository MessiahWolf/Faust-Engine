/**
 * Copyright (c) 2013, Robert Cherry * All rights reserved.
 *
 * This file is part of the Faust Engine.
 *
 * The Faust Engine is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * The Faust Engine is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * the Faust Engine. If not, see <http://www.gnu.org/licenses/>.
 */
package io.resource;

import core.coll.CollisionWrapper;
import core.world.LightSource;
import core.world.WorldAction;
import core.world.WorldResource;
import core.world.Actor;
import core.world.Animation;
import core.world.Backdrop;
import core.world.RoomLayer;
import core.world.Room;
import core.world.Tileset;
import core.world.Illustration;
import core.world.WorldItem;
import core.world.WorldTile;
import core.world.item.Weapon;
import core.world.WorldScript;
import io.util.FileSearch;
import io.util.FileUtils;
import java.awt.Point;
import java.awt.Polygon;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Robert A. Cherry
 */
public class ResourceMolder {

    private ResourceMolder() {
        // Static constructor override
    }

    public static WorldResource handle(ResourceDelegate delegate, File file) throws Exception {

        // Mold the document
        final Document document = moldDocument(file);

        // Grab the root node
        final Node root = document.getDocumentElement();

        // Switch over root node
        switch (root.getNodeName()) {
            case "Tileset":
            case "Animation":
            case "Backdrop":
                return handleIllustration(delegate, file, root);
            case "Weapon":
                return handleWeapon(delegate, file, root);
            case "Room":
                return handleRoom(delegate, file, root);
            case "WorldScript":
                return handleWorldScript(delegate, file, root);
            case "Actor":
                return handleWorldActor(delegate, file, root);
            case "LightSource":
                return handleLightSource(delegate, file, root);
            default:
                return null;
        }
    }

    public static Room handleRoom(ResourceDelegate delegate, File file, Node rootNode) throws Exception {

        // Grab fMap Dimensions from root node attributes
        final NamedNodeMap attributeMap = rootNode.getAttributes();
        final HashMap<String, Object> attributes = handleAttributes(rootNode);

        //
        final String sha1CheckSum = FileUtils.generateChecksum(file.getAbsolutePath(), "SHA-1");

        // Grab information from attributes to create the base map
        final String referenceID = attributeMap.getNamedItem("referenceID").getNodeValue();
        final String packageID = attributeMap.getNamedItem("packageID").getNodeValue();
        final String displayName = attributeMap.getNamedItem("displayName").getNodeValue();
        final String referenceName = attributeMap.getNamedItem("referenceName").getNodeValue();
        final int cellWidth = Integer.parseInt(attributeMap.getNamedItem("width").getNodeValue());
        final int cellHeight = Integer.parseInt(attributeMap.getNamedItem("height").getNodeValue());

        // Create the output fMap
        final Room room = new Room(sha1CheckSum, packageID, referenceID, referenceName, displayName, cellWidth, cellHeight);

        // Apply the attributes found from root node
        room.setAttributeMap(attributes);

        // Grab the background if it exists
        final Node sceneryNode = getNodeNamed(rootNode, "scenery");
        final NodeList sceneryList = sceneryNode.getChildNodes();

        // Iterate
        for (int i = 0; i < sceneryList.getLength(); i++) {

            // Grab node
            final Node node = sceneryList.item(i);

            // Must be an element type; not text
            if (node.getNodeType() == Node.ELEMENT_NODE) {

                // Parse Scenic object; namely background
                parseScenicObject(delegate, room, node);
            }
        }

        // Find the Content node containing all the layers
        final Node contentNode = getNodeNamed(rootNode, "content");
        final NodeList contentList = contentNode.getChildNodes();

        // Iterate
        for (int i = 0; i < contentList.getLength(); i++) {

            // Should be the actual layer: so grab its content list
            final Node node = contentList.item(i);

            // This should be the layer, but might contain type TEXT_NODE so ask for NODE_ELEMENT only
            if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equalsIgnoreCase("Layer")) {

                // Grab the attributes for details about the map
                final NamedNodeMap layerNodeMap = node.getAttributes();

                // Grab the displayname of the layer, because we need it to make a layer
                final String layerName = layerNodeMap.getNamedItem("displayName").getNodeValue();

                // Setup the layer before hand
                final RoomLayer layer = new RoomLayer(room, layerName);

                // This should be the list of world objects
                final NodeList objectList = node.getChildNodes();

                // Iterate over it
                for (int j = 0; j < objectList.getLength(); j++) {

                    // This should be the World Object, but might contain TEXT_NODE  type so ask for NODE_ELEMENT only
                    final Node object = objectList.item(j);

                    if (object.getNodeType() == Node.ELEMENT_NODE) {

                        // Send it to the delegate to register the resources that it needs.
                        parseWorldObject(delegate, layer, object);
                    }
                }

                System.out.println("Lame.");

                // We'll addObject the layer to the world cell here
                room.addLayer(layer);

                System.err.println("Shutup.");
            }
        }

        // Should be done
        return room;
    }

    public static WorldScript handleWorldScript(ResourceDelegate delegate, File file, Node root) throws Exception {

        // Value holder
        final HashMap<String, Object> attributes = handleAttributes(root);

        //
        final String sha1CheckSum = FileUtils.generateChecksum(file.getAbsolutePath(), "SHA-1");

        //
        final NamedNodeMap attrMap = root.getAttributes();

        // Grab the name of the layer
        final String packageID = attrMap.getNamedItem("packageID").getNodeValue();
        final String referenceName = attrMap.getNamedItem("referenceName").getNodeValue();
        final String referenceID = attrMap.getNamedItem("referenceID").getNodeValue();
        final String displayName = attrMap.getNamedItem("displayName").getNodeValue();

        // File Stuff
        final String scriptFileName = attrMap.getNamedItem("scriptFileName").getNodeValue();
        final String scriptCheckSum = attrMap.getNamedItem("scriptCheckSum").getNodeValue();

        // Create a file search for the script file
        final FileSearch search = new FileSearch(new File(delegate.getDataDirectory()), scriptFileName, true);

        // Perform the search
        search.perform();

        // Find it in the script folder from the Java Script's CheckSum
        final File scriptFile = search.check(scriptCheckSum);

        // Craft the Template from the given information
        final WorldScript script = new WorldScript(scriptFile, sha1CheckSum, packageID, referenceID, referenceName, displayName);

        // Apply the attributes; its all we need from this
        script.setAttributeMap(attributes);

        // Return the script
        return script;
    }

    public static Actor handleWorldActor(ResourceDelegate delegate, File file, Node root) throws Exception {

        // Value holder
        final HashMap<String, Object> attributes = handleAttributes(root);

        //
        final NamedNodeMap attrMap = root.getAttributes();

        //
        final String md5CheckSum = FileUtils.generateChecksum(file.getAbsolutePath(), "SHA-1");

        // Grab the name of the layer
        final String packageID = attrMap.getNamedItem("packageID").getNodeValue();
        final String referenceName = attrMap.getNamedItem("referenceName").getNodeValue();
        final String referenceID = attrMap.getNamedItem("referenceID").getNodeValue();
        final String displayName = attrMap.getNamedItem("displayName").getNodeValue();

        // Other stuff (W.I.P)
        final int attrAttackDamage = Integer.parseInt(attrMap.getNamedItem("attrAttackDamage").getNodeValue());
        final int attrAttackDefense = Integer.parseInt(attrMap.getNamedItem("attrAttackDefense").getNodeValue());

        // The delegate and the recieve(); method will give it to the proper layer
        final Actor actor = new Actor(md5CheckSum, packageID, referenceID, referenceName, displayName);
        actor.setAttackDamage(attrAttackDamage);
        actor.setAttackDefense(attrAttackDefense);

        // Requesting a script
        if (attrMap.getNamedItem("scriptPackageId") != null && attrMap.getNamedItem("scriptEditorId") != null) {

            //
            final String scriptEditorId = attrMap.getNamedItem("scriptEditorId").getNodeValue();
            final String scriptPackageId = attrMap.getNamedItem("scriptPackageId").getNodeValue();

            // Check for null packageID
            if (scriptPackageId.isEmpty()) {
                delegate.makeRequest(scriptEditorId, actor);
            } else {
                delegate.makePackageRequest(scriptPackageId, scriptEditorId, actor);
            }
        }

        // Give the actor the found attributes
        actor.setAttributeMap(attributes);

        // Action node
        final Node actionNode = getNodeNamed(root, "Actions");

        //
        if (actionNode != null) {

            //
            final NodeList actionChildren = actionNode.getChildNodes();

            // Iterate
            for (int i = 0; i < actionChildren.getLength(); i++) {

                // Reference Node
                final Node refNode = actionChildren.item(i);

                //
                NamedNodeMap map = refNode.getAttributes();

                // Requesting a world
                if (map.getNamedItem("action") != null && map.getNamedItem("animationPackageId") != null && map.getNamedItem("animationEditorId") != null) {

                    // Grab the name of the action
                    String actionName = map.getNamedItem("action").getNodeValue();
                    String animationPackageId = map.getNamedItem("animationPackageId").getNodeValue();
                    String animationEditorId = map.getNamedItem("animationEditorId").getNodeValue();

                    //
                    final WorldAction action = WorldAction.valueOf(actionName);

                    // Check for null packageID
                    if (animationPackageId.isEmpty()) {
                        delegate.makeRequest(animationEditorId, actor);
                    } else {
                        delegate.makePackageRequest(animationPackageId, animationEditorId, actor);
                    }

                    // Shadow this action
                    actor.shadowAction(animationEditorId, action);
                }
            }
        }

        // Return it
        return actor;
    }

    public static LightSource handleLightSource(ResourceDelegate delegate, File file, Node root) throws Exception {

        // Value holder
        final HashMap<String, Object> attributes = handleAttributes(root);

        //
        final String md5CheckSum = FileUtils.generateChecksum(file.getAbsolutePath(), "SHA-1");

        //
        final NamedNodeMap attrMap = root.getAttributes();

        // Grab the name of the layer
        final String packageID = attrMap.getNamedItem("packageID").getNodeValue();
        final String referenceName = attrMap.getNamedItem("referenceName").getNodeValue();
        final String referenceID = attrMap.getNamedItem("referenceID").getNodeValue();
        final String displayName = attrMap.getNamedItem("displayName").getNodeValue();
        final int angle = Integer.parseInt(attrMap.getNamedItem("angle").getNodeValue());
        final int x = Integer.parseInt(attrMap.getNamedItem("x").getNodeValue());
        final int y = Integer.parseInt(attrMap.getNamedItem("y").getNodeValue());
        final float amps = Float.parseFloat(attrMap.getNamedItem("amps").getNodeValue());
        final float volts = Float.parseFloat(attrMap.getNamedItem("volts").getNodeValue());

        //
        final LightSource source = new LightSource(x, y, angle, amps, volts);
        source.setAttributeMap(attributes);
        source.setPackageID(packageID);
        source.setReferenceName(referenceName);
        source.setDisplayName(displayName);
        source.setReferenceID(referenceID);
        source.validate();

        // Return it
        return source;
    }

    public static WorldItem handleWeapon(ResourceDelegate delegate, File file, Node root) throws Exception {

        // Value holder
        final HashMap<String, Object> attributes = handleAttributes(root);

        //
        final String md5CheckSum = FileUtils.generateChecksum(file.getAbsolutePath(), "SHA-1");

        //
        final NamedNodeMap attrMap = root.getAttributes();

        // Grab the name of the layer
        final String type = root.getNodeName();
        final String packageID = attrMap.getNamedItem("packageID").getNodeValue();
        final String referenceName = attrMap.getNamedItem("referenceName").getNodeValue();
        final String referenceID = attrMap.getNamedItem("referenceID").getNodeValue();
        final String displayName = attrMap.getNamedItem("displayName").getNodeValue();

        //
        WorldItem item = null;

        switch (type) {
            case "Weapon":
                // The delegate and the recieve(); method will give it to the proper layer
                item = new Weapon(md5CheckSum, packageID, referenceID, referenceName, displayName);
                break;
        }

        // Item must exist
        if (item != null) {

            // Give the actor the found attributes
            item.setAttributeMap(attributes);

            //
            item.validate();

            // !WORLD OBJECT REQUESTING BODY
            if (attrMap.getNamedItem("bodyPackageId") != null && attrMap.getNamedItem("bodyEditorId") != null) {

                // These attributes must exist in the xml file
                final String bodyEditorId = attrMap.getNamedItem("bodyEditorId").getNodeValue();
                final String bodyPackageId = attrMap.getNamedItem("bodyPackageId").getNodeValue();

                // No Plugin?
                if (bodyPackageId.isEmpty()) {
                    // Request the body for the tile from loose files
                    delegate.makeRequest(bodyEditorId, item);
                } else {

                    // Request a resource from a dataPackage
                    delegate.makePackageRequest(bodyPackageId, bodyEditorId, item);
                }
            }

            // Requesting a script
            if (attrMap.getNamedItem("scriptPackageId") != null && attrMap.getNamedItem("scriptEditorId") != null) {

                //
                final String scriptEditorId = attrMap.getNamedItem("scriptEditorId").getNodeValue();
                final String scriptPackageId = attrMap.getNamedItem("scriptPackageId").getNodeValue();

                // Check for null packageID
                if (scriptPackageId.isEmpty()) {
                    delegate.makeRequest(scriptEditorId, item);
                } else {
                    delegate.makePackageRequest(scriptPackageId, scriptEditorId, item);
                }
            }

            // Action node
            final Node actionNode = getNodeNamed(root, "Actions");

            //
            if (actionNode != null) {

                //
                final NodeList actionChildren = actionNode.getChildNodes();

                // Iterate
                for (int i = 0; i < actionChildren.getLength(); i++) {

                    // Reference Node
                    final Node node = actionChildren.item(i);

                    // Do not accept text nodes
                    if (node.getNodeType() == Element.ELEMENT_NODE) {

                        //
                        NamedNodeMap map = node.getAttributes();

                        // Requesting a world
                        if (map.getNamedItem("action") != null && map.getNamedItem("animationPackageId") != null && map.getNamedItem("animationEditorId") != null) {

                            // Grab the name of the action
                            String actionName = map.getNamedItem("action").getNodeValue();
                            String animationPackageId = map.getNamedItem("animationPackageId").getNodeValue();
                            String animationEditorId = map.getNamedItem("animationEditorId").getNodeValue();

                            //
                            final WorldAction action = WorldAction.valueOf(actionName);

                            // Shadow this action
                            item.shadowAction(animationEditorId, action);

                            // Check for null packageID
                            if (animationPackageId.isEmpty()) {
                                delegate.makeRequest(animationEditorId, item);
                            } else {
                                delegate.makePackageRequest(animationPackageId, animationEditorId, item);
                            }
                        }
                    }
                }
            }
        }

        // Return it
        return item;
    }

    public static Illustration handleIllustration(ResourceDelegate delegate, File file, Node root) throws Exception {

        // Try--
        try {

            // Grab the attributes and store them in a hashmap
            final HashMap<String, Object> map = handleAttributes(root);

            // We need to generate a checksum for the actual graphic holder(AnimatedSprite, GraphicSet, Background)
            // not just the graphic that the graphic holder containsObject
            final String sha1CheckSum = FileUtils.generateChecksum(file.getAbsolutePath(), "SHA-1");

            // Grab properties from the graphic
            final String referenceID = String.valueOf(map.get("referenceID"));
            final String packageID = String.valueOf(map.get("packageID"));
            final String displayName = String.valueOf(map.get("displayName"));
            final String referenceName = String.valueOf(map.get("referenceName"));
            final String picturePackageID = String.valueOf(map.get("picturePackageID"));
            final String pictureReferenceID = String.valueOf(map.get("pictureReferenceID"));

            // Handle it
            switch (root.getNodeName()) {
                case "Animation":

                    // Create the animation from file
                    final Animation animation = new Animation(sha1CheckSum, packageID, referenceID, referenceName, displayName);
                    animation.setAttributeMap(map);

                    // Collision Node
                    final Node collision = (Element) getNodeNamed(root, "Collision");

                    //
                    if (collision != null) {
                        animation.setWrapper(new CollisionWrapper(handleCollision(collision)));
                    }

                    // Add a resource request for the graphicName of type Image
                    delegate.makePackageRequest(picturePackageID, pictureReferenceID, animation);

                    // Return the animation
                    return animation;
                case "Tileset":

                    // Create the graphic set and call for its image.
                    final Tileset tileset = new Tileset(sha1CheckSum, packageID, referenceID, referenceName, displayName);
                    tileset.setAttributeMap(map);

                    // Add a resource request for the graphicName of type Image
                    delegate.makePackageRequest(picturePackageID, pictureReferenceID, tileset);
                    // System.out.println("Tileset Semi-handled.");

                    // Return the tileset
                    return tileset;
                case "Backdrop":

                    // Couple of extra things to grab here
                    final Boolean bool = Boolean.parseBoolean(String.valueOf(map.get("stretch")));

                    //@DEBUG
                    // System.err.println("Handling Illustration Value: " + bool);
                    // Create the background from file
                    final Backdrop background = new Backdrop(sha1CheckSum, packageID, referenceID, referenceName, displayName);
                    background.setAttributeMap(map);
                    background.setStretching(bool);

                    // Add a resource request for the graphicName of type Image
                    delegate.makePackageRequest(picturePackageID, pictureReferenceID, background);

                    // Return the background
                    return background;
            }
        } catch (NullPointerException | ClassCastException cce) {
            // Will soon throw visible error
            System.err.println(cce);
        }

        //
        return null;
    }

    private static Object[][] handleCollision(Node collision) {

        // The list of nodes.
        final NodeList indexList = collision.getChildNodes();

        // We use half the indexList's length because it has a ton of elements that aren't element_nodes.
        final Object[][] output = new Object[indexList.getLength() / 2][5];
        int v1 = -1;

        // We should be scrolling down Index:[0] - Index[Animation Length]
        for (int i = 0; i < indexList.getLength(); i++) {

            // Each individual animation index <INDEX val, vcount>
            final Node indexNode = indexList.item(i);
            final int indexNodeType = indexNode.getNodeType();

            // ELEMENT_NODE == 1;
            if (indexNodeType == Node.ELEMENT_NODE) {

                //
                final NamedNodeMap indexAttr = indexNode.getAttributes();

                //
                final int pointCount = Integer.parseInt(indexAttr.getNamedItem("pointcount").getNodeValue());
                final int polyCount = Integer.parseInt(indexAttr.getNamedItem("polycount").getNodeValue());
                final int precision = Integer.parseInt(indexAttr.getNamedItem("precision").getNodeValue());

                // Grab named node map for attribute vcount
                final Polygon[] polygons = new Polygon[polyCount];
                final HashMap<Point, Integer> map = new HashMap(pointCount);
                final String[] names = new String[polyCount];
                final double[] mults = new double[polyCount];
                int polyCounter = 0;

                //
                v1++;

                // The list of points and polygons
                final NodeList contentList = indexNode.getChildNodes();

                // Going over each polygon
                for (int j = 0; j < contentList.getLength(); j++) {

                    // <POLYGON mult, name>
                    final Node contentNode = contentList.item(j);
                    final String contentNodeName = contentNode.getNodeName();
                    final int contentNodeType = contentNode.getNodeType();

                    // Again must be an element node.
                    if (contentNodeType == Node.ELEMENT_NODE) {

                        // If it's a point process is differently.
                        if (contentNode.getNodeName().equals("Point")) {

                            // Grab the name of the polygon region and the multiplier
                            final NamedNodeMap pointAttr = contentNode.getAttributes();

                            //
                            final int x = Integer.parseInt(pointAttr.getNamedItem("x").getNodeValue());
                            final int y = Integer.parseInt(pointAttr.getNamedItem("y").getNodeValue());
                            final int type = Integer.parseInt(pointAttr.getNamedItem("type").getNodeValue());

                            // Add to polygon
                            map.put(new Point(x, y), type);
                        } else if (contentNodeName.equals("Polygon")) {

                            //
                            if (contentNode.hasChildNodes()) {

                                // Grab Coord Element
                                final NodeList polygonList = contentNode.getChildNodes();
                                final NamedNodeMap polygonAttr = contentNode.getAttributes();

                                //
                                final String name = polygonAttr.getNamedItem("name").getNodeValue();
                                final double mult = Double.parseDouble(polygonAttr.getNamedItem("mult").getNodeValue());

                                //
                                final Polygon poly = new Polygon();

                                //
                                for (int k = 0; k < polygonList.getLength(); k++) {

                                    // Coord Node
                                    final Node coordElement = (Node) polygonList.item(k);

                                    //
                                    if (coordElement.getNodeType() == Node.ELEMENT_NODE) {

                                        //
                                        final NamedNodeMap attr = coordElement.getAttributes();
                                        final int x = Integer.parseInt(attr.getNamedItem("x").getNodeValue());
                                        final int y = Integer.parseInt(attr.getNamedItem("y").getNodeValue());

                                        //
                                        poly.addPoint(x, y);
                                    }
                                }

                                // Add polygon to list.
                                polygons[polyCounter] = poly;
                                names[polyCounter] = name;
                                mults[polyCounter] = mult;

                                //
                                polyCounter++;
                            }
                        }
                    }
                }

                //
                output[v1][0] = polygons;
                output[v1][1] = map;
                output[v1][2] = names;
                output[v1][3] = mults;
                output[v1][4] = precision;
            }
        }

        //
        return output;
    }

    private static HashMap<String, Object> handleAttributes(Node root) {

        // Create the hashmap
        final HashMap<String, Object> map = new HashMap<>();

        // Grab the attributes
        final NamedNodeMap attrMap = root.getAttributes();

        // Must have existing attributes
        if (attrMap != null) {

            // If we have attributes grab their values
            for (int i = 0; i < attrMap.getLength(); i++) {

                // Grab current
                final Node attrNode = attrMap.item(i);

                // Grab properties
                final String property = attrNode.getNodeName();
                final String value = attrNode.getNodeValue();

                // Store into the hashMap
                map.put(property, value);
            }
        }

        // Return the hashmap
        return map;
    }

    /*
     * Creates an xml document from a file
     */
    public static Document moldDocument(File file) {

        // Document Casing
        Document document = null;

        // Begin reading
        try {

            // Open the document and parse it
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();

            // Out document
            document = builder.parse(file);
        } catch (ParserConfigurationException | SAXException | IOException pe) {
            throw new RuntimeException(pe);
        }

        // Return created XMLDocument
        return document;
    }

    private static Node getNodeNamed(Node node, String search) {

        if (node == null || node.getChildNodes() == null) {
            return null;
        }

        // Grab node list
        final NodeList nodeList = node.getChildNodes();

        // Iterate
        for (int i = 0; i < nodeList.getLength(); i++) {

            // Grab name
            final String name = nodeList.item(i).getNodeName().toLowerCase();

            // Ask
            if (name.equals(search.toLowerCase())) {

                // Found you
                return nodeList.item(i);
            }
        }

        // Lose you
        return null;
    }

    private static void parseScenicObject(ResourceDelegate delegate, Room worldCell, Node node) {

        // Grab the attributes for this xml element
        final NamedNodeMap attributes = node.getAttributes();

        // Extract some information from the node's attributes
        final String packageID = attributes.getNamedItem("packageID").getNodeValue();
        final String referenceName = attributes.getNamedItem("referenceName").getNodeValue();
        final String referenceID = attributes.getNamedItem("referenceID").getNodeValue();

        // Depends on the status of the packageID
        if (packageID.isEmpty() || packageID.equals("null")) {

            // Request from loose files
            delegate.makeRequest(referenceID, worldCell);
        } else {

            // Wait for all resources to load then allocate the fMap to the world.
            delegate.makePackageRequest(packageID, referenceName, worldCell);
        }
    }

    private static void parseWorldObject(ResourceDelegate delegate, RoomLayer layer, Node node) {

        // Value holder
        final HashMap<String, Object> map = handleAttributes(node);

        // Grab the attributes for this xml element
        final NamedNodeMap attributes = node.getAttributes();

        // Extract some information from the node's attributes (All world objects contain these values)
        final String nodeName = node.getNodeName();
        final String packageID = attributes.getNamedItem("packageID").getNodeValue();
        final String referenceName = attributes.getNamedItem("referenceName").getNodeValue();
        final String referenceID = attributes.getNamedItem("referenceID").getNodeValue();
        final String displayName = attributes.getNamedItem("displayName").getNodeValue();
        final int x = Integer.parseInt(attributes.getNamedItem("x").getNodeValue());
        final int y = Integer.parseInt(attributes.getNamedItem("y").getNodeValue());

        // Initially null as we switch over class name given
        // Support for Tiles
        if (nodeName.equalsIgnoreCase("WorldTile")) {

            // Extract raw
            final int index = Integer.parseInt(attributes.getNamedItem("index").getNodeValue());
            final float rotation = Float.parseFloat(attributes.getNamedItem("rotation").getNodeValue());

            // Special case for tiles
            WorldTile tile = new WorldTile(index);

            // Editor and Map information
            tile.setAttributeMap(map);
            tile.setX(x);
            tile.setY(y);
            tile.setReferenceID(referenceID);
            tile.setDisplayName(displayName);
            tile.setReferenceName(referenceName);
            tile.setPackageID(packageID);

            // Properties to set
            tile.setRotation(rotation);

            // !TILE REQUESTING GRAPHICSET
            // The Tile's reference ID is the same as it's graphics set's always so the request will
            // find the graphicset by the referenceId and send it to the Tile.
            if (packageID == null || packageID.isEmpty() || packageID.equals("null")) {

                // Requesting a resource that is not associated with any data package
                delegate.makeRequest(referenceID, tile);
            } else {

                // Requesting a resource that is associated with a data package
                delegate.makePackageRequest(referenceID, referenceName, tile);
            }

            //
            layer.addObject(tile, new Point(x, y));
        } else if (packageID == null || packageID.isEmpty() || packageID.equals("null")) {

            // Request from loose files
            delegate.makeReferenceRequest(referenceID, layer, map);
        } else {

            // Wait for all resources to load then allocate the resource.
            delegate.makePackageRequest(packageID, referenceID, layer);
        }
    }
}
