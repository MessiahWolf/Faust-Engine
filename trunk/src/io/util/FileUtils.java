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

import io.resource.DataPackage;
import io.resource.ResourceDelegate;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;
import javax.swing.JOptionPane;

/**
 *
 * @author Robert A. Cherry
 */
public class FileUtils {

    private FileUtils() {
        // Static class
    }

    /**
     *
     * @param path The abstract path to the File
     * @param flavor Which flavor of checksum for this file (Ex: 'MD5', 'SHA-1')
     * @return The checksum in byte representation
     * @throws Exception
     */
    private static byte[] makeChecksum(String path, String flavor) throws Exception {

        // Message Digest creates the checksum
        final MessageDigest message;

        // Attempt to open channel to the File
        try (InputStream fis = new FileInputStream(path)) {

            // Buffer for the File Input Stream
            final byte[] buffer = new byte[1024];

            // Create an instance of the MessageDigest.class
            message = MessageDigest.getInstance(flavor);

            // Our tracking variable
            int read;

            // Update the digest while read is not -1
            do {

                //
                read = fis.read(buffer);

                // Do not read empty bytes
                if (read > 0) {

                    // Update the digest
                    message.update(buffer, 0, read);
                }
            } while (read != -1);
        }

        // Return the digested message in byte representation
        return message.digest();
    }

    /**
     * Creates a checksum for a file by first reading in its byte and passing
     * those bytes through a formula that allows each chunk of bytes to be read
     * as an integer and then made into a String
     *
     * @param path The abstract path to the File
     * @param flavor Which flavor of checksum for this file (Ex: 'MD5', 'SHA-1')
     * @return The string representation of the files message digest
     * @throws Exception
     */
    public static String generateChecksum(String path, String flavor) throws Exception {

        // Create byte representation of checksum
        final byte[] digest = makeChecksum(path, flavor);

        // Our result
        String output = "";

        // Iterate over the digest
        for (int i = 0; i < digest.length; i++) {

            // @ internet-source-cite: This code portion was possible from many online tutorials and forums
            int current = (digest[i] & 0xff) + 0x100;

            // Add on to the digest 
            output += Integer.toString(current, 16).substring(1);
        }

        // Return our completed checksum
        return output;
    }

    /**
     * Appends an extension onto the String representation of a File's name
     *
     * @param original
     * @param extension
     * @return The original string with the given extension on the end
     */
    public static String extend(String original, String extension) {

        // NULL CHECK
        if (original == null || extension == null) {
            return null;
        }

        // Addon only if needed
        if (original.endsWith(extension)) {

            // Return original
            return original;
        }

        // Append to original
        return original.concat(".").concat(extension);
    }

    public static boolean isSupported(String extension, String[] array) {

        // Iterate over the array of extensions
        for (int i = 0; i < array.length; i++) {

            // Challenge the extension
            if (extension.equalsIgnoreCase(array[i])) {

                // Success
                return true;
            }
        }

        // Return no match found
        return false;
    }

    /**
     * Checks if any of the given extensions matches the extension of the File
     * given
     *
     * @param file The File to check against
     * @param array An array of extensions to challenge the File with
     * @return Whether or not the File has any of the provided extensions
     */
    public static boolean isSupported(File file, String[] array) {

        // NULL CHECK
        if (file == null || array == null) {
            return false;
        }

        // Do not accept directories
        if (!file.isDirectory()) {

            // Grab the extension of the file
            final String extension = getExtension(file);

            //
            return isSupported(extension, array);
        } else {
            return true;
        }
    }

    /**
     * Use this to close a File that has been opened by this Application
     *
     * @param file The Desired File to close using Java API
     * @return Whether or not the File was successfully closed
     */
    public static boolean close(File file) {

        // Do not take null files
        if (file != null) {

            // Try - catch statement (IOException)
            try {

                // (Final) Input stream for the File
                final FileInputStream fis = new FileInputStream(file);

                // Close the channel that Java uses to communicate with the File
                fis.getChannel().close();

                // Successfully close File
                return true;
            } catch (IOException ioe) {

                // This File was not successfully closed
                return false;
            }
        }

        // This File was not successfully closed.
        return false;
    }

    /**
     *
     * @param path The path that the Java API will use to create the file on
     * Drive
     * @return The File created
     * @throws IOException
     */
    public static File makeDirectory(String path) throws IOException {

        // Now we want the Animation Properties
        final File output = new File(path);

        // Does this file exist already on the File System
        if (output.exists()) {

            // @todo Create a Class that contains these string so that they can easily be reused.
            final String error1 = "directory by the name " + path;
            final String error2 = " already exists.";
            final String error3 = "\nConfirm delete on existing directory.";
            final String error = error1.concat(error2).concat(error3);

            // Ask user if its okay to delete this file (Null means it will appear without constraints to any Window)
            final int answer = JOptionPane.showConfirmDialog(null, error);

            // User has accepted to delete directory that already exists
            if (answer == JOptionPane.YES_OPTION) {

                // Erase Contents of folder (see FileUtils.erase(); method)
                eraseContents(output);

                // Erase folder from File System (see FileUtils.erase(); method)
                eraseFile(output);

                // Close the stream with the directory (see FileUtils.close(); method)
                close(output);

                // Java API failed to create directory
                if (!output.mkdir()) {
                    // Throw an error or something later...
                }
            }
        } else {

            //
            output.mkdir();
        }

        // Return directory
        return output;
    }

    /**
     * Creates a directory inside of another directory that exists on File
     * System
     *
     * @param directory The directory to create the new directory inside
     * @param name The name of the directory to be made
     * @return The directory made
     * @throws IOException
     */
    public static File makeDirectoryInside(File directory, String name) throws IOException {

        // Push it over
        final File output = new File(directory, name);

        // Delete the directory if it exist
        if (output.exists()) {

            // @todo Create a Class that contains these string so that they can easily be reused.
            final String error1 = "Directory by the name " + name;
            final String error2 = " already exists.";
            final String error3 = "\nConfirm delete on existing directory.";
            final String error = error1.concat(error2).concat(error3);

            // Ask user if its okay to delete this file (Null means it will appear without constraints to any Window)
            int answer = JOptionPane.showConfirmDialog(null, error);

            // User has accepted to delete directory that already exists
            if (answer == JOptionPane.YES_OPTION) {

                //
                close(output);

                // Erase Contents of folder (see FileUtils.erase(); method)
                eraseContents(output);

                // Erase folder from File System (see FileUtils.erase(); method)
                eraseFile(output);

                // Create directory in its place
                if (output.mkdir()) {

                    // Close the stream with the directory (see FileUtils.close(); method)
                    close(output);
                }
            }
        } else {

            // Make the directory
            output.mkdir();
        }

        // Return the directory made
        return output;
    }

    /**
     * Creates a directory inside of another directory that exists on File
     * System
     *
     * @param directory The directory to create the file copy inside
     * @param file The file to be copied
     * @return The file made
     * @throws IOException
     */
    public static File makeFileInside(File file, File directory) throws IOException {

        // Delete the file if it exist
        if (new File(directory, file.getName()).exists()) {

            // @todo Create a Class that contains these string so that they can easily be reused.
            final String error1 = "File by the name " + file;
            final String error2 = " already exists.";
            final String error3 = "\nConfirm delete on existing file.";
            final String error = error1.concat(error2).concat(error3);

            // Ask user if its okay to delete this file (Null means it will appear without constraints to any Window)
            final int answer = JOptionPane.showConfirmDialog(null, error);

            // User has accepted to delete directory that already exists
            if (answer == JOptionPane.YES_OPTION) {

                // Push it over
                final File output = new File(directory, file.getName());

                // Erase folder from File System (see FileUtils.erase(); method)
                eraseFile(output);

                // Close the stream with the directory (see FileUtils.close(); method)
                close(output);
            }
        } else {

            // Push it over
            return makeCopy(file, directory);
        }

        // Return the directory made
        return null;
    }

    /**
     *
     * @param file
     * @param directory
     * @return
     * @throws IOException
     */
    public static File makeCopy(File file, File directory) throws IOException {

        // Do not accept directories
        if (file.isFile()) {

            // Create destination directory if it does not exist
            if (!directory.exists()) {

                // Create an empty file at the new location
                directory.mkdir();
            }

            // Transfer file to directory (See FileUtils.transfer(); method)
            write(file, new File(directory, file.getName()));
        } else {

            // Transfer the directory and its contents to destination directory (See FileUtils.transfer(); method)
            write(file, new File(directory, file.getName()));
        }

        // Ret
        return directory;
    }

    /**
     * Moves a File from one directory on the File System to another
     *
     * @param file The File to transfer; must exist
     * @param directory The directory to transfer the file to
     * @throws IOException
     */
    public static void move(File file, File directory) throws IOException {

        // Our File channels for both source of the file and the destination
        FileChannel source = null;
        FileChannel destination = null;

        //
        try {

            // Both Input and Output File streams
            final FileInputStream input = new FileInputStream(file);
            final FileOutputStream output = new FileOutputStream(directory);

            // The File channel for the File given
            source = input.getChannel();

            // The File channel for the directory that is the file's desination
            destination = output.getChannel();

            // Transfer it
            destination.transferFrom(source, 0, source.size());
        } finally {

            // Attempt to close both the source and the destination channels
            if (source != null && destination != null) {

                // Close both source and destination channels
                source.close();
                destination.close();
            }
        }
    }

    /**
     * WIP. Transfers all files in array to the target directory; Files will be
     * copied (originals not harmed)
     *
     * @param array An array of the files to be copied
     * @param directory The directory to transfer files to
     * @throws IOException
     */
    public static void copyTo(File[] array, File directory) throws IOException {

        if (directory == null && array == null) {
            return;
        }


        // Consider each file
        for (int i = 0; i < array.length; i++) {

            // Grab the current File
            final File file = array[i];

            // Consider directories
            if (file.isDirectory()) {

                // Make a copy in the destination
                final File destination = new File(directory, file.getName());

                // Create if not existing
                if (!destination.exists()) {
                    destination.mkdir();
                }
            } else {
                //@todo
            }
        }
    }

    public static void copy(File source, File destination) throws IOException {

        // 
        if (source.isDirectory()) {

            //if directory not exists, create it
            if (!destination.exists()) {
                destination.mkdir();
            }

            //list all the directory contents
            final String files[] = source.list();

            //
            for (String file : files) {

                // Define source file and destination file
                File sourceFile = new File(source, file);
                File destinationFile = new File(destination, file);

                // Copy deeper into the file structure
                makeCopy(sourceFile, destinationFile);
            }

        } else {

            //
            write(source, destination);
        }
    }

    public static String createUUID(String string) {

        // Trim and remove hyphens
        String uuid = UUID.nameUUIDFromBytes(string.getBytes()).toString().substring(0, 24).toLowerCase();
        uuid = uuid.replace("-", "");

        // Return created UUID
        return uuid;
    }

    /**
     * Checks if the filename given is a child of the parent directory given
     *
     * @param directory The Directory to search through
     * @param search The File to search for
     * @return Whether or not the file searched is a child of the given
     * directory
     */
    public static boolean isDescendant(File directory, String search) {

        // Sub Directory
        final File[] listFiles = directory.listFiles();

        // Do not take null files
        if (listFiles != null) {

            // Iterate though File list
            for (int i = 0; i < listFiles.length; i++) {

                // Grab current File
                File file = listFiles[i];

                //
                if (file.isDirectory()) {

                    // Scan sub-directories recursively
                    isDescendant(file, search);
                } else if (file.isFile()) {

                    // Break the name down by removing extension and lowercasing it
                    final String name = FileUtils.contract(file).toLowerCase();

                    // Return its absolute path
                    if (name.equals(search.toLowerCase())) {
                        return true;
                    } else {

                        //
                        isDescendant(file, search);
                    }
                }
            }
        }

        //
        return false;
    }

    public static String append(File file, String prefix, HashMap<String, String> optional) {

        // Grab parent folder
        final String parentFolder = file.getParent();

        // New name
        final String name = parentFolder + "\\" + prefix + file.getName();

        // Rename file.
        rename(file, prefix, name, optional);

        // Only take directories
        if (file.isDirectory()) {

            // List of directory contents
            final File[] files = file.listFiles();

            // Iterate
            for (int i = 0; i < files.length; i++) {

                // Current file in array
                final File current = files[i];

                // The caught new name
                final String s = append(current, prefix, optional);
            }
        }

        //
        return name;
    }

    public static void rename(File file, String prefix, String name, HashMap<String, String> optional) {

        // @note This will not rename directories
        if (file.isFile()) {

            //
            final String before = file.getName();
            final String shortName = prefix + file.getName();

            // Store it here
            optional.put(before, shortName);

            //
            file.renameTo(new File(name));
        }
    }

    // @note As far as I've tested it it does get all the directories and sub-directories
    public static File[] filterContents(File[] files) {

        // 
        final ArrayList<File> output = new ArrayList<>();
        int push = 0;

        //
        for (int i = 0; i < files.length; i++) {

            // To break inner loop
            inner:
            // We push so we dont start all the way at the first index again.
            for (int j = push; j < files.length; j++) {

                // The current file
                final File file = files[j];

                // Might be slow but is simpliest way to do this
                boolean contain = output.contains(file);

                // Must not already contain file
                if (contain == false) {

                    // Break out
                    output.add(file);
                    push = j;
                    break inner;
                }
            }
        }

        // Gotta catch 'em all Pokemon.
        return output.toArray(new File[]{});
    }

    public static File[] getDirectoryContents(File directory) {

        if (directory == null) {
            return null;
        }

        // Our flexible storage array
        ArrayList<File> list = new ArrayList<>(directory.listFiles().length);

        // Grab contents
        list = getContents(list, directory);

        // Return what we found
        return list.toArray(new File[]{});
    }

    private static ArrayList<File> getContents(ArrayList<File> list, File directory) {

        // Grab this files list of files
        final File[] children = directory.listFiles();

        // Directory must have children
        if (children != null) {

            // Consider each file
            for (int i = 0; i < children.length; i++) {

                // Grab current file
                final File file = children[i];

                // Don't re-add files
                if (!list.contains(file)) {

                    // Add to the file collection
                    list.add(file);
                }

                // Scan deeper into directories
                if (file.isDirectory()) {

                    // Scan deeper
                    getContents(list, file);
                }
            }
        }

        // Return what we found
        return list;
    }

    public static boolean hasExtension(File file, String newExt) {

        //
        String newString = file.getName();

        //
        if (newString.endsWith(newExt)) {
            return true;
        }

        return false;
    }

    public static String getExtension(File file) {

        //
        String outputString = file.getName();

        //
        if (outputString == null || outputString.length() == 0) {
            return null;
        }

        //
        if (outputString.contains(".")) {
            int index = outputString.indexOf(".") + 1;

            // Fine the Extension
            outputString = outputString.substring(index);
        }

        return outputString;
    }

    public static int getFileLineCount(File file) throws IOException {

        //
        int count = 0;

        //
        final FileReader fileReader = new FileReader(file);
        final BufferedReader bufferedReader = new BufferedReader(fileReader);

        //
        try (Scanner scanner = new Scanner(bufferedReader)) {

            //
            while (scanner.hasNextLine()) {

                // Move it forward
                final String line = scanner.nextLine();

                //
                count++;
            }

            // Close the io stuff
            scanner.close();
            fileReader.close();
            bufferedReader.close();
        }

        //
        return count;
    }

    public static int getDirectoryCount(File directory) {

        // Grab all of its files
        final File[] files = getDirectoryContents(directory);

        //
        int count = 0;

        //
        for (int i = 0; i < files.length; i++) {

            //
            final File current = files[i];

            // Grab only directories or Files
            count += (current.isDirectory() ? 1 : 0);
        }

        //
        return count;
    }

    public static int getFileCount(File directory, boolean includeDirectories) {

        // Grab all of its files
        final File[] files = getDirectoryContents(directory);

        //
        int count = 0;

        //
        for (int i = 0; i < files.length; i++) {

            //
            final File current = files[i];

            //
            if (includeDirectories == false) {

                // Grab only directories or Files
                count += (current.isFile() ? 1 : 0);
            } else {
                count++;
            }
        }

        //
        return count;
    }

    public static String[] getInsideTypes(ResourceDelegate delegate, DataPackage pack) {

        // Find and extract 
        final File cache = PackageUtils.extract(delegate, PackageUtils.findPackageSource(delegate, pack));

        //
        return getInsideTypes(cache);
    }

    public static String[] getInsideTypes(File directory) {

        //
        final ArrayList<String> array = new ArrayList<>();

        // Do it
        getInsideTypes(directory, array);

        // Our output
        return array.toArray(new String[]{});
    }

    private static void getInsideTypes(File directory, ArrayList<String> array) {

        //
        final File[] list = directory.listFiles();

        // @null-check
        if (list != null) {

            // Consider all Files
            for (int i = 0; i < list.length; i++) {

                //
                final File file = list[i];
                final String extension = getExtension(file);

                // Only ask normal files not directories
                if (file.isFile()) {

                    //
                    if (!array.contains(extension)) {
                        array.add(extension);
                    }
                } else {

                    // Do it again till we've checked them all
                    getInsideTypes(file, array);
                }
            }
        }
    }

    public static int getTypeCount(File directory, String extension) {

        // Grab all of its files
        final File[] files = getDirectoryContents(directory);

        //
        int count = 0;

        //
        for (int i = 0; i < files.length; i++) {

            //
            final File current = files[i];

            // Null or empty means get a count of directories
            if (extension == null || extension.equals("")) {
                if (current.isDirectory()) {
                    count++;
                }
            } else if (getExtension(current).equalsIgnoreCase(extension)) {
                // Grab only directories or Files
                count += (current.isFile() ? 1 : 0);
            }
        }

        //
        return count;
    }

    public static String contract(File file) {

        //
        if (file == null) {
            return null;
        }

        //
        String outputString = file.getName();

        //
        if (outputString == null) {
            return null;
        }
        if (outputString.contains(".")) {

            int index = outputString.indexOf(".");

            // Find the Extension
            outputString = outputString.substring(0, index);
        }

        //Returns the File with the extension removed
        return outputString;
    }

    public static void eraseFile(File file) throws IOException {

        // No null files
        if (file != null) {

            // File must exist to be closed.
            if (file.exists()) {

                // Close stream with file
                close(file);

                // Actually delete it.
                Files.delete(file.toPath());
            }
        }
    }

    public static void eraseFiles(File[] files) throws IOException {

        //
        for (int i = 0; i < files.length; i++) {

            // Grab current File
            final File file = files[i];

            // Null check
            if (file != null) {

                // Directory?
                if (!file.isDirectory()) {

                    // Throws IOException (Destroys the file)
                    eraseFile(file);
                } else {

                    // Throws IOException (Clears the entire directory first)
                    eraseContents(file);
                }
            }
        }
    }

    public static boolean eraseContents(File directory) throws IOException {

        //
        if (directory == null) {
            return false;
        }

        // Check if this is a directory
        if (directory.isDirectory()) {

            // Derive all the Directories or Files in parent Directory
            final File[] files = directory.listFiles();

            //
            if (files != null) {

                // Delete individual Files
                for (int i = 0; i < files.length; i++) {

                    // Grab current file
                    final File file = files[i];

                    // Null check
                    if (file != null) {

                        // Directory check
                        if (file.isDirectory()) {

                            // Recursively delete files found
                            eraseContents(file);
                        }

                        // Throws IOException
                        eraseFile(file);
                    }
                }
            }
        }

        // Finished Deleting
        return true;
    }

    private static void write(File source, File destination) throws IOException {

        // Solve for Null cases
        if (source == null || destination == null) {
            return;
        }

        //
        try (FileInputStream input = new FileInputStream(source); FileOutputStream output = new FileOutputStream(destination)) {

            // Byte buffer
            final byte[] buffer = new byte[1024];
            int length;

            //
            while ((length = input.read(buffer)) > 0) {

                // Write to File System
                output.write(buffer, 0, length);
            }

            //
            input.close();
        } catch (IOException ioe) {
            //
        }

        //
        close(source);
        close(destination);
    }
}
