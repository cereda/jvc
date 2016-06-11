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

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class Main {
    
    /**
     * Main class.
     * @param args Command line arguments. 
     */
    public static void main(String[] args) {
        
        try {
            
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
            );
            
        }
        catch (Exception nothandled) {
            // out of cheese
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                Viewer viewer = new Viewer();
                viewer.setVisible(true);
            }
            
        });
    }
    
}
