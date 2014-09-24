package com.tcs.mobility.sf.lecton.xml2xsd2java.xsd2java.controller;

import org.xml.sax.SAXParseException;

import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Driver;
import com.sun.tools.xjc.XJCListener;
import com.tcs.mobility.sf.lecton.utility.logging.WSConsole;

public class XsdToJavaGenerator {
	public static final int TYPE_ADVANCED = 1;
	public static final int TYPE_STANDARD = 0;

	public static void main(String[] args) {
		System.out.println("I am Iron Man...");
		String[] argArray = { "-Xxew", "-disableXmlSecurity", "-d",
				"C:\\Users\\Saravana\\runtime-EclipseApplicationLuna\\BE_FORTIS_DBIA-ap01-module\\src\\main\\java", "-p",
				"com.bnppf.adm.easybanking.dbia.al.message.getAccountList",
				"C:\\Users\\Saravana\\runtime-EclipseApplicationLuna\\BE_FORTIS_DBIA-ap01-module\\src\\main\\resources\\extract\\xsd\\GetAccountListRequest.xsd" };
		try {
			Driver.run(argArray, new XJCListener() {

				@Override
				public void warning(SAXParseException arg0) {
					// TODO Auto-generated method stub
					System.out.println("Warning");
					showError(arg0);
				}

				@Override
				public void info(SAXParseException arg0) {
					// TODO Auto-generated method stub
					System.out.println("Info");
					showError(arg0);
				}

				@Override
				public void fatalError(SAXParseException arg0) {
					// TODO Auto-generated method stub
					System.out.println("Fatal Error");
					showError(arg0);
				}

				@Override
				public void error(SAXParseException arg0) {
					// TODO Auto-generated method stub
					System.out.println("Error");
					showError(arg0);
				}

				private void showError(SAXParseException arg0) {
					System.out.println(arg0.getLocalizedMessage());
					System.out.println(arg0.getMessage());
					System.out.println(arg0.getStackTrace());
				}
			});
		} catch (BadCommandLineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Generates java files from the xsd provided
	 * 
	 * xjc -d %directoryPath% -p %packagePath% %xsdPath%
	 * 
	 * @param directoryPath
	 *            The source directory where the files has to be generated
	 * @param packagePath
	 *            The package where the files has to be generated
	 * @param xsdPath
	 *            The path of the xsd file
	 */
	public void generate(String directoryPath, String packagePath, String xsdPath, int type) {
		String[] argArray;
		switch (type) {
		case TYPE_STANDARD:
			WSConsole.d("XML Wrapper not enabled");
			argArray = new String[] { "-disableXmlSecurity", "-d", directoryPath, "-p", packagePath, xsdPath };
			break;
		case TYPE_ADVANCED:
			WSConsole.d("XML Wrapper enabled");
			argArray = new String[] { "-Xxew", "-disableXmlSecurity", "-d", directoryPath, "-p", packagePath, xsdPath };
			break;
		default:
			argArray = new String[] { "-disableXmlSecurity", "-d", directoryPath, "-p", packagePath, xsdPath };
			break;
		}

		try {
			Driver.run(argArray, new XJCListener() {

				@Override
				public void warning(SAXParseException arg0) {
					// TODO Auto-generated method stub
					System.out.println("Warning");
					showError(arg0);
				}

				@Override
				public void info(SAXParseException arg0) {
					// TODO Auto-generated method stub
					System.out.println("Info");
					showError(arg0);
				}

				@Override
				public void fatalError(SAXParseException arg0) {
					// TODO Auto-generated method stub
					System.out.println("Fatal Error");
					showError(arg0);
				}

				@Override
				public void error(SAXParseException arg0) {
					// TODO Auto-generated method stub
					System.out.println("Error");
					showError(arg0);
				}

				private void showError(SAXParseException arg0) {
					System.out.println(arg0.getLocalizedMessage());
					System.out.println(arg0.getMessage());
					System.out.println(arg0.getStackTrace());
				}
			});
		} catch (BadCommandLineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
