package com.pujanov.pdfCompression;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.PDFBox;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

@SpringBootApplication
public class PdfCompressionApplication {

	private static final String OUTPUT_DIR = "/tmp/images/";
	private static final String COMPRESSED_OUTPUT_DIR = "/tmp/compressed-images/";
	
	@Autowired
	public static void main(String[] args) throws InterruptedException, IOException, DocumentException {
		SpringApplication.run(PdfCompressionApplication.class, args);
		
		 try (final PDDocument document = PDDocument.load(new File("C://tmp/bookmark1.pdf"))){
	            PDFRenderer pdfRenderer = new PDFRenderer(document);
	            for (int page = 0; page < document.getNumberOfPages(); ++page)
	            {
	                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
	                String fileName = OUTPUT_DIR + "image-" + page + ".jpg";
	                ImageIOUtil.writeImage(bim, fileName, 300);
	            }
	            document.close();
	        } catch (IOException e){
	            System.err.println("Exception while trying to create pdf document - " + e);
	        } 
		 
		 
		 //TimeUnit.SECONDS.sleep(40);
		 
		 //List<File> images = new ArrayList<File>();
		 
		 //Iterate over compressed Images
		 
		 File dir = new File(OUTPUT_DIR);
		 File[] directoryListing = dir.listFiles();
		 int n=0;
		 
		 if(directoryListing != null) {
		 for(File child: directoryListing ) {
			 
			
			 System.out.println("Filename-->" + child.getName());
		 
			 //for compression
			 
			 BufferedImage image = ImageIO.read(child);
			 
			 
			 //further compression using Scalr class
			 Scalr scalr = new Scalr();
		
			 BufferedImage scalrImage = scalr.resize(image, 400); 
			 
			 //Scalr class compression ends
			 
			 //File compressedImageFile = new File(COMPRESSED_OUTPUT_DIR +"compressed_image"+ n +".jpg");
			 File compressedImageFile = new File(COMPRESSED_OUTPUT_DIR + child.getName());
			
			 OutputStream os = new FileOutputStream(compressedImageFile);
			 
			 Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
			    ImageWriter writer = (ImageWriter) writers.next();
			    
			    ImageOutputStream ios = ImageIO.createImageOutputStream(os);
			    writer.setOutput(ios);
			    
			    ImageWriteParam param = writer.getDefaultWriteParam();
			    
			    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			    param.setCompressionQuality(0.01f);  // Change the quality value you prefer
				writer.write(null, new IIOImage(image, null, null), param);

			    os.close();
			    ios.close();
			    writer.dispose();
			 
			 //compression ends
			 
			 n++;
			 
		 }
		 }
		//Iterate over compressed Images ends
		 
		 
		 //Write to pdf file using iText
		 
		 
		 File dir1 = new File(COMPRESSED_OUTPUT_DIR);
		 File[] directoryListing1 = dir1.listFiles();
		 
		 int fileCount = directoryListing1.length;
		 int m=0;
		 
		 Document document = new Document();
		 String output = "C://converted/converted.pdf";
		 FileOutputStream fos = new FileOutputStream(output);
	      PdfWriter writer = PdfWriter.getInstance(document, fos);
	      writer.open();
	      document.open();
		 
		 if(directoryListing != null) {
		 for(int i =0; i<fileCount; i++ ) {
		
		 String input = COMPRESSED_OUTPUT_DIR + "image-"+ i +".jpg"; // .gif and .jpg are ok too!
		   
		 try {
			 
			 //for resizing
			 
			//if you would have a chapter indentation
			 int indentation = 0;
			 //whatever
			 Image image = Image.getInstance(input);

			 float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
			                - document.rightMargin() - indentation) / image.getWidth()) * 100;

			 image.scalePercent(scaler);
		     
			 //for resizing ends
			 
		      document.add(image);
		      System.out.println("ImageAdded---->" + input);
		      
		    }
		    catch (Exception e) {
		      e.printStackTrace();
		    }
		 
		 m++;
		 }
		 document.close();
	      writer.close();
		 }
		 
		 //Write to pdf file using iText Ends
		 
	}

}
