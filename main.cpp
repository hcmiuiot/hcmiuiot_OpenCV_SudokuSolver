#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <iostream>
using namespace cv;
using namespace std;

vector<Vec2f> lines;

void mergeRelatedLines(vector<Vec2f> *lines, Mat &img)
{
    vector<Vec2f>::iterator current;
    for(current=lines->begin(); current!=lines->end(); current++)
    {
        if((*current)[0]==0 && (*current)[1]==-100) continue;
        float p1 = (*current)[0];
        float theta1 = (*current)[1];
        Point pt1current, pt2current;
        if(theta1>CV_PI*45/180 && theta1<CV_PI*135/180)
        {
            pt1current.x=0;

            pt1current.y = p1/sin(theta1);

            pt2current.x=img.size().width;
            pt2current.y=-pt2current.x/tan(theta1) + p1/sin(theta1);
        }
        else
        {
            pt1current.y=0;

            pt1current.x=p1/cos(theta1);

            pt2current.y=img.size().height;
            pt2current.x=-pt2current.y/tan(theta1) + p1/cos(theta1);

        }
        vector<Vec2f>::iterator    pos;
        for(pos=lines->begin(); pos!=lines->end(); pos++)
        {
            if(*current==*pos) continue;
            if(fabs((*pos)[0]-(*current)[0])<20 && fabs((*pos)[1]-(*current)[1])<CV_PI*10/180)
            {
                float p = (*pos)[0];
                float theta = (*pos)[1];
                Point pt1, pt2;
                if((*pos)[1]>CV_PI*45/180 && (*pos)[1]<CV_PI*135/180)
                {
                    pt1.x=0;
                    pt1.y = p/sin(theta);
                    pt2.x=img.size().width;
                    pt2.y=-pt2.x/tan(theta) + p/sin(theta);
                }
                else
                {
                    pt1.y=0;
                    pt1.x=p/cos(theta);
                    pt2.y=img.size().height;
                    pt2.x=-pt2.y/tan(theta) + p/cos(theta);
                }
                if(((double)(pt1.x-pt1current.x)*(pt1.x-pt1current.x) + (pt1.y-pt1current.y)*(pt1.y-pt1current.y)<64*64) &&
                        ((double)(pt2.x-pt2current.x)*(pt2.x-pt2current.x) + (pt2.y-pt2current.y)*(pt2.y-pt2current.y)<64*64))
                {
                    // Merge the two
                    (*current)[0] = ((*current)[0]+(*pos)[0])/2;

                    (*current)[1] = ((*current)[1]+(*pos)[1])/2;

                    (*pos)[0]=0;
                    (*pos)[1]=-100;
                }
            }
        }
    }
}


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

int main()
{
    VideoCapture stream(0);   //0 is the id of video device.0 if you have only one camera.

    if (!stream.isOpened())   //check if video device has been initialised
    {
        cout << "cannot open camera";
    }

//unconditional loop
    while (true)
    {
        Mat src;
        stream.read(src);
//flip(src,src,1);
        Mat outerBox = Mat(src.size(), CV_8UC1);
        imshow("cam", src);
        cvtColor(src,src, CV_BGR2GRAY);
        GaussianBlur(src,src,Size(11,11),0);
        adaptiveThreshold(src, outerBox, 255, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY_INV, 9, 2);
        Mat element = getStructuringElement(MORPH_RECT, Size(1,1));
        dilate(outerBox,outerBox,element);
        erode(outerBox,outerBox,element);
        imshow("thresh", outerBox);
        int count=0;
        int max=-1;

        Point maxPt;

        for(int y=0; y<outerBox.size().height; y++)
        {
            uchar *row = outerBox.ptr(y);
            for(int x=0; x<outerBox.size().width; x++)
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
        for(int y=0; y<outerBox.size().height; y++)
        {
            uchar *row = outerBox.ptr(y);
            for(int x=0; x<outerBox.size().width; x++)
            {
                if(row[x]==64 && x!=maxPt.x && y!=maxPt.y)
                {
                    int area = floodFill(outerBox, Point(x,y), CV_RGB(0,0,0));
                }
            }
        }
        HoughLines(outerBox, lines,1, CV_PI/180, 200);
        for (int i=0; i<=lines.size(); i++)
        {
            drawLine(lines[i],outerBox, CV_RGB(0,0,128));
        }
        vector<Vec2f> lines;

        HoughLines(outerBox, lines, 1, CV_PI/180, 200);

        mergeRelatedLines(&lines, src);

        imshow("threshold",outerBox);
        if (waitKey(30) >= 0)
            break;
    }

    return 0;

}
