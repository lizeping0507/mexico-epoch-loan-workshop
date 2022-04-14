package com.epoch.loan.workshop.common.util;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

public class RSAUtils {
			
	private static KeyFactory keyf = null;
	static {
		Security.addProvider(new BouncyCastleProvider());
		try {
			keyf=KeyFactory.getInstance("RSA", "BC");
		}catch(Exception e) {
			//e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public static PublicKey getPublicKey(String publicKey) {
		X509EncodedKeySpec pubX509 = new X509EncodedKeySpec(Base64.decodeBase64(publicKey.getBytes()));
		PublicKey pubKey = null;
		try {
			pubKey=keyf.generatePublic(pubX509);
		} catch (InvalidKeySpecException e) {
			//e.printStackTrace();
			throw new RuntimeException(e);
		}
		return pubKey;
	}
	 
	public static PrivateKey getPrivateKey(String privateKey) {
		PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey.getBytes()));
		PrivateKey privKey = null;
		try {
			privKey=keyf.generatePrivate(priPKCS8);
		} catch (InvalidKeySpecException e) {
			//e.printStackTrace();
			throw new RuntimeException(e);
		}
		return privKey;
	}
	
	public static String encryptData(String data, String publicKey) {
		PublicKey pubKey=getPublicKey(publicKey);
		return encryptData(data,pubKey);
	}
	
	public static String decryptData(String data, String privateKey) {
		PrivateKey privKey = getPrivateKey(privateKey);
		return decryptData(data,privKey);
	}
    
	
    private static String encryptData(String data, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] dataToEncrypt = data.getBytes("utf-8");
            byte[] encryptedData = cipher.doFinal(dataToEncrypt);
            String encryptString = Base64.encodeBase64URLSafeString(encryptedData);
            return encryptString;
        } catch (Exception e) {
            //e.printStackTrace();
        	throw new RuntimeException(e);
        }

    }
    
    private static String decryptData(String data, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] descryptData = Base64.decodeBase64(data);
            byte[] descryptedData = cipher.doFinal(descryptData);
            String srcData = new String(descryptedData, "utf-8");
            return srcData;
        } catch (Exception e) {
            //e.printStackTrace();
        	throw new RuntimeException(e);
        }

    }   
    
    
    
    public static String addSign(String privateKey, String context) {
        //加签算法：SHA1WithRSA
        PrivateKey privKey = getPrivateKey(privateKey);
        byte[] sign=null;
        try {
	        Signature signature = Signature.getInstance("SHA1WithRSA");
	        signature.initSign(privKey);
	        signature.update(context.getBytes("utf-8"));
	        sign = signature.sign();
        } catch (Exception e) {
            //e.printStackTrace();
        	throw new RuntimeException(e);
        }
        return Base64.encodeBase64URLSafeString(sign);
     }

	 public static boolean verifySign(String publicKey, String context, String signData) {
		 PublicKey pubKey=getPublicKey(publicKey);
		 boolean verify=false;
		 try {
		     Signature signature = Signature.getInstance("SHA1WithRSA");
		     signature.initVerify(pubKey);
		     byte[] bytes = Base64.decodeBase64(signData);
		     signature.update(context.getBytes("utf-8"));
		     verify = signature.verify(bytes);
	     } catch (Exception e) {
	    	 //e.printStackTrace();
	    	 throw new RuntimeException(e);
	     }
	     return verify;
	 }

		/**
		 * 按key进行正序排列，之间以&相连
		 * <功能描述>
		 * @param params
		 * @return
		 */
		public static String getSortParams(Map<String, String> params) {
			Map<String, String> map = new TreeMap<String, String>(
				new Comparator<String>() {
					@Override
					public int compare(String obj1, String obj2) {
						// 升序排序
						return obj1.compareTo(obj2);
					}
				});
			for (String key: params.keySet()) {
				map.put(key, params.get(key));
			}

			Set<String> keySet = map.keySet();
			Iterator<String> iter = keySet.iterator();
			String str = "";
			while (iter.hasNext()) {
				String key = iter.next();
				String value = map.get(key);
				str += key + "=" + value + "&";
			}
			if(str.length()>0){
				str = str.substring(0, str.length()-1);
			}
			return str;
		}	 
    
//    public static void main(String args[])  {
//    
//        String privateKey="MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAM6HfYV5lHSwXSEzdCc8X1pxTDgnoFNMr3f1ebYDtC0amcY1k4x2N+49PEk3K5igiGb+CqurlzqlnVkgFr/2iH4FvdoPzpxI7XG8S298Tro/wbRWBcrZd2bakXIwjAU7sasZh/JTjpaTTUg1F3ryIJc6IMc3oRYKxckUpb01khYRAgMBAAECgYEAoIUMWUaCzSMabyinuashzZDLlcWuxa+PnePsAjzkuD25kSWpFX34wLFVfu5jcxAqlCoVLxKByvJX1qKrK+44bJ0Ry7VRMP5Sfr8Xsh31KlKJo9va3DNgPHmT0wF9kyHdQvpKeT0EIm8SJkMCDgz7TLl2ahJzw6hLn2YgjVqWYwUCQQDrLma3OmlBoxtnqMXwHk+KnU37wspy+MCeOtoh3ijeG8DEfyViyEdVxqk9g9r/tLeiO1pORK6QWaAAmSDL1tcvAkEA4M/Jg1gVf81N/BvDaDzwk6Ppr0o2nNd59EQ8nGyh9A0xoChzUp7RxlsV7rHHhmt7nbC0+4FF5oLmFykVuG6WvwJBAMRvmXPp4gjlB/rpSYtqhd2tznk/FoI5rAl99rzbJx995uE5oiyERLEsoiezfrSeadOj56YAUB5Z/f8B6BbaeBkCQElZoo83QzSCwQob6OLu1zPkzE9EMJN1/rWDOh9zllfxohp2eEIhzaIhgAN0f/xMv3WQ/Uv+PtdaKEawQgT+GDMCQCIndkZze93yGmsfxTGCaKmamDdUqdt5AZslIJaBZVvtIVO5G2My++FEb2yRVKfPIcsXCHFDuMccCIFp3oQMTeA=";
//        String publicKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDOh32FeZR0sF0hM3QnPF9acUw4J6BTTK939Xm2A7QtGpnGNZOMdjfuPTxJNyuYoIhm/gqrq5c6pZ1ZIBa/9oh+Bb3aD86cSO1xvEtvfE66P8G0VgXK2Xdm2pFyMIwFO7GrGYfyU46Wk01INRd68iCXOiDHN6EWCsXJFKW9NZIWEQIDAQAB";
//        String data="app_key=12345678&biz_data={\"order_no\":\"245132241561415\"}&biz_enc=0&format=json&method= tis.api.v3.order.notifypushaddinfo&sign_type=RSA&timestamp=2016-01-01 12:00:00&version=2.0";
//		
//        String sign=RSAUtils.addSign(privateKey,data);
//        System.out.println(sign);
//        
//        boolean b=verifySign(publicKey, data, sign) ;
//        //boolean b=verifySign(publicKey, "sDAAFKAFJSDH=a哈哈$dfsf&=dsfs=1#@$&=3", "P0nqh7ghrGsQEtkHZ9APioNh0Ek1ashJWhIOWCI5ws7T7j58vIwccJ2ho11CWDCVJ3IwO2gMxCBvOrpJ+O6m24cb+SIcp+zd9ioMjiAYn6a55mO1hzkaLv+pKN+CV5Ook4h8z3okql2uApaz9UX7o6Va/dd/JtD5yqgaF9T/klw="        		) ;
//        System.out.println(b);
//        
//    }
//    
//	 public static void main(String args[])  {
//		 Map<String,String> param=new HashMap<>();
//		 Map<String, String> dataParam = new HashMap<>();
//		 dataParam.put("catchDataFlag", "1");
//		 dataParam.put("borrowId", "287021853208215552");
//		 dataParam.put("transactionId", "100008");
//		 
//		 param.put("biz_data", JSON.toJSONString(dataParam));
//		 String publicKey="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC8es+OVnNkzjhR9GRI8L2C/xegHIQ0Os34S1PjnoOsX+4UtA9/pEta/0w5lcDfxLTZj6zyJpRcpIHOMtuFUvYWfy4BfEcw/THvzZS6fsjZoZhddh58fSgW1eee2ztKfdQdCf/IjS0creFwItj7bqLPQmriBK9fhon5z3M8Z/vP5QIDAQAB";
//		 boolean b=verifySign(publicKey, RSAUtils.getSortParams(param), "LW2gIoUQa2aZU2HzVSugwhVkViklxrNrf6HcTiVvY8G1siXUC3h5ZKv/gaZAr2JlesJ+6lmxzjIWJgoefwVaMKTPtiFIgNevz2SO/guMG7Vx87vj0nhL//naWZ0d1RkCkDnjgwvTFS9uQ1bhIfaCDeq43JqOIQHiB4xwedE8UAc=") ;
//		 System.out.println(b);
//	 }
}
