package rm.com.microproject;

/**
 * Created by Joseph on 09/11/17.
 */

public class ImageDetails {
    String imageId;
    String imageUrl;


    public ImageDetails(String imageId, String imageUrl) {

        this.imageId = imageId;
        this.imageUrl = imageUrl;
    }


    public ImageDetails() {
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
