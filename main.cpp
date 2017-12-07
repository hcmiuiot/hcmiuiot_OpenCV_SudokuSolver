#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <iostream>
using namespace cv;
using namespace std;

vector<Vec2f> lines;

void drawLine(Vec2f line, Mat &img, Scalar rgb = CV_RGB(0,0,255))
{
    if(line[1]!=0)
    {
        float m = -1/tan(line[1]);

        float c = line[0]/sin(line[1]);

        cv::line(img, Point(0, c), Point(img.size().width, m*img.size().width+c), rgb);
    }
    else
    {
        cv::line(img, Point(line[0], 0), Point(line[0], img.size().height), rgb);
    }

}

int main() {
VideoCapture stream(0);   //0 is the id of video device.0 if you have only one camera.

if (!stream.isOpened()) { //check if video device has been initialised
cout << "cannot open camera";
}

//unconditional loop
while (true) {
Mat src;
stream.read(src);
flip(src,src,1);
Mat outerBox = Mat(src.size(), CV_8UC1);
imshow("cam", src);
cvtColor(src,src, CV_BGR2GRAY);
GaussianBlur(src,src,Size(11,11),0);
adaptiveThreshold(src, outerBox, 255, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY_INV, 9, 2);
Mat element = getStructuringElement(MORPH_RECT, Size(1,1));
dilate(outerBox,outerBox,element);
erode(outerBox,outerBox,element);
int count=0;
    int max=-1;

    Point maxPt;

    for(int y=0;y<outerBox.size().height;y++)
    {
        uchar *row = outerBox.ptr(y);
        for(int x=0;x<outerBox.size().width;x++)
        {
            if(row[x]>=128)
            {

                 int area = floodFill(outerBox, Point(x,y), CV_RGB(0,0,64));

                 if(area>max)
                 {
                     maxPt = Point(x,y);
                     max = area;
                 }
            }
        }

    }
     floodFill(outerBox, maxPt, CV_RGB(255,255,255));
      for(int y=0;y<outerBox.size().height;y++)
    {
        uchar *row = outerBox.ptr(y);
        for(int x=0;x<outerBox.size().width;x++)
        {
            if(row[x]==64 && x!=maxPt.x && y!=maxPt.y)
            {
                int area = floodFill(outerBox, Point(x,y), CV_RGB(0,0,0));
            }
        }
    }
HoughLines(outerBox, lines,1, CV_PI/180, 200);
for (int i=0; i<=lines.size();i++)
{
    drawLine(lines[i],outerBox, CV_RGB(0,0,128));
}
imshow("threshold",outerBox);
if (waitKey(30) >= 0)
break;
}
return 0;
}
