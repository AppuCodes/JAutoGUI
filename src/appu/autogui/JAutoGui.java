package appu.autogui;

import java.awt.AWTException;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

/** x & y depend on screen resolution **/
public class JAutoGui
{
    private static GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    private static HashMap<String, BufferedImage> cachedImages = new HashMap<>();
    public static final Robot robot = createRobot();
    public static final double scalingFactor;
    
    public static void mouseClick(int x, int y, Click click)
    {
        moveMouse(x, y);
        mouseHold(click);
        mouseRelease(click);
    }
    
    public static void mouseHold(Click click)
    {
        int button = 1 << (10 + click.ordinal());
        robot.mousePress(button);
    }
    
    public static void mouseRelease(Click click)
    {
        int button = 1 << (10 + click.ordinal());
        robot.mouseRelease(button);
    }
    
    public static void moveMouse(int x, int y)
    {
        x /= scalingFactor; y /= scalingFactor;
        
        for (int i = 0; i < 3; i++)
        {
            Point point = MouseInfo.getPointerInfo().getLocation();
            
            if (point.getX() != x || point.getY() != y)
                robot.mouseMove(x, y);
        }
    }
    
    public static void dragMouse(int distance, Direction direction)
    {
        mouseHold(Click.LEFT);
        Point point = MouseInfo.getPointerInfo().getLocation();
        point.x *= scalingFactor; point.y *= scalingFactor;
        
        switch (direction)
        {
            case UP:
                moveMouse(point.x, point.y - distance);
                break;
            case DOWN:
                moveMouse(point.x, point.y + distance);
                break;
            case LEFT:
                moveMouse(point.x - distance, point.y);
                break;
            case RIGHT:
                moveMouse(point.x + distance, point.y);
                break;
        }
        
        mouseRelease(Click.LEFT);
    }
    
    public static int[] locateOnScreen(String imagePath)
    {
        BufferedImage image = cachedImages.containsKey(imagePath) ? cachedImages.get(imagePath) : null;
        
        if (image == null)
        {
            File file = new File(imagePath);
            
            try
            {
                if (file.exists())
                    image = ImageIO.read(file);
                else
                {
                    try (InputStream stream = ImageVision.class.getClassLoader().getResourceAsStream(imagePath))
                    {
                        if (stream == null)
                            throw new IOException("Image not found.");
                        image = ImageIO.read(stream);
                    }
                }
            }
            
            catch (IOException e) {}
        }
        
        return ImageVision.locateOnScreen(image);
    }
    
    public static void pause(double seconds)
    {
        try { Thread.sleep((int) (seconds * 1000)); }
        catch (InterruptedException e) {}
    }
    
    public static DisplayMode getScreenResolution()
    {
        return environment.getDefaultScreenDevice().getDisplayMode();
    }
    
    private static Robot createRobot()
    {
        try
        {
            return new Robot();
        }
        
        catch (AWTException e)
        {
            System.err.println("The environment does not have a screen.");
            return null;
        }
    }
    
    static
    {
        double DPI = Toolkit.getDefaultToolkit().getScreenResolution();
        scalingFactor = DPI / 96;
    }
    
    public static enum Click { LEFT, MIDDLE, RIGHT; }
    public static enum Direction { UP, DOWN, LEFT, RIGHT; }
}
