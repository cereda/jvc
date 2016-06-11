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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.io.FileUtils;

/**
 * Provides the GUI interface.
 * @author Paulo Roberto Massa Cereda
 * @version 1.0
 * @since 1.0
 */
public class Viewer extends JFrame {

    // components
    private final JTextField txtFile;
    private final JButton btnOpen;
    private final JButton btnVerify;
    private final JFileChooser chooser;
    private final JLabel lblJar;
    private final JLabel lblResult;
    private final JProgressBar progressBar;
    private File file;

    /**
     * Constructor.
     */
    public Viewer() {
        
        super("Jar Version Checker");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new MigLayout());

        txtFile = new JTextField(20);
        txtFile.setEnabled(false);
        btnOpen = new JButton(new ImageIcon(getClass().
                getResource("/cereda/checker/images/open.png")));
        btnVerify = new JButton(new ImageIcon(getClass().
                getResource("/cereda/checker/images/play.png")));
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(false);
        lblJar = new JLabel("unavailable");
        lblResult = new JLabel("unavailable");
        chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(false);
        FileNameExtensionFilter filter
                = new FileNameExtensionFilter("Jar files", "jar");
        chooser.setFileFilter(filter);

        btnOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int value = chooser.showOpenDialog(Viewer.this);
                if (value == JFileChooser.APPROVE_OPTION) {
                    file = chooser.getSelectedFile();
                    txtFile.setText(file.getAbsolutePath());
                    lblJar.setText(file.getName() +
                            " (" + FileUtils.byteCountToDisplaySize(
                                    file.length()) + ")");
                }
            }
        });
        
        btnVerify.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (file == null) {
                    display(
                            "Woah there, cowboy",
                            "You need to specify at least one jar file in "
                                    + "order to run this tool. Make sure to "
                                    + "select a file and try again. Nothing "
                                    + "to do, the analysis will halt.",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
                else {
                    btnOpen.setEnabled(false);
                    btnVerify.setEnabled(false);
                    lblResult.setText("unavailable");
                    
                    SwingWorker worker = new SwingWorker() {
                        
                        @Override
                        protected Object doInBackground() throws Exception {
                            
                            try {
                                
                                JarReader reader = new JarReader(file);
                                reader.extract();
                                Set<String> versions = reader.getVersions();
                                lblResult.setText(versions.toString());
                                reader.clean();
                                
                                display(
                                        "Yay!",
                                        "The analysis was successful, way to "
                                                + "go! Make sure to interpret "
                                                + "the result correctly. Have "
                                                + "fun with Java!",
                                        JOptionPane.INFORMATION_MESSAGE
                                );
                                
                            }
                            catch (Exception exception) {
                                display(
                                        "An exception was thrown",
                                        exception.getMessage(),
                                        JOptionPane.ERROR_MESSAGE
                                );
                            }
                            
                            return null;
                        }
                        
                        @Override
                        protected void done() {
                            btnVerify.setEnabled(true);
                            btnOpen.setEnabled(true);
                            progressBar.setIndeterminate(false);
                        }
                        
                    };
                    
                    progressBar.setIndeterminate(true);
                    worker.execute();
                    
                }
            }
        });
        
        JPanel results = new JPanel(new MigLayout());
        results.add(new JLabel("<html><body><strong>Jar file:</strong>"
                + "</body></html>"));
        results.add(lblJar, "wrap");
        results.add(new JLabel("<html><body><strong>Results:</strong>"
                + "</body></html>"));
        results.add(lblResult, "wrap");
        
        add(txtFile, "growy");
        add(btnOpen);
        add(btnVerify, "wrap");
        add(results, "span 3, wrap");
        add(progressBar, "span 3, growx");

        pack();
        setLocationRelativeTo(null);

    }
    
    /**
     * Displays messages.
     * @param title Title.
     * @param message Message.
     * @param value Icon reference.
     */
    private void display(String title, String message, int value) {
        JOptionPane.showMessageDialog(
                null,
                String.format(
                        "<html><body style=\"width:250px\">%s</body></html>",
                        message
                ),
                title,
                value
        );
    }

}
