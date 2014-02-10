/**
 * Created by guyhawkins on 2/10/14.
 */
class SensorData{

    private boolean isActive;
    private String time;

    public void setIsActive(boolean isActive){this.isActive = isActive;}
    public void setTime(String time){this.time = time;}

    public boolean getIsActive(){return isActive;}
    public String getTime(){return time;}

    public String toString() {
        return String.format("isActive:%s,time:%s", isActive,time);
    }
}