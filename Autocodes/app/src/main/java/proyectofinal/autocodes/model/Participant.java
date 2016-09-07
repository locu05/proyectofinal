package proyectofinal.autocodes.model;

import android.widget.CheckBox;

/**
 * Created by locu on 2/8/16.
 */
public class Participant {

    String id;
    String name;
    String imageUrl;
    int iconRes;
    boolean showable;
    boolean isDriver;
    CheckBox checkBox;
    Integer groupActive;

    public Participant(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public Participant(String id, String name, String imageUrl, int iconRes) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.iconRes = iconRes;
        showable = true;
    }
    public Integer getGroupActive() {
        return groupActive;
    }

    public void setGroupActive(Integer groupActive) {
        this.groupActive = groupActive;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public boolean isDriver() {
        return isDriver;
    }

    public void setDriver(boolean driver) {
        isDriver = driver;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }
}
