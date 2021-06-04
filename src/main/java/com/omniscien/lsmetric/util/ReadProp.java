package com.omniscien.lsmetric.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.omniscien.lsmetric.model.MetricPropertiesModel;

public class ReadProp {
	
	private String propFilepath = "";
	private FileReader reader = null;
	private MetricPropertiesModel propertiesModel = null;
	private JSONParser jsonParser = null;
	private Gson gson = null;
	
	public ReadProp() {
		// TODO Auto-generated constructor stub
	}
	
	public ReadProp(String propFilepath) {
		this.propFilepath = propFilepath;
		this.jsonParser = new JSONParser();
		this.gson = new Gson();
	}
	
public String getProp(String key)  {
	if (jsonParser == null) {
		jsonParser = new JSONParser();
	}
		String result = "";
		
		
		if (propertiesModel == null) {
			try {
				String propFileName = propFilepath;
				reader = new FileReader(propFileName);
				if (reader != null) {
					// Read JSON file
					String propertiesContentStr = jsonParser.parse(reader).toString();
					if (gson == null) {
						gson = new Gson();
					}
					propertiesModel = gson.fromJson(propertiesContentStr, MetricPropertiesModel.class);
				} else {
					throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
				}

			} catch (Exception e) {
				System.out.println("Exception: " + e);
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		result = getPropettiesValue(key);

		return result;
		
	}

private String getPropettiesValue(String key) {
	String result = "";
	if(key.equals(Constant.temp_Foleder)) {
		result = propertiesModel.getTempFoleder();
	}else if(key.equals(Constant.shell_File_Path)) {
		result = propertiesModel.getShellFilePath();
	}else if(key.equals(Constant.perl_File_Path)) {
		result = propertiesModel.getPerlFilePath();
	}else if(key.equals(Constant.rouge_python_file)) {
		result = propertiesModel.getRougepythonfile();
	}else if(key.equals(Constant.ribse_Script)) {
		result = propertiesModel.getRibseScript();
	}else if(key.equals(Constant.beer_Shell)) {
		result = propertiesModel.getBeerShell();
	}else if(key.equals(Constant.hlepor_Script)) {
		result = propertiesModel.getHleporScript();
	}else if(key.equals(Constant.meteor_Jar)) {
		result = propertiesModel.getMeteorJar();
	}else if(key.equals(Constant.terp_Jar)) {
		result = propertiesModel.getTerpJar();
	}else if(key.equals(Constant.temp_Terp)) {
		result = propertiesModel.getTempTerp();
	}
	return result;
}

}
