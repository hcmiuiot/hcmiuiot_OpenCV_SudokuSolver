package club.hcmiuiot.sudokusolver;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
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
			//ImgShow.imshow("src", frame);
			Imgproc.GaussianBlur(frame, blurred, new Size(11,11), 0);
			ImgShow.imshow("blurred", blurred);
			
			Imgproc.cvtColor(blurred, gray, Imgproc.COLOR_BGR2GRAY);
			
			Imgproc.adaptiveThreshold(gray, thresholded, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 11, 2);
			//ImgShow.imshow("thresholded", thresholded);
			
			Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2,2));
			Imgproc.dilate(thresholded, thresholded, kernel);
			ImgShow.imshow("thresholded", thresholded);
			
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
			ImgShow.imshow("floodfill", thresholded);
			
			//Mat lines = new Mat();
			MatOfPoint2f lines = new MatOfPoint2f();
			Imgproc.HoughLines(thresholded, lines, 1, Math.PI/180, 200);
			System.out.println(lines.dump());
			drawLine(thresholded, lines);
			//lines.
			
			ImgShow.imshow("lines", thresholded);
			
			//Imgproc.warpPerspective(src, dst, Imgproc.getPerspectiveTransform(src, dst), dsize);
			
		}
	}
	
	void drawLine(Mat img, MatOfPoint2f lines) {
		for (int i=0; i<lines.toArray().length; i++) {
			//System.out.println("x:" + lines.toArray()[i].x + "\ty:" + lines.toArray()[i].y);
			double rho = lines.toArray()[i].x;
		    double theta = lines.toArray()[i].y;
		    double a = Math.cos(theta), b = Math.sin(theta);
		    double x0 = a*rho, y0 = b*rho;
		    Point pt1 = new Point(Math.round(x0 + 1000*(-b)),
		    		Math.round(y0 + 1000*(a)));
		    Point pt2 = new Point(Math.round(x0 - 1000*(-b)),
		    		Math.round(y0 - 1000*(a)));
		    Imgproc.line(img, pt1, pt2, new Scalar(200));
		}
	}
}


