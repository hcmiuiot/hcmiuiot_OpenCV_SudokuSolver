package club.hcmiuiot.vnOCR;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.KNearest;
import org.opencv.ml.Ml;
import org.opencv.ml.StatModel;

import club.hcmiuiot.opencv.ImgShow;

public class OCRKNN {

	private static KNearest knn = KNearest.create();
	private static String trainedFile = "trained.txt";
	
	public OCRKNN() {
		//svm.load(trainedFile);
	}
	
	public OCRKNN(String trainedFile) {
		this.trainedFile = trainedFile;
		new OCRKNN();
	}
	
	public static void train() {
		
		Mat img = new Mat();
		Mat label = new Mat(0,0, CvType.CV_32SC1);

		Mat imgTrain = new Mat(0,0, CvType.CV_32F);
		
		for (int digit=0; digit<10; digit++) {
			for (int element=1; element <= 35; element++) {
				img = Imgcodecs.imread("data/digits2/"+digit+"/a (" +  element + ").jpg", 0);
				System.out.println("data/digits2/"+digit+"/a (" +  element + ").jpg");
				img.convertTo(img, CvType.CV_32F);
				img = img.clone().reshape(1,1);
				//System.out.println(img.dump());
				label.push_back(new MatOfInt(digit));
				imgTrain.push_back(img);
			}			
		}

		knn.train(imgTrain, Ml.ROW_SAMPLE, label);
		knn.save(trainedFile);
		
		System.out.println(knn.isTrained());	
	}
	
	public static int predict(String fileName) {	
		Mat a = Imgcodecs.imread(fileName,0);
		Imgproc.threshold(a, a, 200, 255, Imgproc.THRESH_BINARY);
		Imgproc.resize(a, a, new Size(20,30));
		a.convertTo(a, CvType.CV_32F);
		a = a.clone().reshape(1,1);
		Mat res = new Mat();
		knn.findNearest(a, 9, res);
		//System.out.println("Predicted " + res.dump());
		return (int) res.get(0, 0)[0];
	}
	
	public static int predict(Mat m, int k) {	
		//Mat a = Imgcodecs.imread(fileName,0);
		//Imgproc.threshold(a, a, 200, 255, Imgproc.THRESH_BINARY);
		Imgproc.resize(m, m, new Size(20,30));
		m.convertTo(m, CvType.CV_32F);
		m = m.clone().reshape(1,1);
		Mat res = new Mat();
		knn.findNearest(m, k, res);
		//System.out.println("Predicted " + res.dump());
		return (int) res.get(0, 0)[0];
	}
	
	public static void splitAndSave(String fileName) {
		Mat img = Imgcodecs.imread(fileName);
		Mat digit = new Mat();
		
		Rect rec = new Rect(0, 0, 20, 20);
		
		Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);
		Imgproc.adaptiveThreshold(img, img, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 5, 0);
		//ImgShow.imshow("digits", img);
		int c = 0;
		
		for (int i=0; i< 50; i++) {
			for (int j=0; j<100; j++) {
				rec.x = 20*j;
				rec.y = 20*i;
				digit = new Mat(img, rec);
				Imgcodecs.imwrite("data/"+(i/5)+"/"+c+".jpg", digit, new MatOfInt(Imgcodecs.CV_IMWRITE_PXM_BINARY));
				//digit.convertTo(digit, );
				System.out.println("> data/"+(i/5)+"/"+c+".jpg");
				//train(digit);
				c++;
			}
		}	
	}
	
	public static void test() {
		Mat img = Imgcodecs.imread("data/digits2.png");
		Mat gray = new Mat();
		Mat roi = new Mat();
		Mat saved = new Mat();
		Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);
		//System.out.println(gray);
		
		Imgproc.threshold(gray, gray, 50, 255, Imgproc.THRESH_BINARY);
		
		Scanner sc = new Scanner(System.in);
		
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		
		Imgproc.findContours(gray, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		for (int i=0; i<contours.size(); i++) {
			Rect rec = Imgproc.boundingRect(contours.get(i));
			//Imgproc.rectangle(img, rec.tl(), rec.br(), new Scalar(255,0,0), 2);
			ImgShow.imshow("a", img.submat(rec));
			roi = gray.submat(rec);
			//Imgcodecs.imwrite("data/digits2/"+i+".jpg", gray.submat(rec));
			
			float ratio = roi.height()/30.0f;
			
//			if (ratio > 1f) {
//				Imgproc.pyrDown(roi, saved, new Size(roi.width()/ratio, roi.height()/ratio));
				Imgproc.resize(roi, saved, new Size(20, 30));
//			}
//			else
//				if (ratio < 1f) {
//					Imgproc.pyrUp(roi, saved, new Size(roi.width()/ratio, roi.height()/ratio));
//				}
			
			Imgcodecs.imwrite("data/digits2/"+i+".jpg", saved);
			
			//Imgproc.drawContours(img, contours, i, new Scalar(255,0,0));
		}
		
		Rect rec = Imgproc.boundingRect(contours.get(2));
		
		System.out.println(img);
		
		ImgShow.imshow("digits2", img);
	}
	
	public int ocr(Mat img) {
		return 0;
	}
	
}
