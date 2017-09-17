package com.thirdtake.au.rts_returners.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Oliver Clarke
 */

public class FileConfigureation {

	private String m_FilePath;

	/**
	 * @param filePath - Create/load a file at the specified path.
	 */
	public FileConfigureation(String filePath) {
		this.m_FilePath = filePath;
	}

	/**
	 * @param parameterPath - Path to the wanted value.
	 * @return String
	 * @throws IOException If the file does not exist
	 * @throws NullPointerException If the wanted value does not exist
	 */
	public String Get(String parameterPath) {
		
		Properties prop = new Properties();
		
		FileInputStream inputStream = null;
		String o = null;
		try {
			inputStream = new FileInputStream(m_FilePath + ".properties"); // Load the config file into memory.
			prop.load(inputStream); // Format the file

			o = prop.getProperty(parameterPath); // Attempt to grab the value at the specified location.
			
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close(); // Unload the config from the memory.
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		if (o == null) { // Check to make sure the object exists
			throw new NullPointerException(String.format("The specified value {0} could not be found!", parameterPath)); // Throw a null pointer exception.
		}

		return o; // Return the value
	}

	/**
	 * @param valuePath - Path to where the value should be stored
	 * @param value - The value to be stored
	 * @throws IOException If an error occurs getting/creating the file
	 */
	public void Set(String valuePath, Object value) {
		Properties prop = new Properties();
		FileInputStream fin = null;
		FileOutputStream fout = null;
		
		try {
			fin  = new FileInputStream(m_FilePath + ".properties");
			prop.load(fin); 
			prop.setProperty(valuePath, value.toString());

			fout  = new FileOutputStream(m_FilePath + ".properties");

			prop.store(fout, null);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fout != null) {
				try {
					fout.close(); // Unload the file from memory.
					fin.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
