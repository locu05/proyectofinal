package proyectofinal.autocodes.model;

import java.io.Serializable;

/**
 * Created by locu on 30/7/16.
 */
public class Group implements Serializable {

    Integer id;
    String name;
    int active;
    String driverId;

    private static final long serialVersionUID = 7526472295623423447L;

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

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }
}
