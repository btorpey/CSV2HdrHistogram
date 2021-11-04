# CSV2HdrHistogram
Feed arbitrary csv-formatted files to [HdrHistogram](http://hdrhistogram.org/).

CSV2HdrHistogram makes it easy to generate HdrHistogram's from arbitrary csv-format files.

## Usage
java -cp CSV2HdrHistogram.jar CSV2HdrHistogram {parameters} file

## Parameters
Parameter | Description
--- | ---
-h  |  Specifies the number of header rows to skip -- default is 0.  
-s  | Specifies a scaling factor to use on values stored in the histogram -- default is 1.
-c  | Specifies the column containing the value that should be passed to HdrHistogram.
-p  | Specifies the precision to be used for the histogram.
-M  | Specifies the maximum acceptable value for the histogram.  Default is 60,000,000 (60,000,000 microseconds == 1 minute).
-F  | Specifies the separator used in the file.  Default is the tab character ("\t").



