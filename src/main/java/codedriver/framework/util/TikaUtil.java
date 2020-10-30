package codedriver.framework.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import com.alibaba.fastjson.JSONObject;

public class TikaUtil {

    /**
     * @Author 89770
     * @Time 2020年10月27日  
     * @Description: 自动识别parser，并获取文件内容
     * @Param 
     * @return
      */
    public static JSONObject getFileContentByAutoParser(InputStream inputstream,Boolean isRemoveSymbol) throws IOException, TikaException, SAXException {
          JSONObject fileJson = new JSONObject();
          BodyContentHandler handler = new BodyContentHandler();
          Metadata metadata = new Metadata();
          //FileInputStream inputstream = new FileInputStream(new File("C:\\Users\\89770\\Desktop\\desktop\\nginx.pid"));
          AutoDetectParser  TexTParser = new AutoDetectParser();
          TexTParser.parse(inputstream, handler, metadata);
          //System.out.println("Contents of the document:" + handler.toString().replaceAll("\\s{2,}|\t|\n", ""));
          //System.out.println("Metadata of the document:");
          String[] metadataNames = metadata.names();
          JSONObject metaDataJson = new JSONObject();
          for(String name : metadataNames) {
              metaDataJson.put(name , metadata.get(name));
          }
          fileJson.put("metaData", metaDataJson);
          if(isRemoveSymbol) {
              fileJson.put("content", handler.toString().replaceAll("\\s{2,}|\t|\n", ""));
          }else {
              fileJson.put("content", handler.toString());
          }
          return fileJson;
      }
}
