package com.atzuche.order.admin.util.oss;

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
	public static final int APP_CHARGE_ICON_LIMIT_SIZE = 20 * 1024; //20k
	public static final int APP_CHARGE_BANNER_LIMIT_SIZE = 100 * 1024; //100k
	public static final int PACKAGE_DETAIL_PIC_LIMIT_SIZE = 200 * 1024; //200k


	/** 400 x 294 */
	public static final int WIDTH_400 = 400;
	public static final int HEIGHT_294 = 294;

	/** 96 x 63 （订单列表 4k）*/
	//public static final int WIDTH_1 = 32 * 3;
	//public static final int HEIGHT_1 = 21 * 3;
	
	public static final int WIDTH_1 = 32 * 12;
	public static final int HEIGHT_1 = 21 * 12;
	
	/** 224 x 147 */
	/*public static final int WIDTH_2 = 32 * 7;
	public static final int HEIGHT_2 = 21 * 7;*/
	
	/** 640 x 420 */
	/*public static final int WIDTH_2 = 32 * 20;
	public static final int HEIGHT_2 = 21 * 20;*/
	
	/** 480 x 315 */
	public static final int WIDTH_2 = 32 * 15;
	public static final int HEIGHT_2 = 21 * 15;
	
	/** 384 x 252 （车辆列表 20k）*/
	//public static final int WIDTH_2 = 32 * 12;
	//public static final int HEIGHT_2 = 21 * 12;
	
	/** 800 x 525 */
	public static final int MAX_WIDTH = 32 * 25;
	public static final int MAX_HEIGH = 21 * 25;
	
	//public static final String JPG = "jpeg";
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
     * 图片缩放（高质量）
     * @param originalFile
     * @param resizedFile
     * @param newWidth
     * @param quality
     * @throws IOException
     */
    public static void resize(File originalFile, File resizedFile, int newWidth, float quality) throws IOException {   
        if (quality > 1) {   
            throw new IllegalArgumentException("Quality has to be between 0 and 1");   
        }   
  
        ImageIcon ii = new ImageIcon(originalFile.getCanonicalPath());   
        Image i = ii.getImage();   
        Image resizedImage = null;   
  
        int iWidth = i.getWidth(null);   
        int iHeight = i.getHeight(null);   
  
        if (iWidth > iHeight) {   
            resizedImage = i.getScaledInstance(newWidth, (newWidth * iHeight) / iWidth, Image.SCALE_SMOOTH);   
        } else {   
            resizedImage = i.getScaledInstance((newWidth * iWidth) / iHeight, newWidth, Image.SCALE_SMOOTH);   
        }   
  
        // This code ensures that all the pixels in the image are loaded.   
        Image temp = new ImageIcon(resizedImage).getImage();   
        // Create the buffered image.   
        BufferedImage bufferedImage = new BufferedImage(temp.getWidth(null), temp.getHeight(null), BufferedImage.TYPE_INT_RGB);   
        // Copy image to buffered image.   
        Graphics g = bufferedImage.createGraphics();   
        // Clear background and paint the image.   
        g.setColor(Color.white);   
        g.fillRect(0, 0, temp.getWidth(null), temp.getHeight(null));   
        g.drawImage(temp, 0, 0, null);   
        g.dispose();   
  
        // Soften.   
        float softenFactor = 0.05f;   
        float[] softenArray = { 0, softenFactor, 0, softenFactor, 1 - (softenFactor * 4), softenFactor, 0, softenFactor, 0 };   
        Kernel kernel = new Kernel(3, 3, softenArray);   
        ConvolveOp cOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);   
        bufferedImage = cOp.filter(bufferedImage, null);   
  
        // Write the jpeg to a file.   
        FileOutputStream out = new FileOutputStream(resizedFile);   
  
        // Encodes image as a JPEG data stream   
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);   
        JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bufferedImage);   
        param.setQuality(quality, true);   
  
        encoder.setJPEGEncodeParam(param);   
        encoder.encode(bufferedImage);   
    } 
    
    
    /**
     * 添加图片水印 (水印紧贴图片右侧)
     * @param targetImg 目标图片路径
     * @param waterImg 水印图片路径
     * @param y 水印距离目标图片上侧的偏移量，小于0则在中间
     * @param alpha 透明度(0.0 -- 1.0, 0.0为完全透明，1.0为完全不透明)
     */
	public static BufferedImage watermarkImage(int size,BufferedImage targetBufImg) {
		Image wartermarkImg = null;
		float alpha = 1;
		int y = 0;
		if(size == 2){
			y = 10;
			wartermarkImg = WATERMARK_IMG_1;
		}else{
			y = 50;
			wartermarkImg = WATERMARK_IMG_2;
		}
		BufferedImage imgWithWartermark = null;
		try {
			//InputStream inputWarterImg = PicUtils.class.getResourceAsStream("watermark@2x.png");
			//BufferedImage  watermarkBufImg = ImageIO.read(inputWarterImg);//把图片水印读入到内存中
			int waterImgWidth = wartermarkImg.getWidth(null);
			int waterImgHeight = wartermarkImg.getHeight(null);
			
			/*String wartImgPath = PicUtils.class.getResource("/").getPath() + "watermark@2x.png";
			BufferedImage  watermarkBufImg = ImageIO.read(new File(wartImgPath));*/
			
			int width = targetBufImg.getWidth(null);
			int height = targetBufImg.getHeight(null);
			//重新画一个图片
			BufferedImage bufNewImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics2d = bufNewImage.createGraphics();
			graphics2d.drawImage(targetBufImg, 0, 0, width, height, null);//画入“目标图片”
			//graphics2d.drawImage(targetBufImg.getScaledInstance(width, height, Image.SCALE_SMOOTH),0,0,null);//画入“目标图片”
			
			graphics2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));//设置透明度
			//Image waterImage = ImageIO.read(waterImg); // 水印文件
			
			//水印图片偏移位置
			int x = width - waterImgWidth;//图片距离左侧距离，计算之后让水印紧贴图片右侧
			int heightDiff = height - waterImgHeight;
			if (y < 0) {
				y = heightDiff / 2;
			} else if (y > heightDiff) {
				y = heightDiff;
			}
			graphics2d.drawImage(wartermarkImg, x, y, waterImgWidth, waterImgHeight, null);//画入“水印图片”
			//graphics2d.drawImage(targetBufImg.getScaledInstance(x, y, Image.SCALE_SMOOTH),0,0,null);//画入“水印图片”
			graphics2d.dispose();
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();//存储图片文件byte数组
			ImageOutputStream ios = ImageIO.createImageOutputStream(bos); 
			ImageIO.write(bufNewImage, TYPE_JPG, ios);//把新生成的带水印的图片写入 ios
			
			//ImageIO.write(bufNewImage, TYPE_JPG, new File("E:\\车辆照片\\大众CC\\压缩\\test.jpg"));//test
			
			InputStream input = new ByteArrayInputStream(bos.toByteArray());
			imgWithWartermark = ImageIO.read(input);
		} catch (Exception e) {
			logger.error("",e);
			//e.printStackTrace();
		}
		return imgWithWartermark;
	}
	
    /**
     * 添加图片水印 (水印紧贴图片右侧)
     * @param targetImg 目标图片路径
     * @param waterImg 水印图片路径
     * @param y 水印距离目标图片上侧的偏移量，小于0则在中间
     * @param alpha 透明度(0.0 -- 1.0, 0.0为完全透明，1.0为完全不透明)
     */
	public static BufferedImage watermarkImage(BufferedImage targetBufImg, int y, float alpha) {
		BufferedImage imgWithWartermark = null;
		try {
			//InputStream inputWarterImg = PicUtils.class.getResourceAsStream("watermark@2x.png");
			//BufferedImage  watermarkBufImg = ImageIO.read(inputWarterImg);//把图片水印读入到内存中
			int waterImgWidth = WATERMARK_IMG_1.getWidth(null);
			int waterImgHeight = WATERMARK_IMG_1.getHeight(null);
			
			/*String wartImgPath = PicUtils.class.getResource("/").getPath() + "watermark@2x.png";
			BufferedImage  watermarkBufImg = ImageIO.read(new File(wartImgPath));*/
			
			int width = targetBufImg.getWidth(null);
			int height = targetBufImg.getHeight(null);
			//重新画一个图片
			BufferedImage bufNewImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics2d = bufNewImage.createGraphics();
			graphics2d.drawImage(targetBufImg, 0, 0, width, height, null);//画入“目标图片”
			//graphics2d.drawImage(targetBufImg.getScaledInstance(width, height, Image.SCALE_SMOOTH),0,0,null);//画入“目标图片”
			
			graphics2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));//设置透明度
			//Image waterImage = ImageIO.read(waterImg); // 水印文件
			
			//水印图片偏移位置
			int x = width - waterImgWidth;//图片距离左侧距离，计算之后让水印紧贴图片右侧
			int heightDiff = height - waterImgHeight;
			if (y < 0) {
				y = heightDiff / 2;
			} else if (y > heightDiff) {
				y = heightDiff;
			}
			graphics2d.drawImage(WATERMARK_IMG_1, x, y, waterImgWidth, waterImgHeight, null);//画入“水印图片”
			//graphics2d.drawImage(targetBufImg.getScaledInstance(x, y, Image.SCALE_SMOOTH),0,0,null);//画入“水印图片”
			graphics2d.dispose();
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();//存储图片文件byte数组
			ImageOutputStream ios = ImageIO.createImageOutputStream(bos); 
			ImageIO.write(bufNewImage, TYPE_JPG, ios);//把新生成的带水印的图片写入 ios
			//ImageIO.write(bufNewImage, TYPE_JPG, new File("E:\\车辆照片\\大众CC\\压缩\\test.jpg"));//test
			InputStream input = new ByteArrayInputStream(bos.toByteArray());
			imgWithWartermark = ImageIO.read(input);
		} catch (Exception e) {
			logger.error("",e);
			//e.printStackTrace();
		}
		return imgWithWartermark;
	}
	
	 /**
     * 添加图片水印 (水印紧贴图片右侧)
     * @param targetImg 目标图片路径
     * @param waterImg 水印图片路径
     * @param y 水印距离目标图片上侧的偏移量，小于0则在中间
     * @param alpha 透明度(0.0 -- 1.0, 0.0为完全透明，1.0为完全不透明)
     */
	public static void watermarkImage(String targetImg, String waterImg, int y, float alpha) {
		try {
			File targetFile = new File(targetImg);
			Image targetImage = ImageIO.read(targetFile);
			int targetImgWidth = targetImage.getWidth(null);
			int targetImgHeight = targetImage.getHeight(null);
			
			BufferedImage bufferedImage = new BufferedImage(targetImgWidth, targetImgHeight, BufferedImage.TYPE_INT_RGB);//new 一个新图片
			Graphics2D graphics2d = bufferedImage.createGraphics();
			graphics2d.drawImage(targetImage, 0, 0, targetImgWidth, targetImgHeight, null);//重新“画图”（画目标图片）
			
			Image waterImage = ImageIO.read(new File(waterImg)); // 水印文件
			int waterImgWidth = waterImage.getWidth(null);
			int waterImgHeight = waterImage.getHeight(null);
			
			graphics2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));//设置透明度
			
			//水印图片偏移位置
			int x = targetImgWidth - waterImgWidth;//图片距离左侧距离，计算之后让水印紧贴图片右侧
			int heightDiff = targetImgHeight - waterImgHeight;
			if (y < 0) {
				y = heightDiff / 2;
			} else if (y > heightDiff) {
				y = heightDiff;
			}
			graphics2d.drawImage(waterImage, x, y, waterImgWidth, waterImgHeight, null); //画 水印图片
			graphics2d.dispose();
			ImageIO.write(bufferedImage, TYPE_JPG, targetFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
     * 添加图片水印 
     * @param targetImg 目标图片路径
     * @param waterImg 水印图片路径
     * @param x 水印距离目标图片左侧的偏移量，小于0则在中间
     * @param y 水印距离目标图片上侧的偏移量，小于0则在中间
     * @param alpha 透明度(0.0 -- 1.0, 0.0为完全透明，1.0为完全不透明)
     */
	public final static void watermarkImage(String targetImg, String waterImg, int x, int y, float alpha) {
		try {
			File targetFile = new File(targetImg);
			Image targetImage = ImageIO.read(targetFile);
			int width = targetImage.getWidth(null);
			int height = targetImage.getHeight(null);
			BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = bufferedImage.createGraphics();
			g.drawImage(targetImage, 0, 0, width, height, null);
			Image waterImage = ImageIO.read(new File(waterImg)); // 水印文件
			int width_1 = waterImage.getWidth(null);
			int height_1 = waterImage.getHeight(null);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
			int widthDiff = width - width_1;
			int heightDiff = height - height_1;
			if (x < 0) {
				x = widthDiff / 2;
			} else if (x > widthDiff) {
				x = widthDiff;
			}
			if (y < 0) {
				y = heightDiff / 2;
			} else if (y > heightDiff) {
				y = heightDiff;
			}
			g.drawImage(waterImage, x, y, width_1, height_1, null); // 水印文件结束
			g.dispose();
			ImageIO.write(bufferedImage, TYPE_JPG, targetFile);
		} catch (IOException e) {
			e.printStackTrace();
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


    
    /**
     * 图片格式转换
     * @param oriFile 原文件
     * @param destFormat 目标格式（jpg、gif、png、bmp）
     * @param destFile 目标文件
     * @throws IOException
     */
    public static void convertFormat(File oriFile, String destFormat, File destFile) throws IOException{
        BufferedImage bImg =ImageIO.read(oriFile);
        ImageIO.write(bImg, destFormat, destFile);
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
	 * 
	 * @param zipFile  压缩包文件对象
	 * @param listKey  压缩的图片物理地址
	 * @return
	 */
	public static boolean packageZip(File zipFile,List<String> listKey){
		//图片打包操作
		ZipOutputStream zipStream = null;
		FileInputStream zipSource = null;
		BufferedInputStream bufferStream = null;
		try {
			zipStream = new ZipOutputStream(new FileOutputStream(zipFile));// 用这个构造最终压缩包的输出流
//			zipSource = null;// 将源头文件格式化为输入流
			
			for (String picKey : listKey) {
    			if(picKey != null){
	    			File file = new File(picKey);
	    			logger.info("uppic zipFile: " + picKey );
	    			if(file != null && file.exists()){     //判断文件是否存在。 huangjing 140826 不能单一以数据库记录为主。 阿里云上面图片出现不一致的情况。@王成
						zipSource = new FileInputStream(file);
						
						byte[] bufferArea = new byte[1024 * 10];// 读写缓冲区
						
						// 压缩条目不是具体独立的文件，而是压缩包文件列表中的列表项，称为条目，就像索引一样
						ZipEntry zipEntry = new ZipEntry(file.getName());
						zipStream.putNextEntry(zipEntry);// 定位到该压缩条目位置，开始写入文件到压缩包中
		
						bufferStream = new BufferedInputStream(zipSource, 1024 * 10);// 输入缓冲流
						int read = 0;
		
						// 在任何情况下，b[0] 到 b[off] 的元素以及 b[off+len] 到 b[b.length-1]
						// 的元素都不会受到影响。这个是官方API给出的read方法说明，经典！
						while ((read = bufferStream.read(bufferArea, 0, 1024 * 10)) != -1) {
							zipStream.write(bufferArea, 0, read);
						}
	    			}
    			}
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("zipStream下载文件报错：", e);
			return false;
		} finally {
			// 关闭流
			try {
				if (null != bufferStream)
					bufferStream.close();
				if (null != zipStream)
					zipStream.close();
				if (null != zipSource)
					zipSource.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				logger.error("close stream下载文件报错：", e);
				return false;
			}
		}
		return true;
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
	
    public static void main(String[] args) throws IOException{
    	// 转换为JGP
       /* convertFormat(new File("c:\\psb.jpg"),JPG, new File("c:\\psb2.jpg"));
        // 转换为GIF
        convertFormat(new File("c:\\psb.jpg"),GIF, new File("c:\\psb2.gif"));
        // 转换为PNG
        convertFormat(new File("c:\\psb.jpg"),PNG, new File("c:\\psb2.png"));
        // 转换为BMP
        convertFormat(new File("c:\\psb.jpg"),BMP, new File("c:\\psb2.bmp"));*/
    	
    	//convertFormat(new File("E:\\车辆照片\\吉普牧马人\\图片3 - 副本.png"), JPG, new File("E:\\车辆照片\\吉普牧马人\\图片3 - 副本.jpg"));
    	//System.out.println(getPicType(new FileInputStream(new File("E:\\workspace\\autoyol_dir\\Autoyol_web\\che - 副本\\images\\ubox-select3.gif"))));
    	System.out.println("图片格式1： " + getPicType(new FileInputStream(new File("D:\\3_副本.png"))));
    	System.out.println("图片格式2： " + getPicType(new FileInputStream(new File("D:\\3.png"))));
    	
    	System.out.println("图片格式3： " + getPicType(new FileInputStream(new File("D:\\7022.jpg"))));
    	
    	
        
    	//watermarkImage("E:\\车辆照片\\大众CC\\压缩\\test.jpg","E:\\workspace\\autoyol_dir\\图片水印\\watermark@2x.png",50,1);
    	//watermarkImage("C:\\Users\\lsl\\Desktop\\map_marker.png", "E:\\车辆照片\\吉普牧马人\\压缩\\图片3 - 副本_副本_副本.jpg", 100,100);
    	//BufferedImage targetBufImg = ImageIO.read(new File("E:\\车辆照片\\大众CC\\压缩\\test.jpg"));
    	//watermarkImage(targetBufImg, 50, 1);
    	
    	/*BufferedImage image = ImageIO.read(new File("E:\\车辆照片\\吉普牧马人\\压缩\\图片3_gaozhiliang.jpg"));
    	
    	Thumbnails.of(image)   
        .size(WIDTH_2, HEIGHT_2)  
        .outputQuality(1)   
        .toFile("E:\\车辆照片\\吉普牧马人\\压缩\\图片3_gaozhiliang_2.jpg"); */
    	//resize(new File("E:\\车辆照片\\吉普牧马人\\压缩\\图片3_gaozhiliang.jpg"), new File("E:\\车辆照片\\吉普牧马人\\压缩\\图片3_gaozhiliangGGGGGGGG.jpg"), WIDTH_2, 1);
    	
    	/*BufferedImage image = ImageIO.read(new File("E:\\车辆照片\\吉普牧马人\\压缩\\watermarkTest.jpg"));
    	BufferedImage imageNew = pressImage(image, WATERMARK_IMG_2, 30);
    	ImageIO.write(imageNew, "jpg", new File("E:\\车辆照片\\吉普牧马人\\压缩\\watermarkT.jpg"));*/
    	
    	/*Thumbnails.of(new File("E:\\车辆照片\\吉普牧马人\\压缩\\watermarkT.jpg"))
    	.size(WIDTH_2,HEIGHT_2)
    	.watermark(Positions.TOP_RIGHT, ImageIO.read(new File("E:\\workspace\\autoyol_dir\\图片水印\\watermark@2x.png")), 1)
    	.toFile(new File("E:\\车辆照片\\吉普牧马人\\压缩\\watermarkTT.jpg"));;*/
    	
    	
		/*List<String> listKey = new ArrayList<String>();
		listKey.add("C:/AtCarPic22/2014-7-10/101165903/0.png");
		listKey.add("C:/AtCarPic22/2014-7-10/101165903/3.png");
		
		java.io.File zipFile = new java.io.File("D:/DownLoad4.zip");// 最终打包的压缩包
		System.out.println("zipFile exists: " + zipFile.exists());
		System.out.println("zipFile size: " + zipFile.length());
    	packageZip(zipFile,listKey);
    	System.out.println("zipFile exists2: " + zipFile.exists());
    	System.out.println("zipFile size: " + zipFile.length());*/
    	
    	FileInputStream in=new FileInputStream(new File("C:\\Users\\pengcheng.fu\\Desktop\\Search-Globe256.png"));
    	if(validatePicSize(in, 100, 200)){
    		System.out.println("上传图片尺寸符合制定格式！");
    	}else{
    		System.out.println("上传图片尺寸不符合制定格式！");
    	}
    	System.out.println("done!");
    }

}
 