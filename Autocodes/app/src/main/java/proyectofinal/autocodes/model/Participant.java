package proyectofinal.autocodes.model;

/**
 * Created by locu on 2/8/16.
 */
public class Participant {

    Integer id;
    String name;

    public Participant(String name, Integer id) {
        this.name = name;
        this.id = id;
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


}
