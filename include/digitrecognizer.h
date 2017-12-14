#include <cv.h>
#include <highgui.h>
#include <ml.h>
#include <time.h>
#include <stdio.h>
#include <stdlib.h>


using namespace cv::ml;
#define MAX_NUM_IMAGES    60000
class DigitRecognizer
{
public:
    DigitRecognizer();

    ~DigitRecognizer();

    bool train(char* trainPath, char* labelsPath);

    int classify(cv::Mat img);

private:
    cv::Mat preprocessImage(cv::Mat img);

    int readFlippedInteger(FILE *fp);

private:
    Ptr<KNearest> *knn =KNearest::create();
    int numRows, numCols, numImages;

};
