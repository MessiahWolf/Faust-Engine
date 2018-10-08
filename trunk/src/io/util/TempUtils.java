/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Robert A. Cherry
 */
public class TempUtils {

    public static void printList(File[] files) {

        //
        for (int i = 0; i < files.length; i++) {

            //
            final File file = files[i];

            //
            if (file != null) {

                //
                // System.out.println("TP Print: " + file.getAbsolutePath());
            }
        }
    }
}
