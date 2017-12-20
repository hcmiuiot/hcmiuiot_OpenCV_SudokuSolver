package club.hcmiuiot.sudokusolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;
import org.opencv.videoio.VideoCapture;

import club.hcmiuiot.opencv.ImgShow;
import club.hcmiuiot.vnOCR.OCRKNN;

public class ImageProcessing {
	
	public void process() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		OCRKNN.train();
		
		VideoCapture vc = new VideoCapture(0);
		Mat frame = new Mat();
		Mat blurred = new Mat();
		Mat gray = new Mat();
		Mat thresholded = new Mat();
		Mat grayImg = new Mat();
		
		while (true) {
			vc.read(frame);
			Imgproc.cvtColor(frame, grayImg, Imgproc.COLOR_BGR2GRAY);
			//ImgShow.imshow("src", frame);
			Imgproc.GaussianBlur(frame, blurred, new Size(11,11), 0);
			//ImgShow.imshow("blurred", blurred);
			
			Imgproc.cvtColor(blurred, gray, Imgproc.COLOR_BGR2GRAY);
			
			Imgproc.adaptiveThreshold(gray, thresholded, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 15, 2);
			
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
			
			List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
			Mat hierarchy = new Mat();
			
			Imgproc.findContours(thresholded, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
			
			MatOfPoint2f approx = new MatOfPoint2f();
			
			if (contours.size()==1)
			
			Imgproc.approxPolyDP(MatOfPoint_to_MatOfPoint2f(contours.get(0)), approx, Imgproc.arcLength(MatOfPoint_to_MatOfPoint2f(contours.get(0)), true)*0.02f, true);
			if (approx.size().height == 4) {
				contours.add(MatOfPoint2f_to_MatOfPoint(approx));
				Imgproc.drawContours(frame, contours, contours.size()-1, new Scalar(0,0,255));
				
				List<Point> srcPoints = new ArrayList<>();
				Converters.Mat_to_vector_Point2f(approx, srcPoints);
				
//				for (Point p : srcPoints) {
//					Imgproc.putText(frame, p.toString(), p, 0, 1f, new Scalar(255,0,0));
//					//System.out.println(srcPoints);
//				}
				
				List<Point> dstPoints = new ArrayList<>();
				
				int size = 600;

				if (srcPoints.get(2).x <= srcPoints.get(0).x &&
						srcPoints.get(2).y >= srcPoints.get(0).y) {
					dstPoints.add(new Point(size,0));
					dstPoints.add(new Point(0,0));
					dstPoints.add(new Point(0,size));
					dstPoints.add(new Point(size,size));
				}
				else {
					dstPoints.add(new Point(0,0));
					dstPoints.add(new Point(0,size));
					dstPoints.add(new Point(size,size));
					dstPoints.add(new Point(size,0));
				}
				
				Mat perspectiveForm = Imgproc.getPerspectiveTransform(Converters.vector_Point2f_to_Mat(srcPoints), Converters.vector_Point2f_to_Mat(dstPoints));
				
				Mat res = new Mat();
				Mat resColor = new Mat();
				
				Imgproc.warpPerspective(grayImg, res, perspectiveForm, new Size(size,size));
				Imgproc.warpPerspective(frame, resColor, perspectiveForm, new Size(size,size));
				//ImgShow.imshow("res", res);
			
				//ImgShow.imshow("00", subDigit(res, 4, 6));
				Mat textMask = new Mat(size, size, CvType.CV_8U);
				
				for (int row=0; row<9; row++) {
					for (int col=0; col<9; col++) {
						int predictNum = predictCell(subDigit(res, row, col));
						double h = res.size().height /9f;
						double w = res.size().width  /9f;
						Imgproc.putText(resColor, ""+predictNum, new Point(col*w, row*h+h), Core.FONT_HERSHEY_COMPLEX, 1.2f, new Scalar(255,0,0));
					}
				}
				
				ImgShow.imshow("data", resColor);
				
				System.out.println("Predict at 0,0: " + predictCell(subDigit(res, 4, 6)));
				//ImgShow.imshow("text", textMask);
			}

			ImgShow.imshow("contours", frame);
				
		}
	}
	
	private static int predictCell(Mat cellImg) {
		Mat thresholded = new Mat();
		Imgproc.adaptiveThreshold(cellImg, thresholded, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 15, 2);
		Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2,2));
//		Imgproc.dilate(thresholded, thresholded, kernel);
		Imgproc.erode(thresholded, thresholded, kernel);
		Imgproc.dilate(thresholded, thresholded, kernel);
		
		List<MatOfPoint> contours =  new ArrayList<>();
		Mat hierarchy = new Mat();
		
		Imgproc.findContours(thresholded, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		
		for (int i=0; i<contours.size(); i++) {
			//Imgproc.drawContours(textMask, contours, i, new Scalar(255,0,0));
			Rect boundingRec = Imgproc.boundingRect(contours.get(0));
			if (boundingRec.height >= 0.3f*cellImg.size().height &&
					boundingRec.width >= 0.05f*cellImg.size().width) {
				Mat submat = thresholded.submat(boundingRec);
				int predictNumber = OCRKNN.predict(submat);
				System.out.println("Predicted: "+ predictNumber);
				
				//Imgproc.putText(textMask, "" + predictNumber, new Point(5,5), 0, 1f, new Scalar(0,0,255));
				ImgShow.imshow("NUMBER", thresholded.submat(boundingRec));
				
				//ImgShow.imshow("text", textMask);
				return predictNumber;
			}
		}
		
		ImgShow.imshow("predict", thresholded);
		return 0;
	}
	
	private static Mat subDigit(Mat img, int row, int col) {
		double cellH = img.size().height/9f;
		double cellW = img.size().width/9f;
		return img.submat((int)(row*cellH), (int)(row*cellH + cellH), (int)(col*cellW), (int)(col*cellW + cellW));
	}
	
	private MatOfPoint2f MatOfPoint_to_MatOfPoint2f(MatOfPoint mp) {
		List<Point> points = new ArrayList<>();
		Converters.Mat_to_vector_Point(mp, points);
		MatOfPoint2f temp = new MatOfPoint2f();
		temp.fromList(points);
		return temp;
	}
	
	private MatOfPoint MatOfPoint2f_to_MatOfPoint(MatOfPoint2f mp) {
		List<Point> points = new ArrayList<>();
		Converters.Mat_to_vector_Point2f(mp, points);
		MatOfPoint temp = new MatOfPoint();
		temp.fromList(points);
		return temp;
	}
	

}


