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
package io.util;

import io.resource.DataRef;
import io.resource.ResourceDelegate;
import io.resource.DataPackage;
import io.resource.PackageQueue;
import io.resource.PackageQueue.Pair;
import io.resource.ResourceReader;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Robert A. Cherry
 */
public class PackageUtils {

    private PackageUtils() {
        // Overriden constructor; forces entire class to appear staic and public
    }

    // Returns whether the delete succeeded
    public static void preparePackage(File[] files, File content, ResourceDelegate delegate, DataPackage pack, Object[] append) throws IOException {

        // Start to fill the cache directory with the list of files; (Ctrl + B to See source)
        FileUtils.copyTo(files, content);

        // The file list is now invalidated; deep (recursively) search the cache folder for a better view of the contents
        final File[] fileList = FileUtils.getDirectoryContents(content);

        // Place all the dataRef's into the dataPackage
        for (int i = 0; i < fileList.length; i++) {

            // Add as an entry in the dataPackage as to force an entry in the manifest
            pack.addFile(delegate, fileList[i]);
        }

        try {

            // If you chose to append '__' to the front of all (File not directory) names
            if (((Boolean) append[0]) == true) {

                //
                String prefix = String.valueOf(append[1]);

                // Reanmes all files with '__' in front
                final HashMap<String, String> changes = setContentPrefix(content, prefix);

                // Apply the changed names to the package via its update method.
                pack.update(changes);
            }
        } catch (ArrayIndexOutOfBoundsException | NullPointerException | ClassCastException npe) {
        }
    }

    public static void modify(ResourceDelegate delegate, DataPackage dataPackage, PackageQueue queue) throws IOException {

        //
        FileSearch search = new FileSearch(new File(delegate.getAddonDirectory()), dataPackage.getReferenceName(), true);

        // Perform the search
        search.perform();

        // Find the file
        final File file = new File(search.getFirstResult());

        // Extract the dataPackage so that we can adjust its contents and reseal it
        File contentFolder = extract(delegate, file);

        // First lets begin by adjusting the manifest to match the updates
        HashMap<Pair, File> changeMap = queue.getChanges();

        // Iterate over the changes to be made
        for (Map.Entry<Pair, File> map : changeMap.entrySet()) {

            // Current change pair
            Pair commands = map.getKey();

            // Find the resource from the extracted dataPackage
            File resource = map.getValue();

            // Grab the referenceID to find
            String previousId = (String) commands.getFirstValue();

            // Grab the type of change to make
            int flag = (Integer) commands.getSecondValue();

            //
            if (flag == PackageQueue.FLAG_REMOVE || flag == PackageQueue.FLAG_REPLACE) {

                // Resource to apply the change to
                DataRef previous = dataPackage.findByEditorId(previousId);

                //
                FileSearch referenceSearch = new FileSearch(contentFolder, previous.getEditorName(), true);

                // Perform the search
                referenceSearch.perform();

                // Find the file
                File found = referenceSearch.check(previous.getResource().getSHA1CheckSum());

                // Attempt to delete the file
                FileUtils.eraseFile(found);

                // Replace it with the new file if commanded to
                if (flag == PackageQueue.FLAG_REPLACE) {

                    // Add a file to the dataPackage
                    dataPackage.addFile(delegate, resource);

                    // Actually add the file to the contentFolder via the FileUtils.transferCopyTo();
                    FileUtils.move(resource, contentFolder);
                }
            } else if (flag == PackageQueue.FLAG_ADD) {

                // Add a file to a dataPackage
                dataPackage.addFile(delegate, resource);

                // Actually add the file to the contentFolder via the FileUtils.transferCopyTo();
                FileUtils.move(resource, contentFolder);
            }

            // Now the changes to the achive have been made, now write it out
            writePackage(new File(delegate.getAddonDirectory()), contentFolder, dataPackage, true);

            // Delete the content folder from the temporary directory
            //FileUtils.deleteContents(contentFolder);
            //FileUtils.destroy(contentFolder);
        }
    }

    public static File extract(ResourceDelegate delegate, File file) {

        //
        File cacheDirectory = null;

        //
        try {

            // Create the directory in the cache
            cacheDirectory = FileUtils.makeDirectoryInside(new File(delegate.getCacheDirectory()), FileUtils.contract(file));

            // Grab the file extension
            String fileExt = FileUtils.getExtension(file);

            // Iterate over reader format names
            if (ResourceReader.MW_ARCHIVE_EXTENSION.equalsIgnoreCase(fileExt)) {

                //
                try {

                    // The Location of the actual zipped file
                    ZipFile zippedFile = new ZipFile(file);

                    // Extract the zipped file to the output location
                    extract(zippedFile, cacheDirectory);
                } catch (ZipException ex) {
                    Logger.getLogger(PackageUtils.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(PackageUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (IOException ioe) {
        } finally {
            return cacheDirectory;
        }
    }

    // Should extract all subdirectories in subdirectories
    private static void extract(ZipFile zippedFile, File cacheDirectory) {

        try {

            // Grab its entries
            Enumeration zipEnum = zippedFile.entries();

            // Create a Byte Buffer for reading chunks of a file; Online tutorials sure are helpful.
            int byteBuffer = 2048;

            // Iterate over the entries
            while (zipEnum.hasMoreElements()) {

                ZipEntry newEntry = (ZipEntry) zipEnum.nextElement();
                String entryName = newEntry.getName();
                File newFile;

                //
                try (BufferedInputStream is = new BufferedInputStream(zippedFile.getInputStream(newEntry))) {

                    // Current Byte in the Zip File Stream
                    int currentByte;

                    // Byte stream
                    byte data[] = new byte[byteBuffer];

                    // The new File
                    newFile = new File(cacheDirectory, entryName);
                    newFile.getParentFile().mkdirs();

                    // Extract the new File
                    if (newEntry.isDirectory()) {
                        continue;
                    }

                    // Begin to write the file out
                    FileOutputStream fos = new FileOutputStream(newFile);

                    //
                    try (BufferedOutputStream dest = new BufferedOutputStream(fos, byteBuffer)) {

                        // Write out the bytes to the file
                        while ((currentByte = is.read(data, 0, byteBuffer)) != -1) {
                            dest.write(data, 0, currentByte);
                        }

                        // Flush this stream
                        dest.flush();
                    }
                }
            }
        } catch (IOException ioe) {
            System.err.println("Error: " + ioe);
        }
    }

    // Content would be the directory to place all the files into.
    public static void writePackage(File addon, File content, DataPackage pack, boolean delete) throws IOException {

        // @note Each Archive read by this program should include a mainfest detailing all the id's
        // so that resources can be accurately associated and regiestered.
        // The content folder should be full of all files going to be put into the archive
        final File manifest = ManifestUtils.writeManifest(content.getAbsolutePath(), pack);

        // @note addon should be the addon folder for the Faust Game.
        final String name = addon.getAbsolutePath().concat(File.separator).concat(FileUtils.extend(pack.getReferenceName(), ResourceReader.MW_ARCHIVE_EXTENSION));

        // If and only if newFile exists
        if (addon.exists()) {

            try {

                // new Byte buffer size
                final byte[] buffer = new byte[2156];

                // Create the Zip file in the output directory
                try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(name))) {

                    // @note This code portion tells the program to recursively create a folder's subdirectories
                    // The first and second arguments are the same because the first argument is the starting point
                    // of the recursive operation and will change inside of said operation
                    writeDirectory(content.getAbsolutePath(), content, buffer, zos);

                    // @note This code portion tells the program to create a manifest of all files that will be included
                    // in this archive
                    writeFile(manifest, buffer, zos);

                    // Close entire entry
                    zos.close();
                }
            } catch (ArrayIndexOutOfBoundsException | IOException io) {
                System.err.println("Archive Creation failed: " + io);
            }
        }

        // Now delete the cache foler if delete is true
        if (delete) {

            //
            FileUtils.eraseFile(manifest);

            // You'll want to destroy all contents first then delete folder (Recursive delete)
            FileUtils.eraseContents(content);
            FileUtils.eraseFile(content);
        }
    }

    // @note This code portion can produce a recursive path of folders that the Windows operating system cannot handle
    // Download 7-Zip and use its FileManager to [Shift-Delete] the folder as the windows
    // command prompt and explorer will not be able to delete said folder
    private static void writeDirectory(String filePath, File cacheFolder, byte[] buffer, ZipOutputStream zos) {

        // The Directory above this one
        File parentFile = new File(filePath);

        // Must be a directory
        if (parentFile.isDirectory()) {

            // List its collection of files
            File[] listFiles = parentFile.listFiles();

            // Null Check this will occur if the directory has no files or folders
            if (listFiles != null) {

                // Iterate over the list of files
                for (int i = 0; i < listFiles.length; i++) {

                    //
                    final File file = listFiles[i];

                    // @note This code portion RECURSIVELY creates a directory's sub directories until there are none left
                    if (file.isDirectory()) {

                        // @RECURSION
                        writeDirectory(file.getPath(), cacheFolder, buffer, zos);

                        // @note The directory still needs to be written as a file here
                        writeFile(file, buffer, zos);

                        // Keep on keeping on.
                        continue;
                    }

                    // @note This code portion creates files inside of an archive
                    try {

                        // Create the file.
                        try (FileInputStream fis = new FileInputStream(file)) {

                            //
                            String newPath = excludeDirectory(cacheFolder.getPath(), file.getPath());

                            // Add this file as an entry in the archive
                            ZipEntry entry = new ZipEntry(newPath);

                            // Place the entry
                            zos.putNextEntry(entry);

                            //
                            int len;

                            // Now write the bytes into the entry
                            while ((len = fis.read(buffer)) > 0) {
                                zos.write(buffer, 0, len);
                            }

                            // Close the file stream
                            fis.close();
                        }
                    } catch (IOException ioe) {
                        // Throw an error eventually
                    }
                }
            }
        }
    }

    private static void writeFile(File file, byte[] buffer, ZipOutputStream zos) {

        try {
            try (FileInputStream fis = new FileInputStream(file)) {
                ZipEntry entry = new ZipEntry(file.getName());

                // Place the entry
                zos.putNextEntry(entry);

                //
                int len;

                // Write to zipped file
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }

                //
                fis.close();
            }
        } catch (IOException ioe) {
            // Throw an error eventually
        }
    }

    private static String excludeDirectory(String parent, String current) {

        //
        if (current.contains(parent)) {
            return current.substring(parent.length() + 1);
        }

        //
        return current;
    }

    public static HashMap<String, String> setContentPrefix(File file, String prefix) {

        // Our output map
        final HashMap<String, String> map = new HashMap<>();

        //
        if (file.isDirectory()) {

            //
            final File[] files = file.listFiles();

            //
            for (int i = 0; i < files.length; i++) {

                //
                final File current = files[i];

                // Append
                FileUtils.append(current, prefix, map);
            }
        }

        //
        return map;
    }

    public static File findPackageSource(ResourceDelegate delegate, DataPackage pack) {

        //
        final String searchName = FileUtils.extend(pack.getReferenceName(), ResourceReader.MW_ARCHIVE_EXTENSION);

        //
        final FileSearch search = new FileSearch(new File(delegate.getAddonDirectory()), searchName, true);

        // Perform the search for the Data package
        search.perform();

        // Locate the wrap the File
        return new File(search.getFirstResult());
    }
}
