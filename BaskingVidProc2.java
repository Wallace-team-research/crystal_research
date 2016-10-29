import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.features2d.*;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

//code from http://dummyscodes.blogspot.com/2015/12/using-siftsurf-for-object-recognition.html
public class BaskingVidProc2 {

	public static void main(String args[]){
		
		//BufferedWriter bw = null;
		//try {
			/**
			File file = new File("C:/Users/Crystal Ren/Desktop/data.txt");
			if (!file.exists()) {
		   	     file.createNewFile();
		   	  }
			 FileWriter fw = new FileWriter(file);
		   	  bw = new BufferedWriter(fw);
		   	  
	*/
		
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		System.setProperty("java.library.path", "C:/Users/Crystal Ren/eclipse/jee-neon/eclips/opencv/build/java/x64/opencv_ffmpeg2413_64");
		System.loadLibrary("opencv_ffmpeg2413_64"); 

//329-367 frames no butterfly detected		
		   String testerVid ="C:/Users/Crystal Ren/Desktop/finalVidClips/Rec-butterfly_9_clod_15C_30p_cropped.avi";
		   //410+0, 270+
		   //502+ frames not captured
		   //area of only butterfly
		     VideoCapture capture = new VideoCapture(testerVid);
			//capture.open(testerVid);
				//System.out.println("total frames:"+capture.get(7));
		     Mat video_image=new Mat();  

		     int frame =0;
	

		     //to do: find two lines of same intensity along the triangular shadow area (so that the light source can be traced)
		     
		     
	       if( capture.isOpened())  //capture is the video file
		     { 
	    	

		      while( true )  
		      {  
		    	  frame++;
		        capture.read(video_image);
		 
		        
		        //TimeUnit.SECONDS.sleep(1);
		        if (video_image.empty()){
			           System.out.println(" --(!) No captured frame -- Break!");  
			           break; 
		        } 
		       
	        capture.read(video_image); //reads captured frame into the Mat image
    		//Functions.displayImage(Functions.Mat2BufferedImage(video_image));

	        int centerX =video_image.cols()/2-90;
			int centerY =video_image.rows()/2+75;

			Point center= new Point(centerX,centerY);
			
	        		//Mat video_image= video_image.clone();
			
		
			
			
	    			Imgproc.cvtColor(video_image, video_image, Imgproc.COLOR_BGR2GRAY);
	    			Imgproc.threshold(video_image, video_image, 105, 255, Imgproc.THRESH_BINARY_INV); //thresh was 55 changed to 75

	        		//Functions.displayImage(Functions.Mat2BufferedImage(video_image));


	    			//make a circle of radius 507px and make everything outside of it white
	    			//Mat mask = Mat.zeros( video_image.rows()+2,video_image.cols()+2, CvType.CV_8UC1 );
		 			Mat mask = Mat.zeros( video_image.rows()+2,video_image.cols()+2, CvType.CV_8UC1 );
					Core.ellipse(mask, center, new Size(315,245), 5, 0, 360, new Scalar(255,255,255), 1);

	    			Imgproc.floodFill(video_image, mask, new Point(0,0), new Scalar(0,0,0));

	    			Imgproc.floodFill(video_image, mask, new Point(video_image.cols()-1,0), new Scalar(0,0,0));
	    			Imgproc.floodFill(video_image, mask, new Point(0,video_image.rows()-1), new Scalar(0,0,0));
	    			Imgproc.floodFill(video_image, mask, new Point(video_image.cols()-1,video_image.rows()-1), new Scalar(0,0,0));
	    			Imgproc.floodFill(video_image, mask, new Point(video_image.cols()-1,0), new Scalar(0,0,0));
					//Core.ellipse(video_image, center, new Size(315,245), 5, 0, 360, new Scalar(255,255,255), 3);

	        		//Functions.displayImage(Functions.Mat2BufferedImage(video_image));

	    			//Core.ellipse(video_image, center, new Size(500,395), 0, 0, 360, new Scalar(0,0,0), 1);
	    			//Imgproc.erode(video_image, video_image, Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(2,2)), new Point(-1,-1),3);
	    			//Imgproc.erode(video_image, video_image, Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(2,2)));
	    			Imgproc.dilate(video_image, video_image, Imgproc.getStructuringElement(Imgproc.MORPH_DILATE, new Size(3,3)));
	    			Imgproc.erode(video_image, video_image, Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(3,3)));

		    		//Functions.displayImage(Functions.Mat2BufferedImage(video_image));

	        		//Functions.displayImage(Functions.Mat2BufferedImage(video_image));

	    			
	    			ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	    			Imgproc.findContours(video_image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
	    			contours.trimToSize();
	    			Imgproc.drawContours(video_image, contours, -1, new Scalar(255,255,255),2);
	    		
		    		//Functions.displayImage(Functions.Mat2BufferedImage(video_image));

	    			 //int maxIdx=Functions.getLargestContour(contours);
	    			double maxArea = -1;
	    			int maxAreaIdx = -1;
	    			MatOfPoint temp_contour = contours.get(0); //the largest is at the index 0 for starting point
	    			MatOfPoint largest_contour = contours.get(0);
	    			   
	    			for( int i = 0; i < contours.size(); i++ ){
	    				temp_contour = contours.get(i);
    			        double contourArea = Imgproc.contourArea(temp_contour);
    			        
    			     // Get bounding rect of contour
	    	     		Rect rect = Imgproc.boundingRect(temp_contour);
	    	     		// draw enclosing rectangle (all same color, but you could use variable i to make them unique)
	    	     		if (rect.x+rect.width>680 && rect.y+rect.height>175){
	    	     			
	    	     		}
	    	     		
	    	     		else if(rect.x>45 && rect.y>150){
	    	     			if (contourArea > maxArea) {
	    	     				maxArea=contourArea;
	    	     				maxAreaIdx=i;
	    	     				largest_contour = temp_contour;
	    	     		   		
	        			        //compare this contour to the previous largest contour found
	    	     			}
	    	     		}
	    	  
    			        
	    	     		
	    			}

    	     		Rect maxRect = Imgproc.boundingRect(largest_contour); //for testing
    	     		Core.rectangle(video_image, new Point(maxRect.x,maxRect.y), new Point(maxRect.x+maxRect.width,maxRect.y+maxRect.height), new Scalar(255,0,0), 3); //for testing
	    			Core.putText(video_image, "Frame"+frame,new Point(55, 150) , 3,1.0,new Scalar(255,255,255),3); //for test

	    			//Point tster = new Point(680,175);
	    			//Core.circle(video_image, tster, 5, new Scalar(255,255,255));
    	     		Functions.displayImage(Functions.Mat2BufferedImage(video_image));

    	     		//maxArea=Imgproc.contourArea(largest_contour);
		    		System.out.println(maxArea);
		    		
		    		
		    		  //allArea.append(maxArea).append("\n");
	    			//bw.newLine();
	    			
	    			//bw.write(String.valueOf(maxArea));
	    			
	    			//System.out.println("frame "+frame);
    	     		
    	     		
	    			
		      }
		     } 
	       

		//}catch (InterruptedException e) {}
	    		   	  /** 
	    		   	 catch (IOException ioe) {
	    		   	ioe.printStackTrace();
			}
	    		   	finally
	    		   	{ 
	    		   	   try{
	    		   	      if(bw!=null)
	    		   		 bw.close();
	    		   	   }catch(Exception ex){
	    		   	       System.out.println("Error in closing the BufferedWriter"+ex);
	    		   	    }
	    		   	}
	    		   	
	    		   	
	    		   	*/
	    		   	
	    		      }}
	    		   
	    			
	    			
	    			
//271 frame weird contour
