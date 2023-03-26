import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class URLThread implements Runnable{

    private Image image;
    private final Movie movie;


    public Image getImage() {
        return image;
    }


    public URLThread(Movie movie) throws IOException {
        this.movie = movie;
    }

    @Override
    public void run() {
        try {
            doTheUrl();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doTheUrl() throws IOException {
        try {
            URL url = new URL(movie.getImage());
            image = ImageIO.read(url);
        }catch (IOException ex){
            image = ImageIO.read(new File("src/logo.png"));
        }
    }
}
