package org.openoffice.testaddon;

import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.NoSuchElementException;
import com.sun.star.container.XEnumeration;
import com.sun.star.container.XEnumerationAccess;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.lib.uno.helper.Factory;
import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.registry.XRegistryKey;
import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.text.XTextContent;
import com.sun.star.text.XWordCursor;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class TestAddOn extends WeakBase
        implements com.sun.star.frame.XDispatchProvider,
        com.sun.star.frame.XDispatch,
        com.sun.star.lang.XServiceInfo,
        com.sun.star.lang.XInitialization {

    private static HashMap<Integer, String> corpus = new HashMap<>();
    private final XComponentContext m_xContext;
    private com.sun.star.frame.XFrame m_xFrame;
    private static final String m_implementationName = TestAddOn.class.getName();
    private static final String[] m_serviceNames = {
        "com.sun.star.frame.ProtocolHandler"};
    com.sun.star.text.XTextDocument m_xTextDocument;

    public TestAddOn(XComponentContext context) {
        m_xContext = context;
    }

    public static XSingleComponentFactory __getComponentFactory(String sImplementationName) {
        XSingleComponentFactory xFactory = null;
        if (sImplementationName.equals(m_implementationName)) {
            xFactory = Factory.createComponentFactory(TestAddOn.class, m_serviceNames);
        }
        //load hashmap with corpus
        try {
            String word;
            File file = new File("C:/Users/Norman_P/Documents/NetBeansProjects/TestAddOn/ukwebalanacorpus.txt");

            Scanner input = new Scanner(file);
            while (input.hasNext()) {
                word = input.next();
                // writer.write(word);
                corpus.put(word.toLowerCase().hashCode(), word);
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found in TestAddon");
        }

        return xFactory;
    }

    public static boolean __writeRegistryServiceInfo(XRegistryKey xRegistryKey) {
        return Factory.writeRegistryServiceInfo(m_implementationName,
                m_serviceNames,
                xRegistryKey);
    }

    // com.sun.star.frame.XDispatchProvider:
    public com.sun.star.frame.XDispatch queryDispatch(com.sun.star.util.URL aURL,
            String sTargetFrameName,
            int iSearchFlags) {
        if (aURL.Protocol.compareTo("org.openoffice.testaddon.testaddon:") == 0) {
            if (aURL.Path.compareTo("HelloWorld") == 0) {
                return this;
            }
        }
        return null;
    }

    // com.sun.star.frame.XDispatchProvider:
    public com.sun.star.frame.XDispatch[] queryDispatches(
            com.sun.star.frame.DispatchDescriptor[] seqDescriptors) {
        int nCount = seqDescriptors.length;
        com.sun.star.frame.XDispatch[] seqDispatcher
                = new com.sun.star.frame.XDispatch[seqDescriptors.length];
        for (int i = 0; i < nCount; ++i) {
            seqDispatcher[i] = queryDispatch(seqDescriptors[i].FeatureURL,
                    seqDescriptors[i].FrameName,
                    seqDescriptors[i].SearchFlags);
        }
        return seqDispatcher;
    }

    // com.sun.star.frame.XDispatch:
    public void dispatch(com.sun.star.util.URL aURL,
            com.sun.star.beans.PropertyValue[] aArguments) {
        if (aURL.Protocol.compareTo("org.openoffice.testaddon.testaddon:") == 0) {
            if (aURL.Path.compareTo("HelloWorld") == 0) {
                // ================add your own code here=======================
                //Gets text of document
                com.sun.star.text.XText xText = m_xTextDocument.getText();
                com.sun.star.text.XTextCursor mxDocCursor;
                XWordCursor xWordCursor;

                // create enumeration to get all text portions of 
                //the paragraph
                XEnumerationAccess xParaEnumerationAccess = (com.sun.star.container.XEnumerationAccess) UnoRuntime.queryInterface(
                        com.sun.star.container.XEnumerationAccess.class,
                        xText);
                XEnumeration xTextPortionEnum = xParaEnumerationAccess.createEnumeration();

                //step 3  Through the Text portions Enumeration, get interface to each individual text portion
                while (xTextPortionEnum.hasMoreElements()) {
                    try {
                        com.sun.star.text.XTextRange xTextPortion = (com.sun.star.text.XTextRange) UnoRuntime.queryInterface(
                                com.sun.star.text.XTextRange.class,
                                xTextPortionEnum.nextElement());

                        mxDocCursor = xTextPortion.getText().createTextCursor();
                        // Get the XWordCursor interface of our text cursor
                        xWordCursor = (XWordCursor) UnoRuntime.queryInterface(
                                XWordCursor.class, mxDocCursor);

                        int i = 0;

                        while (i < 80) {

                            xWordCursor.gotoNextWord(true);
                            //int str = mxDocCursor.getString().toLowerCase().trim().hashCode();
                            int str = xWordCursor.getString().toLowerCase().trim().hashCode();

                            if (corpus.containsKey(str)) {
                                // Access the property set of the cursor, and set the currently selected text
                                XPropertySet xCursorProps = (XPropertySet) UnoRuntime.queryInterface(
                                        XPropertySet.class, mxDocCursor);
                                xCursorProps.setPropertyValue("CharBackColor", 0xF7B928);
                            }
                            xWordCursor.gotoStartOfWord(false);
                            i++;
                        }

                    } catch (NoSuchElementException | WrappedTargetException | UnknownPropertyException | PropertyVetoException | IllegalArgumentException ex) {
                        Logger.getLogger(TestAddOn.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

            }
        }
    }

    public void addStatusListener(com.sun.star.frame.XStatusListener xControl,
            com.sun.star.util.URL aURL) {
        // add your own code here
    }

    public void removeStatusListener(com.sun.star.frame.XStatusListener xControl,
            com.sun.star.util.URL aURL) {
        // add your own code 
    }

    // com.sun.star.lang.XServiceInfo:
    public String getImplementationName() {
        return m_implementationName;
    }

    public boolean supportsService(String sService) {
        int len = m_serviceNames.length;

        for (int i = 0; i < len; i++) {
            if (sService.equals(m_serviceNames[i])) {
                return true;
            }
        }
        return false;
    }

    public String[] getSupportedServiceNames() {
        return m_serviceNames;
    }

    // com.sun.star.lang.XInitialization:
    public void initialize(Object[] object)
            throws com.sun.star.uno.Exception {
        com.sun.star.frame.XModel m_xModel;

        if (object.length > 0) {
            m_xFrame = (com.sun.star.frame.XFrame) UnoRuntime.queryInterface(
                    com.sun.star.frame.XFrame.class, object[0]);
            com.sun.star.frame.XController xController = m_xFrame.getController();

            if (xController != null) {
                m_xModel = (com.sun.star.frame.XModel) xController.getModel();
                m_xTextDocument = (com.sun.star.text.XTextDocument) UnoRuntime.queryInterface(com.sun.star.text.XTextDocument.class, m_xModel);
            }
        }
    }

}
