/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.util;

import static io.util.FileUtils.getExtension;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author Robert A. Cherry
 */
public class FileSearch {

    // Variable Declaration
    // Java Native Collection Classes
    private ArrayList<String> resultList;
    // Java Native Classes
    private File directory;
    // Data Types
    private boolean caseSensitive;
    private String search;
    // End of Variable Declaration

    public FileSearch(File directory, String search, boolean caseSensitive) {

        // Set values
        this.directory = directory;
        this.search = search;
        this.caseSensitive = caseSensitive;

        // Instantiate the arraylist
        resultList = new ArrayList<>();
    }

    public String get(int index) throws ArrayIndexOutOfBoundsException {
        return resultList.get(index);
    }

    public void switchDirectory(File directory) {
        this.directory = directory;

        // This operation clears the results
        resultList.clear();
    }

    public void switchSearch(String search) {
        this.search = search;

        // This operation clears the results
        resultList.clear();
    }

    public void perform() {

        // The directory must exist
        if (directory != null) {

            if (directory.isDirectory()) {

                // First ask if we can even read the directory
                if (directory.canRead()) {

                    // If we can read and if this is a directory then search it
                    search(directory);
                } else {

                    // Show the message
                    JOptionPane.showMessageDialog(null, directory.getName() + ": Permission to access file has been denied.");
                }
            } else {

                // Show the message
                JOptionPane.showMessageDialog(null, directory.getName() + ": Is not a Directory.");
            }
        } else {

            // Show the message
            JOptionPane.showMessageDialog(null, "The Directory given to search does not exist.");
        }
    }

    private void search(File directory) {

        // File must exist
        if (directory != null && search != null) {

            // File must be a directory
            if (directory.isDirectory()) {

                // Grab all its files
                final File[] files = directory.listFiles();

                // Iterate over those files
                for (int i = 0; i < files.length; i++) {

                    // Grab current file
                    final File file = files[i];

                    // Grab current name 
                    final String current = file.getName();

                    // Must be able to access this directory
                    if (directory.canRead()) {

                        // Pop the question
                        if (caseSensitive == false) {

                            // Non-Case sensitive search
                            if (current.equalsIgnoreCase(search)) {
                                resultList.add(file.getAbsolutePath());
                                continue;
                            }
                        } else {

                            // Case sensitive search
                            if (current.equals(search)) {
                                resultList.add(file.getAbsolutePath());
                                continue;
                            }
                        } 
                        
                        //
                        if (file.isDirectory()) {

                            // Search this
                            search(file);
                        }

                    } else {

                        // Show the message
                        JOptionPane.showMessageDialog(null, current + ": Permission to access file has been denied.");
                    }
                }
            }
        }
    }

    public File getDirectory() {
        return directory;
    }

    public ArrayList<String> getResults() {
        return resultList;
    }

    public String getFirstResult() {

        // Must not be empty
        if (resultList.isEmpty() == false) {
            return resultList.get(0);
        }

        //
        return null;
    }

    public String getLastResult() {

        // Must not be empty
        if (resultList.isEmpty() == false) {
            return resultList.get(resultList.size() - 1);
        }

        //
        return null;
    }

    public static File[] compriseListByExtension(File folder, String extension) {

        //
        final ArrayList<File> fileList = new ArrayList<>();

        //
        compriseListByExtension(folder, extension, fileList);

        //
        return fileList.toArray(new File[]{});
    }

    public static File[] compriseListByExtensions(File folder, String[] extensions) {

        // Our output arraylist
        final ArrayList<File> fileList = new ArrayList<>();

        // Iterate over the array of extensions
        for (int i = 0; i < extensions.length; i++) {

            // Add to the file list
            compriseListByExtension(folder, extensions[i], fileList);
        }

        // Should have all the bells and whistles
        return fileList.toArray(new File[]{});
    }

    private static ArrayList<File> compriseListByExtension(File folder, String extension, ArrayList<File> fileList) {

        //
        File[] contents = folder.listFiles();

        for (int i = 0; i < contents.length; i++) {

            File currentFile = contents[i];

            if (currentFile.isFile()) {

                // Grab the current File
                String fileExtension = getExtension(currentFile);

                if (fileExtension.equalsIgnoreCase(extension)) {
                    fileList.add(currentFile);
                }
            }
        }

        //
        return fileList;
    }

    public File check(String checkSum) {

        // Base Case
        if (search == null || checkSum == null) {
            return null;
        }

        // Result list cannot be empty
        if (resultList.isEmpty() == false) {

            // Iterate over the collection of files
            for (int i = 0; i < resultList.size(); i++) {

                // Grab current file
                final File file = new File(resultList.get(i));

                // File must exist
                if (file.exists()) {

                    try {

                        // Default Checksum for this applicationis SHA-1

                        // Generate a checksum of the file
                        String currentSum = FileUtils.generateChecksum(file.getAbsolutePath(), "SHA-1");

                        // Ask the question
                        if (currentSum.equals(checkSum)) {
                            return file;
                        }
                    } catch (Exception ioe) {
                    }
                }
            }
        }

        //
        return null;
    }

    public void printStats() {
        for (String string : resultList) {
            // System.out.println("Result Find: " + string);
        }
    }
}