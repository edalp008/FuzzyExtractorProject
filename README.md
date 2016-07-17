# Fuzzy Extractor

This repo is the implementation of a fuzzy extractor. Namely, this code should load a set of images that will then be used to generated a codebook that will be use to qualify images of faces

Also included is:
- A folder containing 10 greyscale images per face for 40 faces

###Setup

####Mac/Linux
In order to install and include opencv in this project, we recommend you:
1. create a .jar file of the opencv interface, this might be a good resource https://www.youtube.com/watch?v=2RuJ94Xg3jw&ab_channel=RobotsandCode 
2. Once the .jar file is create, you need to add it to the libraries in your project
3. We then have to include the opencv librbaries that were installed on the computer, this couldook something like         
`String libopencv_java = "locationToOpenCV/opencv-2.4.13/build/lib/libopencv_java2413.dylib";`
`System.load(libopencv_java);`
4. Once this is done, the project should run

####Windows

Please add instructions