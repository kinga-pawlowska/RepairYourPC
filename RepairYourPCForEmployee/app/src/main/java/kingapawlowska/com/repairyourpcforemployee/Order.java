package kingapawlowska.com.repairyourpcforemployee;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Kinga on 2017-01-18.
 */
public class Order implements Serializable {

    @SerializedName("id")
    public int id;

    @SerializedName("login")
    public String login;

    @SerializedName("repairIdentifier")
    public String repairIdentifier;

    @SerializedName("manufacturer")
    public String manufacturer;

    @SerializedName("kindOfHardware")
    public String kindOfHardware;

    @SerializedName("model")
    public String model;

    @SerializedName("service")
    public String service;

    @SerializedName("operatingSystem")
    public String operatingSystem;

    @SerializedName("description")
    public String description;

    @SerializedName("link")
    public String link;

    @SerializedName("repairStatus")
    public String repairStatus;

    @SerializedName("estimatedCost")
    public String estimatedCost;

    @SerializedName("paid")
    public String paid;

    @SerializedName("created_at")
    public String created_at;

    @SerializedName("updated_at")
    public String updated_at;
}
