package com.atzuche.order.commons;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.pow;

/**
 * 坐标几何运算
 * @author zhiping.li
 * @date 2016/08/16
 */
public class GeoUtils {   

	/** 检查一个坐标是否在多边形内     
	 * @param x 纬度 31.000... 
	 * @param y 经度 121.000...   
	 * @param polygonPoints 多边形边界的经纬度数组   
	 * @return     */  
	public static boolean isPointInPolygon(double x, double y, List<Map<String, Double>> polygonPoints) {
		Point2D.Double geoPoint = buildPoint(x, y); 
		List<Point2D.Double> geoPolygon = buildPolygon(polygonPoints); 
		return GeoUtils.isPointInPolygon(geoPoint, geoPolygon); 
	}  
	/** 检查一个坐标是否在多边形内     * 
	 * @param point 检查的点坐标   
	 * @param polygon 参照的多边形   
	 * @return    
	 */   
	public static boolean isPointInPolygon(Point2D.Double point, List<Point2D.Double> polygon) { 
		GeneralPath p = new GeneralPath();    
		Point2D.Double first = polygon.get(0);  
		p.moveTo(first.x, first.y);    
		polygon.remove(0);    
		polygon.forEach(d -> p.lineTo(d.x, d.y));
		p.lineTo(first.x, first.y);     
		p.closePath();      
		return p.contains(point);   
	}   
	/**  
	 * 构建一个坐标点   
	 * @param x 纬度 31.000... 
	 * @param y 经度 121.000...    
	 * @return   
	 */   
	public static Point2D.Double buildPoint(double x, double y) {    
		return new Point2D.Double(x, y); 
	}   

	/**构建一个多边形   
	 * @param polygonPoints  
	 * @return     */   
	public static List<Point2D.Double> buildPolygon(List<Map<String, Double>> polygonPoints) {      
		List<Point2D.Double> geoPolygon = new ArrayList<>(); 
		polygonPoints.forEach(map -> geoPolygon.add(buildPoint(map.get("lon"), map.get("lat"))));
		return geoPolygon;  
	}

	/**
	 * 计算距离 (和数据库算法统一)
	 * @param originCarLat
	 * @param origionCarLon
	 * @param carLat
	 * @param carLon
	 * @return
	 */
	public static double calcDistance(double carLon,double carLat,double origionCarLon,double originCarLat){
		return new BigDecimal(
				6378.137*2*Math.asin(Math.sqrt(Math.pow(Math.sin( (originCarLat*Math.PI/180-carLat*Math.PI/180)/2),2)
						+Math.cos(originCarLat*Math.PI/180)*Math.cos(carLat*Math.PI/180)*
						pow(Math.sin( (origionCarLon*Math.PI/180-carLon*Math.PI/180)/2),2))))
				.doubleValue();
	}
	public static void main(String[] args) {
		List<Map<String, Double>> polygonPoints = new ArrayList<>();
		Map<String, Double> map = new HashMap<>();
		map.put("lon", 116.39665425);
		map.put("lat", 39.90045671);
		Map<String, Double> map1 = new HashMap<>();
		map1.put("lon", 116.39915943);
		map1.put("lat", 39.90053902);
		Map<String, Double> map2 = new HashMap<>();
		map2.put("lon", 116.39917016);
		map2.put("lat", 39.89993817);
		Map<String, Double> map3 = new HashMap<>();
		map3.put("lon", 116.39663815);
		map3.put("lat", 39.89988879);
		polygonPoints.add(map);
		polygonPoints.add(map1);
		polygonPoints.add(map2);
		polygonPoints.add(map3);
		System.out.println(isPointInPolygon(116.39791489,39.90019744,polygonPoints));

	}
}
