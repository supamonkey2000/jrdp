package jrdp;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class NetworkHandler{
	
	public byte[]imageToBytes(BufferedImage image){
		byte[]imageInByte=null;
		try{
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			ImageIO.write(image,"jpg",baos);
			baos.flush();
			imageInByte=baos.toByteArray();
			baos.close();
		}catch(Exception ex){}
        return imageInByte;
	}
	
	public BufferedImage bytesToImage(byte[]imageInByte) {
		BufferedImage bImageFromConvert=null;
		try {
			InputStream in = new ByteArrayInputStream(imageInByte);
			bImageFromConvert = ImageIO.read(in);
		}catch(Exception ex) {}
		return bImageFromConvert;
	}
}