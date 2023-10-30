import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.Timer;
import javax.imageio.ImageIO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

class DogImage {
    List<String> message;
}

public class CarruselDePerritos {
    private JFrame frame;
    private JLabel imageLabel;
    private JComboBox<String> breedComboBox;
    private Timer timer;
    private List<String> breeds;
    private int currentImageIndex;

    public CarruselDePerritos() {
        frame = new JFrame("Carrusel de Imágenes de Perritos");
        imageLabel = new JLabel();
        breedComboBox = new JComboBox<>();
        timer = new Timer();

        // Obtener la lista de razas de perro disponibles
        try {
            URL breedsURL = new URL("https://dog.ceo/api/breeds/list/all");
            String breedsJson = new Scanner(breedsURL.openStream()).useDelimiter("\\A").next();
            JsonObject breedsObject = JsonParser.parseString(breedsJson).getAsJsonObject();
            breeds = breedsObject.keySet().stream().toList();
        } catch (IOException e) {
            e.printStackTrace();
        }

        breedComboBox.addItem("Aleatorio");
        for (String breed : breeds) {
            breedComboBox.addItem(breed);
        }

        breedComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (breedComboBox.getSelectedItem().equals("Aleatorio")) {
                    fetchRandomImages();
                } else {
                    fetchBreedImages((String) breedComboBox.getSelectedItem());
                }
            }
        });

        frame.add(imageLabel, BorderLayout.CENTER);
        frame.add(breedComboBox, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    public void fetchRandomImages() {
        try {
            URL randomImagesURL = new URL("https://dog.ceo/api/breeds/image/random/" + (int)(Math.random() * 11 + 10));
            String randomImagesJson = new Scanner(randomImagesURL.openStream()).useDelimiter("\\A").next();
            List<String> imageUrls = new Gson().fromJson(randomImagesJson, DogImage.class).message;
            showImages(imageUrls);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void fetchBreedImages(String breed) {
        try {
            URL breedImagesURL = new URL("https://dog.ceo/api/breed/" + breed + "/images/random/" + (int)(Math.random() * 11 + 10));
            String breedImagesJson = new Scanner(breedImagesURL.openStream()).useDelimiter("\\A").next();
            List<String> imageUrls = new Gson().fromJson(breedImagesJson, DogImage.class).message;
            showImages(imageUrls);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showImages(List<String> imageUrls) {
        currentImageIndex = 0;
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    String imageUrl = imageUrls.get(currentImageIndex);
                    Image img = ImageIO.read(new URL(imageUrl));
                    imageLabel.setIcon(new ImageIcon(img));
                    currentImageIndex = (currentImageIndex + 1) % imageUrls.size();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        timer.scheduleAtFixedRate(task, 0, 15000);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CarruselDePerritos();
            }
        });
    }
}
