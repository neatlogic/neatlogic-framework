package codedriver.framework.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import codedriver.framework.scheduler.dto.JobObject;

public class SerializerUtil {

	private final static Logger logger = LoggerFactory.getLogger(SerializerUtil.class);
	
	public static byte[] getByteArrayByObject(Object object) {
		
		try( ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
				) {
			oos.writeObject(object);
			byte[] bytes = baos.toByteArray();
			return bytes;
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static Object getObjectByByteArray(byte[] bytes) {
		try( ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
				ObjectInputStream ois = new ObjectInputStream(bais);
				){
				return ois.readObject();
		}catch (IOException | ClassNotFoundException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static void main(String[] args) {
		JobObject jobObject = new JobObject();
		jobObject.setJobId("1");
		jobObject.setJobGroup("abc");
		jobObject.setCron("* * * * * *");
		jobObject.setStartTime(new Date());
		jobObject.setEndTime(new Date());
		jobObject.setJobClassName("qwertyuiop");
		jobObject.setNeedAudit("yes");
		byte[] bytes = getByteArrayByObject(jobObject);
		JobObject jobObject2 = (JobObject) getObjectByByteArray(bytes);
		System.out.println("-----------------------");
	}
}
