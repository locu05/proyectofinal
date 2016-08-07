package proyectofinal.autocodes.model;

/**
 * Created by locu on 2/8/16.
 */
public class Participant {

    Integer id;
    String name;
    String imageUrl;
    int iconRes;
    boolean showable;

    public Participant(String name, Integer id) {
        this.name = name;
        this.id = id;
    }

    public Participant(Integer id, String name, String imageUrl, int iconRes) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.iconRes = iconRes;
        showable = true;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public boolean isShowable() {
        return showable;
    }

    public void setShowable(boolean showable) {
        this.showable = showable;
    }
}
