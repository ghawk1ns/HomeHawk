package DataObjects;

/**
 * Created by guyhawkins on 2/10/14.
 */
public class ClientData {
    private String headerId = "";
    private String clientId = "";
    private String sensorId = "";
    private Boolean checkIn = false;

    private SensorData sensorData;

    //db ids
    public String getHeaderId() { return headerId; }
    public String getClientId() { return clientId; }
    public String getSensorId() { return sensorId; }
    //get the sensorData
    public SensorData getSensorData() {return sensorData;}
    //find out if the device is checking in
    public boolean checkingIn(){ return checkIn!=null? checkIn:false; }

    public void setHeaderId(String headerId) { this.headerId = headerId; }
    public void setClientId(String deviceId) { this.clientId = deviceId; }
    public void setSensorId(String sensorId) { this.sensorId = sensorId; }

    public void setCheckIn(Boolean checkIn) { this.checkIn = checkIn; }

    public void setSensorData(SensorData sensorData) { this.sensorData = sensorData; }


    public String toString() {
        return String.format("header:%s\ndeice:%s\nsensor:%s\ncheckIn:%s\nsensorData:%s", headerId,clientId,sensorId,checkIn,sensorData);
    }

    public String getName() {
        return headerId+clientId+sensorId;
    }


    public boolean isActive(){
        if(sensorData == null){
            return false;
        }
        return sensorData.isActive();
    }

}