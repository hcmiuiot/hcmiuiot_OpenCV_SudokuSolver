package club.hcmiuiot.sudokusolver;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;
import org.opencv.videoio.VideoCapture;

import club.hcmiuiot.opencv.ImgShow;

public class ImageProcessing {
	
	public void process() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		VideoCapture vc = new VideoCapture(0);
		Mat frame = new Mat();
		Mat blurred = new Mat();
		Mat gray = new Mat();
		Mat thresholded = new Mat();
		while (true) {
			vc.read(frame);
			ImgShow.imshow("src", frame);
			Imgproc.GaussianBlur(frame, blurred, new Size(11,11), 0);
			//ImgShow.imshow("blurred", blurred);
			
			Imgproc.cvtColor(blurred, gray, Imgproc.COLOR_BGR2GRAY);
			
			Imgproc.adaptiveThreshold(gray, thresholded, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 15, 2);
			ImgShow.imshow("thresholded", thresholded);
			
			Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3,3));
			Imgproc.dilate(thresholded, thresholded, kernel);
			//ImgShow.imshow("thresholded", thresholded);
			
			int maxArea = -1;
			Point maxPt = null;
			for (int row=0; row < thresholded.size().height; row++) {
				for (int col=0; col < thresholded.size().width; col++) {
					if (thresholded.get(row, col)[0] > 128) {
						int area = Imgproc.floodFill(thresholded, Mat.zeros(frame.rows()+2, frame.cols()+2, CvType.CV_8U), new Point(col, row), new Scalar(64));
						
						if (area > maxArea) {
							maxArea = area;
							maxPt = new Point(col, row);
						}
					}
				}
			}
			
			Imgproc.floodFill(thresholded, Mat.zeros(frame.rows()+2, frame.cols()+2, CvType.CV_8U), maxPt, new Scalar(255));
			
			for (int row=0; row < thresholded.size().height; row++) {
				for (int col=0; col < thresholded.size().width; col++) {
					if (thresholded.get(row, col)[0] == 64 && maxPt.x != col && maxPt.y != row) {
						Imgproc.floodFill(thresholded, Mat.zeros(frame.rows()+2, frame.cols()+2, CvType.CV_8U), new Point(col, row), new Scalar(0));
					}
				}
			}
			
			
			//ImgShow.imshow("floodfill", thresholded);
			
			//Mat lines = new Mat();
			//MatOfPoint2f lines = new MatOfPoint2f();
			//Imgproc.HoughLines(thresholded, lines, 1, Math.PI/180, 200);
			//System.out.println(lines.dump());
			//drawLine(thresholded, lines);
			//lines.
			
			//mergeRealatedLines(lines, thresholded);
			//ImgShow.imshow("lines", thresholded);
			
			List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
			Mat hierarchy = new Mat();
			
			Imgproc.findContours(thresholded, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
			
			for (int j=0; j<contours.size(); j++) {
				Imgproc.drawContours(frame, contours, j, new Scalar(255,0,0));
				
			}	
			
			System.out.println(contours.get(0).dump());
			//RotatedRect = Imgproc.minAreaRect(contours.get(0))
			
			ImgShow.imshow("contours", frame);
			//Mat dst = new Mat(900,900, CvType.CV_32F);
			
			List<Point> points = new ArrayList<>();
			points.add(new Point(0,0));
			points.add(new Point(700,0));
			points.add(new Point(0,700));
			points.add(new Point(700,700));
			
			Mat dst;
			dst = Converters.vector_Point_to_Mat(points);
			//mergeRealatedLines(lines, thresholded);
			//Imgproc.getPerspectiveTransform(contours.get(0), dst);
			
			//Imgproc.warpPerspective(src, dst, Imgproc.getPerspectiveTransform(src, dst), dsize);
			
		}
	}
	
	void mergeRealatedLines(MatOfPoint2f lines, Mat img) {
		double CV_PI = 3.1415926535897932384626433832795f;
		for (int i=0; i<lines.toArray().length; i++) {
			
			//System.out.println("x:" + lines.toArray()[i].x + "\ty:" + lines.toArray()[i].y);
			double rho = lines.toArray()[i].x;
			//System.out.println(">>>>" + rho);
		    double theta = lines.toArray()[i].y;
		    
		    if (rho == 0 && theta == -100) continue;
		    
//		    double a = Math.cos(theta);
//		    double b = Math.sin(theta);
//		    double x0 = a*rho, y0 = b*rho;
//		    Point pt1 = new Point(Math.round(x0 + 1000*(-b)),
//		    		Math.round(y0 + 1000*(a)));
//		    Point pt2 = new Point(Math.round(x0 - 1000*(-b)),
//		    		Math.round(y0 - 1000*(a)));
		   // Imgproc.line(img, pt1, pt2, new Scalar(200)); //draw the line
		    
		    Point pt1current = new Point();
		    Point pt2current = new Point();
		    
		    if (theta > CV_PI*45f/180f && theta < CV_PI*135f/180f) {
		    	pt1current.x = 0;
		    	pt1current.y = rho/Math.sin(theta);	    	
		    	pt2current.x = img.size().width;
		    	pt2current.y = -pt2current.x / Math.tan(theta) + rho/Math.sin(theta);
		    }
		    else {
		    	pt1current.y = 0;
		    	pt1current.x = rho/Math.cos(theta);
		    	pt2current.y = img.size().height;
		    	pt2current.x  =-pt2current.y / Math.tan(theta) + rho/Math.cos(theta);
		    }
		    Imgproc.line(img, pt1current, pt2current, new Scalar(200)); //draw the line
		    
		    for (int j=0; j<lines.toArray().length; j++) {
		    	if (j==i) continue;
		    	
		    	if (Math.abs(lines.toArray()[j].x) - rho < 20 && Math.abs(lines.toArray()[j].y) < CV_PI*10f/180f) {
		    		double p = lines.toArray()[j].x;
		    		double theta2 = lines.toArray()[j].y;
		    		Point pt1 = new Point();
		    		Point pt2 = new Point();
		    		
		    		if (theta2 > CV_PI*45f/180f && theta2 < CV_PI*135f/180f) {
		    			pt1.x = 0;
		    			pt1.y = p/Math.sin(theta2);
		    			pt2.x = img.size().width;
		    			pt2.y = -pt2.x/Math.tan(theta2) + p/Math.sin(theta2);
		    		}
		    		else {
		    			pt1.y = 0;
		    			pt2.x = p/Math.cos(theta2);
		    			pt2.y = img.size().height;
		    			pt2.x = -pt2.y / Math.tan(theta2) + p/Math.cos(theta2);
		    		}
		    		
		    		if ( ((pt1.x - pt1current.x)*(pt1.x - pt1current.x) + (pt1.y - pt1current.y)*(pt1.y - pt1current.y) < 64*64) &&
		    		     ((pt2.x - pt2current.x)*(pt2.x - pt2current.x) + (pt2.y - pt2current.y)*(pt2.y - pt2current.y) < 64*64) ) {
		    			//lines.toArray()[i].x 
//		    			lines.put(i, 0, (rho + p)/2f);
//		    			lines.put(i, 1, (theta + theta2)/2f);
//		    			
//		    			lines.put(j, 0, 0);
		    			//lines.put(j, 1, -100,0,0);
		    			//lines.get(j, 1)[0] = -100;
		    		}
		    	}
		    }
		    
		}
		
		
	}
}


