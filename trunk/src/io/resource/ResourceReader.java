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

import core.world.WorldResource;
import io.util.FileUtils;
import io.util.PackageUtils;
import io.util.FileSearch;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class ResourceReader {

    // Variable Declaration
    public static final String XML_EXTENSION = "xml";
    // 'MW' stands for my Online name "MessiahWolf" the 'A' is for "Archive"
    public static final String MW_ARCHIVE_EXTENSION = "mwa";
    // May remove later
    // End of Variable Delcaration

    private ResourceReader() {
        // Static constructor override; disallows the user from instantiating this class
    }

    public static ImageIcon readClassPathIcon(Class closs, String path) {

        //
        final Toolkit toolkit = Toolkit.getDefaultToolkit();

        //
        final Image image = toolkit.getImage(closs.getResource(path));

        //
        return new ImageIcon(image);
    }

    public static Image readClassImage(Class closs, String path) {

        //
        final Toolkit toolkit = Toolkit.getDefaultToolkit();

        //
        final Image image = toolkit.getImage(closs.getResource(path));

        //
        return image;
    }

    public static WorldResource read(ResourceDelegate delegate, File file) throws Exception {

        // Do not read directories; this should not be used to load data Packages
        if (file.isFile()) {

            // Reads xml files only; add more if you want
            switch (FileUtils.getExtension(file)) {
                case XML_EXTENSION:

                    // If you want it to handle other file types add them yourself. This engine is bare nessecities.
                    return ResourceMolder.handle(delegate, file);
                default:
                    return null;
            }
        } else {
            return null;
        }
    }

    public static Image readImage(File file) {

        //
        Image image = null;

        //
        if (file.isFile()) {

            try {

                //
                image = ImageIO.read(new BufferedInputStream(new FileInputStream(file.getAbsolutePath())));
            } catch (IOException ioe) {
                System.err.println(ioe);
            }
        }

        //
        return image;
    }

    public static DataPackage readPackage(ResourceDelegate delegate, File file, boolean add) {

        // Extract the acrhive to a temporary folder in the cache directory
        final File temporary = PackageUtils.extract(delegate, file);

        // Grab the contents of that extracted folder -- Grabs every last file recursively
        final File[] contents = FileUtils.getDirectoryContents(temporary);

        // Store those entries as citations in an arraylist
        final ArrayList<DataRef> references = new ArrayList<>(contents.length);

        // Create a new file search
        FileSearch search = new FileSearch(temporary, "manifest.xml", true);
        search.perform();

        // Attempt to locate the manifest file inside of the dataPackage; this is essential to the integrity of all resources and their respective ids
        final File manifest = new File(search.getFirstResult());

        // If and only if manifest exists
        if (manifest.exists()) {

            // Mold into a document
            final Document document = ResourceMolder.moldDocument(manifest);

            // Grab the root node
            final Node root = document.getDocumentElement();

            // Grab the attributes map
            final NamedNodeMap map = root.getAttributes();

            // Grab information from the attributes node in the document
            final String author = map.getNamedItem("author").getNodeValue();
            final String email = map.getNamedItem("email").getNodeValue();
            final String version = map.getNamedItem("version").getNodeValue();
            final String referenceID = map.getNamedItem("referenceID").getNodeValue();
            final String displayName = map.getNamedItem("displayName").getNodeValue();
            final String referenceName = map.getNamedItem("referenceName").getNodeValue();

            // Craft a data package from this info
            final DataPackage dataPackage = new DataPackage(author, email, version, referenceID, displayName, referenceName, document);

            // Iterate over the content folder (Will only grab files, not directories)
            for (int i = 0; i < contents.length; i++) {

                // Current file in the iteration
                final File current = contents[i];

                // Store the entry
                dataPackage.addFile(delegate, current);
            }

            // Add here?
            if (add) {

                // Add as a dataPackage
                delegate.addDataPackage(dataPackage);
            }

            // Clear the map
            references.clear();

            // Delete the temporary folders and extracted folders
            try {

                // Delete the manifest
                FileUtils.eraseFile(manifest);

                // Destroy Cache dir
                FileUtils.eraseContents(temporary);
                FileUtils.eraseFile(temporary);
            } catch (IOException ioe) {
                System.err.println(ioe);
            }

            //
            return dataPackage;
        } else {
            // Tell the user that this data package could not be properly read due to non-existent Manifest File.
            // @TODO Ask if the user wants to create a default manifest for the 'dataPackage' (Not finished yet)
        }

        //
        return null;
    }

    public static String[] getReaderFormatNames() {
        return new String[]{XML_EXTENSION};
    }

    public static boolean isImageExtension(String extension) {

        // Grab the acceptable ImageIO reader names
        final String[] formatCopy = ImageIO.getReaderFormatNames();

        // Iterate over the image format names
        for (int i = 0; i < formatCopy.length; i++) {

            // Ask.
            if (extension.equalsIgnoreCase(formatCopy[i])) {
                return true;
            }
        }

        // Not an acceptable Image extension
        return false;
    }

    public static boolean isReaderExtension(String extension) {

        //
        final String[] formatCopy = getReaderFormatNames();

        // Iterate over the reader format names
        for (int i = 0; i < formatCopy.length; i++) {

            // Ask
            if (extension.equalsIgnoreCase(formatCopy[i])) {
                return true;
            }
        }

        // Not a reader extension
        return false;
    }
}
