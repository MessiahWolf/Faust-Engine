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
package io;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class GeneralFileFilter extends FileFilter {

    // Variable Declaration
    // Data Types
    private String fileDescription;
    private String[] acceptedExtensions;

    public GeneralFileFilter(String[] newAcceptedExtensions, String newDesc) {
        acceptedExtensions = newAcceptedExtensions;
        fileDescription = newDesc;
    }

    public String getExtension(File newFile) {
        String referenceName = newFile.getName();
        String editorNameExtension = null;

        int lastIndex = referenceName.lastIndexOf(".");

        if (lastIndex > 0 && lastIndex < newFile.length()) {
            editorNameExtension = referenceName.substring(lastIndex + 1, referenceName.length());
        }

        return editorNameExtension;
    }

    @Override
    public boolean accept(File newFile) {

        // We dont want directories
        if (newFile.isDirectory()) {
            return true;
        }

        if (this.getExtension(newFile) != null) {
            for (int i = 0; i < acceptedExtensions.length; i++) {
                if (this.getExtension(newFile).equalsIgnoreCase(acceptedExtensions[i])) {
                    return true;
                }
            }
        }

        //
        return false;
    }

    @Override
    public String getDescription() {
        return "*" + fileDescription + " File";
    }

}