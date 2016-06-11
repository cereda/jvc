/**
 *   JVC -- Java Version Checker
 *   Copyright 2016 Paulo Roberto Massa Cereda
 *
 *   Licensed under the  Apache License, Version 2.0
 *   (the  "License"); you  may  not  use this  file
 *   except in compliance with  the License. You may
 *   obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable  law or agreed to
 *   in  writing,  software  distributed  under  the
 *   License  is distributed  on an  "AS IS"  BASIS,
 *   WITHOUT WARRANTIES  OR CONDITIONS OF  ANY KIND,
 *   either express or implied.  See the License for
 *   the specific language governing permissions and
 *   limitations under the License.
 */
package cereda.jvc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;

/**
 * Extracts a jar file into a temporary directory and extracts the version
 * information on each class file. This class relies on command line tools.
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class JarReader {
    
    // temporary directory
    private final Path temp;
    
    // jar file
    private final File jar;
    
    // map containing Java versions
    private final Map<String, String> versions;

    /**
     * Constructor.
     * @param jar Jar file reference.
     * @throws Exception Temporary directory could not be created.
     */
    public JarReader(File jar) throws Exception {
        
        require("javap");
        
        try {
            
            this.jar = jar;
            this.temp = Files.createTempDirectory("temp_checker");
            this.versions = populate();
            
        }
        catch (IOException nothandled) {
            throw new Exception("The temporary directory could not be created. "
                    + "Make sure the default temporary directory of the "
                    + "underlying operating system has the proper permissions "
                    + "and try again. Nothing do do, the analysis will halt.");
        }
    }
    
    /**
     * Extracts the jar file into the temporary directory.
     * @throws Exception Copy failed or the jar is invalid.
     */
    public void extract() throws Exception {
        
        try {
            
            File copy = new File(temp.toFile(), jar.getName());
            Files.copy(jar.toPath(), copy.toPath());
            
        }
        catch (IOException nothandled) {
            throw new Exception("The provided jar file could not be copied "
                    + "into the temporary directory. Make sure the directory "
                    + "has proper permissions and if the file is valid. "
                    + "Nothing to do, the analysis will halt.");
        }
            
        try {
            
            CommandLine cli = new CommandLine("jar");
            cli.addArgument("-xvf");
            cli.addArgument(jar.getName());
            
            DefaultExecutor executor = new DefaultExecutor();
            executor.setWorkingDirectory(this.temp.toFile());
            executor.setStreamHandler(new PumpStreamHandler(null, null, null));
            executor.setExitValue(0);
            executor.execute(cli);
            
        }
        catch (IOException nothandled) {
            throw new Exception("The provided jar file could not be extracted. "
                    + "Make sure the file is valid and if the temporary "
                    + "directory has proper permissions. Nothing to do, the "
                    + "analysis will halt.");
        }
    }
    
    /**
     * Extracts the versions from all class files inside the jar structure.
     * @return A set containing Java versions as strings.
     * @throws Exception The version could not be extracted.
     */
    public Set<String> getVersions() throws Exception {
        
        Set<String> result = new HashSet<>();
        
        Collection<File> files = FileUtils.listFiles(
                temp.toFile(),
                new String[] { "class" },
                true
        );
        
        for (File file : files) {
            result.add(getJavaVersion(extractVersion(file)));
        }
        
        return result;
    }
    
    /**
     * Extracts the version from a class file.
     * @param file The class file.
     * @return The corresponding Java version as string.
     * @throws Exception The version could not be extracted.
     */
    private String extractVersion(File file) throws Exception {
        
        try {
            
            CommandLine cli = new CommandLine("javap");
            cli.addArgument("-verbose");
            cli.addArgument(file.getName());
            
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PumpStreamHandler stream = new PumpStreamHandler(out);

            DefaultExecutor executor = new DefaultExecutor();
            executor.setStreamHandler(stream);
            executor.setWorkingDirectory(file.getParentFile());
            executor.setExitValue(0);
            executor.execute(cli);

            return match(out.toString());
            
        }
        catch (IOException nothandled) {
            throw new Exception("The Java version could not be properly "
                    + "extracted from the class file. Make sure the file is "
                    + "valid and if the temporary directory has proper "
                    + "permissions. Nothing to do, the analysis will halt.");
        }
    }
    
    /**
     * Removes the temporary directory and gets rid of all files and directories
     * inside it.
     */
    public void clean() {
        
        try {
            
            FileUtils.deleteDirectory(temp.toFile());
            
        }
        catch (IOException nothandled) {
            // da doo ron ron
        }
    }
    
    /**
     * Extracts the Java version from the command line output.
     * @param text The command line output.
     * @return The Java version as string.
     */
    private String match(String text) {
        
        String result = "0.0";
        
        try {
            
            Pattern pattern = Pattern.compile("minor version: (\\d+)\n"
                    + "\\s*major version: (\\d+)");
            Matcher matcher = pattern.matcher(text);
            
            if (matcher.find()) {
                result = matcher.group(2).
                        concat(".").
                        concat(matcher.group(1));
            }
            
        }
        catch (Exception nothandled) {
            // don't stop believing
        }
        
        return result;
    }

    /**
     * Populates the version map.
     * @return The populated version map.
     */
    private Map<String, String> populate() {
        
        Map<String, String> list = new HashMap<>();
        
        list.put("45.3", "Java 1.0/1.1");
        list.put("46.0", "Java 1.2");
        list.put("47.0", "Java 1.3");
        list.put("48.0", "Java 1.4");
        list.put("49.0", "Java 1.5");
        list.put("50.0", "Java 1.6");
        list.put("51.0", "Java 1.7");
        list.put("52.0", "Java 1.8");    
        
        return list;
        
    }
    
    /**
     * Get Java version as string, in a human-readable format.
     * @param value The Java version, represented as a float string.
     * @return The Java version in a human-readable format.
     */
    private String getJavaVersion(String value) {
        return versions.getOrDefault(value, "Unknown version");
    }
    
    /**
     * Checks if the provided tools are available in the underlying shell.
     * @param requirements Array of tools.
     * @throws Exception At least one of the tools does not exist.
     */
    private void require(String... requirements) throws Exception {
        
        CommandLine cli;
        DefaultExecutor executor;
        PumpStreamHandler stream = new PumpStreamHandler(null, null, null);
        
        for (String requirement : requirements) {
            
            try {
                
                cli = new CommandLine(requirement);
                cli.addArgument("-version");
                
                executor = new DefaultExecutor();
                executor.setStreamHandler(stream);
                executor.setExitValue(0);
                executor.execute(cli);
                
            }
            catch (IOException nothandled) {
                throw new Exception("The following tools are required: "
                + Arrays.toString(requirements) + " Make sure to include them "
                + "in your path and try again. Nothing to do, the analysis "
                + "will halt.");
            } 
        }
    }
    
}
