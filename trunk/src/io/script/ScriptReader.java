/**
 * Copyright (c) 2013, Robert Cherry  *
 * All rights reserved.
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
package io.script;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 *
 * @author Robert A. Cherry
 */
public class ScriptReader {

    private ScriptReader() {
        // Static Constructor Override
    }

    public static WorldScriptInterface read(String newScript, HashMap<String, Object> newRefs) {

        // create a script engine manager
        ScriptEngineManager scriptManager = new ScriptEngineManager();

        // create a JavaScript engine
        ScriptEngine scriptEngine = scriptManager.getEngineByName("JavaScript");

        // Output Script
        WorldScriptInterface script = null;

        try {

            // Find the file
            File newFile = new File(newScript);

            // If and only if it exists
            if (newFile.exists()) {

                // Try to read
                try (FileReader reader = new FileReader(newFile)) {

                    // Evaluate the file
                    scriptEngine.eval(reader);

                    if (newRefs != null) {
                        // Add all the references
                        for (Map.Entry<String, Object> map : newRefs.entrySet()) {
                            scriptEngine.put(map.getKey(), map.getValue());
                        }
                    }

                    // Create an invokable object so we can get an instance of the script
                    Invocable invokable = (Invocable) scriptEngine;

                    // Grab the instance
                    script = invokable.getInterface(WorldScriptInterface.class);

                    // Close reader
                    reader.close();
                }
            }
        } catch (IOException | ScriptException e) {
            System.err.println(e);
        }

        // Return the found script
        return script;
    }
}
