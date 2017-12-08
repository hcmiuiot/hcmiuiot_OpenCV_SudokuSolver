#include <cv.h>
#include <highgui.h>

#include <ml.h>

using namespace cv;
using namespace cv::ml;
#define MAX_NUM_IMAGES    60000
class digitrecognizer
{
public:
    digitrecognizer();

    ~digitrecognizer();

    bool train(char* trainPath, char* labelsPath);

    int classify(Mat img);

private:
    Mat preprocessImage(Mat img);

    int readFlippedInteger(FILE *fp);

private:
   KNearest   *knn;
    int numRows, numCols, numImages;

};
