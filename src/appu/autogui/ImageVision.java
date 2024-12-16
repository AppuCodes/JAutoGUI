package appu.autogui;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

public class ImageVision
{
    /** Makes locating image on a screen more likely to return a result. **/
    public static boolean grayScale = false;
    /** From 0 to 1, 1 being 1-1 image match. **/
    public static float accuracy = 0.9F;
    
    public static int[] locateOnScreen(BufferedImage image)
    {
        if (image == null) return null;
        BufferedImage screenshot = convertGrayscale(getScreenshot());
        image = convertGrayscale(image);
        
        for (int x = 0; x <= screenshot.getWidth() - image.getWidth(); x++)
        {
            for (int y = 0; y <= screenshot.getHeight() - image.getHeight(); y++)
            {
                if (pixelsMatch(screenshot, image, x, y))
                    return new int[] {x + (image.getWidth() / 2), y + (image.getHeight() / 2)};
            }
        }
        
        return null;
    }
    
    private static boolean pixelsMatch(BufferedImage screenshot, BufferedImage image, int x, int y)
    {
        float density = (float) Math.max(0.1, accuracy);
        int widthOffset = Math.min(image.getWidth(), (int) (image.getWidth() / (image.getWidth() * density))),
            heightOffset = Math.min(image.getHeight(), (int) (image.getHeight() / (image.getHeight() * density)));
        
        for (int i = 0; i < image.getWidth(); i += widthOffset)
        {
            for (int j = 0; j < image.getHeight(); j += heightOffset)
            {
                if (screenshot.getRGB(x + i, y + j) != image.getRGB(i, j))
                    return false;
            }
        }
        
        return true;
    }
    
    private static BufferedImage convertGrayscale(BufferedImage image)
    {
        if (!grayScale)
            return image;
        
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics graphics = newImage.getGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();
        return newImage;
    }
    
    public static BufferedImage getScreenshot()
    {
        int width = JAutoGui.getScreenResolution().getWidth(), height = JAutoGui.getScreenResolution().getHeight();
        Rectangle rectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        return (BufferedImage) JAutoGui.robot.createMultiResolutionScreenCapture(rectangle).getResolutionVariant(width, height);
    }
}
