package cirrb.com.cirrab.model;

public class ItemObject {

    private int name;
    private int imageId;

    public ItemObject(int name, int imageId) {
        this.name = name;
        this.imageId = imageId;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }
}

