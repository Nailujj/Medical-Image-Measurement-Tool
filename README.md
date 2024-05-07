README

**Image Measure Tool**

The purpose of this Image Measurement Tool is to provide a user-friendly interface for accurately measuring various aspects of images, including distances, circumferences, lengths along paths, and angles.
This tool aims to cater to diverse applications, ranging from microscopy images to medical imaging such as CT and MRI scans and vtk models. 

For this implementation, the vtk models are scrollable - meaning you can scroll through the different slices of a chosen axis to analyse the image.

**Demo video:**

[![Demonstration](https://drive.google.com/uc?export=view&id=1IDCXo02sk9WkDgWyZ1rthtWxFMkqCC5S
)](https://www.youtube.com/watch?v=hQTpJbokDOU)



Requirements:
You need to Download a JDK (Version 17 or above):
https://bell-sw.com/pages/downloads/#jdk-21-lts


The Metadata File must include at least information in the following format:

```
txt-file:
```
description: This is the description of the image.
image-file: <path-to-image-file>(.png /.jpg)
resolution: <xx.xx> <unit>

```
json-file:
```
{
"description" : "This is the description of the image.",
"image_file"  : "path-to-file(.png /.jpg)",
"image_resolution"  : xx.xx,
"image_resolution_unit" : "unit"
}

```
vtk-file:
```
No specific metadata needed. Check data/vtk/wirbel.vtk for an example.



Main Functions:
load a file (png, jpg, vtk) and see the specified image displayed
load different file-formats
load images with different formats
open a specific slide from a volume-dataset
see the title, description and additional meta-info of the selected file
select two or more points to draw a line and measure the length of this line in order to measure a distance or a circumference
select how the line-length should be displayed: in units (mm, cm, m, km, inch, feet, yard, miles) or amount of pixel
see the intensity profile along the line
measure an angle between two lines
select the unit in which the angle should be displayed (radian or degree)




Additional Tools:

Show all Image Data from a Folder via cmd

You can also run Image Measure Tool from the commandline:
Go to the Path where the Application is stored and type: "java -jar .\amrs-1.0-SNAPSHOT-jar-with-dependencies.jar"
To show all Pictures with Metadata in a Folder type: "java -jar .\amrs-1.0-SNAPSHOT-jar-with-dependencies.jar [Foldername]"







