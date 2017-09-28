package com.wxserver.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;


public class SerializeUtil {
	
	protected static Logger logger =  LoggerFactory.getLogger(SerializeUtil.class);
	
	/**
	 * 反序列化
	 * @param array
	 * @return
	 */
	public static Object deserialize(byte[] array)
	{

		ObjectInputStream OIS = null;
		ByteArrayInputStream BAIS = null;
		try {

			BAIS = new ByteArrayInputStream(array);
			OIS = new ObjectInputStream(BAIS);
			Object o = OIS.readObject();
			OIS.close();
			BAIS.close();
			return o;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (OIS != null) {
				try {
					OIS.close();
				} catch (Throwable thex) {
				}
			}
			if (BAIS != null) {
				try {
					BAIS.close();
				} catch (Throwable thex) {
				}
			}
		}
	
		  return null;
	}
	
	/**
	 * 序列化
	 * @param obj
	 * @return
	 */
	public static byte[] serialize(Object obj)
	{
	    ObjectOutputStream OOS = null;
	    ByteArrayOutputStream BAOS = new ByteArrayOutputStream();
	    try {
	        OOS = new ObjectOutputStream(BAOS);
	        OOS.writeObject(obj);
	        byte[] abc = BAOS.toByteArray();
	        OOS.close();
	        BAOS.close();
	        return abc;  
	    } catch (IOException e) {
	        e.printStackTrace();
	    } catch (Exception ex) {
	        logger.error("序列化时产生错误 ", ex);
	    }finally {
	      if(OOS != null){
	        try{
	          OOS.close();
	        }catch(Throwable thex){}
	      }
	      if(BAOS != null){
	        try{
	          BAOS.close();
	        }catch(Throwable thex){}
	      }
	    }
	    return null;
	}
	public static void main(String[] args) {

	}
	

}
