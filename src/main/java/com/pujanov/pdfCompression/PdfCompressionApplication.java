package com.pujanov.pdfCompression;

import java.awt.image.BufferedImage;
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
	
	public static void main(String[] args) throws InterruptedException, IOException, DocumentException {
		SpringApplication.run(PdfCompressionApplication.class, args);
		
		 try (final PDDocument document = PDDocument.load(new File("C://tmp/bookmark1.pdf"))){
	            PDFRenderer pdfRenderer = new PDFRenderer(document);
	            for (int page = 0; page < document.getNumberOfPages(); ++page)
	            {
	                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
	                String fileName = OUTPUT_DIR + "image-" + page + ".png";
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
			 File compressedImageFile = new File(COMPRESSED_OUTPUT_DIR +"compressed_image"+ n +".png");
			 
			 OutputStream os = new FileOutputStream(compressedImageFile);
			 
			 Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("png");
			    ImageWriter writer = (ImageWriter) writers.next();
			    
			    ImageOutputStream ios = ImageIO.createImageOutputStream(os);
			    writer.setOutput(ios);
			    
			    ImageWriteParam param = writer.getDefaultWriteParam();
			    
			    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			    param.setCompressionQuality(0.005f);  // Change the quality value you prefer
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
		 int m=0;
		 
		 Document document = new Document(PageSize.A4, 20, 20, 20, 20);
		 String output = "C://converted/converted.pdf";
		 FileOutputStream fos = new FileOutputStream(output);
	      PdfWriter writer = PdfWriter.getInstance(document, fos);
	      writer.open();
	      document.open();
		 
		 if(directoryListing != null) {
		 for(File child: directoryListing ) {
		
		 String input = COMPRESSED_OUTPUT_DIR + "compressed_image"+ m +".png"; // .gif and .jpg are ok too!
		   
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
		      System.out.println("ImageAdde---->" + input);
		      
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
