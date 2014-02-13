package DataObjects;

/**
 * Created by guyhawkins on 2/10/14.
 */
public class SensorData{

    private boolean isActive = false;
    private String time;

    public void setIsActive(boolean isActive){this.isActive = isActive;}
    public void setTime(String time){this.time = time;}

    public boolean isActive(){return isActive;}
    public String getTime(){return time;}

    public String toString() {
        return String.format("isActive:%s,time:%s", isActive,time);
    }
}