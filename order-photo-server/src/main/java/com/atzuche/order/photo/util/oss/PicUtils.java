package com.atzuche.order.photo.util.oss;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/** 
 * @comments
 * @author  zg 
 * @version 创建时间：2014年3月28日
 */
public class PicUtils {
	private static Logger logger = LoggerFactory.getLogger(PicUtils.class);
	
	public static final int FILE_LIMIT_SIZE = 1024 * 1024 * 3;//3M

	public static final int WIDTH_1 = 32 * 12;
	public static final int HEIGHT_1 = 21 * 12;

	/** 480 x 315 */
	public static final int WIDTH_2 = 32 * 15;
	public static final int HEIGHT_2 = 21 * 15;


    public static final String TYPE_JPG = "jpg";
    public static final String TYPE_GIF = "gif";
    public static final String TYPE_PNG = "png";
    public static final String TYPE_BMP = "bmp";
    public static final String TYPE_UNKNOWN = "unknown";
    
    public static Image WATERMARK_IMG_1 = null;//水印图
    public static Image WATERMARK_IMG_2 = null;//大水印图
    
    static{
    	try {
			WATERMARK_IMG_1 = ImageIO.read(PicUtils.class.getResourceAsStream("watermard_small.png"));
    		WATERMARK_IMG_2 = ImageIO.read(PicUtils.class.getResourceAsStream("watermark_mid.png"));
		} catch (IOException e) {
			logger.error("",e);
		}
    }

	/**
	 * 添加水印
	 * @param targetBufImg 目标文件
	 * @param pressImg 水印文件
	 * @param x
	 * @param y
	 * @param out
	 */
	public static BufferedImage pressImage(BufferedImage targetBufImg, Image watermarkImg, int y) {
		BufferedImage newImg = null;
		try {
			int width = targetBufImg.getWidth(null);
			int height = targetBufImg.getHeight(null);
			//重新生成的图片 
			BufferedImage newImageBuf = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics g = newImageBuf.createGraphics();
			g.drawImage(targetBufImg, 0, 0, width, height, null);

			// 水印文件
			int watermarkWidth = watermarkImg.getWidth(null);
			int watermarkHeight = watermarkImg.getHeight(null);
			//g.drawImage(watermarkImg, (width - watermarkWidth) / 2, (height - watermarkHeight) / 2, watermarkWidth, watermarkHeight, null);
			//水印图片偏移位置
			int x = width - watermarkWidth;//图片距离左侧距离，计算之后让水印紧贴图片右侧
			int heightDiff = height - watermarkHeight;
			if (y < 0) {
				y = heightDiff / 2;
			} else if (y > heightDiff) {
				y = heightDiff;
			}
			g.drawImage(watermarkImg, x, y, watermarkWidth, watermarkHeight, null); //画 水印图片
			g.dispose();// 水印文件结束

			ByteArrayOutputStream bos = new ByteArrayOutputStream();//存储图片文件byte数组
			
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(bos);
			//JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(newImage);   
	        //param.setQuality(0.9f, true);//设置图片质量
			encoder.encode(newImageBuf);
			//newImg = newImageBuf;
			InputStream input = new ByteArrayInputStream(bos.toByteArray());
			newImg = ImageIO.read(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newImg;
	}


    

    
    //========================================================================================
    /**
     * byte数组转换成16进制字符串
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src){    
           StringBuilder stringBuilder = new StringBuilder();    
           if (src == null || src.length <= 0) {    
               return null;    
           }    
           for (int i = 0; i < src.length; i++) {    
               int v = src[i] & 0xFF;    
               String hv = Integer.toHexString(v);    
               if (hv.length() < 2) {    
                   stringBuilder.append(0);    
               }    
               stringBuilder.append(hv);    
           }    
           return stringBuilder.toString();    
       }
    

    /**
     * 根据文件流判断图片类型
     * @param fis
     * @return jpg/png/gif/bmp
     */
	public static String getPicType(InputStream fis) {
		//读取文件的前几个字节来判断图片格式
		byte[] b = new byte[4];
		try {
			fis.read(b, 0, b.length);
			String type = bytesToHexString(b).toUpperCase();
			if (type.contains("FFD8FF")) {
				return TYPE_JPG;
			} else if (type.contains("89504E47")) {
				return TYPE_PNG;
			} else if (type.contains("47494638")) {
				return TYPE_GIF;
			} else if (type.contains("424D")) {
				return TYPE_BMP;
			}else{
				return TYPE_UNKNOWN;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(fis != null){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static BufferedImage resizeAndPressWatermark(int size, BufferedImage image) throws ImageFormatException, IOException {
		BufferedImage newImage = null;
    	switch (size) {
		case 1:
			newImage = Thumbnails.of(image).size(WIDTH_1, HEIGHT_1).outputQuality(0.9).asBufferedImage();
			break;
		case 2:
			newImage = Thumbnails.of(image).size(WIDTH_2, HEIGHT_2).outputQuality(0.9).asBufferedImage();
			newImage = pressImage(newImage, WATERMARK_IMG_1, 20);
			break;
		case 3:
			newImage = Thumbnails.of(image).size(image.getWidth(), image.getHeight()).outputQuality(0.9).asBufferedImage();
			newImage = pressImage(newImage, WATERMARK_IMG_2, 50);
			break;
		default:
			break;
		}
		return newImage;
	}

	/**
	 * 验证图片是否符合指定的分辨率和大小
	 * 
	 * @param in
	 * @param picWidth
	 * @param picHeight
	 * @return
	 */
	public static boolean validatePicSize(InputStream in, int picWidth, int picHeight){
		BufferedImage bufImg;
		try {
			bufImg = ImageIO.read(in);
			int width = bufImg.getWidth();
			int height = bufImg.getHeight();
			if (picWidth != width || picHeight != height) {
				return false;
			}
		} catch (IOException e) {
			logger.error("读取文件流异常：", e);
			return false;
		}
		return true;
	}


}
 